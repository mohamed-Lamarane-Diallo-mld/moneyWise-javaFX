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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController implements Initializable {

    @FXML
    private VBox rootPane;
    @FXML
    private VBox formCard;
    @FXML
    private Circle bubble1, bubble2, bubble3, bubble4, bubble5, bubble6, bubble7;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private HBox errorBox;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginBtn;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final JournalDAO journalDAO = new JournalDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        javafx.application.Platform.runLater(this::lancerAnimations);
    }

    // ─────────────────────────────────────────
    // ANIMATIONS
    // ─────────────────────────────────────────
    private void lancerAnimations() {
        animerFormCard();
        animerBulle(bubble1, -40, 40, 3800);
        animerBulle(bubble2, -60, 60, 4500);
        animerBulle(bubble3, -30, 35, 3200);
        animerBulle(bubble4, -50, 45, 4000);
        animerBulle(bubble5, -40, 40, 3800);
        animerBulle(bubble6, -60, 60, 4500);
        animerBulle(bubble7, -30, 35, 3200);
    }

    private void animerFormCard() {
        if (formCard == null)
            return;
        formCard.setOpacity(0);
        formCard.setTranslateY(20);
        FadeTransition fade = new FadeTransition(Duration.millis(600), formCard);
        fade.setFromValue(0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(600), formCard);
        slide.setFromY(20);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        new ParallelTransition(fade, slide).play();
    }

    private void animerBulle(Circle c, double fromY, double toY, double ms) {
        if (c == null)
            return;
        TranslateTransition t = new TranslateTransition(Duration.millis(ms), c);
        t.setFromY(fromY);
        t.setToY(toY);
        t.setCycleCount(Animation.INDEFINITE);
        t.setAutoReverse(true);
        t.setInterpolator(Interpolator.EASE_BOTH);
        t.play();
    }

    private void animerErreur() {
        if (formCard == null)
            return;
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), formCard);
        shake.setFromX(0);
        shake.setByX(8);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setInterpolator(Interpolator.LINEAR);
        shake.play();
    }

    private void animerSortie(Runnable onFinish) {
        if (rootPane == null) {
            onFinish.run();
            return;
        }
        FadeTransition fade = new FadeTransition(Duration.millis(280), rootPane);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> onFinish.run());
        fade.play();
    }

    // ─────────────────────────────────────────
    // CONNEXION
    // ─────────────────────────────────────────
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String mdp = passwordField.getText();
        hideError();

        if (email.isEmpty() || mdp.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            animerErreur();
            return;
        }
        if (!email.contains("@")) {
            showError("Adresse email invalide.");
            animerErreur();
            return;
        }

        loginBtn.setDisable(true);
        loginBtn.setText("Connexion en cours…");

        new Thread(() -> {
            Utilisateur user = utilisateurDAO.connecter(email, mdp);
            javafx.application.Platform.runLater(() -> {
                // Dans handleLogin(), après connexion réussie
                if (user != null) {
                    SessionManager.setUtilisateur(user);
                    journalDAO.log(user.getId(), JournalDAO.ACTION_CONNEXION, "Connexion");
                    animerSortie(() -> {
                        try {
                            if (SessionManager.isAdmin()) {
                                NavigationHelper.navigateTo(NavigationHelper.HOME); // ou ADMIN_DASHBOARD
                            } else {
                                NavigationHelper.navigateTo(NavigationHelper.HOME);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    // ─────────────────────────────────────────
    // MOT DE PASSE OUBLIÉ
    // ─────────────────────────────────────────
    @FXML
    private void handleMotDePasseOublie() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/project/view/RecuperationCompte.fxml"));
            Parent root = loader.load();
            Stage modal = new Stage();
            modal.setTitle("Recuperation de compte");
            modal.setScene(new Scene(root, 500, 750));
            modal.setResizable(false);
            modal.centerOnScreen();
            modal.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────
    // NAVIGATION
    // ─────────────────────────────────────────
    @FXML
    private void goToInscription(MouseEvent event) {
        animerSortie(() -> {
            try {
                NavigationHelper.navigateTo(NavigationHelper.INSCRIPTION);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ─────────────────────────────────────────
    // MESSAGES
    // ─────────────────────────────────────────
    private void showError(String msg) {
        errorLabel.setText(msg);
        errorBox.setVisible(true);
        errorBox.setManaged(true);
        errorBox.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(280), errorBox);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void hideError() {
        errorBox.setVisible(false);
        errorBox.setManaged(false);
    }
}