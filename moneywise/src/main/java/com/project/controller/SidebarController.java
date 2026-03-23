package com.project.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.project.dao.AlerteDAO;
import com.project.dao.JournalDAO;
import com.project.model.Utilisateur;
import com.project.utils.NavigationHelper;
import com.project.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SidebarController implements Initializable {

    @FXML
    private HBox navHome;
    @FXML
    private HBox navTransaction;
    @FXML
    private HBox navStatistique;
    @FXML
    private HBox navProfil;
    @FXML
    private HBox navAlertes;
    @FXML
    private Label alerteBadge;
    @FXML
    private Label avatarLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userEmailLabel;

    private final AlerteDAO alerteDAO = new AlerteDAO();
    private final JournalDAO journalDAO = new JournalDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chargerInfosUser();
        chargerBadgeAlertes();
    }

    private void chargerInfosUser() {
        // ✅ getUserId() pour vérification rapide
        if (SessionManager.getUserId() == -1) {
            return;
        }
        Utilisateur user = SessionManager.getUtilisateur();

        userNameLabel.setText(user.getNom());
        userEmailLabel.setText(user.getEmail());

        String[] parts = user.getNom().trim().split(" ");
        String initiales = parts.length >= 2
                ? "" + parts[0].charAt(0) + parts[1].charAt(0)
                : "" + parts[0].charAt(0);
        avatarLabel.setText(initiales.toUpperCase());
    }

    public void chargerBadgeAlertes() {
        int uid = SessionManager.getUserId();
        if (uid == -1) {
            return;
        }

        // ✅ En arrière-plan pour ne pas ralentir la sidebar
        new Thread(() -> {
            int count = alerteDAO.countNonLues(uid);
            javafx.application.Platform.runLater(() -> {
                if (count > 0) {
                    alerteBadge.setText(String.valueOf(count));
                    alerteBadge.setVisible(true);
                    alerteBadge.setManaged(true);
                } else {
                    alerteBadge.setVisible(false);
                    alerteBadge.setManaged(false);
                }
            });
        }).start();
    }

    public void setActiveItem(String page) {
        navHome.getStyleClass().removeAll("nav-item-active");
        navTransaction.getStyleClass().removeAll("nav-item-active");
        navStatistique.getStyleClass().removeAll("nav-item-active");
        navProfil.getStyleClass().removeAll("nav-item-active");
        navAlertes.getStyleClass().removeAll("nav-item-active");

        switch (page) {
            case "home" ->
                navHome.getStyleClass().add("nav-item-active");
            case "transaction" ->
                navTransaction.getStyleClass().add("nav-item-active");
            case "statistique" ->
                navStatistique.getStyleClass().add("nav-item-active");
            case "profil" ->
                navProfil.getStyleClass().add("nav-item-active");
            case "alertes" ->
                navAlertes.getStyleClass().add("nav-item-active");
        }
    }

    public void setSidebarVisible(boolean visible) {
        VBox root = (VBox) navHome.getParent().getParent();
        root.setVisible(visible);
        root.setManaged(visible);
    }

    @FXML
    private void goToHome(MouseEvent e) {
        try {
            NavigationHelper.navigateTo(NavigationHelper.HOME);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void goToTransaction(MouseEvent e) {
        try {
            NavigationHelper.navigateTo(NavigationHelper.TRANSACTION);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void goToStatistique(MouseEvent e) {
        try {
            NavigationHelper.navigateTo(NavigationHelper.STATISTIQUE);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void goToProfil(MouseEvent e) {
        try {
            NavigationHelper.navigateTo(NavigationHelper.PROFIL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void goToAlertes(MouseEvent e) {
        try {
            NavigationHelper.navigateTo(NavigationHelper.ALERTES);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        int uid = SessionManager.getUserId();
        if (uid != -1) {
            journalDAO.log(uid, JournalDAO.ACTION_DECONNEXION, "Déconnexion");
        }
        // SessionManager.clear() appelle App.clearViewCache() automatiquement
        SessionManager.clear();
        try {
            NavigationHelper.navigateTo(NavigationHelper.LOGIN);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
