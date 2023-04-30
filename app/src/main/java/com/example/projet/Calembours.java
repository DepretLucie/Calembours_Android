package com.example.projet;

public class Calembours {
    long id;
    String type;
    String setup;
    String punchline;
    Float note;
    boolean vu;

    public Calembours(long id, String type, String setup, String punchline, Float note, boolean vu) {
        this.id = id;
        this.type = type;
        this.setup = setup;
        this.punchline = punchline;
        this.note = note;
        this.vu = vu;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSetup() {
        return setup;
    }

    public void setSetup(String setup) {
        this.setup = setup;
    }

    public String getPunchline() {
        return punchline;
    }

    public void setPunchline(String punchline) {
        this.punchline = punchline;
    }

    public Float getNote() {
        return note;
    }

    public void setNote(Float note) {
        this.note = note;
    }

    public boolean isVu() {
        return vu;
    }

    public void setVu(boolean vu) {
        this.vu = vu;
    }
}
