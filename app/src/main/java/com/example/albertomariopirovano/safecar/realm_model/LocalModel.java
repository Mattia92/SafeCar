package com.example.albertomariopirovano.safecar.realm_model;

import com.example.albertomariopirovano.safecar.firebase_model.Plug;
import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.firebase_model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albertomariopirovano on 26/04/17.
 */

public class LocalModel {
    // TODO use this class for holding the user data and popolate this static object available in
    // the whole code with the currentUser from database . in this way the network calls are done
    // only once.

    private static LocalModel ourInstance = new LocalModel();
    private User user;
    private List<Trip> trips;
    private List<Plug> plugs;

    public LocalModel() {
        setTrips(new ArrayList<Trip>());
        setPlugs(new ArrayList<Plug>());
    }

    public static LocalModel getInstance() {
        return ourInstance;
    }

    public void drop() {
        this.user = null;
        this.trips = null;
        this.plugs = null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(ArrayList<Trip> trips) {
        this.trips = trips;
    }

    public List<Plug> getPlugs() {
        return plugs;
    }

    public void setPlugs(ArrayList<Plug> plugs) {
        this.plugs = plugs;
    }
}
