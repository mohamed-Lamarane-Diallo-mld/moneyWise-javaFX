package com.project.controller;

import com.project.dao.AlerteDAO;
import com.project.dao.TransactionDAO;
import com.project.model.Alerte;
import com.project.model.Transaction;
import com.project.utils.AlerteHelper;
import com.project.utils.DateHelper;
import com.project.utils.NavigationHelper;
import com.project.utils.ResponsiveHelper;
import com.project.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    // ── Header ──
    @FXML private Label headerDate;
    @FXML private Label headerUser;
    @FXML private Label headerSolde;      
    // ── KPI cards ──
    @FXML private Label kpiRevenus;
    @FXML private Label kpiDepenses;
    @FXML private Label kpiSolde;
    @FXML private Label kpiRevenusEvol;
    @FXML private Label kpiDepensesEvol;
    @FXML private Label kpiSoldeEvol;

    // ── Panel droit — fx:id distincts ──
    @FXML private Label sideRevenus;      
    @FXML private Label sideDepenses; 
    @FXML private Label sideEpargne;     

    // ── Alertes ──
    @FXML private HBox  alerteWidget;
    @FXML private Label alerteWidgetText;

    // ── Table ──
    @FXML private TableView<Transaction>          transactionsTable;
    @FXML private TableColumn<Transaction,String> colDate;
    @FXML private TableColumn<Transaction,String> colDescription;
    @FXML private TableColumn<Transaction,String> colCategorie;
    @FXML private TableColumn<Transaction,String> colType;
    @FXML private TableColumn<Transaction,String> colMontant;

    // ── Sidebar ──
    @FXML private SidebarController sidebarController;

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final AlerteDAO      alerteDAO      = new AlerteDAO();
    private static final NumberFormat NF =
        NumberFormat.getNumberInstance(Locale.FRENCH);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chargerHeader();
        chargerKPI();
        chargerTableTransactions();
        chargerAlertes();
        if (sidebarController != null) sidebarController.setActiveItem("home");
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
        int uid = SessionManager.getUserId();
        headerDate.setText(DateHelper.formaterComplet(LocalDate.now()));
        if (uid != -1)
            headerUser.setText("Bonjour, "
                + SessionManager.getUtilisateur().getNom().split(" ")[0]);
    }

    // ─────────────────────────────────────────
    // KPI — injection directe sans lookup
    // ─────────────────────────────────────────
    private void chargerKPI() {
        int uid = SessionManager.getUserId();
        if (uid == -1) return;

        double revenus  = transactionDAO.getTotalEntreesMois(uid);
        double depenses = transactionDAO.getTotalSortiesMois(uid);
        double solde    = transactionDAO.getSoldeTotal(uid);
        double epargne  = revenus - depenses;
        String mois     = DateHelper.nomMoisCourant();

        // ── KPI cards principales ──
        kpiRevenus.setText(formaterMontant(revenus));
        kpiDepenses.setText(formaterMontant(depenses));
        kpiSolde.setText(formaterMontant(solde));
        kpiRevenusEvol.setText("Entrées de " + mois);
        kpiDepensesEvol.setText("Sorties de " + mois);
        kpiSoldeEvol.setText("Épargne : " + formaterMontant(epargne));

        // Colorer le solde (card violette)
        kpiSolde.setStyle(solde >= 0
            ? "-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:white;"
            : "-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:#FECACA;");

        // ── Header solde rapide ──
        if (headerSolde != null) {
            headerSolde.setText(formaterMontant(solde));
            headerSolde.setStyle(solde >= 0
                ? "-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#276749;"
                : "-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#E74C3C;");
        }

        // ── Panel droit résumé (fx:id distincts — injection directe) ──
        if (sideRevenus  != null) sideRevenus.setText(formaterMontant(revenus));
        if (sideDepenses != null) sideDepenses.setText(formaterMontant(depenses));
        if (sideEpargne  != null) {
            sideEpargne.setText(formaterMontant(epargne));
            sideEpargne.setStyle(epargne >= 0
                ? "-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#6C63FF;"
                : "-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#E74C3C;");
        }
    }

    // ─────────────────────────────────────────
    // TABLE
    // ─────────────────────────────────────────
    private void chargerTableTransactions() {
        int uid = SessionManager.getUserId();
        if (uid == -1) return;

        transactionsTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colDate.setCellValueFactory(data ->
            new SimpleStringProperty(
                DateHelper.labelRelatif(data.getValue().getDateTransaction())));

        colDescription.setCellValueFactory(data -> {
            String desc = data.getValue().getDescription();
            return new SimpleStringProperty(
                (desc != null && !desc.isEmpty()) ? desc : "—");
        });

        colCategorie.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getCategorieNom() != null
                    ? data.getValue().getCategorieNom() : "—"));

        colType.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getType().getLibelle()));
        colType.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle(item.equals("Entrée")
                        ? "-fx-text-fill:#27AE60; -fx-font-weight:bold;"
                        : "-fx-text-fill:#E74C3C; -fx-font-weight:bold;");
                }
            }
        });

        colMontant.setCellValueFactory(data -> {
            Transaction t = data.getValue();
            String signe = t.getType().name().equals("ENTREE") ? "+ " : "- ";
            return new SimpleStringProperty(signe + formaterMontant(t.getMontant()));
        });
        colMontant.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle(item.startsWith("+")
                        ? "-fx-text-fill:#27AE60; -fx-font-weight:bold;"
                        : "-fx-text-fill:#E74C3C; -fx-font-weight:bold;");
                }
            }
        });

        List<Transaction> recentes = transactionDAO.findRecentes(uid, 8);
        transactionsTable.setItems(FXCollections.observableArrayList(recentes));
        transactionsTable.setPlaceholder(
            new Label("Aucune transaction pour le moment."));
    }

    // ─────────────────────────────────────────
    // ALERTES
    // ─────────────────────────────────────────
    private void chargerAlertes() {
        int uid = SessionManager.getUserId();
        if (uid == -1) return;

        List<Alerte> alertes = alerteDAO.findNonLues(uid);
        if (!alertes.isEmpty()) {
            Alerte premiere = alertes.get(0);
            alerteWidgetText.setText(premiere.getMessage()
                + (alertes.size() > 1
                    ? " (+" + (alertes.size() - 1) + " autres)" : ""));
            alerteWidget.setVisible(true);
            alerteWidget.setManaged(true);
            AlerteHelper.verifierEtNotifier(SessionManager.getUtilisateur());
        }
    }

    // ─────────────────────────────────────────
    // NAVIGATION
    // ─────────────────────────────────────────
    @FXML private void goToTransactions() {
        try { NavigationHelper.navigateTo(NavigationHelper.TRANSACTION); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void goToStatistiques() {
        try { NavigationHelper.navigateTo(NavigationHelper.STATISTIQUE); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void goToAlertes() {
        try { NavigationHelper.navigateTo(NavigationHelper.ALERTES); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private String formaterMontant(double m) {
        return NF.format(m) + " FCFA";
    }
}