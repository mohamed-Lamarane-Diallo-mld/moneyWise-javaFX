module com.project {

    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // MySQL
    requires java.sql;
    requires mysql.connector.j;

    // BCrypt
    requires jbcrypt;

    // PDF + Excel
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;

    // Ikonli (icônes FontAwesome)
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    // OBLIGATOIRE : ouvre les packages à JavaFX par réflexion
    opens com.project to javafx.fxml;
    opens com.project.controller to javafx.fxml;
    opens com.project.model to javafx.base;
    opens com.project.fonts;


    // Packages exportés
    exports com.project;
    exports com.project.controller;
    exports com.project.model;
    exports com.project.dao;
    exports com.project.utils;
    exports com.project.enums;

}