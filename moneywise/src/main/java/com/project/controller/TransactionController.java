package com.project.controller;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.project.dao.BudgetDAO;
import com.project.dao.CategorieDAO;
import com.project.dao.TransactionDAO;
import com.project.dao.UtilisateurDAO;
import com.project.enums.TypeTransaction;
import com.project.model.Budget;
import com.project.model.Categorie;
import com.project.model.Transaction;
import com.project.model.Utilisateur;
import com.project.utils.AlerteHelper;
import com.project.utils.DateHelper;
import com.project.utils.ResponsiveHelper;
import com.project.utils.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TransactionController implements Initializable {

    // ── Header ──
    @FXML
    private Label headerDate;
    @FXML
    private Label headerUser;
    @FXML
    private Label soldeLabel;

    // ── KPI ──
    @FXML
    private Label resumeEntrees;
    @FXML
    private Label resumeSorties;
    @FXML
    private Label resumeEpargne;
    @FXML
    private Label resumeNbTrans;

    // ── Budgets ──
    @FXML
    private VBox budgetListContainer;
    @FXML
    private Label noBudgetLabel;
    @FXML
    private Label totalBudgetLabel;
    @FXML
    private Button toggleBudgetBtn;
    @FXML
    private HBox budgetPaginationBox;
    @FXML
    private Button budgetPrevBtn;
    @FXML
    private Button budgetNextBtn;
    @FXML
    private Label budgetPageLabel;

    // ── Catégories ──
    @FXML
    private VBox categorieListContainer;
    @FXML
    private Label noCategorieLabel;
    @FXML
    private HBox categoriePaginationBox;
    @FXML
    private Button categoriePrevBtn;
    @FXML
    private Button categorieNextBtn;
    @FXML
    private Label categoriePageLabel;

    // ── Filtres ──
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterType;
    @FXML
    private ComboBox<Categorie> filterCategorie;
    @FXML
    private DatePicker filterDateDebut;
    @FXML
    private DatePicker filterDateFin;
    @FXML
    private ComboBox<String> filterUtilisateur; // NOUVEAU : pour admin

    // ── Table ──
    @FXML
    private TableView<Transaction> transactionsTable;
    @FXML
    private TableColumn<Transaction, String> colDate;
    @FXML
    private TableColumn<Transaction, String> colDescription;
    @FXML
    private TableColumn<Transaction, String> colCategorie;
    @FXML
    private TableColumn<Transaction, String> colType;
    @FXML
    private TableColumn<Transaction, String> colMontant;
    @FXML
    private TableColumn<Transaction, String> colUtilisateur; // NOUVEAU : colonne utilisateur
    @FXML
    private TableColumn<Transaction, Void> colActions;
    @FXML
    private Label countLabel;

    // ── Pagination table ──
    @FXML
    private Label paginationInfo;
    @FXML
    private Button btnPremierePage;
    @FXML
    private Button btnPagePrev;
    @FXML
    private Button btnPageNext;
    @FXML
    private Button btnDernierePage;
    @FXML
    private HBox pageNumbersBox;
    @FXML
    private ComboBox<Integer> pageSizeCombo;

    // ── Sidebar ──
    @FXML
    private SidebarController sidebarController;

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CategorieDAO categorieDAO = new CategorieDAO();
    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    // ── Données filtrées (source de la pagination) ──
    private List<Transaction> transactionsFiltrees = new ArrayList<>();

    // ── État pagination table ──
    private int pageActuelle = 0;
    private int transParPage = 5;

    // ── Pagination budgets ──
    private List<Budget> tousLesBudgets = new ArrayList<>();
    private int budgetPage = 0;
    private final int BUDGET_PAR_PAGE = 3;
    private boolean budgetVisible = true;

    // ── Pagination catégories ──
    private List<Categorie> toutesLesCategories = new ArrayList<>();
    private int categoriePage = 0;
    private final int CATEGORIE_PAR_PAGE = 5;

    // ── Role ──
    private boolean isAdmin = false;
    private int currentUserId = -1;

    private static final NumberFormat NF = NumberFormat.getNumberInstance(Locale.FRENCH);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isAdmin = SessionManager.isAdmin();
        currentUserId = SessionManager.getUserId();

        chargerHeader();
        chargerSolde();
        chargerResume();
        initTable();
        initPaginationControls();
        rafraichirBudgets();
        rafraichirCategories();
        initFiltres();
        chargerTransactions();

        if (sidebarController != null)
            sidebarController.setActiveItem("transaction");
        ResponsiveHelper.bind(this::onResize);
    }

    private void onResize() {
        if (sidebarController != null)
            sidebarController.setSidebarVisible(
                    ResponsiveHelper.getWidth() >= ResponsiveHelper.BP_SMALL);
    }

    // ─────────────────────────────────────────
    // HEADER
    // ─────────────────────────────────────────
    private void chargerHeader() {
        headerDate.setText(DateHelper.formaterComplet(LocalDate.now()));
        if (currentUserId != -1) {
            String nom = SessionManager.getUtilisateur().getNom().split(" ")[0];
            if (isAdmin) {
                headerUser.setText("Bonjour Admin " + nom + " !");
            } else {
                headerUser.setText("Bonjour " + nom + " !");
            }
        }
    }

    // ─────────────────────────────────────────
    // SOLDE (pour l'admin, solde global ou personnel ?)
    // ─────────────────────────────────────────
    private void chargerSolde() {
        if (soldeLabel == null)
            return;

        double solde;
        if (isAdmin && filterUtilisateur != null && filterUtilisateur.getValue() != null
                && !filterUtilisateur.getValue().equals("Tous les utilisateurs")) {
            // Si admin a filtré un utilisateur, afficher son solde
            String selected = filterUtilisateur.getValue();
            int userId = Integer.parseInt(selected.split(" - ")[0]);
            solde = transactionDAO.getSoldeTotal(userId);
        } else if (isAdmin) {
            // Admin voit solde global
            solde = transactionDAO.getSoldeTotalGlobal();
        } else {
            // Utilisateur normal voit son solde
            solde = transactionDAO.getSoldeTotal(currentUserId);
        }

        soldeLabel.setText(NF.format(solde) + " FCFA");
        soldeLabel.setStyle(solde >= 0
                ? "-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#276749;"
                : "-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#E74C3C;");
    }

    // ─────────────────────────────────────────
    // RÉSUMÉ KPI
    // ─────────────────────────────────────────
    private void chargerResume() {
        if (currentUserId == -1)
            return;

        double entrees, sorties, epargne;
        int nbTrans;

        if (isAdmin && filterUtilisateur != null && filterUtilisateur.getValue() != null
                && !filterUtilisateur.getValue().equals("Tous les utilisateurs")) {
            String selected = filterUtilisateur.getValue();
            int userId = Integer.parseInt(selected.split(" - ")[0]);
            entrees = transactionDAO.getTotalEntreesMois(userId);
            sorties = transactionDAO.getTotalSortiesMois(userId);
            epargne = entrees - sorties;
            nbTrans = transactionDAO.findByUtilisateur(userId).size();
        } else if (isAdmin) {
            entrees = transactionDAO.getTotalEntreesGlobalMois();
            sorties = transactionDAO.getTotalSortiesGlobalMois();
            epargne = entrees - sorties;
            nbTrans = transactionDAO.countAllTransactions();
        } else {
            entrees = transactionDAO.getTotalEntreesMois(currentUserId);
            sorties = transactionDAO.getTotalSortiesMois(currentUserId);
            epargne = entrees - sorties;
            nbTrans = transactionDAO.findByUtilisateur(currentUserId).size();
        }

        resumeEntrees.setText(NF.format(entrees) + " FCFA");
        resumeSorties.setText(NF.format(sorties) + " FCFA");
        resumeEpargne.setText(NF.format(epargne) + " FCFA");
        resumeNbTrans.setText(String.valueOf(nbTrans));

        resumeEpargne.setStyle(epargne >= 0
                ? "-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:#FFFFFF;"
                : "-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:#E74C3C;");
    }

    // ─────────────────────────────────────────
    // PAGINATION — INIT CONTRÔLES
    // ─────────────────────────────────────────
    private void initPaginationControls() {
        pageSizeCombo.setItems(FXCollections.observableArrayList(5, 10, 15, 20, 50));
        pageSizeCombo.getSelectionModel().select(Integer.valueOf(10));
    }

    @FXML
    private void onPageSizeChanged() {
        if (pageSizeCombo.getValue() != null) {
            transParPage = pageSizeCombo.getValue();
            pageActuelle = 0;
            afficherPageTransactions();
        }
    }

    // ─────────────────────────────────────────
    // PAGINATION — NAVIGATION
    // ─────────────────────────────────────────
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
        if (transactionsFiltrees.isEmpty())
            return 1;
        return (int) Math.ceil((double) transactionsFiltrees.size() / transParPage);
    }

    // ─────────────────────────────────────────
    // PAGINATION — AFFICHAGE PAGE
    // ─────────────────────────────────────────
    private void afficherPageTransactions() {
        int total = transactionsFiltrees.size();
        int totalPages = getTotalPages();
        int debut = pageActuelle * transParPage;
        int fin = Math.min(debut + transParPage, total);

        List<Transaction> page = total == 0
                ? new ArrayList<>()
                : transactionsFiltrees.subList(debut, fin);

        transactionsTable.setItems(FXCollections.observableArrayList(page));

        if (total == 0) {
            paginationInfo.setText("Aucune transaction");
        } else {
            paginationInfo.setText(
                    "Affichage " + (debut + 1) + "–" + fin
                            + " sur " + total + " transaction(s)");
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
        if (fin - debut < 4)
            debut = Math.max(0, fin - 4);

        for (int i = debut; i <= fin; i++) {
            final int page = i;
            Button btn = new Button(String.valueOf(i + 1));
            btn.setPrefWidth(36);
            btn.setPrefHeight(34);

            if (i == pageActuelle) {
                btn.setStyle(
                        "-fx-background-color:#6C63FF;" +
                                "-fx-text-fill:white;" +
                                "-fx-font-size:13px;" +
                                "-fx-font-weight:bold;" +
                                "-fx-background-radius:8;" +
                                "-fx-cursor:hand;");
            } else {
                btn.setStyle(
                        "-fx-background-color:#F7FAFC;" +
                                "-fx-border-color:#E2E8F0;" +
                                "-fx-border-radius:8;" +
                                "-fx-background-radius:8;" +
                                "-fx-font-size:13px;" +
                                "-fx-text-fill:#4A5568;" +
                                "-fx-cursor:hand;");
            }

            btn.setOnAction(e -> {
                pageActuelle = page;
                afficherPageTransactions();
            });
            pageNumbersBox.getChildren().add(btn);
        }
    }

    // ─────────────────────────────────────────
    // BUDGETS
    // ─────────────────────────────────────────
    public void rafraichirBudgets() {
        if (currentUserId == -1)
            return;
        tousLesBudgets = budgetDAO.findByUtilisateur(currentUserId);
        budgetPage = 0;
        afficherPageBudgets();
        mettreAJourTotalBudget();
    }

    // ─────────────────────────────────────────
    // RAFRAÎCHIR BADGE ALERTES SIDEBAR
    // ─────────────────────────────────────────
    public void rafraichirBadgeAlertes() {
        if (sidebarController != null) {
            sidebarController.chargerBadgeAlertes();
        }
    }

    private void afficherPageBudgets() {
        budgetListContainer.getChildren().clear();
        if (tousLesBudgets.isEmpty()) {
            noBudgetLabel.setVisible(true);
            noBudgetLabel.setManaged(true);
            budgetPaginationBox.setVisible(false);
            budgetPaginationBox.setManaged(false);
            return;
        }
        noBudgetLabel.setVisible(false);
        noBudgetLabel.setManaged(false);

        int debut = budgetPage * BUDGET_PAR_PAGE;
        int fin = Math.min(debut + BUDGET_PAR_PAGE, tousLesBudgets.size());
        int totalPages = (int) Math.ceil((double) tousLesBudgets.size() / BUDGET_PAR_PAGE);

        for (int i = debut; i < fin; i++) {
            Budget b = tousLesBudgets.get(i);
            double pct = budgetDAO.getConsommation(b.getId());
            budgetListContainer.getChildren().add(creerBudgetItem(b, pct));
        }

        if (totalPages > 1) {
            budgetPaginationBox.setVisible(true);
            budgetPaginationBox.setManaged(true);
            budgetPageLabel.setText((budgetPage + 1) + " / " + totalPages);
            budgetPrevBtn.setDisable(budgetPage == 0);
            budgetNextBtn.setDisable(budgetPage >= totalPages - 1);
        } else {
            budgetPaginationBox.setVisible(false);
            budgetPaginationBox.setManaged(false);
        }
    }

    @FXML
    private void budgetPagePrev() {
        if (budgetPage > 0) {
            budgetPage--;
            afficherPageBudgets();
        }
    }

    @FXML
    private void budgetPageNext() {
        int tp = (int) Math.ceil((double) tousLesBudgets.size() / BUDGET_PAR_PAGE);
        if (budgetPage < tp - 1) {
            budgetPage++;
            afficherPageBudgets();
        }
    }

    private void mettreAJourTotalBudget() {
        if (totalBudgetLabel == null)
            return;
        double total = tousLesBudgets.stream().mapToDouble(Budget::getMontantMax).sum();
        if (budgetVisible) {
            totalBudgetLabel.setText(NF.format(total) + " FCFA");
            if (toggleBudgetBtn != null)
                toggleBudgetBtn.setText("👁");
        } else {
            totalBudgetLabel.setText("••••••");
            if (toggleBudgetBtn != null)
                toggleBudgetBtn.setText("🙈");
        }
    }

    @FXML
    private void toggleBudgetVisible() {
        budgetVisible = !budgetVisible;
        mettreAJourTotalBudget();
    }

    // ─────────────────────────────────────────
    // UTILITAIRE : Formatage du mois
    // ─────────────────────────────────────────
    private String getMoisLabel(int mois, int annee) {
        String[] moisNom = {"", "Jan", "Fév", "Mar", "Avr", "Mai", "Juin",
                           "Juil", "Août", "Sep", "Oct", "Nov", "Déc"};
        int moisCourant = java.time.LocalDate.now().getMonthValue();
        int anneeCourante = java.time.LocalDate.now().getYear();

        if (mois == moisCourant && annee == anneeCourante) {
            return "ce mois-ci";
        } else if (mois >= 1 && mois <= 12) {
            return moisNom[mois] + " " + annee;
        }
        return mois + "/" + annee;
    }

    private VBox creerBudgetItem(Budget budget, double pct) {
        VBox item = new VBox(6);
        item.getStyleClass().add("budget-progress-item");

        // Nom de la catégorie + mois
        String catNom = budget.getCategorieNom() != null ? budget.getCategorieNom() : "—";
        String moisStr = getMoisLabel(budget.getMois(), budget.getAnnee());
        Label nom = new Label(catNom + " (" + moisStr + ")");
        nom.getStyleClass().add("budget-progress-label");

        // Montant consommé / max
        double depense = pct * budget.getMontantMax() / 100.0;
        Label montant = new Label(
                NF.format(depense) + " / " + NF.format(budget.getMontantMax()) + " FCFA");
        montant.getStyleClass().add("budget-progress-montant");

        // ── Barre de progression ──────────────────────────────
        // On remplace ProgressBar par un HBox avec une Region colorée
        // pour contourner les limites CSS de JavaFX sur les sous-éléments.
        HBox track = new HBox();
        track.setMaxWidth(Double.MAX_VALUE);
        track.setPrefHeight(8);
        track.setStyle(
                "-fx-background-color:#F1F5F9;" +
                        "-fx-background-radius:4;");

        double ratio = Math.min(pct / 100.0, 1.0);
        Region fill = new Region();
        fill.setPrefHeight(8);

        String couleur;
        if (pct >= 100)
            couleur = "#EF4444"; // danger
        else if (pct >= 80)
            couleur = "#F59E0B"; // warning
        else
            couleur = "#10B981"; // ok

        fill.setStyle(
                "-fx-background-color:" + couleur + ";" +
                        "-fx-background-radius:4;");

        // Lier la largeur du fill à celle du track
        fill.prefWidthProperty().bind(track.widthProperty().multiply(ratio));
        track.getChildren().add(fill);
        // ─────────────────────────────────────────────────────

        // Footer : % + boutons
        HBox footer = new HBox(6);
        footer.setAlignment(Pos.CENTER_LEFT);

        Label pctLabel = new Label(String.format("%.0f%%", pct));
        pctLabel.getStyleClass().add("budget-progress-pct");
        pctLabel.setStyle(
                "-fx-text-fill:" + couleur + "; -fx-font-weight:bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnEdit = new Button("✎ Modifier");
        btnEdit.getStyleClass().add("btn-action-edit");
        btnEdit.setOnAction(e -> openModalModifierBudget(budget));

        Button btnDel = new Button("✕");
        btnDel.getStyleClass().add("btn-action-delete");
        btnDel.setOnAction(e -> supprimerBudget(budget));

        footer.getChildren().addAll(pctLabel, spacer, btnEdit, btnDel);
        item.getChildren().addAll(nom, montant, track, footer);
        return item;
    }

    // ─────────────────────────────────────────
    // CATÉGORIES
    // ─────────────────────────────────────────
    public void rafraichirCategories() {
        if (currentUserId == -1)
            return;

        List<Categorie> cats = categorieDAO.findByUtilisateur(currentUserId);
        toutesLesCategories = cats.stream().filter(c -> !c.isEstSysteme()).toList();
        categoriePage = 0;
        afficherPageCategories();
        initFiltres();
    }

    private void afficherPageCategories() {
        categorieListContainer.getChildren().clear();
        if (toutesLesCategories.isEmpty()) {
            noCategorieLabel.setVisible(true);
            noCategorieLabel.setManaged(true);
            categoriePaginationBox.setVisible(false);
            categoriePaginationBox.setManaged(false);
            return;
        }
        noCategorieLabel.setVisible(false);
        noCategorieLabel.setManaged(false);

        int debut = categoriePage * CATEGORIE_PAR_PAGE;
        int fin = Math.min(debut + CATEGORIE_PAR_PAGE, toutesLesCategories.size());
        int totalPages = (int) Math.ceil((double) toutesLesCategories.size() / CATEGORIE_PAR_PAGE);

        for (int i = debut; i < fin; i++)
            categorieListContainer.getChildren().add(
                    creerCategorieItem(toutesLesCategories.get(i)));

        if (totalPages > 1) {
            categoriePaginationBox.setVisible(true);
            categoriePaginationBox.setManaged(true);
            categoriePageLabel.setText((categoriePage + 1) + " / " + totalPages);
            categoriePrevBtn.setDisable(categoriePage == 0);
            categorieNextBtn.setDisable(categoriePage >= totalPages - 1);
        } else {
            categoriePaginationBox.setVisible(false);
            categoriePaginationBox.setManaged(false);
        }
    }

    @FXML
    private void categoriePagePrev() {
        if (categoriePage > 0) {
            categoriePage--;
            afficherPageCategories();
        }
    }

    @FXML
    private void categoriePageNext() {
        int tp = (int) Math.ceil((double) toutesLesCategories.size() / CATEGORIE_PAR_PAGE);
        if (categoriePage < tp - 1) {
            categoriePage++;
            afficherPageCategories();
        }
    }

    private HBox creerCategorieItem(Categorie cat) {
        HBox item = new HBox(10);
        item.getStyleClass().add("categorie-item");
        item.setAlignment(Pos.CENTER_LEFT);

        Label nom = new Label(
                (cat.getIcone() != null ? cat.getIcone() + "  " : "") + cat.getNom());
        nom.getStyleClass().add("categorie-nom");
        HBox.setHgrow(nom, Priority.ALWAYS);

        Button btnEdit = new Button("✎ Modifier");
        btnEdit.getStyleClass().add("btn-action-icon");
        btnEdit.getStyleClass().add("btn-action-edit");
        btnEdit.setStyle("-fx-text-fill:#6C63FF;");
        btnEdit.setOnAction(e -> openModalModifierCategorie(cat));

        Button btnDel = new Button("✕");
        btnDel.getStyleClass().add("btn-action-icon");
        btnDel.setStyle("-fx-text-fill:#E74C3C;");
        btnDel.setOnAction(e -> supprimerCategorie(cat));

        item.getChildren().addAll(nom, btnEdit, btnDel);
        return item;
    }

    // ─────────────────────────────────────────
    // FILTRES
    // ─────────────────────────────────────────
    private void initFiltres() {
        filterType.setItems(FXCollections.observableArrayList("Tous", "Entrée", "Sortie"));
        filterType.getSelectionModel().selectFirst();

        // FILTRE UTILISATEUR (visible seulement pour admin)
        if (isAdmin && filterUtilisateur != null) {
            filterUtilisateur.setVisible(true);
            filterUtilisateur.setManaged(true);
            chargerListeUtilisateurs();
            filterUtilisateur.getSelectionModel().selectFirst();
            filterUtilisateur.setOnAction(e -> onFilterChanged());
        }

        // Charger les catégories
        List<Categorie> cats = categorieDAO.findByUtilisateur(currentUserId);
        ObservableList<Categorie> items = FXCollections.observableArrayList();
        items.add(null);
        items.addAll(cats);
        filterCategorie.setItems(items);

        filterCategorie.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : (item == null ? "Toutes" : item.getNom()));
            }
        });
        filterCategorie.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "Catégorie" : (item == null ? "Toutes" : item.getNom()));
            }
        });
        filterCategorie.getSelectionModel().selectFirst();
    }

    private void chargerListeUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurDAO.findAll();
        filterUtilisateur.getItems().clear();
        filterUtilisateur.getItems().add("Tous les utilisateurs");
        for (Utilisateur u : utilisateurs) {
            filterUtilisateur.getItems().add(u.getId() + " - " + u.getNom());
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
        filterCategorie.getSelectionModel().selectFirst();
        filterDateDebut.setValue(null);
        filterDateFin.setValue(null);
        if (isAdmin && filterUtilisateur != null) {
            filterUtilisateur.getSelectionModel().selectFirst();
        }
        appliquerFiltres();
    }

    private void appliquerFiltres() {
        if (currentUserId == -1)
            return;

        String motCle = searchField.getText().trim();
        String typeStr = filterType.getValue();
        Categorie cat = filterCategorie.getValue();
        LocalDate debut = filterDateDebut.getValue();
        LocalDate fin = filterDateFin.getValue();

        String typeParam = (typeStr == null || typeStr.equals("Tous"))
                ? null
                : (typeStr.equals("Entrée") ? "ENTREE" : "SORTIE");
        Integer catId = (cat != null) ? cat.getId() : null;

        // Déterminer l'utilisateur à filtrer
        Integer userIdParam = null;
        if (isAdmin && filterUtilisateur != null && filterUtilisateur.getValue() != null
                && !filterUtilisateur.getValue().equals("Tous les utilisateurs")) {
            String selected = filterUtilisateur.getValue();
            userIdParam = Integer.parseInt(selected.split(" - ")[0]);
        } else if (!isAdmin) {
            userIdParam = currentUserId;
        }

        if (isAdmin && userIdParam == null) {
            // Admin voit toutes les transactions
            transactionsFiltrees = transactionDAO.rechercherGlobal(
                    typeParam, catId, debut, fin,
                    motCle.isEmpty() ? null : motCle);
            // Ajouter le nom de l'utilisateur pour l'affichage
            for (Transaction t : transactionsFiltrees) {
                t.setUtilisateurNom(utilisateurDAO.findById(t.getUtilisateurId()).getNom());
            }
        } else {
            transactionsFiltrees = transactionDAO.rechercher(
                    userIdParam, typeParam, catId, debut, fin,
                    motCle.isEmpty() ? null : motCle);
        }

        pageActuelle = 0;
        afficherPageTransactions();
        chargerResume();
        chargerSolde();
    }

    // ─────────────────────────────────────────
    // INIT TABLE
    // ─────────────────────────────────────────
    private void initTable() {
        transactionsTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colDate.setCellValueFactory(data -> new SimpleStringProperty(
                DateHelper.formaterCourt(data.getValue().getDateTransaction())));

        colDescription.setCellValueFactory(data -> {
            String d = data.getValue().getDescription();
            return new SimpleStringProperty((d != null && !d.isEmpty()) ? d : "—");
        });

        colCategorie.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCategorieNom() != null
                        ? data.getValue().getCategorieNom()
                        : "—"));

        // Colonne utilisateur (visible seulement pour admin)
        if (isAdmin && colUtilisateur != null) {
            colUtilisateur.setVisible(true);
            // colUtilisateur.setManaged(true);
            colUtilisateur.setCellValueFactory(data -> {
                Transaction t = data.getValue();
                String nom = t.getUtilisateurNom();
                if (nom == null && t.getUtilisateurId() > 0) {
                    // Si le nom n'est pas deja charge, on le charge
                    try {
                        UtilisateurDAO dao = new UtilisateurDAO();
                        Utilisateur u = dao.findById(t.getUtilisateurId());
                        nom = u != null ? u.getNom() : "—";
                        t.setUtilisateurNom(nom);
                    } catch (Exception e) {
                        nom = "—";
                    }
                }
                return new SimpleStringProperty(nom != null ? nom : "—");
            });
        }

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
                    setStyle(item.equals("Entrée")
                            ? "-fx-text-fill:#27AE60; -fx-font-weight:bold;"
                            : "-fx-text-fill:#E74C3C; -fx-font-weight:bold;");
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
                    setStyle(item.startsWith("+")
                            ? "-fx-text-fill:#27AE60; -fx-font-weight:bold;"
                            : "-fx-text-fill:#E74C3C; -fx-font-weight:bold;");
                }
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
                btnEdit.setOnAction(e -> {
                    Transaction t = getTableView().getItems().get(getIndex());
                    ouvrirModal(t);
                });
                btnDelete.setOnAction(e -> {
                    Transaction t = getTableView().getItems().get(getIndex());
                    confirmerSuppression(t);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        transactionsTable.setPlaceholder(new Label("Aucune transaction trouvée."));
    }

    // ─────────────────────────────────────────
    // CHARGER TOUTES LES TRANSACTIONS
    // ─────────────────────────────────────────
    public void chargerTransactions() {
        if (currentUserId == -1)
            return;

        if (isAdmin) {
            transactionsFiltrees = transactionDAO.rechercherGlobal(null, null, null, null, null);
            for (Transaction t : transactionsFiltrees) {
                t.setUtilisateurNom(utilisateurDAO.findById(t.getUtilisateurId()).getNom());
            }
        } else {
            transactionsFiltrees = transactionDAO.findByUtilisateur(currentUserId);
        }

        pageActuelle = 0;
        afficherPageTransactions();
        chargerSolde();
        chargerResume();
    }

    // ─────────────────────────────────────────
    // MODALS TRANSACTIONS
    // ─────────────────────────────────────────
    @FXML
    private void openModalAjouter() {
        ouvrirModal(null);
    }

    private void ouvrirModal(Transaction t) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/project/view/TransactionModal.fxml"));
            Parent root = loader.load();
            TransactionModalController ctrl = loader.getController();
            ctrl.setParentController(this);
            ctrl.setTransaction(t);
            Stage modal = new Stage();
            modal.setTitle(t == null ? "Nouvelle transaction" : "Modifier");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setScene(new Scene(root));
            modal.setResizable(false);
            modal.centerOnScreen();
            modal.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmerSuppression(Transaction t) {
        String msg = "Supprimer : "
                + (t.getDescription() != null ? t.getDescription() : "—")
                + " — " + NF.format(t.getMontant()) + " FCFA\n\nIrréversible.";
        if (AlerteHelper.confirmer("Confirmer la suppression", msg)) {
            if (transactionDAO.supprimer(t.getId(), t.getUtilisateurId()))
                chargerTransactions();
            else
                AlerteHelper.erreur("Erreur", "Impossible de supprimer.");
        }
    }

    // ─────────────────────────────────────────
    // MODALS BUDGETS
    // ─────────────────────────────────────────
    @FXML
    private void openModalAjouterBudget() {
        ouvrirModalBudget(null);
    }

    private void openModalModifierBudget(Budget b) {
        ouvrirModalBudget(b);
    }

    private void ouvrirModalBudget(Budget b) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/project/view/BudgetModal.fxml"));
            Parent root = loader.load();
            BudgetModalController ctrl = loader.getController();
            ctrl.setParentController(this);
            ctrl.setBudget(b);
            Stage modal = new Stage();
            modal.setTitle(b == null ? "Nouveau budget" : "Modifier le budget");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setScene(new Scene(root));
            modal.setResizable(false);
            modal.centerOnScreen();
            modal.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void supprimerBudget(Budget b) {
        if (AlerteHelper.confirmer("Supprimer",
                "Supprimer le budget \"" + b.getCategorieNom() + "\" ?")) {
            if (budgetDAO.supprimer(b.getId(), currentUserId))
                rafraichirBudgets();
            else
                AlerteHelper.erreur("Erreur", "Impossible de supprimer.");
        }
    }

    // ─────────────────────────────────────────
    // MODALS CATÉGORIES
    // ─────────────────────────────────────────
    @FXML
    private void openModalAjouterCategorie() {
        ouvrirModalCategorie(null);
    }

    private void openModalModifierCategorie(Categorie c) {
        ouvrirModalCategorie(c);
    }

    private void ouvrirModalCategorie(Categorie c) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/project/view/CategorieModal.fxml"));
            Parent root = loader.load();
            CategorieModalController ctrl = loader.getController();
            ctrl.setParentController(this);
            ctrl.setCategorie(c);
            Stage modal = new Stage();
            modal.setTitle(c == null ? "Nouvelle catégorie" : "Modifier");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setScene(new Scene(root));
            modal.setResizable(false);
            modal.centerOnScreen();
            modal.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void supprimerCategorie(Categorie c) {
        if (AlerteHelper.confirmer("Supprimer",
                "Supprimer \"" + c.getNom() + "\" ?\n\n⚠ Impossible si liée à des transactions.")) {
            if (categorieDAO.supprimer(c.getId()))
                rafraichirCategories();
            else
                AlerteHelper.erreur("Suppression impossible",
                        "Cette catégorie est utilisée par des transactions.");
        }
    }
}