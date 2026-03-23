package com.project.model;

import com.project.enums.FormatExport;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Export {

    private int id;
    private FormatExport format;
    private LocalDateTime dateExport;
    private String cheminFichier;
    private LocalDate periodeDebut;
    private LocalDate periodeFin;
    private int utilisateurId;

    public Export() {}

    public Export(FormatExport format, String cheminFichier,
                  LocalDate periodeDebut, LocalDate periodeFin, int utilisateurId) {
        this.format        = format;
        this.cheminFichier = cheminFichier;
        this.periodeDebut  = periodeDebut;
        this.periodeFin    = periodeFin;
        this.utilisateurId = utilisateurId;
        this.dateExport    = LocalDateTime.now();
    }

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public FormatExport getFormat()             { return format; }
    public void setFormat(FormatExport format)  { this.format = format; }

    public LocalDateTime getDateExport()        { return dateExport; }
    public void setDateExport(LocalDateTime d)  { this.dateExport = d; }

    public String getCheminFichier()            { return cheminFichier; }
    public void setCheminFichier(String c)      { this.cheminFichier = c; }

    public LocalDate getPeriodeDebut()          { return periodeDebut; }
    public void setPeriodeDebut(LocalDate d)    { this.periodeDebut = d; }

    public LocalDate getPeriodeFin()            { return periodeFin; }
    public void setPeriodeFin(LocalDate d)      { this.periodeFin = d; }

    public int getUtilisateurId()               { return utilisateurId; }
    public void setUtilisateurId(int uid)       { this.utilisateurId = uid; }
}