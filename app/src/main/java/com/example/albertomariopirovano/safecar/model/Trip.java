package com.example.albertomariopirovano.safecar.model;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class Trip extends RealmObject implements Serializable{

    private Date date;
    private int finalDSI;
    private int km;
    private double timeDuration;
    private String departure;
    private String arrival;

    public Trip(){}

    public void setTrip(Date date, int finalDSI, int km, double timeDuration, String departure, String arrival) {
        this.departure = departure;
        this.arrival = arrival;
        this.date = date;
        this.finalDSI = finalDSI;
        this.km = km;
        this.timeDuration = timeDuration;
    }

    public String getAttr(String attributeToShow) {
        switch (attributeToShow){
            case "duration": return String.valueOf(getTimeDuration());
            case "date": return String.valueOf(getDate());
            case "DSI": return String.valueOf(getFinalDSI());
            case "KM": return String.valueOf(getKm());
            default: return "error";
        }
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
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
