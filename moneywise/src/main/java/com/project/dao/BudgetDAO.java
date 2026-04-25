package com.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.project.model.Budget;

public class BudgetDAO {

    private Connection getConn() {
        return DatabaseConnection.getConnection();
    }

    public boolean ajouter(Budget b) {
        String sql = "INSERT INTO budget (montant_max, mois, annee, est_actif, utilisateur_id, categorie_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, b.getMontantMax());
            ps.setInt(2, b.getMois());
            ps.setInt(3, b.getAnnee());
            ps.setBoolean(4, true);
            ps.setInt(5, b.getUtilisateurId());
            ps.setInt(6, b.getCategorieId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) b.setId(keys.getInt(1));
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Budget déjà existant pour cette catégorie ce mois-ci.");
            return false;
        } catch (SQLException e) {
            System.err.println(" Erreur ajouter budget : " + e.getMessage());
            return false;
        }
    }

    public boolean modifier(Budget b) {
        String sql = "UPDATE budget SET montant_max = ?, mois = ?, annee = ? " +
                     "WHERE id = ? AND utilisateur_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDouble(1, b.getMontantMax());
            ps.setInt(2, b.getMois());
            ps.setInt(3, b.getAnnee());
            ps.setInt(4, b.getId());
            ps.setInt(5, b.getUtilisateurId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modifier budget : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimer(int id, int utilisateurId) {
        String sql = "DELETE FROM budget WHERE id = ? AND utilisateur_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, utilisateurId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur supprimer budget : " + e.getMessage());
            return false;
        }
    }

    public boolean changerStatut(int id, boolean estActif) {
        String sql = "UPDATE budget SET est_actif = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setBoolean(1, estActif);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur changerStatut budget : " + e.getMessage());
            return false;
        }
    }

    public List<Budget> findByUtilisateur(int utilisateurId) {
        List<Budget> liste = new ArrayList<>();
        String sql = "SELECT b.*, c.nom AS categorie_nom " +
                     "FROM budget b " +
                     "LEFT JOIN categorie c ON b.categorie_id = c.id " +
                     "WHERE b.utilisateur_id = ? " +
                     "ORDER BY b.annee DESC, b.mois DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Erreur findByUtilisateur budget : " + e.getMessage());
        }
        return liste;
    }

    public List<Budget> findActifsMoisCourant(int utilisateurId) {
        List<Budget> liste = new ArrayList<>();
        String sql = "SELECT b.*, c.nom AS categorie_nom " +
                     "FROM budget b " +
                     "LEFT JOIN categorie c ON b.categorie_id = c.id " +
                     "WHERE b.utilisateur_id = ? AND b.est_actif = TRUE " +
                     "AND b.mois = MONTH(CURRENT_DATE) " +
                     "AND b.annee = YEAR(CURRENT_DATE)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Erreur findActifsMoisCourant : " + e.getMessage());
        }
        return liste;
    }

    public Budget findById(int id) {
        String sql = "SELECT b.*, c.nom AS categorie_nom " +
                     "FROM budget b " +
                     "LEFT JOIN categorie c ON b.categorie_id = c.id " +
                     "WHERE b.id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("Erreur findById budget : " + e.getMessage());
        }
        return null;
    }

    public double getConsommation(int budgetId) {
        String sql = "SELECT b.montant_max, " +
                     "COALESCE(SUM(t.montant), 0) AS total_depenses " +
                     "FROM budget b " +
                     "LEFT JOIN transaction t ON t.categorie_id = b.categorie_id " +
                     "  AND t.utilisateur_id = b.utilisateur_id " +
                     "  AND t.type = 'SORTIE' " +
                     "  AND MONTH(t.date_transaction) = b.mois " +
                     "  AND YEAR(t.date_transaction)  = b.annee " +
                     "WHERE b.id = ? GROUP BY b.montant_max";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, budgetId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double max      = rs.getDouble("montant_max");
                double depenses = rs.getDouble("total_depenses");
                if (max > 0) return (depenses / max) * 100;
            }
        } catch (SQLException e) {
            System.err.println("Erreur getConsommation : " + e.getMessage());
        }
        return 0.0;
    }

    public void verifierSeuils(int utilisateurId) {
        List<Budget> budgets = findActifsMoisCourant(utilisateurId);
        AlerteDAO alerteDAO  = new AlerteDAO();
        for (Budget b : budgets) {
            double pct = getConsommation(b.getId());
            if (pct == 100 || pct > 100) {
                if (!alerteDAO.existeDeja(b.getId(), "SEUIL_100"))
                    alerteDAO.creerAlerte(b.getId(), "SEUIL_100",
                        "Budget \"" + b.getCategorieNom() + "\" dépassé à 100% !");
            } else if (pct >= 80) {
                if (!alerteDAO.existeDeja(b.getId(), "SEUIL_80"))
                    alerteDAO.creerAlerte(b.getId(), "SEUIL_80",
                        "Budget \"" + b.getCategorieNom() + "\" atteint à 80%.");
            }
        }
    }

    private Budget mapResultSet(ResultSet rs) throws SQLException {
        Budget b = new Budget();
        b.setId(rs.getInt("id"));
        b.setMontantMax(rs.getDouble("montant_max"));
        b.setMois(rs.getInt("mois"));
        b.setAnnee(rs.getInt("annee"));
        b.setEstActif(rs.getBoolean("est_actif"));
        b.setUtilisateurId(rs.getInt("utilisateur_id"));
        b.setCategorieId(rs.getInt("categorie_id"));
        b.setCategorieNom(rs.getString("categorie_nom"));
        return b;
    }
}