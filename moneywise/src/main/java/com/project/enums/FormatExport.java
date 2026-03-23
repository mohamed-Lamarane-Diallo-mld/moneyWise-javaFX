package com.project.enums;

public enum FormatExport {
    PDF("PDF"),
    EXCEL("Excel");

    private final String libelle;

    FormatExport(String libelle) {
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