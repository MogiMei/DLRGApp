package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class Termine implements Comparable<Termine> {
    public static String[] Monate = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};

    private String datum;
    private String datumZeit;
    private Timestamp anfang;
    private Timestamp ende;
    public Long duration;

    public Termine(Timestamp anfang, Timestamp ende) {
        this.anfang = anfang;
        this.ende = ende;
        duration = ende.getSeconds() - anfang.getSeconds();
        Date date = anfang.toDate();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTime(date);
        this.datum = cal.get(Calendar.DAY_OF_MONTH) + ". " + Termine.Monate[cal.get(Calendar.MONTH)];
        setDatumZeit();
    }

    public Termine(Date anfang, Date ende) {
        this.anfang = new Timestamp(anfang);
        this.ende = new Timestamp(ende);
        duration = this.ende.getSeconds() - this.anfang.getSeconds();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTime(anfang);
        this.datum = cal.get(Calendar.DAY_OF_MONTH) + ". " + Termine.Monate[cal.get(Calendar.MONTH)];
        setDatumZeit();
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

    public void setDatumZeit() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Calendar calAnfang = Calendar.getInstance();
        Long timeAnfang = anfang.getSeconds() * 1000;
        calAnfang.setTimeInMillis(timeAnfang);
        Calendar calEnde = Calendar.getInstance();
        Long timeEnde = ende.getSeconds() * 1000;
        calEnde.setTimeInMillis(timeEnde);
        datumZeit = calAnfang.get(Calendar.DAY_OF_MONTH) + ". " + Termine.Monate[calAnfang.get(Calendar.MONTH)] + " " + simpleDateFormat.format(calAnfang.getTime()) + " - " + simpleDateFormat.format(calEnde.getTime());
    }

    public String getDatumZeit() {
        return datumZeit;
    }
}
