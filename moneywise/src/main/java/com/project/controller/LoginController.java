package com.project.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.project.dao.JournalDAO;
import com.project.dao.UtilisateurDAO;
import com.project.model.Utilisateur;
import com.project.utils.NavigationHelper;
import com.project.utils.SessionManager;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LoginController implements Initializable {

    @FXML private VBox   leftPane;
    @FXML private VBox   rightPane;
    @FXML private VBox   brandContent;
    @FXML private VBox   formCard;
    @FXML private VBox   featuresBox;

    // Cercles décoratifs
    @FXML private Circle circle1;
    @FXML private Circle circle2;
    @FXML private Circle circle3;
    @FXML private Circle circle4;

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private HBox          errorBox;
    @FXML private Label         errorLabel;
    @FXML private Button        loginBtn;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final JournalDAO     journalDAO     = new JournalDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Lancer les animations au démarrage
        javafx.application.Platform.runLater(this::lancerAnimations);
    }

    // ─────────────────────────────────────────
    // ANIMATIONS
    // ─────────────────────────────────────────
    private void lancerAnimations() {
        animerEntreeGauche();
        animerEntreeDroite();
        animerCercles();
        animerFeaturesBox();
    }

    /** Panneau gauche glisse depuis la gauche */
    private void animerEntreeGauche() {
        if (brandContent == null) return;
        brandContent.setOpacity(0);
        brandContent.setTranslateX(-40);

        FadeTransition fade = new FadeTransition(Duration.millis(700), brandContent);
        fade.setFromValue(0); fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(
            Duration.millis(700), brandContent);
        slide.setFromX(-40); slide.setToX(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        new ParallelTransition(fade, slide).play();
    }

    /** Formulaire glisse depuis la droite avec léger rebond */
    private void animerEntreeDroite() {
        if (formCard == null) return;
        formCard.setOpacity(0);
        formCard.setTranslateY(30);

        FadeTransition fade = new FadeTransition(Duration.millis(600), formCard);
        fade.setFromValue(0); fade.setToValue(1);
        fade.setDelay(Duration.millis(200));

        TranslateTransition slide = new TranslateTransition(
            Duration.millis(300), formCard);
        slide.setFromY(30); slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        slide.setDelay(Duration.millis(200));

        new ParallelTransition(fade, slide).play();
    }

    /** Cercles décoratifs — mouvement flottant en boucle */
    private void animerCercles() {
        animerCercle(circle1, 8, 12, 3200);
        animerCercle(circle2, -10, 8, 4100);
        animerCercle(circle3, 6, -10, 3700);
        animerCercle(circle4, -8, 6, 2900);
    }

    private void animerCercle(Circle cercle, double dx, double dy, int dureeMs) {
        if (cercle == null) return;

        TranslateTransition t = new TranslateTransition(
            Duration.millis(dureeMs), cercle);
        t.setByX(dx); t.setByY(dy);
        t.setAutoReverse(true);
        t.setCycleCount(Animation.INDEFINITE);
        t.setInterpolator(Interpolator.EASE_BOTH);
        t.play();
    }

    /** Features box apparaît en cascade avec délai */
    private void animerFeaturesBox() {
        if (featuresBox == null) return;
        featuresBox.setOpacity(0);
        featuresBox.setTranslateY(20);

        FadeTransition fade = new FadeTransition(Duration.millis(600), featuresBox);
        fade.setFromValue(0); fade.setToValue(1);
        fade.setDelay(Duration.millis(200));

        TranslateTransition slide = new TranslateTransition(
            Duration.millis(600), featuresBox);
        slide.setFromY(20); slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        slide.setDelay(Duration.millis(200));

        new ParallelTransition(fade, slide).play();
    }

    /** Shake animation en cas d'erreur */
    private void animerErreur() {
        if (formCard == null) return;

        TranslateTransition shake = new TranslateTransition(
            Duration.millis(60), formCard);
        shake.setFromX(0); shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setInterpolator(Interpolator.LINEAR);
        shake.play();
    }

    /** Animation bouton cliqué */
    private void animerBoutonSuccess() {
        if (loginBtn == null) return;

        ScaleTransition scale = new ScaleTransition(Duration.millis(150), loginBtn);
        scale.setFromX(1); scale.setFromY(1);
        scale.setToX(0.96); scale.setToY(0.96);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }
    

    // ─────────────────────────────────────────
    // CONNEXION
    // ─────────────────────────────────────────
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String mdp   = passwordField.getText();

        hideError();

        if (email.isEmpty() || mdp.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            animerErreur(); 
            return;
        }
        if (!email.contains("@")) {
            showError("Adresse email invalide.");
            animerErreur(); return;
        }

        animerBoutonSuccess();
        loginBtn.setDisable(true);
        loginBtn.setText("Connexion en cours...");

        new Thread(() -> {
            Utilisateur user = utilisateurDAO.connecter(email, mdp);
            javafx.application.Platform.runLater(() -> {
                if (user != null) {
                    SessionManager.setUtilisateur(user);
                    journalDAO.log(user.getId(),
                        JournalDAO.ACTION_CONNEXION, "Connexion");

                    // Animation de sortie avant navigation
                    animerSortie(() -> {
                        try { NavigationHelper.navigateTo(NavigationHelper.HOME); }
                        catch (Exception e) { e.printStackTrace(); }
                    });
                } else {
                    showError("Email ou mot de passe incorrect.");
                    animerErreur();
                    passwordField.clear();
                    loginBtn.setDisable(false);
                    loginBtn.setText("Se connecter  →");
                }
            });
        }).start();
    }

    /** Animation de sortie avant de naviguer */
    private void animerSortie(Runnable onFinish) {
        if (rightPane == null) { onFinish.run(); return; }

        FadeTransition fade = new FadeTransition(Duration.millis(300), rightPane);
        fade.setFromValue(1); fade.setToValue(0);
        fade.setOnFinished(e -> onFinish.run());
        fade.play();
    }

    @FXML
    private void goToInscription(MouseEvent event) {
        animerSortie(() -> {
            try { NavigationHelper.navigateTo(NavigationHelper.INSCRIPTION); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorBox.setVisible(true);
        errorBox.setManaged(true);
        // Animation apparition erreur
        errorBox.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(300), errorBox);
        fade.setFromValue(0); fade.setToValue(1);
        fade.play();
    }

    private void hideError() {
        errorBox.setVisible(false);
        errorBox.setManaged(false);
    }
}