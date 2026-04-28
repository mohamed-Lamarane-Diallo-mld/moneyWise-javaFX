package com.project.controller;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UsersTransactionsController implements Initializable {

    @FXML private Label headerDate;
    @FXML private Label headerUser;
    @FXML private Label totalTransactionsLabel;
    @FXML private Label totalEntreesLabel;
    @FXML private Label totalSortiesLabel;
    @FXML private Label soldeGlobalLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterType;
    @FXML private ComboBox<String> filterUtilisateur;
    @FXML private DatePicker filterDateDebut;
    @FXML private DatePicker filterDateFin;
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colDescription;
    @FXML private TableColumn<Transaction, String> colCategorie;
    @FXML private TableColumn<Transaction, String> colUtilisateur;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, String> colMontant;
    @FXML private TableColumn<Transaction, Void> colActions;
    @FXML private Label countLabel;
    @FXML private Label paginationInfo;
    @FXML private Button btnPremierePage;
    @FXML private Button btnPagePrev;
    @FXML private Button btnPageNext;
    @FXML private Button btnDernierePage;
    @FXML private HBox pageNumbersBox;
    @FXML private ComboBox<Integer> pageSizeCombo;
    @FXML private BarChart<String, Number> chartTransactionsParMois;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private SidebarController sidebarController;

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private List<Transaction> transactionsFiltrees = new ArrayList<>();
    private int pageActuelle = 0;
    private int transParPage = 10;
    private static final NumberFormat NF = NumberFormat.getNumberInstance(Locale.FRENCH);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chargerHeader();
        initTable();
        initPagination();
        initFiltres();
        chargerToutesTransactions();
        chargerGraphique();
        if (sidebarController != null) sidebarController.setActiveItem("adminTransactions");
        ResponsiveHelper.bind(this::onResize);
    }

    private void onResize() {
        if (sidebarController != null) sidebarController.setSidebarVisible(ResponsiveHelper.getWidth() >= ResponsiveHelper.BP_SMALL);
    }

    private void chargerHeader() {
        headerDate.setText(DateHelper.formaterComplet(LocalDate.now()));
        String nom = SessionManager.getUtilisateur().getNom().split(" ")[0];
        headerUser.setText("Vue globale");
    }

    private void initPagination() {
        pageSizeCombo.setItems(FXCollections.observableArrayList(5, 10, 15, 20, 50));
        pageSizeCombo.getSelectionModel().select(Integer.valueOf(10));
        btnPremierePage.setOnAction(e -> goToPremierePage());
        btnPagePrev.setOnAction(e -> goToPagePrecedente());
        btnPageNext.setOnAction(e -> goToPageSuivante());
        btnDernierePage.setOnAction(e -> goToDernierePage());
    }

    @FXML private void onPageSizeChanged() { if (pageSizeCombo.getValue() != null) { transParPage = pageSizeCombo.getValue(); pageActuelle = 0; afficherPageTransactions(); } }
    private void goToPremierePage() { pageActuelle = 0; afficherPageTransactions(); }
    private void goToDernierePage() { pageActuelle = getTotalPages() - 1; afficherPageTransactions(); }
    private void goToPagePrecedente() { if (pageActuelle > 0) { pageActuelle--; afficherPageTransactions(); } }
    private void goToPageSuivante() { if (pageActuelle < getTotalPages() - 1) { pageActuelle++; afficherPageTransactions(); } }
    private int getTotalPages() { if (transactionsFiltrees.isEmpty()) return 1; return (int) Math.ceil((double) transactionsFiltrees.size() / transParPage); }

    private void afficherPageTransactions() {
        int total = transactionsFiltrees.size();
        int totalPages = getTotalPages();
        int debut = pageActuelle * transParPage;
        int fin = Math.min(debut + transParPage, total);
        List<Transaction> page = total == 0 ? new ArrayList<>() : transactionsFiltrees.subList(debut, fin);
        transactionsTable.setItems(FXCollections.observableArrayList(page));
        if (total == 0) { paginationInfo.setText("Aucune transaction"); }
        else { paginationInfo.setText("Affichage " + (debut + 1) + "–" + fin + " sur " + total + " transaction(s)"); }
        btnPremierePage.setDisable(pageActuelle == 0);
        btnPagePrev.setDisable(pageActuelle == 0);
        btnPageNext.setDisable(pageActuelle >= totalPages - 1);
        btnDernierePage.setDisable(pageActuelle >= totalPages - 1);
        construireNumeroPages(totalPages);
        countLabel.setText(total + " transaction(s)");
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
            if (i == pageActuelle) { btn.setStyle("-fx-background-color:#6C63FF;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;"); }
            else { btn.setStyle("-fx-background-color:#F7FAFC;-fx-border-color:#E2E8F0;-fx-border-radius:8;-fx-background-radius:8;-fx-font-size:13px;-fx-text-fill:#4A5568;-fx-cursor:hand;"); }
            btn.setOnAction(e -> { pageActuelle = page; afficherPageTransactions(); });
            pageNumbersBox.getChildren().add(btn);
        }
    }

    private void initTable() {
        transactionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colDate.setCellValueFactory(data -> new SimpleStringProperty(DateHelper.formaterCourt(data.getValue().getDateTransaction())));
        colDescription.setCellValueFactory(data -> new SimpleStringProperty((data.getValue().getDescription() != null && !data.getValue().getDescription().isEmpty()) ? data.getValue().getDescription() : "—"));
        colCategorie.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategorieNom() != null ? data.getValue().getCategorieNom() : "—"));
        colUtilisateur.setCellValueFactory(data -> {
            Transaction t = data.getValue();
            String nom = t.getUtilisateurNom();
            if (nom == null && t.getUtilisateurId() > 0) {
                try { Utilisateur u = utilisateurDAO.findById(t.getUtilisateurId()); nom = u != null ? u.getNom() : "—"; t.setUtilisateurNom(nom); }
                catch (Exception e) { nom = "—"; }
            }
            return new SimpleStringProperty(nom != null ? nom : "—");
        });
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType().getLibelle()));
        colType.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else { setText(item); setStyle(item.equals("Entrée") ? "-fx-text-fill:#27AE60; -fx-font-weight:bold;" : "-fx-text-fill:#E74C3C; -fx-font-weight:bold;"); }
            }
        });
        colMontant.setCellValueFactory(data -> { Transaction t = data.getValue(); String signe = t.getType() == TypeTransaction.ENTREE ? "+ " : "- "; return new SimpleStringProperty(signe + NF.format(t.getMontant()) + " FCFA"); });
        colMontant.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else { setText(item); setStyle(item.startsWith("+") ? "-fx-text-fill:#27AE60; -fx-font-weight:bold;" : "-fx-text-fill:#E74C3C; -fx-font-weight:bold;"); }
            }
        });
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✎ Modifier");
            private final Button btnDelete = new Button("✕ Supprimer");
            private final HBox box = new HBox(8, btnEdit, btnDelete);
            {
                btnEdit.getStyleClass().add("btn-action-edit");
                btnDelete.getStyleClass().add("btn-action-delete");
                box.setAlignment(Pos.CENTER);
                btnEdit.setOnAction(e -> { Transaction t = getTableView().getItems().get(getIndex()); ouvrirModal(t); });
                btnDelete.setOnAction(e -> { Transaction t = getTableView().getItems().get(getIndex()); confirmerSuppression(t); });
            }
            @Override protected void updateItem(Void item, boolean empty) { setGraphic(empty ? null : box); }
        });
        transactionsTable.setPlaceholder(new Label("Aucune transaction trouvée."));
    }

    private void initFiltres() {
        filterType.setItems(FXCollections.observableArrayList("Tous", "Entrée", "Sortie"));
        filterType.getSelectionModel().selectFirst();
        chargerListeUtilisateurs();
        searchField.textProperty().addListener((obs, old, val) -> appliquerFiltres());
        filterType.valueProperty().addListener((obs, old, val) -> appliquerFiltres());
        filterUtilisateur.valueProperty().addListener((obs, old, val) -> appliquerFiltres());
        filterDateDebut.valueProperty().addListener((obs, old, val) -> appliquerFiltres());
        filterDateFin.valueProperty().addListener((obs, old, val) -> appliquerFiltres());
    }

    private void chargerListeUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurDAO.findAll();
        filterUtilisateur.getItems().clear();
        filterUtilisateur.getItems().add("Tous les utilisateurs");
        for (Utilisateur u : utilisateurs) { filterUtilisateur.getItems().add(u.getId() + " - " + u.getNom()); }
        filterUtilisateur.getSelectionModel().selectFirst();
    }

    @FXML private void onResetFilters() {
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
        Integer userIdParam = null;
        if (filterUtilisateur.getValue() != null && !filterUtilisateur.getValue().equals("Tous les utilisateurs")) {
            String selected = filterUtilisateur.getValue();
            userIdParam = Integer.parseInt(selected.split(" - ")[0]);
        }
        if (userIdParam != null) {
            transactionsFiltrees = transactionDAO.rechercher(userIdParam, typeParam, null, debut, fin, motCle.isEmpty() ? null : motCle);
            for (Transaction t : transactionsFiltrees) { Utilisateur u = utilisateurDAO.findById(t.getUtilisateurId()); t.setUtilisateurNom(u != null ? u.getNom() : "—"); }
        } else {
            transactionsFiltrees = transactionDAO.rechercherGlobal(typeParam, null, debut, fin, motCle.isEmpty() ? null : motCle);
            for (Transaction t : transactionsFiltrees) { Utilisateur u = utilisateurDAO.findById(t.getUtilisateurId()); t.setUtilisateurNom(u != null ? u.getNom() : "—"); }
        }
        transactionsFiltrees.sort((a, b) -> b.getDateTransaction().compareTo(a.getDateTransaction()));
        pageActuelle = 0;
        afficherPageTransactions();
        chargerKPIGlobal();
        chargerGraphique();
    }

    private void chargerToutesTransactions() {
        transactionsFiltrees = transactionDAO.rechercherGlobal(null, null, null, null, null);
        for (Transaction t : transactionsFiltrees) { Utilisateur u = utilisateurDAO.findById(t.getUtilisateurId()); t.setUtilisateurNom(u != null ? u.getNom() : "—"); }
        transactionsFiltrees.sort((a, b) -> b.getDateTransaction().compareTo(a.getDateTransaction()));
        pageActuelle = 0;
        afficherPageTransactions();
        chargerKPIGlobal();
    }

    private void chargerKPIGlobal() {
        double totalEntrees = 0, totalSorties = 0;
        int totalTrans = transactionsFiltrees.size();
        for (Transaction t : transactionsFiltrees) {
            if (t.getType() == TypeTransaction.ENTREE) totalEntrees += t.getMontant();
            else totalSorties += t.getMontant();
        }
        double soldeGlobal = totalEntrees - totalSorties;
        totalTransactionsLabel.setText(String.valueOf(totalTrans));
        totalEntreesLabel.setText(NF.format(totalEntrees) + " FCFA");
        totalSortiesLabel.setText(NF.format(totalSorties) + " FCFA");
        soldeGlobalLabel.setText(NF.format(soldeGlobal) + " FCFA");
        soldeGlobalLabel.setStyle(soldeGlobal >= 0 ? "-fx-text-fill:#27AE60; -fx-font-weight:bold;" : "-fx-text-fill:#E74C3C; -fx-font-weight:bold;");
    }

    private void chargerGraphique() {
        LocalDate now = LocalDate.now();
        List<Transaction> dernieresTransactions = new ArrayList<>();
        if (filterUtilisateur.getValue() != null && !filterUtilisateur.getValue().equals("Tous les utilisateurs")) {
            String selected = filterUtilisateur.getValue();
            int userId = Integer.parseInt(selected.split(" - ")[0]);
            dernieresTransactions = transactionDAO.findByUtilisateur(userId);
        } else {
            dernieresTransactions = transactionDAO.rechercherGlobal(null, null, null, null, null);
        }
        java.util.Map<String, double[]> monthlyData = new java.util.HashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH);
        for (int i = 5; i >= 0; i--) { LocalDate date = now.minusMonths(i); String key = date.format(monthFormatter); monthlyData.put(key, new double[]{0, 0}); }
        for (Transaction t : dernieresTransactions) {
            String month = t.getDateTransaction().format(monthFormatter);
            if (monthlyData.containsKey(month)) {
                double[] data = monthlyData.get(month);
                if (t.getType() == TypeTransaction.ENTREE) data[0] += t.getMontant();
                else data[1] += t.getMontant();
            }
        }
        chartTransactionsParMois.getData().clear();
        XYChart.Series<String, Number> entreesSeries = new XYChart.Series<>();
        entreesSeries.setName("Entrées");
        XYChart.Series<String, Number> sortiesSeries = new XYChart.Series<>();
        sortiesSeries.setName("Sorties");
        for (java.util.Map.Entry<String, double[]> entry : monthlyData.entrySet()) {
            entreesSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()[0]));
            sortiesSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()[1]));
        }
        chartTransactionsParMois.getData().addAll(entreesSeries, sortiesSeries);
    }

    private void ouvrirModal(Transaction t) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/view/TransactionModal.fxml"));
            Parent root = loader.load();
            TransactionModalController ctrl = loader.getController();
            ctrl.setParentControllerForAdmin(this);
            ctrl.setTransaction(t);
            Stage modal = new Stage();
            modal.setTitle(t == null ? "Nouvelle transaction" : "Modifier");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setScene(new Scene(root));
            modal.setResizable(false);
            modal.centerOnScreen();
            modal.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void confirmerSuppression(Transaction t) {
        String msg = "Supprimer la transaction de " + t.getUtilisateurNom() + " :\n" + (t.getDescription() != null ? t.getDescription() : "—") + " — " + NF.format(t.getMontant()) + " FCFA\n\nIrréversible.";
        if (AlerteHelper.confirmer("Confirmer la suppression", msg)) {
            if (transactionDAO.supprimer(t.getId(), t.getUtilisateurId())) { chargerToutesTransactions(); chargerGraphique(); }
            else { AlerteHelper.erreur("Erreur", "Impossible de supprimer."); }
        }
    }

    public void rafraichirDonnees() { chargerToutesTransactions(); chargerGraphique(); if (sidebarController != null) { sidebarController.chargerBadgeAlertes(); } }
}