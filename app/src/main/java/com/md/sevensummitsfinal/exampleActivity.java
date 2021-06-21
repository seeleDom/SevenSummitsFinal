package com.md.sevensummitsfinal;

import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;

public class exampleActivity {
    private String titel;
    private String beschreibung;
    private String image;

    public exampleActivity(String titel, String beschreibung, String photo){
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.image = photo;

    }

    public String getImage() {
        return image;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public String getTitel() {
        return titel;
    }
}
