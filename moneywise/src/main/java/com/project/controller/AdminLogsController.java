package com.project.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.project.dao.DatabaseConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class AdminLogsController implements Initializable {

    @FXML private Label totalLogs;
    @FXML private Label totalConnexions;
    @FXML private Label totalTransactions;
    @FXML private Label totalInscriptions;
    @FXML private Label headerDate;
    @FXML private Label headerTotalLogs;
    @FXML private Label countLabel;
    @FXML private Label paginationInfo;

    @FXML private SidebarController sidebarController;

    @FXML private TextField        searchField;
    @FXML private ComboBox<String> filterAction;
    @FXML private DatePicker       filterDateDebut;
    @FXML private DatePicker       filterDateFin;

    @FXML private TableView<LogRow>               logsTable;
    @FXML private TableColumn<LogRow, Integer>    colId;
    @FXML private TableColumn<LogRow, String>     colUtilisateur;
    @FXML private TableColumn<LogRow, String>     colAction;
    @FXML private TableColumn<LogRow, String>     colDetails;
    @FXML private TableColumn<LogRow, String>     colDate;
    @FXML private TableColumn<LogRow, String>     colIp;

    @FXML private Button            btnPremierePage;
    @FXML private Button            btnPagePrev;
    @FXML private Button            btnPageNext;
    @FXML private Button            btnDernierePage;
    @FXML private HBox              pageNumbersBox;
    @FXML private ComboBox<Integer> pageSizeCombo;

    private ObservableList<LogRow> logsList = FXCollections.observableArrayList();
    private int currentPage = 1;
    private int pageSize    = 15;
    private int totalPages  = 1;
    private int totalItems  = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        setupFilters();
        setupPagination();
        chargerStatistiques();
        chargerLogs();
        headerDate.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy")));
        if (sidebarController != null)
            sidebarController.setActiveItem("adminLogs");
    }

    // ── Colonnes ─────────────────────────────────────────────────────────────
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUtilisateur.setCellValueFactory(new PropertyValueFactory<>("utilisateur"));
        colDetails.setCellValueFactory(new PropertyValueFactory<>("details"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colIp.setCellValueFactory(new PropertyValueFactory<>("ip"));

        // ACTION avec badge coloré
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colAction.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                String style;
                if (item.contains("CONNEXION"))        style = "-fx-text-fill:#10B981; -fx-font-weight:bold;";
                else if (item.contains("INSCRIPTION")) style = "-fx-text-fill:#6C63FF; -fx-font-weight:bold;";
                else if (item.contains("SUPPRESSION")) style = "-fx-text-fill:#EF4444; -fx-font-weight:bold;";
                else if (item.contains("TRANSACTION")) style = "-fx-text-fill:#F59E0B; -fx-font-weight:bold;";
                else                                   style = "-fx-text-fill:#64748B;";
                setStyle(style);
            }
        });
    }

    // ── Filtres ──────────────────────────────────────────────────────────────
    private void setupFilters() {
        filterAction.setItems(FXCollections.observableArrayList(
                "Toutes", "CONNEXION", "DECONNEXION", "INSCRIPTION",
                "AJOUT_TRANSACTION", "MODIFICATION_TRANSACTION", "SUPPRESSION_TRANSACTION",
                "MODIFICATION_PROFIL", "CHANGEMENT_MOT_DE_PASSE"));
        filterAction.getSelectionModel().selectFirst();
        filterDateDebut.setValue(LocalDate.now().minusMonths(1));
        filterDateFin.setValue(LocalDate.now());
    }

    // ── Pagination ───────────────────────────────────────────────────────────
    private void setupPagination() {
        pageSizeCombo.setItems(FXCollections.observableArrayList(10, 15, 20, 25, 30, 50));
        pageSizeCombo.setValue(15);
    }

    // ── Stats ─────────────────────────────────────────────────────────────────
    private void chargerStatistiques() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            chargerKpi(conn, "SELECT COUNT(*) FROM journal_activite",                                       totalLogs);
            chargerKpi(conn, "SELECT COUNT(*) FROM journal_activite WHERE action='CONNEXION'",              totalConnexions);
            chargerKpi(conn, "SELECT COUNT(*) FROM journal_activite WHERE action LIKE '%TRANSACTION%'",     totalTransactions);
            chargerKpi(conn, "SELECT COUNT(*) FROM journal_activite WHERE action='INSCRIPTION'",            totalInscriptions);

            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM journal_activite");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && headerTotalLogs != null)
                    headerTotalLogs.setText(rs.getInt(1) + " enregistrées");
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
    private void chargerLogs() {
        logsList.clear();
        String    search     = searchField.getText().trim();
        String    action     = filterAction.getValue();
        LocalDate dateDebut  = filterDateDebut.getValue();
        LocalDate dateFin    = filterDateFin.getValue();

        StringBuilder sql = new StringBuilder(
                "SELECT j.id, j.action, j.details, j.date_action, j.adresse_ip, " +
                "u.nom AS utilisateur_nom, u.email AS utilisateur_email " +
                "FROM journal_activite j " +
                "LEFT JOIN utilisateur u ON j.utilisateur_id = u.id WHERE 1=1");

        if (!search.isEmpty())
            sql.append(" AND (u.nom LIKE ? OR u.email LIKE ? OR j.action LIKE ? OR j.details LIKE ?)");
        if (action != null && !action.equals("Toutes"))
            sql.append(" AND j.action = ?");
        if (dateDebut != null) sql.append(" AND DATE(j.date_action) >= ?");
        if (dateFin   != null) sql.append(" AND DATE(j.date_action) <= ?");
        sql.append(" ORDER BY j.id DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            if (!search.isEmpty()) {
                String lk = "%" + search + "%";
                ps.setString(p++, lk); ps.setString(p++, lk);
                ps.setString(p++, lk); ps.setString(p++, lk);
            }
            if (action != null && !action.equals("Toutes")) ps.setString(p++, action);
            if (dateDebut != null) ps.setDate(p++, java.sql.Date.valueOf(dateDebut));
            if (dateFin   != null) ps.setDate(p++, java.sql.Date.valueOf(dateFin));
            ps.setInt(p++, pageSize);
            ps.setInt(p,   (currentPage - 1) * pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LogRow log = new LogRow();
                log.setId(rs.getInt("id"));
                String nom = rs.getString("utilisateur_nom");
                String email = rs.getString("utilisateur_email");
                log.setUtilisateur(nom != null
                        ? nom + (email != null ? " (" + email + ")" : "")
                        : "Système");
                log.setAction(rs.getString("action"));
                String details = rs.getString("details");
                log.setDetails(details != null ? details : "—");
                java.sql.Timestamp ts = rs.getTimestamp("date_action");
                log.setDate(ts != null ? ts.toString().replace(".0", "") : "—");
                String ip = rs.getString("adresse_ip");
                log.setIp(ip != null ? ip : "—");
                logsList.add(log);
            }
            logsTable.setItems(logsList);
            compterTotalItems();
            updatePaginationUI();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void compterTotalItems() {
        String    search    = searchField.getText().trim();
        String    action    = filterAction.getValue();
        LocalDate dateDebut = filterDateDebut.getValue();
        LocalDate dateFin   = filterDateFin.getValue();

        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM journal_activite j " +
                "LEFT JOIN utilisateur u ON j.utilisateur_id = u.id WHERE 1=1");
        if (!search.isEmpty())
            sql.append(" AND (u.nom LIKE ? OR u.email LIKE ? OR j.action LIKE ? OR j.details LIKE ?)");
        if (action != null && !action.equals("Toutes")) sql.append(" AND j.action = ?");
        if (dateDebut != null) sql.append(" AND DATE(j.date_action) >= ?");
        if (dateFin   != null) sql.append(" AND DATE(j.date_action) <= ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            if (!search.isEmpty()) {
                String lk = "%" + search + "%";
                ps.setString(p++, lk); ps.setString(p++, lk);
                ps.setString(p++, lk); ps.setString(p++, lk);
            }
            if (action != null && !action.equals("Toutes")) ps.setString(p++, action);
            if (dateDebut != null) ps.setDate(p++, java.sql.Date.valueOf(dateDebut));
            if (dateFin   != null) ps.setDate(p++, java.sql.Date.valueOf(dateFin));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalItems = rs.getInt(1);
                totalPages = Math.max(1, (int) Math.ceil((double) totalItems / pageSize));
                countLabel.setText(totalItems + " action(s)");
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
        chargerLogs();
    }

    // ── FXML handlers ─────────────────────────────────────────────────────────
    @FXML private void onSearchChanged()  { currentPage = 1; chargerLogs(); }
    @FXML private void onFilterChanged()  { currentPage = 1; chargerLogs(); }
    @FXML private void resetFiltres()     {
        searchField.clear();
        filterAction.getSelectionModel().selectFirst();
        filterDateDebut.setValue(LocalDate.now().minusMonths(1));
        filterDateFin.setValue(LocalDate.now());
        currentPage = 1; chargerLogs();
    }
    @FXML private void onPageSizeChanged()  { pageSize = pageSizeCombo.getValue(); currentPage = 1; chargerLogs(); }
    @FXML private void goToPremierePage()   { goToPage(1); }
    @FXML private void goToPagePrecedente() { goToPage(currentPage - 1); }
    @FXML private void goToPageSuivante()   { goToPage(currentPage + 1); }
    @FXML private void goToDernierePage()   { goToPage(totalPages); }

    // ── Inner class ──────────────────────────────────────────────────────────
    public static class LogRow {
        private int id; private String utilisateur, action, details, date, ip;

        public int    getId()                 { return id; }
        public void   setId(int id)           { this.id = id; }
        public String getUtilisateur()        { return utilisateur; }
        public void   setUtilisateur(String v){ this.utilisateur = v; }
        public String getAction()             { return action; }
        public void   setAction(String v)     { this.action = v; }
        public String getDetails()            { return details; }
        public void   setDetails(String v)    { this.details = v; }
        public String getDate()               { return date; }
        public void   setDate(String v)       { this.date = v; }
        public String getIp()                 { return ip; }
        public void   setIp(String v)         { this.ip = v; }
    }
}