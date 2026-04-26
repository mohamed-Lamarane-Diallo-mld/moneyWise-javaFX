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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SidebarController implements Initializable {

    // Navigation items standards
    @FXML private HBox navHome;
    @FXML private HBox navTransaction;
    @FXML private HBox navStatistique;
    @FXML private HBox navProfil;
    @FXML private HBox navAlertes;
    
    // Navigation items admin
    @FXML private Label adminSectionLabel;
    @FXML private HBox navAdminUtilisateurs;
    @FXML private HBox navAdminCategories;
    @FXML private HBox navAdminLogs;
    
    // User info
    @FXML private Label alerteBadge;
    @FXML private Label avatarLabel;
    // @FXML private Label userNameLabel;
    // @FXML private Label userEmailLabel;

    private final AlerteDAO alerteDAO = new AlerteDAO();
    private final JournalDAO journalDAO = new JournalDAO();
    private boolean isAdmin = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isAdmin = SessionManager.isAdmin();
        
        chargerInfosUser();
        chargerBadgeAlertes();
        afficherMenusSelonRole();
    }
    
    /**
     * Affiche ou masque les menus admin selon le rôle de l'utilisateur
     */
    private void afficherMenusSelonRole() {
        boolean visible = isAdmin;
        
        adminSectionLabel.setVisible(visible);
        adminSectionLabel.setManaged(visible);
        navAdminUtilisateurs.setVisible(visible);
        navAdminUtilisateurs.setManaged(visible);
        navAdminCategories.setVisible(visible);
        navAdminCategories.setManaged(visible);
        navAdminLogs.setVisible(visible);
        navAdminLogs.setManaged(visible);
    }

    private void chargerInfosUser() {
        if (SessionManager.getUserId() == -1) {
            return;
        }
        Utilisateur user = SessionManager.getUtilisateur();

        // userNameLabel.setText(user.getNom());
        // userEmailLabel.setText(user.getEmail());

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
        // Reset tous les menus standards
        navHome.getStyleClass().removeAll("nav-item-active");
        navTransaction.getStyleClass().removeAll("nav-item-active");
        navStatistique.getStyleClass().removeAll("nav-item-active");
        navProfil.getStyleClass().removeAll("nav-item-active");
        navAlertes.getStyleClass().removeAll("nav-item-active");
        
        // Reset menus admin
        navAdminUtilisateurs.getStyleClass().removeAll("nav-item-active");
        navAdminCategories.getStyleClass().removeAll("nav-item-active");
        navAdminLogs.getStyleClass().removeAll("nav-item-active");

        switch (page) {
            case "home" -> navHome.getStyleClass().add("nav-item-active");
            case "transaction" -> navTransaction.getStyleClass().add("nav-item-active");
            case "statistique" -> navStatistique.getStyleClass().add("nav-item-active");
            case "profil" -> navProfil.getStyleClass().add("nav-item-active");
            case "alertes" -> navAlertes.getStyleClass().add("nav-item-active");
            case "adminUtilisateurs" -> navAdminUtilisateurs.getStyleClass().add("nav-item-active");
            case "adminCategories" -> navAdminCategories.getStyleClass().add("nav-item-active");
            case "adminLogs" -> navAdminLogs.getStyleClass().add("nav-item-active");
        }
    }

    public void setSidebarVisible(boolean visible) {
        VBox root = (VBox) navHome.getParent().getParent();
        root.setVisible(visible);
        root.setManaged(visible);
    }

    // Navigation standards
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
        } catch (Exception ex) {
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

    // Navigation admin
    @FXML
    private void goToAdminUtilisateurs(MouseEvent e) {
        try {
            NavigationHelper.navigateTo(NavigationHelper.ADMIN_UTILISATEURS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void goToAdminCategories(MouseEvent e) {
        try {
            NavigationHelper.navigateTo(NavigationHelper.ADMIN_CATEGORIES);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void goToAdminLogs(MouseEvent e) {
        try {
            NavigationHelper.navigateTo(NavigationHelper.ADMIN_LOGS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de déconnexion");
        confirmation.setHeaderText("Êtes-vous sûr de vouloir vous déconnecter ?");
        if (confirmation.showAndWait().orElse(null) != ButtonType.OK) {
            return;
        }

        int uid = SessionManager.getUserId();
        if (uid != -1) {
            journalDAO.log(uid, JournalDAO.ACTION_DECONNEXION, "Déconnexion");
        }
        SessionManager.clear();
        try {
            NavigationHelper.navigateTo(NavigationHelper.LOGIN);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}