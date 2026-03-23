package com.project.model;

import java.time.LocalDateTime;

public class JournalActivite {

    private int id;
    private int utilisateurId;
    private String action;
    private String details;
    private LocalDateTime dateAction;
    private String adresseIp;
    private String nomUtilisateur;  // champ pratique pour l'affichage (JOIN admin)

    public JournalActivite() {}

    public JournalActivite(int utilisateurId, String action, String details) {
        this.utilisateurId = utilisateurId;
        this.action        = action;
        this.details       = details;
        this.dateAction    = LocalDateTime.now();
    }

    public int getId()                           { return id; }
    public void setId(int id)                    { this.id = id; }

    public int getUtilisateurId()                { return utilisateurId; }
    public void setUtilisateurId(int uid)        { this.utilisateurId = uid; }

    public String getAction()                    { return action; }
    public void setAction(String action)         { this.action = action; }

    public String getDetails()                   { return details; }
    public void setDetails(String details)       { this.details = details; }

    public LocalDateTime getDateAction()         { return dateAction; }
    public void setDateAction(LocalDateTime d)   { this.dateAction = d; }

    public String getAdresseIp()                 { return adresseIp; }
    public void setAdresseIp(String ip)          { this.adresseIp = ip; }

    public String getNomUtilisateur()            { return nomUtilisateur; }
    public void setNomUtilisateur(String nom)    { this.nomUtilisateur = nom; }

    @Override
    public String toString() {
        return "[" + dateAction + "] " + action + " — " + details;
    }
}