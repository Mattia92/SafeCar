package com.example.albertomariopirovano.safecar.data_comparators;

import com.example.albertomariopirovano.safecar.firebase_model.Trip;

import java.util.Comparator;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class DurationComparator implements Comparator<Trip> {

    public int compare(Trip trip1, Trip trip2){
        return ((Double) trip1.timeDuration).compareTo((Double) trip2.timeDuration);
    }
}