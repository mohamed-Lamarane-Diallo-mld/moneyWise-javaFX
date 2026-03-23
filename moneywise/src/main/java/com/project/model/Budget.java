package com.project.model;

public class Budget {

    private int id;
    private double montantMax;
    private int mois;
    private int annee;
    private boolean estActif;
    private int utilisateurId;
    private int categorieId;
    private String categorieNom;   // pour l'affichage (JOIN)

    public Budget() {}

    public Budget(double montantMax, int mois, int annee,
                  int utilisateurId, int categorieId) {
        this.montantMax    = montantMax;
        this.mois          = mois;
        this.annee         = annee;
        this.estActif      = true;
        this.utilisateurId = utilisateurId;
        this.categorieId   = categorieId;
    }

    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public double getMontantMax()             { return montantMax; }
    public void setMontantMax(double m)       { this.montantMax = m; }

    public int getMois()                      { return mois; }
    public void setMois(int mois)             { this.mois = mois; }

    public int getAnnee()                     { return annee; }
    public void setAnnee(int annee)           { this.annee = annee; }

    public boolean isEstActif()               { return estActif; }
    public void setEstActif(boolean b)        { this.estActif = b; }

    public int getUtilisateurId()             { return utilisateurId; }
    public void setUtilisateurId(int uid)     { this.utilisateurId = uid; }

    public int getCategorieId()               { return categorieId; }
    public void setCategorieId(int cid)       { this.categorieId = cid; }

    public String getCategorieNom()           { return categorieNom; }
    public void setCategorieNom(String nom)   { this.categorieNom = nom; }
}