package com.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.project.model.JournalActivite;

public class JournalDAO {

    private Connection getConn() {
        return DatabaseConnection.getConnection();
    }

    public static final String ACTION_CONNEXION               = "CONNEXION";
    public static final String ACTION_DECONNEXION             = "DECONNEXION";
    public static final String ACTION_INSCRIPTION             = "INSCRIPTION";
    public static final String ACTION_AJOUT_TRANSACTION       = "AJOUT_TRANSACTION";
    public static final String ACTION_SUPPRESSION_TRANSACTION = "SUPPRESSION_TRANSACTION";
    public static final String ACTION_MODIFICATION_PROFIL     = "MODIFICATION_PROFIL";
    public static final String ACTION_EXPORT                  = "EXPORT";

    public void log(int utilisateurId, String action, String details) {
        String sql = "INSERT INTO journal_activite (utilisateur_id, action, details) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ps.setString(2, action);
            ps.setString(3, details);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Erreur log journal : " + e.getMessage());
        }
    }

    public List<JournalActivite> findByUtilisateur(int utilisateurId) {
        List<JournalActivite> liste = new ArrayList<>();
        String sql = "SELECT * FROM journal_activite WHERE utilisateur_id = ? ORDER BY date_action DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur findByUtilisateur journal : " + e.getMessage());
        }
        return liste;
    }

    public List<JournalActivite> findAll() {
        List<JournalActivite> liste = new ArrayList<>();
        String sql = "SELECT j.*, u.nom AS nom_utilisateur " +
                     "FROM journal_activite j " +
                     "LEFT JOIN utilisateur u ON j.utilisateur_id = u.id " +
                     "ORDER BY j.date_action DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur findAll journal : " + e.getMessage());
        }
        return liste;
    }

    public List<JournalActivite> findByAction(String action) {
        List<JournalActivite> liste = new ArrayList<>();
        String sql = "SELECT j.*, u.nom AS nom_utilisateur " +
                     "FROM journal_activite j " +
                     "LEFT JOIN utilisateur u ON j.utilisateur_id = u.id " +
                     "WHERE j.action = ? ORDER BY j.date_action DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, action);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur findByAction : " + e.getMessage());
        }
        return liste;
    }

    public boolean purgerAnciensLogs() {
        String sql = "DELETE FROM journal_activite " +
                     "WHERE date_action < DATE_SUB(CURRENT_DATE, INTERVAL 90 DAY)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            int rows = ps.executeUpdate();
            System.out.println("🧹 " + rows + " log(s) supprimé(s).");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Erreur purgerAnciensLogs : " + e.getMessage());
            return false;
        }
    }

    private JournalActivite mapResultSet(ResultSet rs) throws SQLException {
        JournalActivite j = new JournalActivite();
        j.setId(rs.getInt("id"));
        j.setUtilisateurId(rs.getInt("utilisateur_id"));
        j.setAction(rs.getString("action"));
        j.setDetails(rs.getString("details"));
        j.setDateAction(rs.getTimestamp("date_action").toLocalDateTime());
        j.setAdresseIp(rs.getString("adresse_ip"));
        return j;
    }
}