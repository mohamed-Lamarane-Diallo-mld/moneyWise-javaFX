package com.project.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.project.dao.JournalDAO;
import com.project.dao.QuestionSecuriteDAO;
import com.project.dao.UtilisateurDAO;
import com.project.model.QuestionSecurite;
import com.project.model.Utilisateur;
import com.project.utils.NavigationHelper;
import com.project.utils.SessionManager;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class InscriptionController implements Initializable {

    // ── Layout ──
    @FXML private VBox   rootPane;
    @FXML private VBox   formCard;
    @FXML private Circle bubble1, bubble2, bubble3, bubble4;

    // ── Indicateurs ──
    @FXML private Label stepIndicator;
    @FXML private Label stepTitle;
    @FXML private Label stepSubtitle;

    // ── Étape 1 ──
    @FXML private VBox          step1Pane;
    @FXML private TextField     nomField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    // ── Étape 2 ──
    @FXML private VBox                       step2Pane;
    @FXML private ComboBox<QuestionSecurite> question1Combo;
    @FXML private TextField                  reponse1Field;
    @FXML private ComboBox<QuestionSecurite> question2Combo;
    @FXML private TextField                  reponse2Field;
    @FXML private ComboBox<QuestionSecurite> question3Combo;
    @FXML private TextField                  reponse3Field;

    // ── Boutons ──
    @FXML private Button btnSuivant;
    @FXML private Button btnPrecedent;
    @FXML private Button inscriptionBtn;

    // ── Messages ──
    @FXML private HBox  errorBox;
    @FXML private Label errorLabel;
    @FXML private HBox  successBox;
    @FXML private Label successLabel;

    private final UtilisateurDAO      utilisateurDAO      = new UtilisateurDAO();
    private final JournalDAO          journalDAO          = new JournalDAO();
    private final QuestionSecuriteDAO questionSecuriteDAO = new QuestionSecuriteDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chargerQuestions();
        afficherEtape(1);
        javafx.application.Platform.runLater(this::lancerAnimations);
    }

    // ─────────────────────────────────────────
    // QUESTIONS
    // ─────────────────────────────────────────
    private void chargerQuestions() {
        List<QuestionSecurite> questions = questionSecuriteDAO.findAll();
        question1Combo.setItems(FXCollections.observableArrayList(questions));
        question2Combo.setItems(FXCollections.observableArrayList(questions));
        question3Combo.setItems(FXCollections.observableArrayList(questions));
    }

    // ─────────────────────────────────────────
    // ÉTAPES
    // ─────────────────────────────────────────
    private void afficherEtape(int etape) {
        hideMessages();
        boolean e1 = (etape == 1);

        step1Pane.setVisible(e1);  step1Pane.setManaged(e1);
        step2Pane.setVisible(!e1); step2Pane.setManaged(!e1);
        btnSuivant.setVisible(e1);    btnSuivant.setManaged(e1);
        btnPrecedent.setVisible(!e1); btnPrecedent.setManaged(!e1);
        inscriptionBtn.setVisible(!e1); inscriptionBtn.setManaged(!e1);

        if (e1) {
            stepIndicator.setText("Étape 1 / 2");
            stepTitle.setText("Créer votre compte");
            stepSubtitle.setText("Renseignez vos informations personnelles");
        } else {
            stepIndicator.setText("Étape 2 / 2");
            stepTitle.setText("Questions de sécurité");
            stepSubtitle.setText("Choisissez 3 questions pour récupérer votre compte");
            step2Pane.setTranslateX(20); step2Pane.setOpacity(0);
            
            // Animation d'entrée pour étape 2
            step2Pane.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(300), step2Pane);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

    @FXML
    private void handleSuivant() {
        hideMessages();
        String nom     = nomField.getText().trim();
        String email   = emailField.getText().trim();
        String mdp     = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (nom.isEmpty() || email.isEmpty() || mdp.isEmpty() || confirm.isEmpty()) {
            showError("Veuillez remplir tous les champs."); animerErreur(); return;
        }
        if (nom.length() < 2) {
            showError("Le nom doit contenir au moins 2 caractères."); animerErreur(); return;
        }

        if (mdp.length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères."); animerErreur(); return;
        }

        if(
            !email.contains("@") ||
             !email.contains(".") || 
             email.startsWith("@") || 
             email.endsWith("@") || 
             email.startsWith(".") || 
             email.endsWith(".") ||
            !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") || // seule mail .gmail et .uadb.edu.sn
            email.contains("..") ||
            !email.matches("^[A-Za-z0-9._%+-]+@(gmail\\.com|uadb\\.edu\\.sn)$") // restreindre aux domaines autorisés
        ) {
            showError("Adresse email invalide."); animerErreur(); return;
        }

        if (mdp.length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères."); animerErreur(); return;
        }
        if (!mdp.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas.");
            confirmPasswordField.clear(); animerErreur(); return;
        }
        if (utilisateurDAO.emailExiste(email)) {
            showError("Cette adresse email est déjà utilisée."); animerErreur(); return;
        }
        afficherEtape(2);
    }

    @FXML private void handlePrecedent() { afficherEtape(1); }

    // ─────────────────────────────────────────
    // INSCRIPTION FINALE
    // ─────────────────────────────────────────
    @FXML
    private void handleInscription() {
        hideMessages();
        QuestionSecurite q1 = question1Combo.getValue();
        QuestionSecurite q2 = question2Combo.getValue();
        QuestionSecurite q3 = question3Combo.getValue();
        String r1 = reponse1Field.getText().trim();
        String r2 = reponse2Field.getText().trim();
        String r3 = reponse3Field.getText().trim();

        if (q1 == null || q2 == null || q3 == null) {
            showError("Veuillez choisir 3 questions de sécurité."); return;
        }
        if (r1.isEmpty() || r2.isEmpty() || r3.isEmpty()) {
            showError("Veuillez répondre à toutes les questions."); return;
        }
        if (q1.getId() == q2.getId() || q1.getId() == q3.getId()
                || q2.getId() == q3.getId()) {
            showError("Choisissez 3 questions différentes."); return;
        }

        inscriptionBtn.setDisable(true);
        inscriptionBtn.setText("Création en cours…");

        String nom   = nomField.getText().trim();
        String email = emailField.getText().trim();
        String mdp   = passwordField.getText();

        new Thread(() -> {
            boolean ok = utilisateurDAO.inscrire(new Utilisateur(nom, email, mdp));
            javafx.application.Platform.runLater(() -> {
                if (ok) {
                    Utilisateur user = utilisateurDAO.connecter(email, mdp);
                    if (user != null) {
                        questionSecuriteDAO.enregistrerReponses(
                            user.getId(),
                            new int[]{ q1.getId(), q2.getId(), q3.getId() },
                            new String[]{ r1, r2, r3 }
                        );
                        SessionManager.setUtilisateur(user);
                        journalDAO.log(user.getId(),
                            JournalDAO.ACTION_INSCRIPTION, "Nouvelle inscription");
                        animerSortie(() -> {
                            try { NavigationHelper.navigateTo(NavigationHelper.LOGIN); }
                            catch (Exception e) { e.printStackTrace(); }
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

    // ─────────────────────────────────────────
    // ANIMATIONS
    // ─────────────────────────────────────────
    private void lancerAnimations() {
        animerFormCard();
        animerBulle(bubble1, -40, 40, 3800);
        animerBulle(bubble2, -60, 60, 4500);
        animerBulle(bubble3, -30, 35, 3200);
        animerBulle(bubble4, -50, 45, 4000);
    }

    private void animerFormCard() {
        if (formCard == null) return;
        formCard.setOpacity(0); formCard.setTranslateY(20);
        FadeTransition fade = new FadeTransition(Duration.millis(600), formCard);
        fade.setFromValue(0); fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(600), formCard);
        slide.setFromY(20); slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        new ParallelTransition(fade, slide).play();
    }

    private void animerBulle(Circle c, double fromY, double toY, double ms) {
        if (c == null) return;
        TranslateTransition t = new TranslateTransition(Duration.millis(ms), c);
        t.setFromY(fromY); t.setToY(toY);
        t.setCycleCount(Animation.INDEFINITE); t.setAutoReverse(true);
        t.setInterpolator(Interpolator.EASE_BOTH);
        t.play();
    }

    private void animerErreur() {
        if (formCard == null) return;
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), formCard);
        shake.setFromX(0); shake.setByX(8);
        shake.setCycleCount(6); shake.setAutoReverse(true);
        shake.setInterpolator(Interpolator.LINEAR);
        shake.play();
    }

    private void animerSortie(Runnable onFinish) {
        if (rootPane == null) { onFinish.run(); return; }
        FadeTransition fade = new FadeTransition(Duration.millis(280), rootPane);
        fade.setFromValue(1); fade.setToValue(0);
        fade.setOnFinished(e -> onFinish.run());
        fade.play();
    }

    

    @FXML
    private void goToLogin(MouseEvent event) {
        animerSortie(() -> {
            try { NavigationHelper.navigateTo(NavigationHelper.LOGIN); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    // ─────────────────────────────────────────
    // MESSAGES
    // ─────────────────────────────────────────
    private void showError(String msg) {
        errorLabel.setText(msg);
        errorBox.setVisible(true); errorBox.setManaged(true);
        errorBox.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(280), errorBox);
        fade.setFromValue(0); fade.setToValue(1);
        fade.play();
    }

    private void hideMessages() {
        errorBox.setVisible(false);   errorBox.setManaged(false);
        successBox.setVisible(false); successBox.setManaged(false);
    }
}