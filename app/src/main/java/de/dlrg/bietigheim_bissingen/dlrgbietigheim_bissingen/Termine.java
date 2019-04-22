package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import com.google.firebase.Timestamp;

import java.util.Map;

public class Termine {
    public static String[] Monate = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};

    private String datum;
    private Timestamp anfang;
    private Timestamp ende;

    public Termine(Timestamp anfang, Timestamp ende) {
        this.anfang = anfang;
        this.ende = ende;
    }

    public Timestamp getAnfang() {
        return anfang;
    }

    public Timestamp getEnde() {
        return ende;
    }

    public static Termine create(Map<String, Object> map) {
        return new Termine((Timestamp) map.get("anfangsZeit"), (Timestamp) map.get("endZeit"));
    }
}
