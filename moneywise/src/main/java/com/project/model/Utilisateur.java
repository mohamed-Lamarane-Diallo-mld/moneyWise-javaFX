package com.project.model;

import java.time.LocalDate;

public class Utilisateur {

    private int id;
    private String nom;
    private String email;
    private String motDePasse;
    private LocalDate dateInscription;
    private boolean estActif;
    private boolean estAdmin;
    private Integer niveauAcces;

    // Constructeur vide
    public Utilisateur() {}

    // Constructeur inscription
    public Utilisateur(String nom, String email, String motDePasse) {
        this.nom             = nom;
        this.email           = email;
        this.motDePasse      = motDePasse;
        this.dateInscription = LocalDate.now();
        this.estActif        = true;
        this.estAdmin        = false;
    }

    // Constructeur complet (depuis BDD)
    public Utilisateur(int id, String nom, String email, String motDePasse,
                       LocalDate dateInscription, boolean estActif,
                       boolean estAdmin, Integer niveauAcces) {
        this.id              = id;
        this.nom             = nom;
        this.email           = email;
        this.motDePasse      = motDePasse;
        this.dateInscription = dateInscription;
        this.estActif        = estActif;
        this.estAdmin        = estAdmin;
        this.niveauAcces     = niveauAcces;
    }

    // Getters & Setters
    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public String getNom()                    { return nom; }
    public void setNom(String nom)            { this.nom = nom; }

    public String getEmail()                  { return email; }
    public void setEmail(String email)        { this.email = email; }

    public String getMotDePasse()             { return motDePasse; }
    public void setMotDePasse(String mdp)     { this.motDePasse = mdp; }

    public LocalDate getDateInscription()     { return dateInscription; }
    public void setDateInscription(LocalDate d) { this.dateInscription = d; }

    public boolean isEstActif()               { return estActif; }
    public void setEstActif(boolean estActif) { this.estActif = estActif; }

    public boolean isEstAdmin()               { return estAdmin; }
    public void setEstAdmin(boolean estAdmin) { this.estAdmin = estAdmin; }

    public Integer getNiveauAcces()           { return niveauAcces; }
    public void setNiveauAcces(Integer n)     { this.niveauAcces = n; }

    @Override
    public String toString() {
        return "Utilisateur{id=" + id + ", nom='" + nom + "', email='" + email + "'}";
    }
}