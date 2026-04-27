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

    public static Stage getStage() {
        return primaryStage;
    }

    public static Scene getScene() {
        return scene;
    }

    public static void main(String[] args) {
        launch();
    }
}

// j'aimerais comprendre comment les controleurs, les vues et les modèles
// interagissent dans cette application. pourrais tu m'expliquer ?Bien sûr !
// Dans une application JavaFX comme celle-ci, on suit généralement le modèle de
// conception MVC (Model-View-Controller) pour organiser le code de manière
// claire et maintenable. Voici comment les différentes parties interagissent :
// 1. **Modèle (Model)** : C'est la partie qui représente les données et la
// logique métier de l'application. Par exemple, dans votre application, vous
// avez des classes comme `Budget`, `Categorie`, et des DAO (Data Access Object)
// comme `BudgetDAO` et `CategorieDAO` qui gèrent l'accès aux données de la base
// de données. Le modèle ne connaît pas l'interface utilisateur ; il se
// concentre uniquement sur les données et les règles métier.
// 2. **Vue (View)** : C'est la partie qui représente l'interface utilisateur.
// Dans votre application, les vues sont définies dans des fichiers FXML (par
// exemple, `Login.fxml`, `Transaction.fxml`, etc.) qui décrivent la structure
// de l'interface utilisateur (boutons, champs de texte, etc.). La vue est
// responsable de l'affichage des données et de la capture des interactions de
// l'utilisateur, mais elle ne contient pas de logique métier.
// 3. **Contrôleur (Controller)** : C'est la partie qui fait le lien entre le
// modèle et la vue. Les contrôleurs (comme `BudgetModalController`,
// `TransactionController`, etc.) contiennent la logique pour répondre aux
// actions de l'utilisateur (par exemple, lorsqu'un utilisateur clique sur un
// bouton pour enregistrer un budget). Le contrôleur interagit avec le modèle
// pour récupérer ou mettre à jour les données, puis met à jour la vue en
// conséquence.
// Par exemple, dans `BudgetModalController`, lorsque l'utilisateur clique sur
// le bouton "Enregistrer", le contrôleur peut utiliser `BudgetDAO` pour
// enregistrer les données du budget dans la base de données, puis mettre à jour
// la vue pour refléter les changements.
// En résumé, le modèle gère les données, la vue gère l'affichage, et le
// contrôleur gère la logique de l'application et les interactions entre le
// modèle et la vue. Cette séparation permet de rendre le code plus organisé,
// plus facile à maintenir et à tester.