package com.example.albertomariopirovano.safecar.firebase_model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;


/**
 * Created by albertomariopirovano on 04/04/17.
 */

@IgnoreExtraProperties
public class Trip implements Serializable {

    public Date date;
    public Integer finalDSI;
    public Integer km;
    public Double timeDuration;
    public LatLng departure;
    public LatLng arrival;
    public String userId;
    public String depName;
    public String arrName;

    public Trip() {
    }

    public Trip(String userId, Date date, int finalDSI, int km, double timeDuration, LatLng departure, LatLng arrival, String depName, String arrName) {
        this.date = date;
        this.finalDSI = finalDSI;
        this.km = km;
        this.timeDuration = timeDuration;
        this.departure = departure;
        this.arrival = arrival;
        this.userId = userId;
        this.depName = depName;
        this.arrName = arrName;
    }

    public String getAttr(String attributeToShow) {
        switch (attributeToShow) {
            case "duration":
                return String.valueOf(getTimeDuration());
            case "date":
                return String.valueOf(getDate());
            case "DSI":
                return String.valueOf(getFinalDSI());
            case "KM":
                return String.valueOf(getKm());
            default:
                return "error";
        }
    }

    public double getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(Double timeDuration) {
        this.timeDuration = timeDuration;
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

    public LatLng getDeparture() {
        return departure;
    }

    public void setDeparture(LatLng departure) {
        this.departure = departure;
    }

    public LatLng getArrival() {
        return arrival;
    }

    public void setArrival(LatLng arrival) {
        this.arrival = arrival;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public void setFinalDSI(Integer finalDSI) {
        this.finalDSI = finalDSI;
    }

    public int getKm() {
        return km;
    }

    public void setKm(Integer km) {
        this.km = km;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "date=" + date +
                ", finalDSI=" + finalDSI +
                ", km=" + km +
                ", timeDuration=" + timeDuration +
                ", departure='" + departure + '\'' +
                ", arrival='" + arrival + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}