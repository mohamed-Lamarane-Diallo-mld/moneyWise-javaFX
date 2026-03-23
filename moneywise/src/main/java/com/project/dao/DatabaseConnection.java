package com.project.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/moneywise_db" +
        "?useSSL=false" +
        "&serverTimezone=UTC" +
        "&allowPublicKeyRetrieval=true" +
        "&connectTimeout=3000" +
        "&socketTimeout=10000" +
        "&cachePrepStmts=true" +
        "&prepStmtCacheSize=250" +
        "&useServerPrepStmts=true";
        // ✅ autoReconnect supprimé — causait 3 tentatives lentes

    private static final String USER     = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || !isConnectionValid()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion MySQL établie.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL introuvable : " + e.getMessage());
            connection = null;
        } catch (SQLException e) {
            System.err.println("❌ Erreur connexion MySQL : " + e.getMessage());
            connection = null; // ✅ Évite de retourner une connexion fermée
        }
        return connection;
    }

    private static boolean isConnectionValid() {
        try {
            return connection != null
                && !connection.isClosed()
                && connection.isValid(1);
        } catch (SQLException e) {
            return false;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                System.out.println("🔌 Connexion MySQL fermée.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur fermeture : " + e.getMessage());
        }
    }

    // ✅ Vérifier si la connexion est disponible
    public static boolean isAvailable() {
        return getConnection() != null;
    }
}