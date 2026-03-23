package com.project.utils;

import com.project.App;
import com.project.model.Utilisateur;

public class SessionManager {

    private static Utilisateur utilisateurConnecte = null;

    // ─────────────────────────────────────────
    // GESTION SESSION
    // ─────────────────────────────────────────

    public static void setUtilisateur(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
    }

    public static Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }

    public static boolean isConnecte() {
        return utilisateurConnecte != null;
    }

    public static boolean isAdmin() {
        return utilisateurConnecte != null
            && utilisateurConnecte.isEstAdmin();
    }

    // Raccourci pratique — évite les null checks dans les controllers
    public static int getUserId() {
        return utilisateurConnecte != null ? utilisateurConnecte.getId() : -1;
    }

    // Rafraîchit les infos depuis la BDD (après modif profil)
    public static void rafraichir() {
        if (utilisateurConnecte == null) return;
        com.project.dao.UtilisateurDAO dao = new com.project.dao.UtilisateurDAO();
        Utilisateur updated = dao.findById(utilisateurConnecte.getId());
        if (updated != null) {
            utilisateurConnecte = updated;
        }
    }

    // Déconnexion propre
    public static void clear() {
        utilisateurConnecte = null;
        // Vider le cache des vues
        App.clearViewCache();
    }
}