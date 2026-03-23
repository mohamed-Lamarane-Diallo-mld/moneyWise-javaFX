package com.project.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class DateHelper {

    // ── Formats disponibles ──
    public static final String FORMAT_COURT    = "dd/MM/yyyy";
    public static final String FORMAT_LONG     = "dd MMMM yyyy";
    public static final String FORMAT_COMPLET  = "EEEE d MMMM yyyy";
    public static final String FORMAT_HEURE    = "dd/MM/yyyy HH:mm";
    public static final String FORMAT_ISO      = "yyyy-MM-dd";

    private static final Locale LOCALE = Locale.FRENCH;

    // ─────────────────────────────────────────
    // FORMATAGE
    // ─────────────────────────────────────────

    // LocalDate → String format court (01/03/2026)
    public static String formaterCourt(LocalDate date) {
        if (date == null) return "—";
        return date.format(DateTimeFormatter.ofPattern(FORMAT_COURT, LOCALE));
    }

    // LocalDate → String format long (01 mars 2026)
    public static String formaterLong(LocalDate date) {
        if (date == null) return "—";
        return date.format(DateTimeFormatter.ofPattern(FORMAT_LONG, LOCALE));
    }

    // LocalDate → String format complet (dimanche 1 mars 2026)
    public static String formaterComplet(LocalDate date) {
        if (date == null) return "—";
        return date.format(DateTimeFormatter.ofPattern(FORMAT_COMPLET, LOCALE));
    }

    // LocalDateTime → String avec heure (01/03/2026 14:30)
    public static String formaterAvecHeure(LocalDateTime dateTime) {
        if (dateTime == null) return "—";
        return dateTime.format(DateTimeFormatter.ofPattern(FORMAT_HEURE, LOCALE));
    }

    // ─────────────────────────────────────────
    // DATES UTILES
    // ─────────────────────────────────────────

    // Premier jour du mois courant
    public static LocalDate debutMoisCourant() {
        return LocalDate.now().withDayOfMonth(1);
    }

    // Dernier jour du mois courant
    public static LocalDate finMoisCourant() {
        return LocalDate.now().withDayOfMonth(
            LocalDate.now().lengthOfMonth());
    }

    // Premier jour du trimestre courant
    public static LocalDate debutTrimestreCourant() {
        int mois = LocalDate.now().getMonthValue();
        int debutTrimestre = ((mois - 1) / 3) * 3 + 1;
        return LocalDate.now().withMonth(debutTrimestre).withDayOfMonth(1);
    }

    // Premier jour de l'année courante
    public static LocalDate debutAnneeCourante() {
        return LocalDate.now().withDayOfYear(1);
    }

    // ─────────────────────────────────────────
    // CALCULS
    // ─────────────────────────────────────────

    // Nombre de jours entre deux dates
    public static long joursEntre(LocalDate debut, LocalDate fin) {
        return ChronoUnit.DAYS.between(debut, fin);
    }

    // Vérifier si une date est aujourd'hui
    public static boolean estAujourdhui(LocalDate date) {
        return date != null && date.equals(LocalDate.now());
    }

    // Vérifier si une date est dans le mois courant
    public static boolean estMoisCourant(LocalDate date) {
        if (date == null) return false;
        LocalDate now = LocalDate.now();
        return date.getMonth() == now.getMonth()
            && date.getYear() == now.getYear();
    }

    // Vérifier si une date est dans le futur
    public static boolean estFutur(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    // ─────────────────────────────────────────
    // LABELS RELATIFS
    // (utilisés dans la table des transactions)
    // ─────────────────────────────────────────
    public static String labelRelatif(LocalDate date) {
        if (date == null) return "—";
        long jours = ChronoUnit.DAYS.between(date, LocalDate.now());

        if (jours == 0)  return "Aujourd'hui";
        if (jours == 1)  return "Hier";
        if (jours <= 7)  return "Il y a " + jours + " jours";
        if (jours <= 30) return "Il y a " + (jours / 7) + " semaine(s)";
        if (jours <= 365) return "Il y a " + (jours / 30) + " mois";
        return formaterCourt(date);
    }

    // ─────────────────────────────────────────
    // NOM DU MOIS
    // ─────────────────────────────────────────
    public static String nomMois(int numeroMois) {
        return LocalDate.of(2000, numeroMois, 1)
            .format(DateTimeFormatter.ofPattern("MMMM", LOCALE));
    }

    // Nom du mois courant
    public static String nomMoisCourant() {
        return nomMois(LocalDate.now().getMonthValue());
    }

    // ─────────────────────────────────────────
    // ANNÉE COURANTE
    // ─────────────────────────────────────────
    public static int anneeCourante() {
        return LocalDate.now().getYear();
    }

    public static int moisCourant() {
        return LocalDate.now().getMonthValue();
    }
}