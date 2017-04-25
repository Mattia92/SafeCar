package com.example.albertomariopirovano.safecar.data_comparators;

import com.example.albertomariopirovano.safecar.firebase_model.Trip;

import java.util.Comparator;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class KMComparator implements Comparator<Trip> {

    public int compare(Trip trip1, Trip trip2){
        return ((Integer)trip1.getKm()).compareTo((Integer)trip2.getKm());
    }
}