package com.example.albertomariopirovano.safecar.realm_model;

import android.util.Log;

import com.example.albertomariopirovano.safecar.activity.MainActivity;
import com.example.albertomariopirovano.safecar.firebase_model.Plug;
import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.firebase_model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albertomariopirovano on 26/04/17.
 */

public class LocalModel {
    // TODO use this class for holding the user data and popolate this static object available in
    // the whole code with the currentUser from database . in this way the network calls are done
    // only once.

    private static final String TAG = MainActivity.class.getSimpleName();
    private static LocalModel ourInstance = new LocalModel();
    private User user;
    private List<Trip> trips;
    private List<Plug> plugs;

    private DatabaseReference database;

    public LocalModel() {
        database = FirebaseDatabase.getInstance().getReference();
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

    public void updateCloudModel() {

        Log.d(TAG, "create resources before exit");

        for (final Trip trip : trips) {
            if (trip.getIsnew()) {
                database.child("trips").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String tripID = database.child("trips").push().getKey();
                        database.child("trips").child(tripID).setValue(trip);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        for (final Plug plug : plugs) {
            if (plug.getActivePlug().equals(Boolean.TRUE)) {
                plug.setActivePlug(Boolean.FALSE);
            }
            if (plug.getIsnew()) {
                database.child("plugs").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        database.child("plugs").child(plug.getPlugId()).setValue(plug);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            if (plug.getIsDropped()) {
                database.child("plugs").child(plug.getPlugId()).removeValue();
            }
        }

        Log.d(TAG, "update resources before exit");

        database.child("users").child(user.authUID).child("percentage").setValue(user.percentage);
        database.child("users").child(user.authUID).child("level").setValue(user.level);
    }

    public void setActivePlug(String id) {
        for (Plug plug : plugs) {
            if (plug.getActivePlug().equals(Boolean.TRUE)) {
                plug.setActivePlug(Boolean.FALSE);
            }
            if (plug.getPlugId().equals(id)) {
                plug.setActivePlug(Boolean.TRUE);
            }
        }

    }

    public void dropPlug(String MAC_key) {
        for (Plug plug : plugs) {
            if (plug.getAddress_MAC().equals(MAC_key)) {
                plug.setIsDropped(Boolean.TRUE);
            }
        }
    }
}
