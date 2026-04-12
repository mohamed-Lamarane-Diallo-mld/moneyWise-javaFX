package com.project.utils;

import java.util.List;

import com.project.App;
import com.project.dao.AlerteDAO;
import com.project.dao.BudgetDAO;
import com.project.model.Alerte;
import com.project.model.Utilisateur;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class AlerteHelper {

    private static final AlerteDAO  alerteDAO  = new AlerteDAO();
    private static final BudgetDAO  budgetDAO  = new BudgetDAO();

    // ─────────────────────────────────────────
    // VÉRIFIER ET DÉCLENCHER LES ALERTES
    // Appelé après chaque ajout/modif de transaction
    // ─────────────────────────────────────────
    public static void verifierEtNotifier(Utilisateur user) {
        if (user == null) return;

        // Vérification des seuils en BDD
        budgetDAO.verifierSeuils(user.getId());

        // Récupérer les alertes non lues
        List<Alerte> nonLues = alerteDAO.findNonLues(user.getId());
        if (nonLues.isEmpty()) return;

        // Afficher une notification JavaFX pour chaque alerte non lue
        Platform.runLater(() -> {
            for (Alerte a : nonLues) {
                afficherNotificationBudget(a);
                // Marquer comme lue après affichage
                alerteDAO.marquerLue(a.getId());
            }
        });
    }

    // ─────────────────────────────────────────
    // NOTIFICATION VISUELLE (Popup flottant)
    // ─────────────────────────────────────────
    public static void afficherNotificationBudget(Alerte alerte) {
        Stage stage = App.getStage();
        if (stage == null) return;

        Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.setAutoFix(true);

        // Couleur selon le type
        String couleurBg   = alerte.getTypeAlerte().name().equals("SEUIL_100")
            ? "#FDECEA" : "#FFF8E7";
        String couleurBord = alerte.getTypeAlerte().name().equals("SEUIL_100")
            ? "#E74C3C" : "#F39C12";
        String emoji = alerte.getTypeAlerte().name().equals("SEUIL_100")
            ? "erreur" : "avertissement";

        // Construction du popup
        VBox container = new VBox(6);
        container.setStyle(
            "-fx-background-color: " + couleurBg + ";" +
            "-fx-border-color: " + couleurBord + ";" +
            "-fx-border-width: 0 0 0 4;" +
            "-fx-background-radius: 0 8 8 0;" +
            "-fx-padding: 14 18;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 4);" +
            "-fx-min-width: 320px;" +
            "-fx-max-width: 380px;"
        );

        HBox titre = new HBox(8);
        titre.setAlignment(Pos.CENTER_LEFT);

        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 16px;");

        Label titreLabel = new Label("Alerte Budget");
        titreLabel.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + couleurBord + ";"
        );

        titre.getChildren().addAll(emojiLabel, titreLabel);

        Label messageLabel = new Label(alerte.getMessage());
        messageLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #4A5568;" +
            "-fx-wrap-text: true;"
        );
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(340);

        Label dateLabel = new Label(
            DateHelper.formaterAvecHeure(alerte.getDateAlerte()));
        dateLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #A0AEC0;"
        );

        container.getChildren().addAll(titre, messageLabel, dateLabel);
        popup.getContent().add(container);

        // Position : coin bas droit de la fenêtre
        double x = stage.getX() + stage.getWidth()  - 400;
        double y = stage.getY() + stage.getHeight()  - 120;
        popup.show(stage, x, y);

        // Fermeture automatique après 4 secondes
        new Thread(() -> {
            try {
                Thread.sleep(4000);
                Platform.runLater(popup::hide);
            } catch (InterruptedException ignored) {}
        }).start();
    }

    // ─────────────────────────────────────────
    // ALERTE DIALOG (confirmation suppression etc.)
    // ─────────────────────────────────────────
    public static boolean confirmer(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Personnaliser les boutons
        ButtonType btnOui = new ButtonType("Confirmer");
        ButtonType btnNon = new ButtonType("Annuler");
        alert.getButtonTypes().setAll(btnOui, btnNon);

        return alert.showAndWait()
            .map(r -> r == btnOui)
            .orElse(false);
    }

    // ─────────────────────────────────────────
    // ALERTE ERREUR
    // ─────────────────────────────────────────
    public static void erreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ─────────────────────────────────────────
    // ALERTE SUCCÈS
    // ─────────────────────────────────────────
    public static void succes(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ─────────────────────────────────────────
    // ALERTE SOLDE INSUFFISANT
    // ─────────────────────────────────────────
    public static void soldeInsuffisant(double solde, double montant) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Solde insuffisant");
        alert.setHeaderText("⚠ Transaction impossible");
        alert.setContentText(
            "Solde disponible : " + String.format("%,.0f", solde) + " FCFA\n" +
            "Montant demandé  : " + String.format("%,.0f", montant) + " FCFA\n\n" +
            "Vous ne pouvez pas effectuer cette sortie."
        );
        alert.showAndWait();
    }

    // ─────────────────────────────────────────
    // COMPTER LES ALERTES NON LUES
    // ─────────────────────────────────────────
    public static int countNonLues(int utilisateurId) {
        return alerteDAO.countNonLues(utilisateurId);
    }

    // ─────────────────────────────────────────
    // MARQUER TOUTES LES ALERTES COMME LUES
    // ─────────────────────────────────────────
    public static void marquerToutesLues(int utilisateurId) {
        alerteDAO.marquerToutesLues(utilisateurId);
    }
}