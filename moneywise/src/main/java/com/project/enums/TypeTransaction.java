package com.project.enums;

public enum TypeTransaction {
    ENTREE("Entrée"),
    SORTIE("Sortie");

    private final String libelle;

    TypeTransaction(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}