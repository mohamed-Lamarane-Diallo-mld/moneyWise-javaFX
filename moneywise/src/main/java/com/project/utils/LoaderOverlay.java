package com.project.utils;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class LoaderOverlay extends StackPane {
    
    private Label messageLabel;
    private ProgressIndicator progressIndicator;
    
    public LoaderOverlay() {
        setStyle("-fx-background-color: rgba(0,0,0,0.75);");
        setPrefSize(800, 600);
        
        // Conteneur principal du loader
        VBox loaderContainer = new VBox(20);
        loaderContainer.setAlignment(Pos.CENTER);
        
        // ProgressIndicator stylisé
        progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-progress-color: #4a90e2;");
        progressIndicator.setPrefSize(60, 60);
        
        // Label du message
        messageLabel = new Label("Connexion en cours...");
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        messageLabel.setEffect(new DropShadow(10, Color.BLACK));
        
        // Animation de rotation pour le cercle
        Circle loadingCircle = new Circle(35);
        loadingCircle.setFill(null);
        loadingCircle.setStroke(Color.web("#4a90e2"));
        loadingCircle.setStrokeWidth(3);
        loadingCircle.getStrokeDashArray().addAll(20d, 15d);
        
        RotateTransition rotate = new RotateTransition(Duration.seconds(2), loadingCircle);
        rotate.setByAngle(360);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.play();
        
        // Ajouter tous les éléments
        loaderContainer.getChildren().addAll(loadingCircle, progressIndicator, messageLabel);
        
        // Animation d'entrée
        setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        getChildren().add(loaderContainer);
    }
    
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
    
    public void updateProgress(double progress) {
        progressIndicator.setProgress(progress);
    }
    
    public void hideLoader() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), this);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            if (getParent() != null) {
                ((StackPane) getParent()).getChildren().remove(this);
            }
        });
        fadeOut.play();
    }
}