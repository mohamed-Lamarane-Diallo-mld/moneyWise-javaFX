package com.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.project.model.Categorie;

public class CategorieDAO {

    private Connection getConn() {
        return DatabaseConnection.getConnection();
    }

    public boolean ajouter(Categorie c) {
        String sql = "INSERT INTO categorie (nom, icone, est_systeme, utilisateur_id) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNom());
            ps.setString(2, c.getIcone());
            ps.setBoolean(3, false);
            ps.setInt(4, c.getUtilisateurId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) c.setId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajouter catégorie : " + e.getMessage());
            return false;
        }
    }

    public boolean modifier(Categorie c) {
        String sql = "UPDATE categorie SET nom = ?, icone = ? " +
                     "WHERE id = ? AND est_systeme = FALSE";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setString(2, c.getIcone());
            ps.setInt(3, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur modifier catégorie : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM categorie WHERE id = ? AND est_systeme = FALSE";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0)
                System.err.println("⚠️ Suppression impossible : catégorie système ou introuvable.");
            return rows > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("⚠️ Catégorie utilisée par des transactions.");
            return false;
        } catch (SQLException e) {
            System.err.println("❌ Erreur supprimer catégorie : " + e.getMessage());
            return false;
        }
    }

    public List<Categorie> findByUtilisateur(int utilisateurId) {
        List<Categorie> liste = new ArrayList<>();
        String sql = "SELECT * FROM categorie " +
                     "WHERE utilisateur_id IS NULL OR utilisateur_id = ? " +
                     "ORDER BY est_systeme DESC, nom ASC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur findByUtilisateur : " + e.getMessage());
        }
        return liste;
    }

    public List<Categorie> findSysteme() {
        List<Categorie> liste = new ArrayList<>();
        String sql = "SELECT * FROM categorie WHERE est_systeme = TRUE ORDER BY nom ASC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur findSysteme : " + e.getMessage());
        }
        return liste;
    }

    public Categorie findById(int id) {
        String sql = "SELECT * FROM categorie WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("❌ Erreur findById catégorie : " + e.getMessage());
        }
        return null;
    }

    private Categorie mapResultSet(ResultSet rs) throws SQLException {
        Categorie c = new Categorie();
        c.setId(rs.getInt("id"));
        c.setNom(rs.getString("nom"));
        c.setIcone(rs.getString("icone"));
        c.setEstSysteme(rs.getBoolean("est_systeme"));
        Object uid = rs.getObject("utilisateur_id");
        c.setUtilisateurId(uid != null ? (Integer) uid : null);
        return c;
    }
}