package com.project.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.project.enums.FormatExport;
import com.project.model.Export;

public class ExportDAO {

    private Connection getConn() {
        return DatabaseConnection.getConnection();
    }

    public boolean enregistrer(Export e) {
        String sql = "INSERT INTO export (format, chemin_fichier, periode_debut, periode_fin, utilisateur_id) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getFormat().name());
            ps.setString(2, e.getCheminFichier());
            ps.setDate(3, Date.valueOf(e.getPeriodeDebut()));
            ps.setDate(4, Date.valueOf(e.getPeriodeFin()));
            ps.setInt(5, e.getUtilisateurId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) e.setId(keys.getInt(1));
            return true;
        } catch (SQLException ex) {
            System.err.println("Erreur enregistrer export : " + ex.getMessage());
            return false;
        }
    }

    public List<Export> findByUtilisateur(int utilisateurId) {
        List<Export> liste = new ArrayList<>();
        String sql = "SELECT * FROM export WHERE utilisateur_id = ? ORDER BY date_export DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Erreur findByUtilisateur export : " + e.getMessage());
        }
        return liste;
    }

    public boolean supprimer(int id, int utilisateurId) {
        String sql = "DELETE FROM export WHERE id = ? AND utilisateur_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, utilisateurId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur supprimer export : " + e.getMessage());
            return false;
        }
    }

    private Export mapResultSet(ResultSet rs) throws SQLException {
        Export e = new Export();
        e.setId(rs.getInt("id"));
        e.setFormat(FormatExport.valueOf(rs.getString("format")));
        e.setDateExport(rs.getTimestamp("date_export").toLocalDateTime());
        e.setCheminFichier(rs.getString("chemin_fichier"));
        e.setPeriodeDebut(rs.getDate("periode_debut").toLocalDate());
        e.setPeriodeFin(rs.getDate("periode_fin").toLocalDate());
        e.setUtilisateurId(rs.getInt("utilisateur_id"));
        return e;
    }
}