package com.project.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.project.model.Utilisateur;

public class UtilisateurDAO {

    private Connection getConn() {
        return DatabaseConnection.getConnection();
    }

    public boolean inscrire(Utilisateur u) {
        String sql = "INSERT INTO utilisateur (nom, email, mot_de_passe, date_inscription, est_actif, est_admin) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            String hash = BCrypt.hashpw(u.getMotDePasse(), BCrypt.gensalt(12));
            ps.setString(1, u.getNom());
            ps.setString(2, u.getEmail());
            ps.setString(3, hash);
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            ps.setBoolean(5, true);
            ps.setBoolean(6, false);
            ps.executeUpdate();
            System.out.println("Utilisateur inscrit : " + u.getEmail());
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Email déjà existant : " + u.getEmail());
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur inscription : " + e.getMessage());
            return false;
        }
    }

    public Utilisateur connecter(String email, String motDePasse) {
        String sql = "SELECT * FROM utilisateur WHERE email = ? AND est_actif = TRUE";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (BCrypt.checkpw(motDePasse, rs.getString("mot_de_passe"))) {
                    return mapResultSet(rs);
                } else {
                    System.err.println("Mot de passe incorrect.");
                }
            } else {
                System.err.println("Email introuvable ou compte inactif.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur connexion : " + e.getMessage());
        }
        return null;
    }

    public Utilisateur findById(int id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("Erreur findById : " + e.getMessage());
        }
        return null;
    }

    public boolean emailExiste(String email) {
        String sql = "SELECT id FROM utilisateur WHERE email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("Erreur emailExiste : " + e.getMessage());
        }
        return false;
    }

    public boolean modifierProfil(int id, String nom, String email) {
        String sql = "UPDATE utilisateur SET nom = ?, email = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setString(2, email);
            ps.setInt(3, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur modifierProfil : " + e.getMessage());
            return false;
        }
    }

    public boolean modifierMotDePasse(int id, String ancienMdp, String nouveauMdp) {
        Utilisateur u = findById(id);
        if (u == null || !BCrypt.checkpw(ancienMdp, u.getMotDePasse())) {
            System.err.println("-> Ancien mot de passe incorrect.");
            return false;
        }
        String sql = "UPDATE utilisateur SET mot_de_passe = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, BCrypt.hashpw(nouveauMdp, BCrypt.gensalt(12)));
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("-> Erreur modifierMotDePasse : " + e.getMessage());
            return false;
        }
    }

    public List<Utilisateur> findAll() {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur ORDER BY date_inscription DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur findAll : " + e.getMessage());
        }
        return liste;
    }

    public boolean changerStatut(int id, boolean estActif) {
        String sql = "UPDATE utilisateur SET est_actif = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setBoolean(1, estActif);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Erreur changerStatut : " + e.getMessage());
            return false;
        }
    }

    private Utilisateur mapResultSet(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getInt("id"));
        u.setNom(rs.getString("nom"));
        u.setEmail(rs.getString("email"));
        u.setMotDePasse(rs.getString("mot_de_passe"));
        u.setDateInscription(rs.getDate("date_inscription").toLocalDate());
        u.setEstActif(rs.getBoolean("est_actif"));
        u.setEstAdmin(rs.getBoolean("est_admin"));
        u.setNiveauAcces(rs.getObject("niveau_acces") != null
            ? rs.getInt("niveau_acces") : null);
        return u;
    }

    // Trouver l'ID d'un utilisateur par email - CORRIGÉ
    public int findIdByEmail(String email) {
        String sql = "SELECT id FROM utilisateur WHERE email = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("✅ Email trouvé : " + email + " -> ID: " + id);
                return id;
            } else {
                System.out.println("⚠️ Email non trouvé : " + email);
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur findIdByEmail : " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    // Mettre à jour le mot de passe - CORRIGÉ
    public boolean mettreAJourMotDePasse(int userId, String newPassword) {
        String sql = "UPDATE utilisateur SET mot_de_passe = ? WHERE id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt(12)));
            stmt.setInt(2, userId);
            int result = stmt.executeUpdate();
            System.out.println("✅ Mise à jour mot de passe : " + (result > 0));
            return result > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur mettreAJourMotDePasse : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}