package com.project.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.enums.TypeTransaction;

public class Transaction {

    private int id;
    private double montant;
    private TypeTransaction type;
    private LocalDate dateTransaction;
    private String description;
    private int utilisateurId;
    private int categorieId;
    private String categorieNom;     // champ pratique pour l'affichage (JOIN)
    private LocalDateTime dateSaisie;

    public Transaction() {}

    public Transaction(double montant, TypeTransaction type, LocalDate dateTransaction,
                       String description, int utilisateurId, int categorieId) {
        this.montant         = montant;
        this.type            = type;
        this.dateTransaction = dateTransaction;
        this.description     = description;
        this.utilisateurId   = utilisateurId;
        this.categorieId     = categorieId;
        this.dateSaisie      = LocalDateTime.now();
    }

    public int getId()                           { return id; }
    public void setId(int id)                    { this.id = id; }

    public double getMontant()                   { return montant; }
    public void setMontant(double montant)       { this.montant = montant; }

    public TypeTransaction getType()             { return type; }
    public void setType(TypeTransaction type)    { this.type = type; }

    public LocalDate getDateTransaction()        { return dateTransaction; }
    public void setDateTransaction(LocalDate d)  { this.dateTransaction = d; }

    public String getDescription()               { return description; }
    public void setDescription(String desc)      { this.description = desc; }

    public int getUtilisateurId()                { return utilisateurId; }
    public void setUtilisateurId(int uid)        { this.utilisateurId = uid; }

    public int getCategorieId()                  { return categorieId; }
    public void setCategorieId(int cid)          { this.categorieId = cid; }

    public String getCategorieNom()              { return categorieNom; }
    public void setCategorieNom(String nom)      { this.categorieNom = nom; }

    public LocalDateTime getDateSaisie()         { return dateSaisie; }
    public void setDateSaisie(LocalDateTime d)   { this.dateSaisie = d; }
}