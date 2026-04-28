package com.project.controller;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.project.dao.TransactionDAO;
import com.project.dao.UtilisateurDAO;
import com.project.enums.TypeTransaction;
import com.project.model.Transaction;
import com.project.model.Utilisateur;
import com.project.utils.AlerteHelper;
import com.project.utils.DateHelper;
import com.project.utils.ResponsiveHelper;
import com.project.utils.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminTransactionsController implements Initializable {

    // ── Header ──
    @FXML private Label headerDate;
    @FXML private Label headerTotalTrans;

    // ── KPI ──
    @FXML private Label kpiTotalTransactions;
    @FXML private Label kpiTotalEntrees;
    @FXML private Label kpiTotalSorties;
    @FXML private Label kpiMontantMoyen;

    // ── Filtres ──
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterType;
    @FXML private ComboBox<String> filterUtilisateur;
    @FXML private DatePicker filterDateDebut;
    @FXML private DatePicker filterDateFin;

    // ── Table ──
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colDescription;
    @FXML private TableColumn<Transaction, String> colCategorie;
    @FXML private TableColumn<Transaction, String> colUtilisateur;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, String> colMontant;
    @FXML private TableColumn<Transaction, Void> colActions;
    @FXML private Label countLabel;

    // ── Pagination ──
    @FXML private Label paginationInfo;
    @FXML private Button btnPremierePage;
    @FXML private Button btnPagePrev;
    @FXML private Button btnPageNext;
    @FXML private Button btnDernierePage;
    @FXML private HBox pageNumbersBox;
    @FXML private ComboBox<Integer> pageSizeCombo;

    // ── Sidebar ──
    @FXML private SidebarController sidebarController;

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    private List<Transaction> transactionsFiltrees = new ArrayList<>();
    private int pageActuelle = 0;
    private int transParPage = 10;
    private boolean isAdmin = false;

    private static final NumberFormat NF = NumberFormat.getNumberInstance(Locale.FRENCH);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isAdmin = SessionManager.isAdmin();
        
        if (!isAdmin) {
            try {
                com.project.utils.NavigationHelper.navigateTo(com.project.utils.NavigationHelper.HOME);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        chargerHeader();
        initFiltres();
        initTable();
        initPaginationControls();
        chargerKPIs();
        chargerTransactions();

        if (sidebarController != null) {
            sidebarController.setActiveItem("adminTransactions");
        }
        ResponsiveHelper.bind(this::onResize);
    }

    private void onResize() {
        if (sidebarController != null) {
            sidebarController.setSidebarVisible(ResponsiveHelper.getWidth() >= ResponsiveHelper.BP_SMALL);
        }
    }

    private void chargerHeader() {
        headerDate.setText(DateHelper.formaterComplet(LocalDate.now()));
    }

    private void initFiltres() {
        filterType.setItems(FXCollections.observableArrayList("Tous", "Entrée", "Sortie"));
        filterType.getSelectionModel().selectFirst();

        List<Utilisateur> utilisateurs = utilisateurDAO.findAll();
        filterUtilisateur.getItems().clear();
        filterUtilisateur.getItems().add("Tous les utilisateurs");
        for (Utilisateur u : utilisateurs) {
            filterUtilisateur.getItems().add(u.getId() + " - " + u.getNom());
        }
        filterUtilisateur.getSelectionModel().selectFirst();
    }

    private void chargerKPIs() {
        double totalEntrees = transactionDAO.getTotalEntreesGlobal();
        double totalSorties = transactionDAO.getTotalSortiesGlobal();
        int totalTransactions = transactionDAO.countAllTransactions();
        double montantMoyen = totalTransactions > 0 ? (totalEntrees + totalSorties) / totalTransactions : 0;

        kpiTotalTransactions.setText(String.valueOf(totalTransactions));
        kpiTotalEntrees.setText(NF.format(totalEntrees) + " FCFA");
        kpiTotalSorties.setText(NF.format(totalSorties) + " FCFA");
        kpiMontantMoyen.setText(NF.format(montantMoyen) + " FCFA");
        headerTotalTrans.setText(totalTransactions + " transactions");
    }

    private void initPaginationControls() {
        pageSizeCombo.setItems(FXCollections.observableArrayList(5, 10, 15, 20, 50));
        pageSizeCombo.getSelectionModel().select(Integer.valueOf(10));
        pageSizeCombo.setOnAction(e -> onPageSizeChanged());
    }

    @FXML
    private void onPageSizeChanged() {
        if (pageSizeCombo.getValue() != null) {
            transParPage = pageSizeCombo.getValue();
            pageActuelle = 0;
            afficherPageTransactions();
        }
    }

    @FXML
    private void goToPremierePage() { 
        pageActuelle = 0; 
        afficherPageTransactions(); 
    }
    
    @FXML
    private void goToDernierePage() { 
        pageActuelle = getTotalPages() - 1; 
        afficherPageTransactions(); 
    }
    
    @FXML
    private void goToPagePrecedente() { 
        if (pageActuelle > 0) { 
            pageActuelle--; 
            afficherPageTransactions(); 
        } 
    }
    
    @FXML
    private void goToPageSuivante() { 
        if (pageActuelle < getTotalPages() - 1) { 
            pageActuelle++; 
            afficherPageTransactions(); 
        } 
    }

    private int getTotalPages() {
        if (transactionsFiltrees.isEmpty()) return 1;
        return (int) Math.ceil((double) transactionsFiltrees.size() / transParPage);
    }

    private void afficherPageTransactions() {
        int total = transactionsFiltrees.size();
        int totalPages = getTotalPages();
        int debut = pageActuelle * transParPage;
        int fin = Math.min(debut + transParPage, total);

        List<Transaction> page = total == 0 ? new ArrayList<>() : transactionsFiltrees.subList(debut, fin);
        transactionsTable.setItems(FXCollections.observableArrayList(page));

        if (total == 0) {
            paginationInfo.setText("Aucune transaction");
        } else {
            paginationInfo.setText("Affichage " + (debut + 1) + "–" + fin + " sur " + total + " transaction(s)");
        }

        btnPremierePage.setDisable(pageActuelle == 0);
        btnPagePrev.setDisable(pageActuelle == 0);
        btnPageNext.setDisable(pageActuelle >= totalPages - 1);
        btnDernierePage.setDisable(pageActuelle >= totalPages - 1);

        construireNumeroPages(totalPages);
        countLabel.setText(total + " transaction(s) trouvée(s)");
    }

    private void construireNumeroPages(int totalPages) {
        pageNumbersBox.getChildren().clear();
        int debut = Math.max(0, pageActuelle - 2);
        int fin = Math.min(totalPages - 1, debut + 4);
        if (fin - debut < 4) debut = Math.max(0, fin - 4);

        for (int i = debut; i <= fin; i++) {
            final int page = i;
            Button btn = new Button(String.valueOf(i + 1));
            btn.setPrefWidth(36);
            btn.setPrefHeight(34);
            btn.setStyle(i == pageActuelle 
                ? "-fx-background-color:#6C63FF; -fx-text-fill:white; -fx-font-size:13px; -fx-font-weight:bold; -fx-background-radius:8; -fx-cursor:hand;"
                : "-fx-background-color:#F7FAFC; -fx-border-color:#E2E8F0; -fx-border-radius:8; -fx-background-radius:8; -fx-font-size:13px; -fx-text-fill:#4A5568; -fx-cursor:hand;");
            btn.setOnAction(e -> { 
                pageActuelle = page; 
                afficherPageTransactions(); 
            });
            pageNumbersBox.getChildren().add(btn);
        }
    }

    @FXML 
    private void onSearchChanged() { 
        appliquerFiltres(); 
    }
    
    @FXML 
    private void onFilterChanged() { 
        appliquerFiltres(); 
    }

    @FXML
    private void onResetFilters() {
        searchField.clear();
        filterType.getSelectionModel().selectFirst();
        filterUtilisateur.getSelectionModel().selectFirst();
        filterDateDebut.setValue(null);
        filterDateFin.setValue(null);
        appliquerFiltres();
    }

    private void appliquerFiltres() {
        String motCle = searchField.getText().trim();
        String typeStr = filterType.getValue();
        LocalDate debut = filterDateDebut.getValue();
        LocalDate fin = filterDateFin.getValue();

        String typeParam = (typeStr == null || typeStr.equals("Tous")) ? null : (typeStr.equals("Entrée") ? "ENTREE" : "SORTIE");
        
        // Filtrer par utilisateur
        Integer userIdParam = null;
        String selectedUser = filterUtilisateur.getValue();
        if (selectedUser != null && !selectedUser.equals("Tous les utilisateurs")) {
            String[] parts = selectedUser.split(" - ");
            if (parts.length > 0) {
                try {
                    userIdParam = Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    System.err.println("Erreur parsing userId: " + e.getMessage());
                }
            }
        }

        // Récupérer les transactions
        transactionsFiltrees = transactionDAO.rechercherGlobal(typeParam, null, debut, fin, motCle.isEmpty() ? null : motCle);
        
        // Filtrer par utilisateur si nécessaire
        if (userIdParam != null) {
            List<Transaction> filtered = new ArrayList<>();
            for (Transaction t : transactionsFiltrees) {
                if (t.getUtilisateurId() == userIdParam) {
                    filtered.add(t);
                }
            }
            transactionsFiltrees = filtered;
        }

        // Ajouter le nom de l'utilisateur
        for (Transaction t : transactionsFiltrees) {
            Utilisateur u = utilisateurDAO.findById(t.getUtilisateurId());
            t.setUtilisateurNom(u != null ? u.getNom() : "—");
        }

        // Trier par date décroissante
        transactionsFiltrees.sort((a, b) -> b.getDateTransaction().compareTo(a.getDateTransaction()));
        
        pageActuelle = 0;
        afficherPageTransactions();
        chargerKPIs();
    }

    private void initTable() {
        transactionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colDate.setCellValueFactory(data -> new SimpleStringProperty(DateHelper.formaterCourt(data.getValue().getDateTransaction())));
        colDescription.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription() != null ? data.getValue().getDescription() : "—"));
        colCategorie.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategorieNom() != null ? data.getValue().getCategorieNom() : "—"));
        colUtilisateur.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUtilisateurNom() != null ? data.getValue().getUtilisateurNom() : "—"));
        
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType().getLibelle()));
        colType.setCellFactory(col -> new TableCell<>() {
            @Override 
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { 
                    setText(null); 
                    setStyle(""); 
                } else { 
                    setText(item); 
                    setStyle(item.equals("Entrée") ? "-fx-text-fill:#27AE60; -fx-font-weight:bold;" : "-fx-text-fill:#E74C3C; -fx-font-weight:bold;"); 
                }
            }
        });

        colMontant.setCellValueFactory(data -> {
            Transaction t = data.getValue();
            String signe = t.getType() == TypeTransaction.ENTREE ? "+ " : "- ";
            return new SimpleStringProperty(signe + NF.format(t.getMontant()) + " FCFA");
        });
        colMontant.setCellFactory(col -> new TableCell<>() {
            @Override 
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { 
                    setText(null); 
                    setStyle(""); 
                } else { 
                    setText(item); 
                    setStyle(item.startsWith("+") ? "-fx-text-fill:#27AE60; -fx-font-weight:bold;" : "-fx-text-fill:#E74C3C; -fx-font-weight:bold;"); 
                }
            }
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("✕ Supprimer");
            private final HBox box = new HBox(8, btnDelete);
            { 
                btnDelete.getStyleClass().add("btn-action-delete"); 
                box.setAlignment(Pos.CENTER); 
            }
            @Override 
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { 
                    setGraphic(null); 
                } else {
                    Transaction t = getTableView().getItems().get(getIndex());
                    btnDelete.setOnAction(e -> confirmerSuppression(t));
                    setGraphic(box);
                }
            }
        });

        transactionsTable.setPlaceholder(new Label("Aucune transaction trouvée."));
    }

    private void chargerTransactions() {
        transactionsFiltrees = transactionDAO.rechercherGlobal(null, null, null, null, null);
        for (Transaction t : transactionsFiltrees) {
            Utilisateur u = utilisateurDAO.findById(t.getUtilisateurId());
            t.setUtilisateurNom(u != null ? u.getNom() : "—");
        }
        transactionsFiltrees.sort((a, b) -> b.getDateTransaction().compareTo(a.getDateTransaction()));
        pageActuelle = 0;
        afficherPageTransactions();
    }

    private void confirmerSuppression(Transaction t) {
        String msg = "Supprimer la transaction de " + t.getUtilisateurNom() + " :\n" 
            + (t.getDescription() != null ? t.getDescription() : "—") + " — " + NF.format(t.getMontant()) + " FCFA\n\n⚠ Action irréversible !";
        if (AlerteHelper.confirmer("Confirmer la suppression", msg)) {
            if (transactionDAO.supprimer(t.getId(), t.getUtilisateurId())) {
                chargerTransactions();
                chargerKPIs();
            } else {
                AlerteHelper.erreur("Erreur", "Impossible de supprimer cette transaction.");
            }
        }
    }
}