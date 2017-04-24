package com.example.albertomariopirovano.safecar.firebase_model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;


/**
 * Created by albertomariopirovano on 04/04/17.
 */

@IgnoreExtraProperties
public class Trip {

    public Date date;
    public Integer finalDSI;
    public Integer km;
    public Double timeDuration;
    public String departure;
    public String arrival;

    public Trip() {
    }

    public Trip(Date date, int finalDSI, int km, double timeDuration, String departure, String arrival) {
        date = date;
        finalDSI = finalDSI;
        km = km;
        timeDuration = timeDuration;
        departure = departure;
        arrival = arrival;
    }
}