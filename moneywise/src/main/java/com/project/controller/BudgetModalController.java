package com.project.controller;

import com.project.dao.BudgetDAO;
import com.project.dao.CategorieDAO;
import com.project.model.Budget;
import com.project.model.Categorie;
import com.project.utils.DateHelper;
import com.project.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BudgetModalController implements Initializable {

    @FXML private Label               modalTitle;
    @FXML private ComboBox<Categorie> categorieCombo;
    @FXML private TextField           montantField;
    @FXML private ComboBox<String>    moisCombo;
    @FXML private TextField           anneeField;
    @FXML private Label               errorLabel;
    @FXML private Button              saveBtn;

    private final BudgetDAO    budgetDAO    = new BudgetDAO();
    private final CategorieDAO categorieDAO = new CategorieDAO();

    private Budget              budgetAModifier = null;
    private TransactionController parentController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chargerCategories();
        chargerMois();
        // ✅ DateHelper
        anneeField.setText(String.valueOf(DateHelper.anneeCourante()));
        moisCombo.getSelectionModel().select(DateHelper.moisCourant() - 1);
    }

    public void setParentController(TransactionController ctrl) {
        this.parentController = ctrl;
    }

    public void setBudget(Budget b) {
        this.budgetAModifier = b;
        if (b == null) return;

        modalTitle.setText("Modifier le budget");
        saveBtn.setText("Enregistrer les modifications");
        montantField.setText(String.valueOf(b.getMontantMax()));
        anneeField.setText(String.valueOf(b.getAnnee()));
        moisCombo.getSelectionModel().select(b.getMois() - 1);

        categorieCombo.getItems().stream()
            .filter(c -> c != null && c.getId() == b.getCategorieId())
            .findFirst()
            .ifPresent(c -> categorieCombo.getSelectionModel().select(c));
    }

    // ─────────────────────────────────────────
    private void chargerCategories() {
        // ✅ getUserId()
        int uid = SessionManager.getUserId();
        if (uid == -1) return;

        List<Categorie> cats = categorieDAO.findByUtilisateur(uid);
        categorieCombo.setItems(FXCollections.observableArrayList(cats));

        categorieCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });
        categorieCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Choisir une catégorie" : item.getNom());
            }
        });
    }

    private void chargerMois() {
        moisCombo.setItems(FXCollections.observableArrayList(
            "Janvier","Février","Mars","Avril","Mai","Juin",
            "Juillet","Août","Septembre","Octobre","Novembre","Décembre"
        ));
    }

    // ─────────────────────────────────────────
    @FXML
    private void handleSave() {
        hideError();

        if (categorieCombo.getValue() == null) {
            showError("Veuillez choisir une catégorie."); return;
        }

        double montant;
        try {
            montant = Double.parseDouble(
                montantField.getText().trim().replace(",", "."));
            if (montant <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Montant invalide."); return;
        }

        int annee;
        try {
            annee = Integer.parseInt(anneeField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Année invalide."); return;
        }

        int mois = moisCombo.getSelectionModel().getSelectedIndex() + 1;

        // ✅ getUserId()
        int uid = SessionManager.getUserId();
        if (uid == -1) return;

        saveBtn.setDisable(true);

        if (budgetAModifier == null) {
            Budget b = new Budget(montant, mois, annee,
                uid, categorieCombo.getValue().getId());
            boolean ok = budgetDAO.ajouter(b);
            if (ok) {
                fermer();
                if (parentController != null) parentController.rafraichirBudgets();
            } else {
                showError("Erreur : budget déjà existant pour cette période.");
                saveBtn.setDisable(false);
            }
        } else {
            budgetAModifier.setMontantMax(montant);
            budgetAModifier.setMois(mois);
            budgetAModifier.setAnnee(annee);
            budgetAModifier.setCategorieId(categorieCombo.getValue().getId());
            boolean ok = budgetDAO.modifier(budgetAModifier);
            if (ok) {
                fermer();
                if (parentController != null) parentController.rafraichirBudgets();
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