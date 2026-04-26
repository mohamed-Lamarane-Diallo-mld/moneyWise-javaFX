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
import com.project.dao.UtilisateurDAO;
import com.project.utils.SessionManager;

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

public class AdminUtilisateursController implements Initializable {

    @FXML private Label totalUtilisateurs;
    @FXML private Label totalAdmins;
    @FXML private Label totalInactifs;
    @FXML private Label totalNouveauxMois;
    @FXML private Label headerDate;
    @FXML private Label headerTotalUsers;
    @FXML private Label countLabel;
    @FXML private Label paginationInfo;

    @FXML private SidebarController sidebarController;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatut;
    @FXML private ComboBox<String> filterRole;

    @FXML private TableView<UtilisateurRow> utilisateursTable;
    @FXML private TableColumn<UtilisateurRow, Integer> colId;
    @FXML private TableColumn<UtilisateurRow, String>  colNom;
    @FXML private TableColumn<UtilisateurRow, String>  colEmail;
    @FXML private TableColumn<UtilisateurRow, String>  colDateInscription;
    @FXML private TableColumn<UtilisateurRow, String>  colStatut;
    @FXML private TableColumn<UtilisateurRow, String>  colRole;
    @FXML private TableColumn<UtilisateurRow, Void>    colActions;

    @FXML private Button   btnPremierePage;
    @FXML private Button   btnPagePrev;
    @FXML private Button   btnPageNext;
    @FXML private Button   btnDernierePage;
    @FXML private HBox     pageNumbersBox;
    @FXML private ComboBox<Integer> pageSizeCombo;

