package com.project.model;

import com.project.enums.TypeAlerte;
import java.time.LocalDateTime;

public class Alerte {

    private int id;
    private String message;
    private LocalDateTime dateAlerte;
    private TypeAlerte typeAlerte;
    private boolean estLue;
    private int budgetId;

    public Alerte() {}

    public Alerte(String message, TypeAlerte typeAlerte, int budgetId) {
        this.message    = message;
        this.typeAlerte = typeAlerte;
        this.budgetId   = budgetId;
        this.dateAlerte = LocalDateTime.now();
        this.estLue     = false;
    }

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public String getMessage()                  { return message; }
    public void setMessage(String message)      { this.message = message; }

    public LocalDateTime getDateAlerte()        { return dateAlerte; }
    public void setDateAlerte(LocalDateTime d)  { this.dateAlerte = d; }

    public TypeAlerte getTypeAlerte()           { return typeAlerte; }
    public void setTypeAlerte(TypeAlerte t)     { this.typeAlerte = t; }

    public boolean isEstLue()                   { return estLue; }
    public void setEstLue(boolean estLue)       { this.estLue = estLue; }

    public int getBudgetId()                    { return budgetId; }
    public void setBudgetId(int budgetId)       { this.budgetId = budgetId; }
}