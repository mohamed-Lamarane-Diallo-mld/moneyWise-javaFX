package com.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.project.model.QuestionSecurite;

public class QuestionSecuriteDAO {

    private Connection getConn() {
        return DatabaseConnection.getConnection();
    }

    // ── Récupérer toutes les questions disponibles ──
    public List<QuestionSecurite> findAll() {
        List<QuestionSecurite> liste = new ArrayList<>();
        String sql = "SELECT * FROM question_securite ORDER BY id";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                liste.add(new QuestionSecurite(
                    rs.getInt("id"), rs.getString("question")));
        } catch (SQLException e) {
            System.err.println("Erreur findAll questions : " + e.getMessage());
        }
        return liste;
    }

    // ── Enregistrer les 3 réponses à l'inscription ──
    public boolean enregistrerReponses(int utilisateurId,
                                       int[] questionIds,
                                       String[] reponses) {
        String sql = "INSERT INTO reponse_securite " +
                     "(utilisateur_id, question_id, reponse) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            for (int i = 0; i < questionIds.length; i++) {
                String hash = BCrypt.hashpw(
                    reponses[i].trim().toLowerCase(), BCrypt.gensalt(10));
                ps.setInt(1, utilisateurId);
                ps.setInt(2, questionIds[i]);
                ps.setString(3, hash);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Réponses enregistrées pour l'utilisateur " + utilisateurId);
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur enregistrerReponses : " + e.getMessage());
            return false;
        }
    }

    // ── Récupérer les questions choisies par un utilisateur (CORRIGÉ) ──
    public List<QuestionSecurite> findByUtilisateur(int userId) {
        List<QuestionSecurite> list = new ArrayList<>();
        String sql = "SELECT qs.id, qs.question FROM question_securite qs " +
                     "JOIN reponse_securite rs ON qs.id = rs.question_id " +
                     "WHERE rs.utilisateur_id = ? LIMIT 3";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                QuestionSecurite q = new QuestionSecurite();
                q.setId(rs.getInt("id"));
                q.setQuestion(rs.getString("question"));
                list.add(q);
                System.out.println("Question chargée: " + rs.getString("question"));
            }
            System.out.println(list.size() + " questions trouvées pour l'utilisateur " + userId);
        } catch (SQLException e) {
            System.err.println("Erreur findByUtilisateur : " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // ── Vérifier si l'email a des questions de sécurité (CORRIGÉ) ──
    public boolean aDesQuestions(String email) {
        String sql = "SELECT COUNT(*) FROM reponse_securite r " +
                     "JOIN utilisateur u ON r.utilisateur_id = u.id " +
                     "WHERE u.email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean existe = rs.getInt(1) > 0;
                System.out.println("Vérification questions pour " + email + " : " + existe);
                return existe;
            }
        } catch (SQLException e) {
            System.err.println("Erreur aDesQuestions : " + e.getMessage());
        }
        return false;
    }

    // ── Vérifier une réponse à une question (CORRIGÉ) ──
    public boolean verifierReponse(int userId, int questionId, String reponse) {
        String sql = "SELECT reponse FROM reponse_securite WHERE utilisateur_id = ? AND question_id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, questionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedResponse = rs.getString("reponse");
                // BCrypt check pour les réponses hashées
                boolean estCorrect = BCrypt.checkpw(reponse.toLowerCase().trim(), storedResponse);
                System.out.println("Vérification réponse - Question " + questionId + " : " + (estCorrect ? "Correct" : "Incorrect"));
                return estCorrect;
            }
            System.out.println("Aucune réponse trouvée pour question " + questionId);
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur verifierReponse : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ── Réinitialiser le mot de passe après vérification ──
    public boolean reinitialiserMotDePasse(String email, String nouveauMdp) {
        String sql = "UPDATE utilisateur SET mot_de_passe = ? WHERE email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            String hash = BCrypt.hashpw(nouveauMdp, BCrypt.gensalt(12));
            ps.setString(1, hash);
            ps.setString(2, email);
            int result = ps.executeUpdate();
            System.out.println("Réinitialisation mot de passe : " + (result > 0));
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Erreur reinitialiserMotDePasse : " + e.getMessage());
            return false;
        }
    }

    // ── Trouver l'utilisateur par email ──
    public int findUtilisateurIdByEmail(String email) {
        String sql = "SELECT id FROM utilisateur WHERE email = ? AND est_actif = TRUE";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("Utilisateur trouvé : " + email + " -> ID: " + id);
                return id;
            }
            System.out.println("Utilisateur non trouvé : " + email);
        } catch (SQLException e) {
            System.err.println("Erreur findUtilisateurIdByEmail : " + e.getMessage());
        }
        return -1;
    }
    
}