package com.project.controller;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

import com.project.dao.CategorieDAO;
import com.project.dao.JournalDAO;
import com.project.dao.TransactionDAO;
import com.project.dao.UtilisateurDAO;
import com.project.model.Utilisateur;
import com.project.utils.DateHelper;
import com.project.utils.ResponsiveHelper;
import com.project.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProfilController implements Initializable {

    @FXML
    private Label headerDate;
    @FXML
    private Label headerUser;
    @FXML
    private Label avatarBig;
    @FXML
    private Label profilNomBig;
    @FXML
    private Label profilEmailBig;
    @FXML
    private Label badgeAdmin;
    @FXML
    private Label badgeInscription;
    @FXML
    private TextField nomField;
    @FXML
    private TextField emailField;
    @FXML
    private Label infoMessage;
    @FXML
    private PasswordField ancienMdpField;
    @FXML
    private PasswordField nouveauMdpField;
    @FXML
    private PasswordField confirmMdpField;
    @FXML
    private Label mdpMessage;
    @FXML
    private Label statNbTransactions;
    @FXML
    private Label statSolde;
    @FXML
    private Label statNbCategories;
    @FXML
    private Label statDepensesMois;
    @FXML
    private SidebarController sidebarController;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CategorieDAO categorieDAO = new CategorieDAO();
    private final JournalDAO journalDAO = new JournalDAO();
    private static final NumberFormat NF
            = NumberFormat.getNumberInstance(Locale.FRENCH);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chargerHeader();
        chargerProfil();
        chargerStats();
        if (sidebarController != null) {
            sidebarController.setActiveItem("profil");
        }
        ResponsiveHelper.bind(this::onResize);
    }

    private void onResize() {
        if (sidebarController != null) {
            sidebarController.setSidebarVisible(
                    ResponsiveHelper.getWidth() >= ResponsiveHelper.BP_SMALL);
        }
    }

    private void chargerHeader() {
        headerDate.setText(DateHelper.formaterComplet(LocalDate.now()));
        int uid = SessionManager.getUserId();
        if (uid != -1) {
            String prenom = SessionManager.getUtilisateur().getNom().split(" ")[0];
            headerUser.setText("Bonjour, " + prenom);
            if (badgeInscription != null) {
                Utilisateur user = SessionManager.getUtilisateur();
                if (user.getDateInscription() != null) {
                    badgeInscription.setText(
                            "Membre depuis le " + DateHelper.formaterLong(
                                    user.getDateInscription()));
                }
            }
        }
    }

    private void chargerProfil() {
        int uid = SessionManager.getUserId();
        if (uid == -1) {
            return;
        }
        Utilisateur user = SessionManager.getUtilisateur();

        profilNomBig.setText(user.getNom());
        profilEmailBig.setText(user.getEmail());

        String[] parts = user.getNom().trim().split(" ");
        String initiales = parts.length >= 2
                ? "" + parts[0].charAt(0) + parts[1].charAt(0)
                : "" + parts[0].charAt(0);
        avatarBig.setText(initiales.toUpperCase());

        if (user.isEstAdmin()) {
            badgeAdmin.setVisible(true);
            badgeAdmin.setManaged(true);
        }

        if (user.getDateInscription() != null) 
        {
            badgeInscription.setText("Membre depuis le "
                    + DateHelper.formaterLong(user.getDateInscription()));
        }

        nomField.setText(user.getNom());
        emailField.setText(user.getEmail());
    }

    private void chargerStats() {
        int uid = SessionManager.getUserId();
        if (uid == -1) {
            return;
        }

        var transactions = transactionDAO.findByUtilisateur(uid);
        statNbTransactions.setText(String.valueOf(transactions.size()));

        double solde = transactionDAO.getSoldeTotal(uid);
        statSolde.setText(NF.format(solde) + " FCFA");
        if (solde < 0) {
            statSolde.setStyle(
                    "-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:#E74C3C;");
        }

        statNbCategories.setText(
                String.valueOf(categorieDAO.findByUtilisateur(uid).size()));
        statDepensesMois.setText(
                NF.format(transactionDAO.getTotalSortiesMois(uid)) + " FCFA");
    }

    @FXML
    private void handleModifierInfos() {
        hideMessages();
        String nom = nomField.getText().trim();
        String email = emailField.getText().trim();

        if (nom.isEmpty() || email.isEmpty()) {
            showInfoMessage("Veuillez remplir tous les champs.", false);
            return;
        }
        if (nom.length() < 2) {
            showInfoMessage("Nom trop court.", false);
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showInfoMessage("Email invalide.", false);
            return;
        }

        int uid = SessionManager.getUserId();
        if (uid == -1) {
            return;
        }
        Utilisateur user = SessionManager.getUtilisateur();

        if (!email.equals(user.getEmail()) && utilisateurDAO.emailExiste(email)) {
            showInfoMessage("Cet email est déjà utilisé.", false);
            return;
        }

        boolean ok = utilisateurDAO.modifierProfil(uid, nom, email);
        if (ok) {
            SessionManager.rafraichir();
            chargerProfil();
            journalDAO.log(uid, JournalDAO.ACTION_MODIFICATION_PROFIL, "Profil mis à jour");
            showInfoMessage("✓ Profil mis à jour avec succès !", true);
        } else {
            showInfoMessage("Erreur lors de la mise à jour.", false);
        }
    }

    @FXML
    private void handleChangerMdp() {
        hideMessages();
        String ancien = ancienMdpField.getText();
        String nouveau = nouveauMdpField.getText();
        String confirm = confirmMdpField.getText();

        if (ancien.isEmpty() || nouveau.isEmpty() || confirm.isEmpty()) {
            showMdpMessage("Veuillez remplir tous les champs.", false);
            return;
        }
        if (nouveau.length() < 6) {
            showMdpMessage("Minimum 6 caractères.", false);
            return;
        }
        if (!nouveau.equals(confirm)) {
            showMdpMessage("Les mots de passe ne correspondent pas.", false);
            confirmMdpField.clear();
            return;
        }

        int uid = SessionManager.getUserId();
        if (uid == -1) {
            return;
        }

        boolean ok = utilisateurDAO.modifierMotDePasse(uid, ancien, nouveau);
        if (ok) {
            ancienMdpField.clear();
            nouveauMdpField.clear();
            confirmMdpField.clear();
            showMdpMessage("✓ Mot de passe changé avec succès !", true);
        } else {
            showMdpMessage("Ancien mot de passe incorrect.", false);
        }
    }

    private void showMdpMessage(String msg, boolean succes) {
        mdpMessage.setText(msg);
        mdpMessage.setStyle(succes
                ? "-fx-text-fill:#2ECC71; -fx-font-size:13px; -fx-font-weight:bold;"
                : "-fx-text-fill:#E74C3C; -fx-font-size:13px;");
        mdpMessage.setVisible(true);
        mdpMessage.setManaged(true);
    }

    private void showInfoMessage(String msg, boolean succes) {
        infoMessage.setText(msg);
        infoMessage.setStyle(succes
                ? "-fx-text-fill:#2ECC71; -fx-font-size:13px; -fx-font-weight:bold;"
                : "-fx-text-fill:#E74C3C; -fx-font-size:13px;");
        infoMessage.setVisible(true);
        infoMessage.setManaged(true);
    }

    private void hideMessages() {
        infoMessage.setVisible(false);
        infoMessage.setManaged(false);
        mdpMessage.setVisible(false);
        mdpMessage.setManaged(false);
    }
}
