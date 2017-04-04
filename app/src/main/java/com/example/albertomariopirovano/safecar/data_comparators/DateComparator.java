package com.example.albertomariopirovano.safecar.data_comparators;

import com.example.albertomariopirovano.safecar.model.Trip;

import java.util.Comparator;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class DateComparator implements Comparator<Trip> {

    public int compare(Trip trip1, Trip trip2){
        return trip1.getDate().compareTo(trip2.getDate());
    }
}