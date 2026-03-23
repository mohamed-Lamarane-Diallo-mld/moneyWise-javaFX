package com.project.enums;

public enum TypeAlerte {
    SEUIL_80("Attention : 80% du budget atteint"),
    SEUIL_100("Alerte : budget dépassé !");

    private final String message;

    TypeAlerte(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}