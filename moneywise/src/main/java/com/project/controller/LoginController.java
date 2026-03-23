package com.project.controller;

import com.project.dao.JournalDAO;
import com.project.dao.UtilisateurDAO;
import com.project.model.Utilisateur;
import com.project.utils.NavigationHelper;
import com.project.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class LoginController {

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;
    @FXML private Button        loginBtn;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final JournalDAO     journalDAO     = new JournalDAO();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String mdp   = passwordField.getText();

        if (email.isEmpty() || mdp.isEmpty()) {
            showError("Veuillez remplir tous les champs."); return;
        }
        if (!email.contains("@")) {
            showError("Adresse email invalide."); return;
        }

        loginBtn.setDisable(true);
        loginBtn.setText("Connexion...");

        // ✅ Connexion en arrière-plan pour ne pas bloquer l'UI
        new Thread(() -> {
            Utilisateur user = utilisateurDAO.connecter(email, mdp);
            javafx.application.Platform.runLater(() -> {
                if (user != null) {
                    SessionManager.setUtilisateur(user);
                    journalDAO.log(user.getId(),
                        JournalDAO.ACTION_CONNEXION, "Connexion");
                    try {
                        NavigationHelper.navigateTo(NavigationHelper.HOME);
                    } catch (Exception e) {
                        showError("Erreur chargement dashboard.");
                        e.printStackTrace();
                    }
                } else {
                    showError("Email ou mot de passe incorrect.");
                    passwordField.clear();
                    loginBtn.setDisable(false);
                    loginBtn.setText("Se connecter");
                }
            });
        }).start();
    }

    @FXML
    private void goToInscription(MouseEvent event) {
        try { NavigationHelper.navigateTo(NavigationHelper.INSCRIPTION); }
        catch (Exception e) { showError("Erreur de navigation."); }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}