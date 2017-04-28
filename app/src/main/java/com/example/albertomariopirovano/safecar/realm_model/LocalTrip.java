package com.example.albertomariopirovano.safecar.realm_model;

import com.example.albertomariopirovano.safecar.firebase_model.Trip;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by albertomariopirovano on 28/04/17.
 */

public class LocalTrip extends RealmObject implements Serializable {

    private Date date;
    private int finalDSI;
    private int km;
    private double timeDuration;

    private double departure_latitude;
    private double departure_longitude;
    private double arrival_latitude;
    private double arrival_longitude;

    private String depName;
    private String arrName;
    //private ArrayList<LatLng> wayPoints;

    public LocalTrip() {
    }

    public LocalTrip(Trip t) {
        this.date = t.getDate();
        this.finalDSI = t.getFinalDSI();
        this.km = t.getKm();
        this.timeDuration = t.getTimeDuration();
        this.arrival_latitude = t.getArrival().latitude;
        this.arrival_longitude = t.getArrival().longitude;
        this.departure_latitude = t.getDeparture().latitude;
        this.arrival_longitude = t.getArrival().longitude;
        this.depName = t.getDepName();
        this.arrName = t.getArrName();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getFinalDSI() {
        return finalDSI;
    }

    public void setFinalDSI(int finalDSI) {
        this.finalDSI = finalDSI;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public double getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(double timeDuration) {
        this.timeDuration = timeDuration;
    }

    public double getDeparture_latitude() {
        return departure_latitude;
    }

    public void setDeparture_latitude(double departure_latitude) {
        this.departure_latitude = departure_latitude;
    }

    public double getDeparture_longitude() {
        return departure_longitude;
    }

    public void setDeparture_longitude(double departure_longitude) {
        this.departure_longitude = departure_longitude;
    }

    public double getArrival_latitude() {
        return arrival_latitude;
    }

    public void setArrival_latitude(double arrival_latitude) {
        this.arrival_latitude = arrival_latitude;
    }

    public double getArrival_longitude() {
        return arrival_longitude;
    }

    public void setArrival_longitude(double arrival_longitude) {
        this.arrival_longitude = arrival_longitude;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public String getArrName() {
        return arrName;
    }

    public void setArrName(String arrName) {
        this.arrName = arrName;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}