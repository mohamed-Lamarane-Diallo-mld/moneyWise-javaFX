package com.project.utils;

import com.project.App;

import javafx.beans.value.ChangeListener;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ResponsiveHelper {

    // Breakpoints
    public static final double BP_SMALL  = 900;
    public static final double BP_MEDIUM = 1100;
    public static final double BP_LARGE  = 1400;

    // ─────────────────────────────────────────
    // ÉCOUTER LES CHANGEMENTS DE TAILLE
    // Appelé dans chaque controller (initialize)
    // ─────────────────────────────────────────
    public static void bind(Runnable onResize) {
        Stage stage = App.getStage();
        if (stage == null) return;

        ChangeListener<Number> listener = (obs, oldVal, newVal) -> onResize.run();
        stage.widthProperty().addListener(listener);
        stage.heightProperty().addListener(listener);

        // Appel initial
        onResize.run();
    }

    // ─────────────────────────────────────────
    // TAILLE ACTUELLE DE LA FENÊTRE
    // ─────────────────────────────────────────
    public static double getWidth() {
        Stage stage = App.getStage();
        return stage != null ? stage.getWidth() : App.INIT_WIDTH;
    }

    public static double getHeight() {
        Stage stage = App.getStage();
        return stage != null ? stage.getHeight() : App.INIT_HEIGHT;
    }

    // ─────────────────────────────────────────
    // BREAKPOINTS
    // ─────────────────────────────────────────
    public static boolean isSmall()  { return getWidth() < BP_SMALL;  }
    public static boolean isMedium() { return getWidth() < BP_MEDIUM; }
    public static boolean isLarge()  { return getWidth() >= BP_LARGE; }

    // ─────────────────────────────────────────
    // HELPERS LAYOUT
    // ─────────────────────────────────────────

    // Faire grandir un Region dans un HBox
    public static void hgrow(Region... nodes) {
        for (Region node : nodes) {
            HBox.setHgrow(node, Priority.ALWAYS);
            node.setMaxWidth(Double.MAX_VALUE);
        }
    }

    // Faire grandir un Region dans un VBox
    public static void vgrow(Region... nodes) {
        for (Region node : nodes) {
            VBox.setVgrow(node, Priority.ALWAYS);
            node.setMaxHeight(Double.MAX_VALUE);
        }
    }

    // Largeur du contenu principal (fenêtre - sidebar)
    public static double getContentWidth() {
        return getWidth() - 240; // 240 = largeur sidebar
    }

    // Largeur d'une KPI card selon le nombre de cards
    public static double getKpiCardWidth(int nbCards) {
        double available = getContentWidth() - 64 - (20 * (nbCards - 1));
        return Math.max(160, available / nbCards);
    }
}