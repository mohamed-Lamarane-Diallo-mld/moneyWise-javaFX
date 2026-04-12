package com.project.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

import com.project.dao.DatabaseConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class AdminCategoriesController implements Initializable {

    @FXML private Label totalCategories;
    @FXML private Label totalSysteme;
    @FXML private Label totalPersonnalisees;
    @FXML private Label categoriesAvecBudget;
    @FXML private Label headerDate;
    @FXML private Label headerTotalCats;
    @FXML private Label countLabel;
    @FXML private Label paginationInfo;

    @FXML private SidebarController sidebarController;

    @FXML private TextField        searchField;
    @FXML private ComboBox<String> filterType;

    @FXML private TableView<CategorieRow>               categoriesTable;
    @FXML private TableColumn<CategorieRow, Integer>    colId;
    @FXML private TableColumn<CategorieRow, String>     colNom;
    @FXML private TableColumn<CategorieRow, String>     colType;
    @FXML private TableColumn<CategorieRow, String>     colUtilisateur;
    @FXML private TableColumn<CategorieRow, Void>       colActions;

    @FXML private Button            btnPremierePage;
    @FXML private Button            btnPagePrev;
    @FXML private Button            btnPageNext;
    @FXML private Button            btnDernierePage;
    @FXML private HBox              pageNumbersBox;
    @FXML private ComboBox<Integer> pageSizeCombo;

    private ObservableList<CategorieRow> categoriesList = FXCollections.observableArrayList();
    private int currentPage = 1;
    private int pageSize    = 10;
    private int totalPages  = 1;
    private int totalItems  = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        setupFilters();
        setupPagination();
        chargerStatistiques();
        chargerCategories();
        headerDate.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy")));
        if (sidebarController != null)
            sidebarController.setActiveItem("adminCategories");
    }

    // ── Colonnes ─────────────────────────────────────────────────────────────
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        // TYPE avec badge coloré
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Système".equals(item)
                        ? "-fx-text-fill:#6C63FF; -fx-font-weight:bold;"
                        : "-fx-text-fill:#10B981; -fx-font-weight:bold;");
            }
        });

        colUtilisateur.setCellValueFactory(new PropertyValueFactory<>("utilisateur"));

        // ACTIONS
        colActions.setCellFactory(new Callback<>() {
            @Override
            public TableCell<CategorieRow, Void> call(TableColumn<CategorieRow, Void> param) {
                return new TableCell<>() {
                    private final Button btnModifier  = new Button("✎ Modifier");
                    private final Button btnSupprimer = new Button("✕ Suppr.");
                    private final HBox   box = new HBox(8, btnModifier, btnSupprimer);
                    {
                        box.setAlignment(Pos.CENTER);
                        btnModifier.getStyleClass().add("btn-action-edit");
                        btnSupprimer.getStyleClass().add("btn-action-delete");
                        btnModifier.setOnAction(e ->
                                modifierCategorie(getTableView().getItems().get(getIndex())));
                        btnSupprimer.setOnAction(e ->
                                supprimerCategorie(getTableView().getItems().get(getIndex())));
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : box);
                    }
                };
            }
        });
    }

    // ── Filtres ──────────────────────────────────────────────────────────────
    private void setupFilters() {
        filterType.setItems(FXCollections.observableArrayList("Toutes", "Système", "Personnalisée"));
        filterType.getSelectionModel().selectFirst();
    }

    // ── Pagination ───────────────────────────────────────────────────────────
    private void setupPagination() {
        pageSizeCombo.setItems(FXCollections.observableArrayList(5, 10, 15, 20, 25, 50));
        pageSizeCombo.setValue(10);
    }

    // ── Stats ─────────────────────────────────────────────────────────────────
    private void chargerStatistiques() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            chargerKpi(conn, "SELECT COUNT(*) FROM categorie",                                  totalCategories);
            chargerKpi(conn, "SELECT COUNT(*) FROM categorie WHERE est_systeme = 1",            totalSysteme);
            chargerKpi(conn, "SELECT COUNT(*) FROM categorie WHERE est_systeme = 0",            totalPersonnalisees);
            chargerKpi(conn, "SELECT COUNT(DISTINCT categorie_id) FROM budget WHERE est_actif=1", categoriesAvecBudget);

            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM categorie");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && headerTotalCats != null)
                    headerTotalCats.setText(rs.getInt(1) + " au total");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void chargerKpi(Connection conn, String sql, Label label) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) label.setText(String.valueOf(rs.getInt(1)));
        }
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private void chargerCategories() {
        categoriesList.clear();
        String search = searchField.getText().trim();
        String type   = filterType.getValue();

        StringBuilder sql = new StringBuilder(
                "SELECT c.id, c.nom, c.est_systeme, u.nom AS utilisateur_nom " +
                "FROM categorie c LEFT JOIN utilisateur u ON c.utilisateur_id = u.id WHERE 1=1");
        if (!search.isEmpty())    sql.append(" AND c.nom LIKE ?");
        if (type != null && !type.equals("Toutes"))
            sql.append(" AND c.est_systeme = ").append("Système".equals(type) ? "1" : "0");
        sql.append(" ORDER BY c.est_systeme DESC, c.nom ASC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            if (!search.isEmpty()) ps.setString(p++, "%" + search + "%");
            ps.setInt(p++, pageSize);
            ps.setInt(p,   (currentPage - 1) * pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CategorieRow c = new CategorieRow();
                c.setId(rs.getInt("id"));
                c.setNom(rs.getString("nom"));
                c.setEstSysteme(rs.getBoolean("est_systeme"));
                String u = rs.getString("utilisateur_nom");
                c.setUtilisateur(u != null ? u : "Système");
                categoriesList.add(c);
            }
            categoriesTable.setItems(categoriesList);
            compterTotalItems();
            updatePaginationUI();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void compterTotalItems() {
        String search = searchField.getText().trim();
        String type   = filterType.getValue();

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM categorie WHERE 1=1");
        if (!search.isEmpty()) sql.append(" AND nom LIKE ?");
        if (type != null && !type.equals("Toutes"))
            sql.append(" AND est_systeme = ").append("Système".equals(type) ? "1" : "0");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            if (!search.isEmpty()) ps.setString(p, "%" + search + "%");

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalItems = rs.getInt(1);
                totalPages = Math.max(1, (int) Math.ceil((double) totalItems / pageSize));
                countLabel.setText(totalItems + " catégorie(s)");
                int debut = (currentPage - 1) * pageSize + 1;
                int fin   = Math.min(currentPage * pageSize, totalItems);
                paginationInfo.setText(totalItems == 0 ? "Aucun résultat"
                        : "Affichage " + debut + "–" + fin + " sur " + totalItems);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void updatePaginationUI() {
        btnPremierePage.setDisable(currentPage == 1);
        btnPagePrev.setDisable(currentPage == 1);
        btnPageNext.setDisable(currentPage == totalPages);
        btnDernierePage.setDisable(currentPage == totalPages);

        pageNumbersBox.getChildren().clear();
        int start = Math.max(1, currentPage - 2);
        int end   = Math.min(totalPages, currentPage + 2);
        for (int i = start; i <= end; i++) {
            Button btn = new Button(String.valueOf(i));
            btn.setPrefWidth(34); btn.setPrefHeight(32);
            final int pg = i;
            if (i == currentPage) {
                btn.setStyle("-fx-background-color:#6C63FF; -fx-text-fill:white;" +
                             "-fx-font-weight:bold; -fx-background-radius:8; -fx-cursor:hand;");
            } else {
                btn.setStyle("-fx-background-color:#F7FAFC; -fx-border-color:#E2E8F0;" +
                             "-fx-border-radius:8; -fx-background-radius:8;" +
                             "-fx-font-size:13px; -fx-text-fill:#4A5568; -fx-cursor:hand;");
            }
            btn.setOnAction(e -> goToPage(pg));
            pageNumbersBox.getChildren().add(btn);
        }
    }

    private void goToPage(int page) {
        if (page < 1 || page > totalPages) return;
        currentPage = page;
        chargerCategories();
    }

    // ── Actions ──────────────────────────────────────────────────────────────
    @FXML
    private void ajouterCategorie() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Nouvelle catégorie");
        dlg.setHeaderText("Ajouter une nouvelle catégorie personnalisée");
        dlg.setContentText("Nom de la catégorie :");
        dlg.showAndWait().ifPresent(nom -> {
            if (nom.isBlank()) return;
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO categorie (nom, est_systeme) VALUES (?, 0)")) {
                ps.setString(1, nom.trim());
                ps.executeUpdate();
                info("Catégorie ajoutée avec succès !");
                chargerStatistiques(); chargerCategories();
            } catch (SQLException e) {
                e.printStackTrace(); err("Impossible d'ajouter la catégorie.");
            }
        });
    }

    private void modifierCategorie(CategorieRow cat) {
        if (cat.isEstSysteme()) {
            warn("Impossible de modifier une catégorie système."); return;
        }
        TextInputDialog dlg = new TextInputDialog(cat.getNom());
        dlg.setTitle("Modifier catégorie");
        dlg.setHeaderText("Modifier le nom de : " + cat.getNom());
        dlg.setContentText("Nouveau nom :");
        dlg.showAndWait().ifPresent(nom -> {
            if (nom.isBlank()) return;
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE categorie SET nom=? WHERE id=?")) {
                ps.setString(1, nom.trim()); ps.setInt(2, cat.getId());
                ps.executeUpdate();
                info("Catégorie modifiée avec succès !");
                chargerCategories();
            } catch (SQLException e) {
                e.printStackTrace(); err("Impossible de modifier la catégorie.");
            }
        });
    }

    private void supprimerCategorie(CategorieRow cat) {
        if (cat.isEstSysteme()) {
            warn("Impossible de supprimer une catégorie système."); return;
        }
        if (!confirmer("Supprimer " + cat.getNom() + " ?",
                "Cette action est irréversible.\n⚠ Impossible si liée à des transactions.")) return;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM categorie WHERE id=?")) {
            ps.setInt(1, cat.getId());
            ps.executeUpdate();
            info("Catégorie supprimée avec succès !");
            chargerStatistiques(); chargerCategories();
        } catch (SQLException e) {
            e.printStackTrace(); err("Impossible de supprimer — catégorie utilisée par des transactions.");
        }
    }

    // ── Utilitaires ──────────────────────────────────────────────────────────
    private boolean confirmer(String titre, String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titre); a.setHeaderText(null); a.setContentText(msg);
        return a.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }
    private void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Succès"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
    private void warn(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Attention"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
    private void err(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erreur"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    // ── FXML handlers ─────────────────────────────────────────────────────────
    @FXML private void onSearchChanged()  { currentPage = 1; chargerCategories(); }
    @FXML private void onFilterChanged()  { currentPage = 1; chargerCategories(); }
    @FXML private void resetFiltres()     {
        searchField.clear();
        filterType.getSelectionModel().selectFirst();
        currentPage = 1; chargerCategories();
    }
    @FXML private void onPageSizeChanged()  { pageSize = pageSizeCombo.getValue(); currentPage = 1; chargerCategories(); }
    @FXML private void goToPremierePage()   { goToPage(1); }
    @FXML private void goToPagePrecedente() { goToPage(currentPage - 1); }
    @FXML private void goToPageSuivante()   { goToPage(currentPage + 1); }
    @FXML private void goToDernierePage()   { goToPage(totalPages); }

    // ── Inner class ──────────────────────────────────────────────────────────
    public static class CategorieRow {
        private int id; private String nom, utilisateur; private boolean estSysteme;

        public int     getId()                { return id; }
        public void    setId(int id)          { this.id = id; }
        public String  getNom()               { return nom; }
        public void    setNom(String v)       { this.nom = v; }
        public boolean isEstSysteme()         { return estSysteme; }
        public void    setEstSysteme(boolean v){ this.estSysteme = v; }
        public String  getUtilisateur()       { return utilisateur; }
        public void    setUtilisateur(String v){ this.utilisateur = v; }
        public String  getType()              { return estSysteme ? "Système" : "Personnalisée"; }
    }
}