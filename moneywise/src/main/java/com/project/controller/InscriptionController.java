package com.project.controller;

import com.project.dao.JournalDAO;
import com.project.dao.UtilisateurDAO;
import com.project.model.Utilisateur;
import com.project.utils.NavigationHelper;
import com.project.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class InscriptionController {

    @FXML private TextField     nomField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label         errorLabel;
    @FXML private Label         successLabel;
    @FXML private Button        inscriptionBtn;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final JournalDAO     journalDAO     = new JournalDAO();

    @FXML
    private void handleInscription() {
        String nom     = nomField.getText().trim();
        String email   = emailField.getText().trim();
        String mdp     = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        hideMessages();

        if (nom.isEmpty() || email.isEmpty() || mdp.isEmpty() || confirm.isEmpty()) {
            showError("Veuillez remplir tous les champs."); return;
        }
        if (nom.length() < 2) {
            showError("Le nom doit contenir au moins 2 caractères."); return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showError("Adresse email invalide."); return;
        }
        if (mdp.length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères."); return;
        }
        if (!mdp.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas.");
            confirmPasswordField.clear(); return;
        }
        if (utilisateurDAO.emailExiste(email)) {
            showError("Cette adresse email est déjà utilisée."); return;
        }

        inscriptionBtn.setDisable(true);
        inscriptionBtn.setText("Création...");

        // ✅ En arrière-plan
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
                        try {
                            NavigationHelper.navigateTo(NavigationHelper.HOME);
                        } catch (Exception e) {
                            showSuccess("Compte créé ! Connectez-vous.");
                        }
                    }
                } else {
                    showError("Erreur lors de la création. Réessayez.");
                    inscriptionBtn.setDisable(false);
                    inscriptionBtn.setText("Créer mon compte");
                }
            });
        }).start();
    }

    @FXML
    private void goToLogin(MouseEvent event) {
        try { NavigationHelper.navigateTo(NavigationHelper.LOGIN); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void showError(String msg) {
        errorLabel.setText(msg); errorLabel.setVisible(true); errorLabel.setManaged(true);
    }
    private void showSuccess(String msg) {
        successLabel.setText(msg); successLabel.setVisible(true); successLabel.setManaged(true);
    }
    private void hideMessages() {
        errorLabel.setVisible(false); errorLabel.setManaged(false);
        successLabel.setVisible(false); successLabel.setManaged(false);
    }
}