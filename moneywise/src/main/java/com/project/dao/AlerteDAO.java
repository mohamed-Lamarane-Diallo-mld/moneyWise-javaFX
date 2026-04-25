package com.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.project.enums.TypeAlerte;
import com.project.model.Alerte;

public class AlerteDAO {

    private Connection getConn() {
        return DatabaseConnection.getConnection();
    }

    public boolean creerAlerte(int budgetId, String typeAlerte, String message) {
        String sql = "INSERT INTO alerte (message, type_alerte, budget_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, message);
            ps.setString(2, typeAlerte);
            ps.setInt(3, budgetId);
            ps.executeUpdate();
            System.out.println("Alerte créée : " + message);
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur creerAlerte : " + e.getMessage());
            return false;
        }
    }

    public boolean existeDeja(int budgetId, String typeAlerte) {
        String sql = "SELECT id FROM alerte " +
                     "WHERE budget_id = ? AND type_alerte = ? " +
                     "AND MONTH(date_alerte) = MONTH(CURRENT_DATE) " +
                     "AND YEAR(date_alerte)  = YEAR(CURRENT_DATE)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, budgetId);
            ps.setString(2, typeAlerte);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Erreur existeDeja : " + e.getMessage());
        }
        return false;
    }

    public List<Alerte> findNonLues(int utilisateurId) {
        List<Alerte> liste = new ArrayList<>();
        String sql = "SELECT a.* FROM alerte a " +
                     "JOIN budget b ON a.budget_id = b.id " +
                     "WHERE b.utilisateur_id = ? AND a.est_lue = FALSE " +
                     "ORDER BY a.date_alerte DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Erreur findNonLues : " + e.getMessage());
        }
        return liste;
    }

    public List<Alerte> findAll(int utilisateurId) {
        List<Alerte> liste = new ArrayList<>();
        String sql = "SELECT a.* FROM alerte a " +
                     "JOIN budget b ON a.budget_id = b.id " +
                     "WHERE b.utilisateur_id = ? " +
                     "ORDER BY a.date_alerte DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Erreur findAll alertes : " + e.getMessage());
        }
        return liste;
    }

    public int countNonLues(int utilisateurId) {
        // On compte seulement les alertes critiques (100% et plus)
        String sql = "SELECT COUNT(*) AS total FROM alerte a " +
                     "JOIN budget b ON a.budget_id = b.id " +
                     "WHERE b.utilisateur_id = ? AND a.est_lue = FALSE " +
                     "AND a.type_alerte = 'SEUIL_100'";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.err.println("Erreur countNonLues : " + e.getMessage());
        }
        return 0;
    }

    public boolean marquerLue(int id) {
        String sql = "UPDATE alerte SET est_lue = TRUE WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur marquerLue : " + e.getMessage());
            return false;
        }
    }

    public boolean marquerToutesLues(int utilisateurId) {
        String sql = "UPDATE alerte a " +
                     "JOIN budget b ON a.budget_id = b.id " +
                     "SET a.est_lue = TRUE " +
                     "WHERE b.utilisateur_id = ? AND a.est_lue = FALSE";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur marquerToutesLues : " + e.getMessage());
            return false;
        }
    }

    private Alerte mapResultSet(ResultSet rs) throws SQLException {
        Alerte a = new Alerte();
        a.setId(rs.getInt("id"));
        a.setMessage(rs.getString("message"));
        a.setDateAlerte(rs.getTimestamp("date_alerte").toLocalDateTime());
        a.setTypeAlerte(TypeAlerte.valueOf(rs.getString("type_alerte")));
        a.setEstLue(rs.getBoolean("est_lue"));
        a.setBudgetId(rs.getInt("budget_id"));
        return a;
    }
}