    private ObservableList<UtilisateurRow> utilisateursList = FXCollections.observableArrayList();
    private int currentPage = 1;
    private int pageSize    = 10;
    private int totalPages  = 1;
    private int totalItems  = 0;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        setupFilters();
        setupPagination();
        chargerStatistiques();
        chargerUtilisateurs();
        headerDate.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy")));
        if (sidebarController != null)
            sidebarController.setActiveItem("adminUtilisateurs");
    }

    // ── Colonnes ────────────────────────────────────────────────────────────
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDateInscription.setCellValueFactory(new PropertyValueFactory<>("dateInscription"));

        // Colonne STATUT avec badge coloré
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Actif".equals(item)
                        ? "-fx-text-fill:#10B981; -fx-font-weight:bold;"
                        : "-fx-text-fill:#EF4444; -fx-font-weight:bold;");
            }
        });

        // Colonne RÔLE avec badge coloré
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Admin".equals(item)
                        ? "-fx-text-fill:#6C63FF; -fx-font-weight:bold;"
                        : "-fx-text-fill:#64748B;");
            }
        });

        // Colonne ACTIONS
        colActions.setCellFactory(new Callback<>() {
            @Override
            public TableCell<UtilisateurRow, Void> call(TableColumn<UtilisateurRow, Void> param) {
                return new TableCell<>() {
                    private final Button btnModifier      = new Button("✎ Modifier");
                    private final Button btnToggleStatut  = new Button("⏻ Statut");
                    private final Button btnToggleAdmin   = new Button("🛡 Rôle");
                    private final Button btnSupprimer     = new Button("✕ Suppr.");
                    private final HBox   box = new HBox(10,
                            btnModifier, btnToggleStatut, btnToggleAdmin, btnSupprimer);
                    {
                        box.setAlignment(Pos.CENTER);
                        btnModifier.getStyleClass().add("btn-action-edit");
                        btnToggleStatut.setStyle(
                                "-fx-background-color:#FEF3C7; -fx-text-fill:#D97706;" +
                                "-fx-font-size:11px; -fx-font-weight:bold;" +
                                "-fx-padding:5 9; -fx-background-radius:7; -fx-cursor:hand;");
                        btnToggleAdmin.setStyle(
                                "-fx-background-color:#EAE8FF; -fx-text-fill:#6C63FF;" +
                                "-fx-font-size:11px; -fx-font-weight:bold;" +
                                "-fx-padding:5 9; -fx-background-radius:7; -fx-cursor:hand;");
                        btnSupprimer.getStyleClass().add("btn-action-delete");

                        btnModifier.setOnAction(e ->
                                modifierUtilisateur(getTableView().getItems().get(getIndex())));
                        btnToggleStatut.setOnAction(e ->
                                toggleStatut(getTableView().getItems().get(getIndex())));
                        btnToggleAdmin.setOnAction(e ->
                                toggleAdmin(getTableView().getItems().get(getIndex())));
                        btnSupprimer.setOnAction(e ->
                                supprimerUtilisateur(getTableView().getItems().get(getIndex())));
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

    // ── Filtres ─────────────────────────────────────────────────────────────
    private void setupFilters() {
        filterStatut.setItems(FXCollections.observableArrayList("Tous", "Actif", "Inactif"));
        filterStatut.getSelectionModel().selectFirst();
        filterRole.setItems(FXCollections.observableArrayList("Tous", "Admin", "Utilisateur"));
        filterRole.getSelectionModel().selectFirst();
    }

    // ── Pagination ───────────────────────────────────────────────────────────
    private void setupPagination() {
        pageSizeCombo.setItems(FXCollections.observableArrayList(5, 10, 15, 20, 25, 50));
        pageSizeCombo.setValue(10);
    }

    // ── Stats ────────────────────────────────────────────────────────────────
    private void chargerStatistiques() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            chargerKpi(conn, "SELECT COUNT(*) FROM utilisateur",                                             totalUtilisateurs);
            chargerKpi(conn, "SELECT COUNT(*) FROM utilisateur WHERE est_admin = 1",                        totalAdmins);
            chargerKpi(conn, "SELECT COUNT(*) FROM utilisateur WHERE est_actif = 0",                        totalInactifs);
            chargerKpi(conn, "SELECT COUNT(*) FROM utilisateur WHERE MONTH(date_inscription)=MONTH(CURDATE()) AND YEAR(date_inscription)=YEAR(CURDATE())", totalNouveauxMois);

            // header badge
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM utilisateur");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && headerTotalUsers != null)
                    headerTotalUsers.setText(rs.getInt(1) + " comptes");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void chargerKpi(Connection conn, String sql, Label label) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) label.setText(String.valueOf(rs.getInt(1)));
        }
    }

    // ── Chargement table ─────────────────────────────────────────────────────
    private void chargerUtilisateurs() {
        utilisateursList.clear();
        String search      = searchField.getText().trim();
        String statut      = filterStatut.getValue();
        String role        = filterRole.getValue();

        StringBuilder sql = new StringBuilder(
                "SELECT id, nom, email, date_inscription, est_actif, est_admin " +
                "FROM utilisateur WHERE 1=1");
        if (!search.isEmpty())
            sql.append(" AND (nom LIKE ? OR email LIKE ?)");
        if (statut != null && !statut.equals("Tous"))
            sql.append(" AND est_actif = ").append("Actif".equals(statut) ? "1" : "0");
        if (role != null && !role.equals("Tous"))
            sql.append(" AND est_admin = ").append("Admin".equals(role) ? "1" : "0");
        sql.append(" ORDER BY id DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            if (!search.isEmpty()) {
                String lk = "%" + search + "%";
                ps.setString(p++, lk); ps.setString(p++, lk);
            }
            ps.setInt(p++, pageSize);
            ps.setInt(p,   (currentPage - 1) * pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UtilisateurRow u = new UtilisateurRow();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                u.setEmail(rs.getString("email"));
                u.setDateInscription(rs.getDate("date_inscription").toString());
                u.setEstActif(rs.getBoolean("est_actif"));
                u.setEstAdmin(rs.getBoolean("est_admin"));
                utilisateursList.add(u);
            }
            utilisateursTable.setItems(utilisateursList);
            compterTotalItems();
            updatePaginationUI();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void compterTotalItems() {
        String search = searchField.getText().trim();
        String statut = filterStatut.getValue();
        String role   = filterRole.getValue();

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM utilisateur WHERE 1=1");
        if (!search.isEmpty()) sql.append(" AND (nom LIKE ? OR email LIKE ?)");
        if (statut != null && !statut.equals("Tous"))
            sql.append(" AND est_actif = ").append("Actif".equals(statut) ? "1" : "0");
        if (role != null && !role.equals("Tous"))
            sql.append(" AND est_admin = ").append("Admin".equals(role) ? "1" : "0");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            if (!search.isEmpty()) {
                String lk = "%" + search + "%";
                ps.setString(p++, lk); ps.setString(p, lk);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalItems = rs.getInt(1);
                totalPages = Math.max(1, (int) Math.ceil((double) totalItems / pageSize));
                countLabel.setText(totalItems + " utilisateur(s)");
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
        chargerUtilisateurs();
    }

    // ── Actions ──────────────────────────────────────────────────────────────
    private void modifierUtilisateur(UtilisateurRow user) {
        TextInputDialog dlg = new TextInputDialog(user.getNom());
        dlg.setTitle("Modifier utilisateur");
        dlg.setHeaderText("Modifier le nom de : " + user.getEmail());
        dlg.setContentText("Nouveau nom :");
        dlg.showAndWait().ifPresent(nom -> {
            if (nom.isBlank()) return;
            execUpdate("UPDATE utilisateur SET nom=? WHERE id=?",
                    ps -> { ps.setString(1, nom.trim()); ps.setInt(2, user.getId()); });
            info("Utilisateur modifié avec succès !");
            chargerStatistiques(); chargerUtilisateurs();
        });
    }

    private void supprimerUtilisateur(UtilisateurRow user) {
        if (user.getId() == SessionManager.getUserId()) {
            warn("Vous ne pouvez pas supprimer votre propre compte !"); return;
        }
        if (!confirmer("Supprimer " + user.getNom() + " ?",
                "Toutes ses données (transactions, budgets…) seront supprimées. Irréversible.")) return;

        try {
            execUpdate("DELETE FROM reponse_securite WHERE utilisateur_id=?",
                    ps -> ps.setInt(1, user.getId()));
            execUpdate("DELETE FROM alerte WHERE budget_id IN (SELECT id FROM budget WHERE utilisateur_id=?)",
                    ps -> ps.setInt(1, user.getId()));
            execUpdate("DELETE FROM budget WHERE utilisateur_id=?",
                    ps -> ps.setInt(1, user.getId()));
            execUpdate("DELETE FROM transaction WHERE utilisateur_id=?",
                    ps -> ps.setInt(1, user.getId()));
            execUpdate("DELETE FROM categorie WHERE utilisateur_id=?",
                    ps -> ps.setInt(1, user.getId()));
            execUpdate("DELETE FROM journal_activite WHERE utilisateur_id=?",
                    ps -> ps.setInt(1, user.getId()));
            execUpdate("DELETE FROM utilisateur WHERE id=?",
                    ps -> ps.setInt(1, user.getId()));
            info("Utilisateur supprimé avec succès !");
            chargerStatistiques(); chargerUtilisateurs();
        } catch (Exception e) { e.printStackTrace(); err("Erreur lors de la suppression."); }
    }

    private void toggleStatut(UtilisateurRow user) {
        if (user.getId() == SessionManager.getUserId()) {
            warn("Vous ne pouvez pas désactiver votre propre compte !"); return;
        }
        String action = user.isEstActif() ? "désactiver" : "activer";
        if (!confirmer("Confirmation", "Voulez-vous " + action + " " + user.getNom() + " ?")) return;
        execUpdate("UPDATE utilisateur SET est_actif=? WHERE id=?",
                ps -> { ps.setInt(1, user.isEstActif() ? 0 : 1); ps.setInt(2, user.getId()); });
        chargerStatistiques(); chargerUtilisateurs();
    }

    private void toggleAdmin(UtilisateurRow user) {
        if (user.getId() == SessionManager.getUserId()) {
            warn("Vous ne pouvez pas modifier votre propre rôle !"); return;
        }
        String action = user.isEstAdmin() ? "retirer les droits admin à" : "donner les droits admin à";
        if (!confirmer("Modifier le rôle", "Voulez-vous " + action + " " + user.getNom() + " ?")) return;
        execUpdate("UPDATE utilisateur SET est_admin=? WHERE id=?",
                ps -> { ps.setInt(1, user.isEstAdmin() ? 0 : 1); ps.setInt(2, user.getId()); });
        chargerStatistiques(); chargerUtilisateurs();
    }

    // ── Utilitaires ──────────────────────────────────────────────────────────
    @FunctionalInterface interface PsSetter { void set(PreparedStatement ps) throws SQLException; }

    private void execUpdate(String sql, PsSetter setter) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

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

    // ── FXML handlers ────────────────────────────────────────────────────────
    @FXML private void onSearchChanged()  { currentPage = 1; chargerUtilisateurs(); }
    @FXML private void onFilterChanged()  { currentPage = 1; chargerUtilisateurs(); }
    @FXML private void resetFiltres()     {
        searchField.clear();
        filterStatut.getSelectionModel().selectFirst();
        filterRole.getSelectionModel().selectFirst();
        currentPage = 1; chargerUtilisateurs();
    }
    @FXML private void onPageSizeChanged()    { pageSize = pageSizeCombo.getValue(); currentPage = 1; chargerUtilisateurs(); }
    @FXML private void goToPremierePage()     { goToPage(1); }
    @FXML private void goToPagePrecedente()   { goToPage(currentPage - 1); }
    @FXML private void goToPageSuivante()     { goToPage(currentPage + 1); }
    @FXML private void goToDernierePage()     { goToPage(totalPages); }

    // ── Inner class ──────────────────────────────────────────────────────────
    public static class UtilisateurRow {
        private int id; private String nom, email, dateInscription;
        private boolean estActif, estAdmin;

        public int     getId()               { return id; }
        public void    setId(int id)         { this.id = id; }
        public String  getNom()              { return nom; }
        public void    setNom(String v)      { this.nom = v; }
        public String  getEmail()            { return email; }
        public void    setEmail(String v)    { this.email = v; }
        public String  getDateInscription()  { return dateInscription; }
        public void    setDateInscription(String v) { this.dateInscription = v; }
        public boolean isEstActif()          { return estActif; }
        public void    setEstActif(boolean v){ this.estActif = v; }
        public boolean isEstAdmin()          { return estAdmin; }
        public void    setEstAdmin(boolean v){ this.estAdmin = v; }
        public String  getStatut()           { return estActif ? "Actif" : "Inactif"; }
        public String  getRole()             { return estAdmin ? "Admin" : "Utilisateur"; }
    }
}