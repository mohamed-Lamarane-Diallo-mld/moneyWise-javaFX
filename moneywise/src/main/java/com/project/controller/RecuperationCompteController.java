package com.project.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.project.dao.QuestionSecuriteDAO;
import com.project.dao.UtilisateurDAO;
import com.project.model.QuestionSecurite;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RecuperationCompteController implements Initializable {

    @FXML
    private VBox rootPane;
    @FXML
    private VBox formCard;
    @FXML
    private Circle bubble1, bubble2, bubble3, bubble4;

    @FXML
    private Label stepIndicator;
    @FXML
    private Label stepTitle;
    @FXML
    private Label stepSubtitle;

    // Étape 1
    @FXML
    private VBox step1Pane;
    @FXML
    private TextField emailField;

    // Étape 2
    @FXML
    private VBox step2Pane;
    @FXML
    private Label question1Label;
    @FXML
    private TextField reponse1Field;
    @FXML
    private Label question2Label;
    @FXML
    private TextField reponse2Field;
    @FXML
    private Label question3Label;
    @FXML
    private TextField reponse3Field;

    // Étape 3
    @FXML
    private VBox step3Pane;
    @FXML
    private PasswordField nouveauMdpField;
    @FXML
    private PasswordField confirmMdpField;

    @FXML
    private Button btnAction;
    @FXML
    private Button btnRetour;

    @FXML
    private HBox errorBox;
    @FXML
    private Label errorLabel;
    @FXML
    private HBox successBox;
    @FXML
    private Label successLabel;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final QuestionSecuriteDAO questionDAO = new QuestionSecuriteDAO();

    private int etape = 1;
    private int utilisateurId = -1;
    private String emailValide = null;
    private List<QuestionSecurite> questions;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        afficherEtape(1);
        javafx.application.Platform.runLater(this::lancerAnimations);

        // Détecter quand l'email change pour charger les questions
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.contains("@")) {
                prechargerQuestions(newVal);
            }
        });
    }

    private void prechargerQuestions(String email) {
        int uid = utilisateurDAO.findIdByEmail(email);
        if (uid != -1) {
            utilisateurId = uid;
            questions = questionDAO.findByUtilisateur(uid);
            if (questions != null && questions.size() >= 3) {
                question1Label.setText(questions.get(0).getQuestion());
                question2Label.setText(questions.get(1).getQuestion());
                question3Label.setText(questions.get(2).getQuestion());
            }
        }
    }

    private void lancerAnimations() {
        animerFormCard();
        animerBulle(bubble1, -40, 40, 3800);
        animerBulle(bubble2, -60, 60, 4500);
        animerBulle(bubble3, -30, 35, 3200);
        animerBulle(bubble4, -50, 45, 4000);
    }

    private void animerFormCard() {
        if (formCard == null) {
            return;
        }
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
        if (c == null) {
            return;
        }
        TranslateTransition t = new TranslateTransition(Duration.millis(ms), c);
        t.setFromY(fromY);
        t.setToY(toY);
        t.setCycleCount(Animation.INDEFINITE);
        t.setAutoReverse(true);
        t.setInterpolator(Interpolator.EASE_IN);
        t.setInterpolator(Interpolator.EASE_OUT);
        t.play();
    }

    private void animerErreur() {
        if (formCard == null) {
            return;
        }
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), formCard);
        shake.setFromX(0);
        shake.setByX(8);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setInterpolator(Interpolator.LINEAR);
        shake.play();
    }

    private void animerTransitionEtape(VBox nouvellePane) {
        nouvellePane.setOpacity(0);
        nouvellePane.setTranslateX(20);
        FadeTransition fade = new FadeTransition(Duration.millis(300), nouvellePane);
        fade.setFromValue(0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), nouvellePane);
        slide.setFromX(20);
        slide.setToX(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        new ParallelTransition(fade, slide).play();
    }

    private void afficherEtape(int e) {
        etape = e;
        hideMessages();

        // Cacher toutes les étapes
        step1Pane.setVisible(false);
        step1Pane.setManaged(false);
        step2Pane.setVisible(false);
        step2Pane.setManaged(false);
        step3Pane.setVisible(false);
        step3Pane.setManaged(false);

        // Afficher l'étape correspondante
        switch (e) {
            case 1:
                step1Pane.setVisible(true);
                step1Pane.setManaged(true);
                stepIndicator.setText("Étape 1 / 3");
                stepTitle.setText("Récupérer votre compte");
                stepSubtitle.setText("Entrez votre adresse email");
                btnAction.setText("Continuer →");
                btnRetour.setVisible(false);
                btnRetour.setManaged(false);
                animerTransitionEtape(step1Pane);
                break;
            case 2:
                step2Pane.setVisible(true);
                step2Pane.setManaged(true);
                stepIndicator.setText("Étape 2 / 3");
                stepTitle.setText("Vérification de sécurité");
                stepSubtitle.setText("Répondez aux questions ci-dessous");
                btnAction.setText("Vérifier →");
                btnRetour.setVisible(true);
                btnRetour.setManaged(true);
                animerTransitionEtape(step2Pane);
                break;
            case 3:
                step3Pane.setVisible(true);
                step3Pane.setManaged(true);
                stepIndicator.setText("Étape 3 / 3");
                stepTitle.setText("Nouveau mot de passe");
                stepSubtitle.setText("Choisissez un mot de passe sécurisé");
                btnAction.setText("Réinitialiser →");
                btnRetour.setVisible(true);
                btnRetour.setManaged(true);
                animerTransitionEtape(step3Pane);
                break;
        }
    }

    @FXML
    private void handleAction() {
        hideMessages();
        switch (etape) {
            case 1:
                verifierEmail();
                break;
            case 2:
                verifierReponses();
                break;
            case 3:
                reinitialiserMotDePasse();
                break;
        }
    }

    @FXML
    private void handleRetour() {
        hideMessages();
        if (etape == 2) {
            afficherEtape(1);
        } else if (etape == 3) {
            afficherEtape(2);
        }
    }

    // ÉTAPE 1 : Vérifier l'email
    private void verifierEmail() {
        String email = emailField.getText().trim();
        System.out.println("🔍 Recherche de l'email : " + email);

        if (email.isEmpty() || !email.contains("@")) {
            showError("Entrez une adresse email valide.");
            animerErreur();
            return;
        }

        // Utiliser la bonne méthode
        utilisateurId = questionDAO.findUtilisateurIdByEmail(email);
        System.out.println("📊 ID trouvé : " + utilisateurId);

        if (utilisateurId == -1) {
            showError("Aucun compte trouvé pour cet email.");
            animerErreur();
            return;
        }

        // Charger les questions
        questions = questionDAO.findByUtilisateur(utilisateurId);
        System.out.println("Questions chargées : " + (questions != null ? questions.size() : "null"));

        if (questions == null || questions.size() < 3) {
            showError("Ce compte n'a pas de questions de sécurité configurées (besoin de "
                    + (questions == null ? 0 : questions.size()) + "/3).");
            animerErreur();
            return;
        }

        // Afficher les questions
        question1Label.setText(questions.get(0).getQuestion());
        question2Label.setText(questions.get(1).getQuestion());
        question3Label.setText(questions.get(2).getQuestion());

        System.out.println("Questions affichées");

        emailValide = email;
        afficherEtape(2);
    }

    // ÉTAPE 2 : Vérifier les réponses
    private void verifierReponses() {
        String r1 = reponse1Field.getText().trim();
        String r2 = reponse2Field.getText().trim();
        String r3 = reponse3Field.getText().trim();

        if (r1.isEmpty() || r2.isEmpty() || r3.isEmpty()) {
            showError("Répondez à toutes les questions.");
            animerErreur();
            return;
        }

        boolean ok1 = questionDAO.verifierReponse(utilisateurId, questions.get(0).getId(), r1);
        boolean ok2 = questionDAO.verifierReponse(utilisateurId, questions.get(1).getId(), r2);
        boolean ok3 = questionDAO.verifierReponse(utilisateurId, questions.get(2).getId(), r3);

        if (!ok1 || !ok2 || !ok3) {
            showError("Une ou plusieurs réponses sont incorrectes.");
            animerErreur();
            reponse1Field.clear();
            reponse2Field.clear();
            reponse3Field.clear();
            return;
        }

        afficherEtape(3);
    }

    // ÉTAPE 3 : Réinitialiser le mot de passe
    private void reinitialiserMotDePasse() {
        String mdp = nouveauMdpField.getText();
        String confirm = confirmMdpField.getText();

        if (mdp.isEmpty() || confirm.isEmpty()) {
            showError("Remplissez tous les champs.");
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
            animerErreur();
            confirmMdpField.clear();
            return;
        }

        btnAction.setDisable(true);
        btnAction.setText("Réinitialisation...");

        new Thread(() -> {
            boolean ok = utilisateurDAO.mettreAJourMotDePasse(utilisateurId, mdp);
            javafx.application.Platform.runLater(() -> {
                if (ok) {
                    showSuccess("Mot de passe réinitialisé avec succès !");
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                        }
                        javafx.application.Platform.runLater(this::fermer);
                    }).start();
                } else {
                    showError("Erreur lors de la réinitialisation.");
                    animerErreur();
                    btnAction.setDisable(false);
                    btnAction.setText("Réinitialiser →");
                }
            });
        }).start();
    }

    @FXML
    private void handleAnnuler() {
        fermer();
    }

    private void fermer() {
        Stage stage = (Stage) btnAction.getScene().getWindow();
        stage.close();
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
