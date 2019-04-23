package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class Termine implements Comparable<Termine> {
    public static String[] Monate = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};

    private String datum;
    private Timestamp anfang;
    private Timestamp ende;

    public Termine(Timestamp anfang, Timestamp ende) {
        this.anfang = anfang;
        this.ende = ende;
        Date date = anfang.toDate();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTime(date);
        this.datum = cal.get(Calendar.DAY_OF_MONTH) + ". " + Termine.Monate[cal.get(Calendar.MONTH)];
    }

    public Termine(Date anfang, Date ende) {
        this.anfang = new Timestamp(anfang);
        this.ende = new Timestamp(ende);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTime(anfang);
        this.datum = cal.get(Calendar.DAY_OF_MONTH) + ". " + Termine.Monate[cal.get(Calendar.MONTH)];
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

    @Override
    public int compareTo(Termine o) {
        if(anfang.getSeconds() > o.anfang.getSeconds()) {
            return 1;
        } else if(anfang.getSeconds() < o.anfang.getSeconds()){
            return -1;
        }
        return 0;
    }

    public String getDatum() {
        return datum;
    }
}
