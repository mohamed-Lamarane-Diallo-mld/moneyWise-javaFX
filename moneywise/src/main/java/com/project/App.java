package com.project;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.project.dao.DatabaseConnection;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene  scene;
    private static Stage  primaryStage;

    private static final Map<String, Parent> viewCache = new HashMap<>();

    public static final double MIN_WIDTH   = 900;
    public static final double MIN_HEIGHT  = 600;
    public static final double INIT_WIDTH  = 1200;
    public static final double INIT_HEIGHT = 750;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Connexion DB en arrière-plan au démarrage
        new Thread(DatabaseConnection::getConnection).start();

        Parent root = loadFXML("/com/project/view/Login");
        scene = new Scene(root, INIT_WIDTH, INIT_HEIGHT);
        scene.getStylesheets().add(
            getClass().getResource("/com/project/style/global.css")
                      .toExternalForm());

        stage.setTitle("MoneyWise");
        stage.setScene(scene);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.centerOnScreen();
        stage.show();
    }

    public static void navigateTo(String fxmlPath) throws IOException {
        double w = primaryStage.getWidth();
        double h = primaryStage.getHeight();

        // Pages auth → toujours rechargées, pas de cache
        if (!shouldCache(fxmlPath)) {
            Parent root = loadFXML(fxmlPath);
            scene.setRoot(root);
            primaryStage.setWidth(w);
            primaryStage.setHeight(h);
            return;
        }

        // Pages principales → chargement en arrière-plan
        // mais SANS bloquer l'UI avec un spinner
        new Thread(() -> {
            try {
                Parent root = loadFXML(fxmlPath);
                Platform.runLater(() -> {
                    scene.setRoot(root);
                    primaryStage.setWidth(w);
                    primaryStage.setHeight(h);
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    try {
                        scene.setRoot(loadFXML("/com/project/view/Login"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        }).start();
    }

    // Pages qui profitent du chargement async
    private static boolean shouldCache(String path) {
        return path.contains("Home")
            || path.contains("Transaction")
            || path.contains("Statistique")
            || path.contains("Profil")
            || path.contains("Alertes");
    }

    private static Parent loadFXML(String path) throws IOException {
        var url = App.class.getResource(path + ".fxml");
        if (url == null) {
            throw new IOException("❌ FXML introuvable : " + path + ".fxml");
        }
        return new FXMLLoader(url).load();
    }

    public static void clearViewCache() {
        viewCache.clear();
    }

    public static Stage getStage() { return primaryStage; }
    public static Scene getScene() { return scene; }

    public static void main(String[] args) { launch(); }
}