package com.project.model;

public class Categorie {

    private int id;
    private String nom;
    private String icone;
    private boolean estSysteme;
    private Integer utilisateurId;

    public Categorie() {}

    public Categorie(String nom, String icone, boolean estSysteme, Integer utilisateurId) {
        this.nom           = nom;
        this.icone         = icone;
        this.estSysteme    = estSysteme;
        this.utilisateurId = utilisateurId;
    }

    public Categorie(int id, String nom, String icone,
                     boolean estSysteme, Integer utilisateurId) {
        this.id            = id;
        this.nom           = nom;
        this.icone         = icone;
        this.estSysteme    = estSysteme;
        this.utilisateurId = utilisateurId;
    }

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public String getNom()                      { return nom; }
    public void setNom(String nom)              { this.nom = nom; }

    public String getIcone()                    { return icone; }
    public void setIcone(String icone)          { this.icone = icone; }

    public boolean isEstSysteme()               { return estSysteme; }
    public void setEstSysteme(boolean b)        { this.estSysteme = b; }

    public Integer getUtilisateurId()           { return utilisateurId; }
    public void setUtilisateurId(Integer uid)   { this.utilisateurId = uid; }

    // Utilisé dans les ComboBox JavaFX
    @Override
    public String toString() {
        return nom;
    }
}