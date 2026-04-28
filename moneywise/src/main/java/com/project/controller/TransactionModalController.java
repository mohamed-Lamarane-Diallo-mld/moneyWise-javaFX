package com.project.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import com.project.dao.BudgetDAO;
import com.project.dao.CategorieDAO;
import com.project.dao.JournalDAO;
import com.project.dao.TransactionDAO;
import com.project.enums.TypeTransaction;
import com.project.model.Categorie;
import com.project.model.Transaction;
import com.project.model.Utilisateur;
import com.project.utils.AlerteHelper;
import com.project.utils.SessionManager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TransactionModalController implements Initializable {

    @FXML private Label modalTitle;
    @FXML private Button btnEntree;
    @FXML private Button btnSortie;
    @FXML private TextField montantField;
    @FXML private ComboBox<Categorie> categorieCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField descriptionField;
    @FXML private Label errorLabel;
    @FXML private Button saveBtn;

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CategorieDAO categorieDAO = new CategorieDAO();
    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final JournalDAO journalDAO = new JournalDAO();

    private Transaction transactionAModifier = null;
    private TransactionController parentController;
    private UsersTransactionsController adminParentController;
    private TypeTransaction typeSelectionne = TypeTransaction.SORTIE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chargerCategories();
        datePicker.setValue(LocalDate.now());
        selectSortie();
    }

    public void setParentController(TransactionController ctrl) { this.parentController = ctrl; }
    public void setParentControllerForAdmin(UsersTransactionsController ctrl) { this.adminParentController = ctrl; }

    public void setTransaction(Transaction t) {
        this.transactionAModifier = t;
        if (t == null) return;
        modalTitle.setText("Modifier la transaction");
        saveBtn.setText("Enregistrer les modifications");
        montantField.setText(String.valueOf(t.getMontant()));
        datePicker.setValue(t.getDateTransaction());
        descriptionField.setText(t.getDescription() != null ? t.getDescription() : "");
        if (t.getType() == TypeTransaction.ENTREE) selectEntree();
        else selectSortie();
        categorieCombo.getItems().stream()
                .filter(c -> c != null && c.getId() == t.getCategorieId())
                .findFirst()
                .ifPresent(c -> categorieCombo.getSelectionModel().select(c));
    }

    @FXML private void selectEntree() {
        typeSelectionne = TypeTransaction.ENTREE;
        btnEntree.getStyleClass().add("toggle-entree-active");
        btnSortie.getStyleClass().remove("toggle-sortie-active");
    }

    @FXML private void selectSortie() {
        typeSelectionne = TypeTransaction.SORTIE;
        btnSortie.getStyleClass().add("toggle-sortie-active");
        btnEntree.getStyleClass().remove("toggle-entree-active");
    }

    private void chargerCategories() {
        Utilisateur user = SessionManager.getUtilisateur();
        if (user == null) return;
        List<Categorie> cats = categorieDAO.findByUtilisateur(user.getId());
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

    @FXML
    private void handleSave() {
        hideError();
        String montantStr = montantField.getText().trim();
        if (montantStr.isEmpty()) { showError("Le montant est obligatoire."); return; }
        double montant;
        try { montant = Double.parseDouble(montantStr.replace(",", ".")); if (montant <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { showError("Montant invalide. Entrez un nombre positif."); return; }
        if (categorieCombo.getValue() == null) { showError("Veuillez choisir une catégorie."); return; }
        if (datePicker.getValue() == null) { showError("Veuillez choisir une date."); return; }

        Utilisateur user = SessionManager.getUtilisateur();
        if (user == null) return;

        LocalDate dateTx = datePicker.getValue();
        LocalDate aujourdhui = LocalDate.now();
        boolean estDansMoisFutur = dateTx.getYear() > aujourdhui.getYear() || (dateTx.getYear() == aujourdhui.getYear() && dateTx.getMonthValue() > aujourdhui.getMonthValue());
        if (estDansMoisFutur) { showError("Impossible d'enregistrer une transaction sur un mois futur."); return; }

        if (typeSelectionne == TypeTransaction.SORTIE) {
            double soldeActuel = transactionDAO.getSoldeTotal(user.getId());
            double soldeDisponible = soldeActuel;
            if (transactionAModifier != null && transactionAModifier.getType() == TypeTransaction.SORTIE) {
                soldeDisponible += transactionAModifier.getMontant();
            }
            if (montant > soldeDisponible) {
                showError(String.format("Solde insuffisant ! Disponible : %,.0f FCFA — Demandé : %,.0f FCFA", soldeDisponible, montant));
                AlerteHelper.soldeInsuffisant(soldeDisponible, montant);
                return;
            }
        }

        saveBtn.setDisable(true);

        if (transactionAModifier == null) {
            Transaction t = new Transaction(montant, typeSelectionne, datePicker.getValue(), descriptionField.getText().trim(), user.getId(), categorieCombo.getValue().getId());
            boolean ok = transactionDAO.ajouter(t);
            if (ok) {
                journalDAO.log(user.getId(), JournalDAO.ACTION_AJOUT_TRANSACTION, typeSelectionne.name() + " : " + montant + " FCFA");
                AlerteHelper.verifierEtNotifier(user);
                fermerModal();
                if (parentController != null) {
                    parentController.chargerTransactions();
                    parentController.rafraichirBudgets();
                    parentController.rafraichirBadgeAlertes();
                } else if (adminParentController != null) {
                    adminParentController.rafraichirDonnees();
                }
            } else { showError("Erreur lors de l'enregistrement."); saveBtn.setDisable(false); }
        } else {
            transactionAModifier.setMontant(montant);
            transactionAModifier.setType(typeSelectionne);
            transactionAModifier.setDateTransaction(datePicker.getValue());
            transactionAModifier.setDescription(descriptionField.getText().trim());
            transactionAModifier.setCategorieId(categorieCombo.getValue().getId());
            boolean ok = transactionDAO.modifier(transactionAModifier);
            if (ok) {
                AlerteHelper.verifierEtNotifier(user);
                fermerModal();
                if (parentController != null) {
                    parentController.chargerTransactions();
                    parentController.rafraichirBudgets();
                    parentController.rafraichirBadgeAlertes();
                } else if (adminParentController != null) {
                    adminParentController.rafraichirDonnees();
                }
            } else { showError("Erreur lors de la modification."); saveBtn.setDisable(false); }
        }
    }

    @FXML private void handleCancel() { fermerModal(); }
    private void fermerModal() { ((Stage) saveBtn.getScene().getWindow()).close(); }
    private void showError(String msg) { errorLabel.setText(msg); errorLabel.setVisible(true); errorLabel.setManaged(true); }
    private void hideError() { errorLabel.setVisible(false); errorLabel.setManaged(false); }
}