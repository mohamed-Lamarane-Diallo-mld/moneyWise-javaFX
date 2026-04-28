package com.project.utils;

import java.io.IOException;

import com.project.App;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NavigationHelper {

    public static void navigateTo(String fxmlPath) throws IOException {
        App.navigateTo(fxmlPath);
    }

    public static void openModal(String fxmlPath, String titre) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationHelper.class.getResource(fxmlPath + ".fxml"));
        Parent root = loader.load();
        Stage modal = new Stage();
        modal.setTitle(titre);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setScene(new Scene(root));
        modal.setResizable(false);
        modal.showAndWait();
    }

    public static final String LOGIN = "/com/project/view/Login";
    public static final String INSCRIPTION = "/com/project/view/Inscription";
    public static final String HOME = "/com/project/view/Home";
    public static final String TRANSACTION = "/com/project/view/Transaction";
    public static final String STATISTIQUE = "/com/project/view/Statistique";
    public static final String PROFIL = "/com/project/view/Profil";
    public static final String MODAL_TRANS = "/com/project/view/TransactionModal";
    public static final String ALERTES = "/com/project/view/Alertes";
    public static final String RECUPERATION = "/com/project/view/RecuperationCompte";
    public static final String ADMIN_UTILISATEURS = "/com/project/view/AdminUtilisateurs";
    public static final String ADMIN_CATEGORIES = "/com/project/view/AdminCategories";
    public static final String ADMIN_LOGS = "/com/project/view/AdminLogs";
    public static final String ADMIN_TRANSACTIONS = "/com/project/view/UsersTransactions";
    public static final String ADMIN_STATISTIQUES = "/com/project/view/UsersStatistique";  // NOUVEAU
}