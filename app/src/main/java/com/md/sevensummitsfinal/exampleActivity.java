package com.md.sevensummitsfinal;

public class exampleActivity {
    private String titel;
    private String beschreibung;
    private int image;

    public exampleActivity(String titel, String beschreibung, int photo){
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.image = image;

    }

    public int getImage() {
        return image;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public String getTitel() {
        return titel;
    }
}
