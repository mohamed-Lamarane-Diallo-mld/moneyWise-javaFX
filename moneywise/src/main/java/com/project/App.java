package com.project;

import java.io.IOException;

import com.project.dao.DatabaseConnection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    public static final double MIN_WIDTH = 900;
    public static final double MIN_HEIGHT = 600;
    public static final double INIT_WIDTH = 1200;
    public static final double INIT_HEIGHT = 750;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
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
        
        Parent root = loadFXML(fxmlPath);
        scene.setRoot(root);
        primaryStage.setWidth(w);
        primaryStage.setHeight(h);
    }

    private static Parent loadFXML(String path) throws IOException {
        var url = App.class.getResource(path + ".fxml");
        if (url == null) {
            throw new IOException("FXML introuvable : " + path + ".fxml");
        }
        FXMLLoader loader = new FXMLLoader(url);
        return loader.load();
    }

    public static void clearViewCache() {
        // Rien à faire pour l'instant
    }

    public static Stage getStage() { return primaryStage; }
    public static Scene getScene() { return scene; }

    public static void main(String[] args) { launch(); }
}