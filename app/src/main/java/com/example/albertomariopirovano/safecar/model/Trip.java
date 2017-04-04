package com.example.albertomariopirovano.safecar.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class Trip extends RealmObject {

    private Date date;
    private int finalDSI;
    private int km;
    private double timeDuration;

    public Trip(){}

    public void setTrip(Date date, int finalDSI, int km, double timeDuration) {
        this.date = date;
        this.finalDSI = finalDSI;
        this.km = km;
        this.timeDuration = timeDuration;
    }

    public Date getDate() {
        return date;
    }

    public int getFinalDSI() {
        return finalDSI;
    }

    public int getKm() {
        return km;
    }

    public double getTimeDuration() {
        return timeDuration;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setFinalDSI(int finalDSI) {
        this.finalDSI = finalDSI;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public void setTimeDuration(double timeDuration) {
        this.timeDuration = timeDuration;
    }
}
