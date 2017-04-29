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

    public Double departure_latitude;
    public Double departure_longitude;
    public Double arrival_latitude;
    public Double arrival_longitude;

    public String userId;
    public String depName;
    public String arrName;

    public Trip() {
    }

    public Trip(String userId,
                Date date,
                Integer finalDSI,
                Integer km,
                Double timeDuration,
                Double arrival_latitude,
                Double arrival_longitude,
                Double departure_latitude,
                Double departure_longitude,
                String depName,
                String arrName) {
        this.date = date;
        this.finalDSI = finalDSI;
        this.km = km;
        this.timeDuration = timeDuration;
        this.arrival_latitude = arrival_latitude;
        this.arrival_longitude = arrival_longitude;
        this.departure_latitude = departure_latitude;
        this.departure_longitude = departure_longitude;
        this.userId = userId;
        this.depName = depName;
        this.arrName = arrName;
    }

    public String getAttr(String attributeToShow) {
        switch (attributeToShow) {
            case "duration":
                return String.valueOf(timeDuration);
            case "date":
                return String.valueOf(date);
            case "DSI":
                return String.valueOf(finalDSI);
            case "KM":
                return String.valueOf(km);
            default:
                return "error";
        }
    }

    @Override
    public String toString() {
        return "Trip{" +
                "date=" + date +
                ", finalDSI=" + finalDSI +
                ", km=" + km +
                ", timeDuration=" + timeDuration +
                ", departure_latitude=" + departure_latitude +
                ", departure_longitude=" + departure_longitude +
                ", arrival_latitude=" + arrival_latitude +
                ", arrival_longitude=" + arrival_longitude +
                ", userId='" + userId + '\'' +
                ", depName='" + depName + '\'' +
                ", arrName='" + arrName + '\'' +
                '}';
    }
}