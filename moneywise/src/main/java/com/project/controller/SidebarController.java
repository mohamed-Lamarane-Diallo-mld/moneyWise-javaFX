package com.project.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.project.dao.AlerteDAO;
import com.project.dao.JournalDAO;
import com.project.model.Utilisateur;
import com.project.utils.NavigationHelper;
import com.project.utils.SessionManager;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SidebarController implements Initializable {

    @FXML private HBox navHome;
    @FXML private HBox navTransaction;
    @FXML private HBox navStatistique;
    @FXML private HBox navProfil;
    @FXML private HBox navAlertes;
    
    @FXML private Label navHomeIcon;
    @FXML private Label navTransactionIcon;
    @FXML private Label navStatistiqueIcon;
    @FXML private Label navProfilIcon;
    @FXML private Label navAlertesIcon;
    
    @FXML private Label adminSectionLabel;
    @FXML private HBox navAdminUtilisateurs;
    @FXML private HBox navAdminCategories;
    @FXML private HBox navAdminLogs;
    @FXML private HBox navAdminTransactions;
    @FXML private HBox navAdminStatistiques;  // NOUVEAU
    
    @FXML private Label navAdminUtilisateursIcon;
    @FXML private Label navAdminCategoriesIcon;
    @FXML private Label navAdminLogsIcon;
    @FXML private Label navAdminTransactionsIcon;
    @FXML private Label navAdminStatistiquesIcon;  // NOUVEAU
    
    @FXML private Label alerteBadge;
    @FXML private Label avatarLabel;

    private final AlerteDAO alerteDAO = new AlerteDAO();
    private final JournalDAO journalDAO = new JournalDAO();
    private boolean isAdmin = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isAdmin = SessionManager.isAdmin();
        ajouterIcones();
        chargerInfosUser();
        chargerBadgeAlertes();
        afficherMenusSelonRole();
    }
    
    private void ajouterIcones() {
        setIcon(navHomeIcon, FontAwesomeSolid.HOME);
        setIcon(navTransactionIcon, FontAwesomeSolid.EXCHANGE_ALT);
        setIcon(navStatistiqueIcon, FontAwesomeSolid.CHART_LINE);
        setIcon(navProfilIcon, FontAwesomeSolid.USER_CIRCLE);
        setIcon(navAlertesIcon, FontAwesomeSolid.BELL);
        setIcon(navAdminUtilisateursIcon, FontAwesomeSolid.USERS);
        setIcon(navAdminCategoriesIcon, FontAwesomeSolid.TAGS);
        setIcon(navAdminLogsIcon, FontAwesomeSolid.HISTORY);
        setIcon(navAdminTransactionsIcon, FontAwesomeSolid.CHART_LINE);
        setIcon(navAdminStatistiquesIcon, FontAwesomeSolid.CHART_PIE);  // NOUVEAU
    }
    
    private void setIcon(Label label, FontAwesomeSolid icon) {
        if (label != null) {
            FontIcon fontIcon = new FontIcon(icon);
            fontIcon.setIconSize(18);
            fontIcon.getStyleClass().add("nav-item-icon");
            label.setGraphic(fontIcon);
            label.setText("");
        }
    }
    
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
        navAdminTransactions.setVisible(visible);
        navAdminTransactions.setManaged(visible);
        navAdminStatistiques.setVisible(visible);  // NOUVEAU
        navAdminStatistiques.setManaged(visible);  // NOUVEAU
    }

    private void chargerInfosUser() {
        if (SessionManager.getUserId() == -1) return;
        Utilisateur user = SessionManager.getUtilisateur();
        String[] parts = user.getNom().trim().split(" ");
        String initiales = parts.length >= 2 ? "" + parts[0].charAt(0) + parts[1].charAt(0) : "" + parts[0].charAt(0);
        avatarLabel.setText(initiales.toUpperCase());
    }

    public void chargerBadgeAlertes() {
        int uid = SessionManager.getUserId();
        if (uid == -1) return;
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
        navAdminUtilisateurs.getStyleClass().removeAll("nav-item-active");
        navAdminCategories.getStyleClass().removeAll("nav-item-active");
        navAdminLogs.getStyleClass().removeAll("nav-item-active");
        navAdminTransactions.getStyleClass().removeAll("nav-item-active");
        navAdminStatistiques.getStyleClass().removeAll("nav-item-active");  // NOUVEAU

        switch (page) {
            case "home": navHome.getStyleClass().add("nav-item-active"); break;
            case "transaction": navTransaction.getStyleClass().add("nav-item-active"); break;
            case "statistique": navStatistique.getStyleClass().add("nav-item-active"); break;
            case "profil": navProfil.getStyleClass().add("nav-item-active"); break;
            case "alertes": navAlertes.getStyleClass().add("nav-item-active"); break;
            case "adminUtilisateurs": navAdminUtilisateurs.getStyleClass().add("nav-item-active"); break;
            case "adminCategories": navAdminCategories.getStyleClass().add("nav-item-active"); break;
            case "adminLogs": navAdminLogs.getStyleClass().add("nav-item-active"); break;
            case "adminTransactions": navAdminTransactions.getStyleClass().add("nav-item-active"); break;
            case "adminStatistiques": navAdminStatistiques.getStyleClass().add("nav-item-active"); break;  // NOUVEAU
        }
    }

    public void setSidebarVisible(boolean visible) {
        VBox root = (VBox) navHome.getParent().getParent();
        root.setVisible(visible);
        root.setManaged(visible);
    }

    @FXML private void goToHome(MouseEvent e) { try { NavigationHelper.navigateTo(NavigationHelper.HOME); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML private void goToTransaction(MouseEvent e) { try { NavigationHelper.navigateTo(NavigationHelper.TRANSACTION); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML private void goToStatistique(MouseEvent e) { try { NavigationHelper.navigateTo(NavigationHelper.STATISTIQUE); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML private void goToProfil(MouseEvent e) { try { NavigationHelper.navigateTo(NavigationHelper.PROFIL); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML private void goToAlertes(MouseEvent e) { try { NavigationHelper.navigateTo(NavigationHelper.ALERTES); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML private void goToAdminUtilisateurs(MouseEvent e) { try { NavigationHelper.navigateTo(NavigationHelper.ADMIN_UTILISATEURS); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML private void goToAdminCategories(MouseEvent e) { try { NavigationHelper.navigateTo(NavigationHelper.ADMIN_CATEGORIES); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML private void goToAdminLogs(MouseEvent e) { try { NavigationHelper.navigateTo(NavigationHelper.ADMIN_LOGS); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML private void goToAdminTransactions(MouseEvent e) { try { NavigationHelper.navigateTo(NavigationHelper.ADMIN_TRANSACTIONS); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML private void goToAdminStatistiques(MouseEvent e) { try { NavigationHelper.navigateTo(NavigationHelper.ADMIN_STATISTIQUES); } catch (Exception ex) { ex.printStackTrace(); } }  // NOUVEAU

    @FXML
    private void handleLogout() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de déconnexion");
        confirmation.setHeaderText("Êtes-vous sûr de vouloir vous déconnecter ?");
        if (confirmation.showAndWait().orElse(null) != ButtonType.OK) return;
        int uid = SessionManager.getUserId();
        if (uid != -1) journalDAO.log(uid, JournalDAO.ACTION_DECONNEXION, "Déconnexion");
        SessionManager.clear();
        try { NavigationHelper.navigateTo(NavigationHelper.LOGIN); } catch (Exception ex) { ex.printStackTrace(); }
    }
}