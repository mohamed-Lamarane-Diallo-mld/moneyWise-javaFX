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

public class InscriptionController implements Initializable {

    @FXML
    private VBox leftPane;
    @FXML
    private VBox rightPane;
    @FXML
    private VBox brandContent;
    @FXML
    private VBox formCard;
    @FXML
    private VBox featuresBox;

    @FXML
    private Circle circle1;
    @FXML
    private Circle circle2;
    @FXML
    private Circle circle3;
    @FXML
    private Circle circle4;

    @FXML
    private TextField nomField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private HBox errorBox;
    @FXML
    private Label errorLabel;
    @FXML
    private HBox successBox;
    @FXML
    private Label successLabel;
    @FXML
    private Button inscriptionBtn;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final JournalDAO journalDAO = new JournalDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        javafx.application.Platform.runLater(this::lancerAnimations);
    }

    // ─────────────────────────────────────────
    // ANIMATIONS (identiques au Login)
    // ─────────────────────────────────────────
    private void lancerAnimations() {
        animerEntreeGauche();
        animerEntreeDroite();
        animerCercles();
        animerFeaturesBox();
    }

    private void animerEntreeGauche() {
        if (brandContent == null) {
            return;
        }
        brandContent.setOpacity(0);
        brandContent.setTranslateX(-40);
        FadeTransition fade = new FadeTransition(Duration.millis(700), brandContent);
        fade.setFromValue(0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(
                Duration.millis(700), brandContent);
        slide.setFromX(-40);
        slide.setToX(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        new ParallelTransition(fade, slide).play();
    }

    private void animerEntreeDroite() {
        if (formCard == null) {
            return;
        }
        formCard.setOpacity(0);
        formCard.setTranslateY(30);
        FadeTransition fade = new FadeTransition(Duration.millis(600), formCard);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(200));
        TranslateTransition slide = new TranslateTransition(
                Duration.millis(600), formCard);
        slide.setFromY(30);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        slide.setDelay(Duration.millis(200));
        new ParallelTransition(fade, slide).play();
    }

    private void animerCercles() {
        animerCercle(circle1, 8, 12, 3200);
        animerCercle(circle2, -10, 8, 4100);
        animerCercle(circle3, 6, -10, 3700);
        animerCercle(circle4, -8, 6, 2900);
    }

    private void animerCercle(Circle c, double dx, double dy, int ms) {
        if (c == null) {
            return;
        }
        TranslateTransition t = new TranslateTransition(Duration.millis(ms), c);
        t.setByX(dx);
        t.setByY(dy);
        t.setAutoReverse(true);
        t.setCycleCount(Animation.INDEFINITE);
        t.setInterpolator(Interpolator.EASE_BOTH);
        t.play();
    }

    private void animerFeaturesBox() {
        if (featuresBox == null) {
            return;
        }
        featuresBox.setOpacity(0);
        featuresBox.setTranslateY(20);
        FadeTransition fade = new FadeTransition(Duration.millis(600), featuresBox);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(500));
        TranslateTransition slide = new TranslateTransition(
                Duration.millis(600), featuresBox);
        slide.setFromY(20);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        slide.setDelay(Duration.millis(500));
        new ParallelTransition(fade, slide).play();
    }

    private void animerErreur() {
        if (formCard == null) {
            return;
        }
        TranslateTransition shake = new TranslateTransition(
                Duration.millis(60), formCard);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setInterpolator(Interpolator.LINEAR);
        shake.play();
    }

    private void animerSortie(Runnable onFinish) {
        if (rightPane == null) {
            onFinish.run();
            return;
        }
        FadeTransition fade = new FadeTransition(Duration.millis(300), rightPane);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> onFinish.run());
        fade.play();
    }

    // ─────────────────────────────────────────
    // INSCRIPTION
    // ─────────────────────────────────────────
    @FXML
    private void handleInscription() {
        String nom = nomField.getText().trim();
        String email = emailField.getText().trim();
        String mdp = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        hideMessages();

        if (nom.isEmpty() || email.isEmpty() || mdp.isEmpty() || confirm.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            animerErreur();
            return;
        }
        if (nom.length() < 2) {
            showError("Le nom doit contenir au moins 2 caractères.");
            animerErreur();
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showError("Adresse email invalide.");
            animerErreur();
            return;
        }
        if (mdp.length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères.");
            animerErreur();
            return;
        }
        if (!mdp.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas.");
            confirmPasswordField.clear();
            animerErreur();
            return;
        }
        if (utilisateurDAO.emailExiste(email)) {
            showError("Cette adresse email est déjà utilisée.");
            animerErreur();
            return;
        }

        inscriptionBtn.setDisable(true);
        inscriptionBtn.setText("Création en cours...");

        new Thread(() -> {
            Utilisateur nouvelUser = new Utilisateur(nom, email, mdp);
            boolean succes = utilisateurDAO.inscrire(nouvelUser);

            javafx.application.Platform.runLater(() -> {
                if (succes) {
                    Utilisateur userConnecte = utilisateurDAO.connecter(email, mdp);
                    if (userConnecte != null) {
                        SessionManager.setUtilisateur(userConnecte);
                        journalDAO.log(userConnecte.getId(),
                                JournalDAO.ACTION_INSCRIPTION, "Nouvelle inscription");
                        animerSortie(() -> {
                            try {
                                NavigationHelper.navigateTo(NavigationHelper.HOME);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } else {
                    showError("Erreur lors de la création. Réessayez.");
                    animerErreur();
                    inscriptionBtn.setDisable(false);
                    inscriptionBtn.setText("Créer mon compte  →");
                }
            });
        }).start();
    }

    @FXML
    private void goToLogin(MouseEvent event) {
        animerSortie(() -> {
            try {
                NavigationHelper.navigateTo(NavigationHelper.LOGIN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorBox.setVisible(true);
        errorBox.setManaged(true);
        errorBox.setOpacity(0);

        FadeTransition fade = new FadeTransition(Duration.millis(300), errorBox);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void showSuccess(String msg) {
        successLabel.setText(msg);
        successBox.setVisible(true);
        successBox.setManaged(true);
    }

    private void hideMessages() {
        errorBox.setVisible(false);
        errorBox.setManaged(false);
        successBox.setVisible(false);
        successBox.setManaged(false);
    }
}
