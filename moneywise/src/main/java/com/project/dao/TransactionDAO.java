package com.project.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.project.enums.TypeTransaction;
import com.project.model.Transaction;

public class TransactionDAO {

    private Connection getConn() {
        return DatabaseConnection.getConnection();
    }

    public boolean ajouter(Transaction t) {
        String sql = "INSERT INTO transaction " +
                     "(montant, type, date_transaction, description, utilisateur_id, categorie_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, t.getMontant());
            ps.setString(2, t.getType().name());
            ps.setDate(3, Date.valueOf(t.getDateTransaction()));
            ps.setString(4, t.getDescription());
            ps.setInt(5, t.getUtilisateurId());
            ps.setInt(6, t.getCategorieId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) t.setId(keys.getInt(1));
            System.out.println("✅ Transaction ajoutée : " + t.getMontant());
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajouter transaction : " + e.getMessage());
            return false;
        }
    }

    public boolean modifier(Transaction t) {
        String sql = "UPDATE transaction SET montant = ?, type = ?, date_transaction = ?, " +
                     "description = ?, categorie_id = ? " +
                     "WHERE id = ? AND utilisateur_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDouble(1, t.getMontant());
            ps.setString(2, t.getType().name());
            ps.setDate(3, Date.valueOf(t.getDateTransaction()));
            ps.setString(4, t.getDescription());
            ps.setInt(5, t.getCategorieId());
            ps.setInt(6, t.getId());
            ps.setInt(7, t.getUtilisateurId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur modifier transaction : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimer(int id, int utilisateurId) {
        String sql = "DELETE FROM transaction WHERE id = ? AND utilisateur_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, utilisateurId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur supprimer transaction : " + e.getMessage());
            return false;
        }
    }

    public List<Transaction> findByUtilisateur(int utilisateurId) {
        List<Transaction> liste = new ArrayList<>();
        String sql = "SELECT t.*, c.nom AS categorie_nom " +
                     "FROM transaction t " +
                     "LEFT JOIN categorie c ON t.categorie_id = c.id " +
                     "WHERE t.utilisateur_id = ? " +
                     "ORDER BY t.date_transaction DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur findByUtilisateur : " + e.getMessage());
        }
        return liste;
    }

    public List<Transaction> findRecentes(int utilisateurId, int limite) {
        List<Transaction> liste = new ArrayList<>();
        String sql = "SELECT t.*, c.nom AS categorie_nom " +
                     "FROM transaction t " +
                     "LEFT JOIN categorie c ON t.categorie_id = c.id " +
                     "WHERE t.utilisateur_id = ? " +
                     "ORDER BY t.date_transaction DESC LIMIT ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ps.setInt(2, limite);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur findRecentes : " + e.getMessage());
        }
        return liste;
    }

    public List<Transaction> rechercher(int utilisateurId, String type,
                                         Integer categorieId, LocalDate debut,
                                         LocalDate fin, String motCle) {
        List<Transaction> liste = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT t.*, c.nom AS categorie_nom FROM transaction t " +
            "LEFT JOIN categorie c ON t.categorie_id = c.id " +
            "WHERE t.utilisateur_id = ? ");

        if (type != null && !type.isEmpty())        sql.append("AND t.type = ? ");
        if (categorieId != null)                    sql.append("AND t.categorie_id = ? ");
        if (debut != null)                          sql.append("AND t.date_transaction >= ? ");
        if (fin != null)                            sql.append("AND t.date_transaction <= ? ");
        if (motCle != null && !motCle.isEmpty())    sql.append("AND t.description LIKE ? ");
        sql.append("ORDER BY t.date_transaction DESC");

        try (PreparedStatement ps = getConn().prepareStatement(sql.toString())) {
            int i = 1;
            ps.setInt(i++, utilisateurId);
            if (type != null && !type.isEmpty())     ps.setString(i++, type);
            if (categorieId != null)                 ps.setInt(i++, categorieId);
            if (debut != null)                       ps.setDate(i++, Date.valueOf(debut));
            if (fin != null)                         ps.setDate(i++, Date.valueOf(fin));
            if (motCle != null && !motCle.isEmpty()) ps.setString(i++, "%" + motCle + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur rechercher : " + e.getMessage());
        }
        return liste;
    }

    public double getTotalEntreesMois(int utilisateurId) {
        return getTotalParType(utilisateurId, "ENTREE");
    }

    public double getTotalSortiesMois(int utilisateurId) {
        return getTotalParType(utilisateurId, "SORTIE");
    }

    private double getTotalParType(int utilisateurId, String type) {
        String sql = "SELECT COALESCE(SUM(montant), 0) AS total FROM transaction " +
                     "WHERE utilisateur_id = ? AND type = ? " +
                     "AND MONTH(date_transaction) = MONTH(CURRENT_DATE) " +
                     "AND YEAR(date_transaction)  = YEAR(CURRENT_DATE)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            System.err.println("❌ Erreur getTotalParType : " + e.getMessage());
        }
        return 0.0;
    }

    public double getSoldeTotal(int utilisateurId) {
        String sql = "SELECT " +
                     "COALESCE(SUM(CASE WHEN type='ENTREE' THEN montant ELSE 0 END),0) - " +
                     "COALESCE(SUM(CASE WHEN type='SORTIE' THEN montant ELSE 0 END),0) AS solde " +
                     "FROM transaction WHERE utilisateur_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("solde");
        } catch (SQLException e) {
            System.err.println("❌ Erreur getSoldeTotal : " + e.getMessage());
        }
        return 0.0;
    }

    public List<Object[]> getDepensesParCategorie(int utilisateurId) {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT c.nom, COALESCE(SUM(t.montant), 0) AS total " +
                     "FROM transaction t LEFT JOIN categorie c ON t.categorie_id = c.id " +
                     "WHERE t.utilisateur_id = ? AND t.type = 'SORTIE' " +
                     "AND MONTH(t.date_transaction) = MONTH(CURRENT_DATE) " +
                     "AND YEAR(t.date_transaction)  = YEAR(CURRENT_DATE) " +
                     "GROUP BY c.nom ORDER BY total DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                data.add(new Object[]{rs.getString("nom"), rs.getDouble("total")});
        } catch (SQLException e) {
            System.err.println("❌ Erreur getDepensesParCategorie : " + e.getMessage());
        }
        return data;
    }

    public List<Object[]> getEntreesSortiesParMois(int utilisateurId, int annee) {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT MONTH(date_transaction) AS mois, " +
                     "COALESCE(SUM(CASE WHEN type='ENTREE' THEN montant ELSE 0 END),0) AS entrees, " +
                     "COALESCE(SUM(CASE WHEN type='SORTIE' THEN montant ELSE 0 END),0) AS sorties " +
                     "FROM transaction WHERE utilisateur_id = ? AND YEAR(date_transaction) = ? " +
                     "GROUP BY MONTH(date_transaction) ORDER BY mois ASC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ps.setInt(2, annee);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                data.add(new Object[]{
                    rs.getInt("mois"),
                    rs.getDouble("entrees"),
                    rs.getDouble("sorties")});
        } catch (SQLException e) {
            System.err.println("❌ Erreur getEntreesSortiesParMois : " + e.getMessage());
        }
        return data;
    }

    private Transaction mapResultSet(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getInt("id"));
        t.setMontant(rs.getDouble("montant"));
        t.setType(TypeTransaction.valueOf(rs.getString("type")));
        t.setDateTransaction(rs.getDate("date_transaction").toLocalDate());
        t.setDescription(rs.getString("description"));
        t.setUtilisateurId(rs.getInt("utilisateur_id"));
        t.setCategorieId(rs.getInt("categorie_id"));
        t.setCategorieNom(rs.getString("categorie_nom"));
        Timestamp ts = rs.getTimestamp("date_saisie");
        if (ts != null) t.setDateSaisie(ts.toLocalDateTime());
        return t;
    }
}