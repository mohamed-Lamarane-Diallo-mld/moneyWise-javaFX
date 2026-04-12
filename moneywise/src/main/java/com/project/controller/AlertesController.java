package com.project.controller;

import com.project.dao.AlerteDAO;
import com.project.dao.BudgetDAO;
import com.project.model.Alerte;
import com.project.model.Budget;
import com.project.utils.AlerteHelper;
import com.project.utils.DateHelper;
import com.project.utils.ResponsiveHelper;
import com.project.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AlertesController implements Initializable {

    @FXML private Label  headerDate;
    @FXML private Label  headerUser;
    @FXML private Label  moisLabel;
    @FXML private VBox   budgetListContainer;
    @FXML private Label  noBudgetLabel;
    @FXML private Button btnTous;
    @FXML private Button btnNonLues;
    @FXML private Button btnSeuil80;
    @FXML private Button btnSeuil100;
    @FXML private Label  countAlertes;
    @FXML private VBox   alertesContainer;
    @FXML private VBox   noAlerteContainer;
    @FXML private SidebarController sidebarController;

    private final AlerteDAO alerteDAO = new AlerteDAO();
    private final BudgetDAO budgetDAO = new BudgetDAO();
    private List<Alerte> toutesLesAlertes;
    private static final NumberFormat NF =
        NumberFormat.getNumberInstance(Locale.FRENCH);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chargerHeader();
        chargerBudgets();
        chargerAlertes();
        if (sidebarController != null) sidebarController.setActiveItem("alertes");
        ResponsiveHelper.bind(this::onResize);
    }

    private void onResize() {
        if (sidebarController != null)
            sidebarController.setSidebarVisible(
                ResponsiveHelper.getWidth() >= ResponsiveHelper.BP_SMALL);
    }

    private void chargerHeader() {
        headerDate.setText(DateHelper.formaterComplet(LocalDate.now()));
        moisLabel.setText(DateHelper.nomMoisCourant() + " " + DateHelper.anneeCourante());
        int uid = SessionManager.getUserId();
        if (uid != -1)
            headerUser.setText("Bonjour, "
                + SessionManager.getUtilisateur().getNom().split(" ")[0] + " !");
    }

    private void chargerBudgets() {
        int uid = SessionManager.getUserId();
        if (uid == -1) return;

        List<Budget> budgets = budgetDAO.findActifsMoisCourant(uid);
        budgetListContainer.getChildren().clear();

        if (budgets.isEmpty()) {
            noBudgetLabel.setVisible(true); noBudgetLabel.setManaged(true); return;
        }
        noBudgetLabel.setVisible(false); noBudgetLabel.setManaged(false);

        for (Budget b : budgets) {
            double pct = budgetDAO.getConsommation(b.getId());
            budgetListContainer.getChildren().add(creerBudgetItem(b, pct));
        }
    }

    private VBox creerBudgetItem(Budget budget, double pct) {
        VBox item = new VBox(8);
        item.getStyleClass().add("budget-item");

        HBox ligne = new HBox(0);
        ligne.setAlignment(Pos.CENTER_LEFT);

        Label cat = new Label(budget.getCategorieNom() != null
            ? budget.getCategorieNom() : "—");
        cat.getStyleClass().add("budget-cat-label");
        HBox.setHgrow(cat, Priority.ALWAYS);

        Label montants = new Label(
            NF.format(pct * budget.getMontantMax() / 100)
            + " / " + NF.format(budget.getMontantMax()) + " FCFA");
        montants.getStyleClass().add("budget-montant-label");

        Label pctLabel = new Label(String.format("%.0f%%", pct));
        pctLabel.getStyleClass().add("budget-pct-label");
        pctLabel.setStyle(pct >= 100
            ? "-fx-text-fill:#E74C3C; -fx-font-weight:bold;"
            : pct >= 80 ? "-fx-text-fill:#F39C12; -fx-font-weight:bold;"
            : "-fx-text-fill:#2ECC71; -fx-font-weight:bold;");

        ligne.getChildren().addAll(cat, montants, pctLabel);

        ProgressBar bar = new ProgressBar(Math.min(pct / 100.0, 1.0));
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.getStyleClass().add("progress-bar");
        if (pct >= 100) bar.getStyleClass().add("progress-bar-danger");
        else if (pct >= 80) bar.getStyleClass().add("progress-bar-warning");

        item.getChildren().addAll(ligne, bar);
        return item;
    }

    private void chargerAlertes() {
        int uid = SessionManager.getUserId();
        if (uid == -1) return;
        toutesLesAlertes = alerteDAO.findAll(uid);
        afficherAlertes(toutesLesAlertes);
    }

    private void afficherAlertes(List<Alerte> alertes) {
        alertesContainer.getChildren().clear();
        if (alertes.isEmpty()) {
            noAlerteContainer.setVisible(true); 
            noAlerteContainer.setManaged(true);
            countAlertes.setText("0 alerte(s)"); 
            return;
        }
        noAlerteContainer.setVisible(false); 
        noAlerteContainer.setManaged(false);
        countAlertes.setText(alertes.size() + " alerte(s)");
        for (Alerte a : alertes)
            alertesContainer.getChildren().add(creerAlerteItem(a));
    }

    private HBox creerAlerteItem(Alerte alerte) {
        HBox item = new HBox(14);
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("alerte-item");

        if (!alerte.isEstLue()) {
            item.getStyleClass().add(alerte.getTypeAlerte().name().equals("SEUIL_100")
                ? "alerte-item-seuil100" : "alerte-item-seuil80");
        } else {
            item.getStyleClass().add("alerte-item-lue");
        }

        Label emoji = new Label(alerte.getTypeAlerte().name().equals("SEUIL_100")
            ? "🚨" : "⚠️");
        emoji.setStyle("-fx-font-size:22px;");

        VBox content = new VBox(4);
        HBox.setHgrow(content, Priority.ALWAYS);

        Label badge = new Label(alerte.getTypeAlerte().name().equals("SEUIL_100")
            ? "Budget dépassé (100%)" : "Alerte (80%)");
        badge.getStyleClass().add(alerte.getTypeAlerte().name().equals("SEUIL_100")
            ? "alerte-badge-100" : "alerte-badge-80");

        Label msg = new Label(alerte.getMessage());
        msg.getStyleClass().add("alerte-msg");
        msg.setWrapText(true);

        // DateHelper.formaterAvecHeure
        Label date = new Label(DateHelper.formaterAvecHeure(alerte.getDateAlerte()));
        date.getStyleClass().add("alerte-date");

        content.getChildren().addAll(badge, msg, date);

        VBox actions = new VBox(6);
        actions.setAlignment(Pos.CENTER);

        if (!alerte.isEstLue()) {
            Button btnLu = new Button("✓ Lu");
            btnLu.setStyle(
                "-fx-background-color:#EAE8FF; -fx-text-fill:#6C63FF;" +
                "-fx-font-size:12px; -fx-padding:5 12;" +
                "-fx-background-radius:6; -fx-cursor:hand;");
            btnLu.setOnAction(e -> {
                alerteDAO.marquerLue(alerte.getId());
                chargerAlertes(); chargerBudgets();
                if (sidebarController != null) sidebarController.chargerBadgeAlertes();
            });
            actions.getChildren().add(btnLu);
        } else {
            Label luLabel = new Label("✓ Lu");
            luLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#A0AEC0;");
            actions.getChildren().add(luLabel);
        }

        item.getChildren().addAll(emoji, content, actions);
        return item;
    }

    @FXML private void filterTous() {
        updateFilterBtns(btnTous); afficherAlertes(toutesLesAlertes);
    }
    @FXML private void filterNonLues() {
        updateFilterBtns(btnNonLues);
        afficherAlertes(toutesLesAlertes.stream()
            .filter(a -> !a.isEstLue()).collect(Collectors.toList()));
    }
    @FXML private void filterSeuil80() {
        updateFilterBtns(btnSeuil80);
        afficherAlertes(toutesLesAlertes.stream()
            .filter(a -> a.getTypeAlerte().name().equals("SEUIL_80"))
            .collect(Collectors.toList()));
    }
    @FXML private void filterSeuil100() {
        updateFilterBtns(btnSeuil100);
        afficherAlertes(toutesLesAlertes.stream()
            .filter(a -> a.getTypeAlerte().name().equals("SEUIL_100"))
            .collect(Collectors.toList()));
    }

    private void updateFilterBtns(Button actif) {
        btnTous.getStyleClass().remove("alerte-filter-btn-active");
        btnNonLues.getStyleClass().remove("alerte-filter-btn-active");
        btnSeuil80.getStyleClass().remove("alerte-filter-btn-active");
        btnSeuil100.getStyleClass().remove("alerte-filter-btn-active");
        actif.getStyleClass().add("alerte-filter-btn-active");
    }

    @FXML
    private void handleMarquerToutLu() {
        int uid = SessionManager.getUserId();
        if (uid == -1) return;

        if (AlerteHelper.confirmer("Marquer tout comme lu",
                "Marquer toutes les alertes comme lues ?")) {
            // AlerteHelper.marquerToutesLues
            AlerteHelper.marquerToutesLues(uid);
            chargerAlertes(); chargerBudgets();
            if (sidebarController != null) sidebarController.chargerBadgeAlertes();
        }
    }
}