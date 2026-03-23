package com.project.controller;

import com.project.dao.CategorieDAO;
import com.project.model.Categorie;
import com.project.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CategorieModalController implements Initializable {

    @FXML private Label     modalTitle;
    @FXML private TextField nomField;
    @FXML private TextField iconeField;
    @FXML private Label     errorLabel;
    @FXML private Button    saveBtn;

    private final CategorieDAO categorieDAO = new CategorieDAO();

    private Categorie             categorieAModifier = null;
    private TransactionController parentController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    public void setParentController(TransactionController ctrl) {
        this.parentController = ctrl;
    }

    public void setCategorie(Categorie c) {
        this.categorieAModifier = c;
        if (c == null) return;
        modalTitle.setText("Modifier la catégorie");
        saveBtn.setText("Enregistrer les modifications");
        nomField.setText(c.getNom());
        iconeField.setText(c.getIcone() != null ? c.getIcone() : "");
    }

    @FXML
    private void handleSave() {
        hideError();

        String nom = nomField.getText().trim();
        if (nom.isEmpty()) { showError("Le nom est obligatoire."); return; }
        if (nom.length() < 2) { showError("Nom trop court."); return; }

        // ✅ getUserId()
        int uid = SessionManager.getUserId();
        if (uid == -1) return;

        String icone = iconeField.getText().trim();
        saveBtn.setDisable(true);

        if (categorieAModifier == null) {
            // ✅ uid directement au lieu de user.getId()
            Categorie c = new Categorie(nom,
                icone.isEmpty() ? null : icone, false, uid);
            boolean ok = categorieDAO.ajouter(c);
            if (ok) {
                fermer();
                if (parentController != null) parentController.rafraichirCategories();
            } else {
                showError("Erreur lors de la création.");
                saveBtn.setDisable(false);
            }
        } else {
            categorieAModifier.setNom(nom);
            categorieAModifier.setIcone(icone.isEmpty() ? null : icone);
            boolean ok = categorieDAO.modifier(categorieAModifier);
            if (ok) {
                fermer();
                if (parentController != null) parentController.rafraichirCategories();
            } else {
                showError("Erreur lors de la modification.");
                saveBtn.setDisable(false);
            }
        }
    }

    @FXML private void handleCancel() { fermer(); }
    private void fermer() { ((Stage) saveBtn.getScene().getWindow()).close(); }
    private void showError(String msg) {
        errorLabel.setText(msg); errorLabel.setVisible(true); errorLabel.setManaged(true);
    }
    private void hideError() {
        errorLabel.setVisible(false); errorLabel.setManaged(false);
    }
}