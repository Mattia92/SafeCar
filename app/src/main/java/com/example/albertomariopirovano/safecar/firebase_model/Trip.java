package com.example.albertomariopirovano.safecar.firebase_model;

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
    public String departure;
    public String arrival;
    public String userId;

    public Trip() {
    }

    public Trip(String userId, Date date, int finalDSI, int km, double timeDuration, String departure, String arrival) {
        this.date = date;
        this.finalDSI = finalDSI;
        this.km = km;
        this.timeDuration = timeDuration;
        this.departure = departure;
        this.arrival = arrival;
        this.userId = userId;
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

    public String getDeparture() {
        return departure;
    }

    public String getArrival() {
        return arrival;
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