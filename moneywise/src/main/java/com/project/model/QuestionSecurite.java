package com.project.model;

public class QuestionSecurite {

    private int    id;
    private String question;

    public QuestionSecurite() {}

    public QuestionSecurite(int id, String question) {
        this.id       = id;
        this.question = question;
    }

    public int    getId()              { return id; }
    public void   setId(int id)        { this.id = id; }

    public String getQuestion()        { return question; }
    public void   setQuestion(String q){ this.question = q; }

    /** Affiché dans les ComboBox JavaFX */
    @Override
    public String toString() { return question; }
}