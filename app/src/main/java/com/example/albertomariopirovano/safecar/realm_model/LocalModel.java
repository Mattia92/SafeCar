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
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by albertomariopirovano on 26/04/17.
 */

public class LocalModel {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static LocalModel ourInstance = new LocalModel();
    private User user;
    private List<Trip> trips;
    private List<Plug> plugs;
    private Boolean dropped = Boolean.FALSE;

    private DatabaseReference database;

    public LocalModel() {
        this.database = FirebaseDatabase.getInstance().getReference();
        this.dropped = Boolean.FALSE;
        setTrips(new ArrayList<Trip>());
        setPlugs(new ArrayList<Plug>());
    }

    public static LocalModel getInstance() {
        return ourInstance;
    }

    public void drop() {
        this.dropped = Boolean.TRUE;
        this.user = null;
        this.trips = null;
        this.plugs = null;
    }

    public Boolean getDropped() {
        return dropped;
    }

    public void setDropped(Boolean dropped) {
        this.dropped = dropped;
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

        Log.i(TAG, "synchronize cloud database");

        for (final Trip trip : trips) {
            if (trip.getIsnew() && !trip.getDropped()) {
                database.child("trips").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        database.child("trips").child(trip.getTripId()).setValue(trip);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            if (trip.getDropped()) {
                database.child("trips").child(trip.getTripId()).removeValue();
            }
        }

        for (final Plug plug : plugs) {
            if (plug.getActivePlug().equals(Boolean.TRUE)) {
                plug.setActivePlug(Boolean.FALSE);
            }
            if (plug.getIsnew() && !plug.getIsDropped()) {
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

        Log.i(TAG, "update resources before exit");

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

    public void dropTrip(String trip_id) {
        for (Trip trip : trips) {
            if (trip.getTripId().equals(trip_id)) {
                trip.setDropped(Boolean.TRUE);
            }
        }
    }

    public ArrayList<Map<String, String>> getValuesToRender(final Trip t) {

        ArrayList<Map<String, String>> temp = new ArrayList<Map<String, String>>();
        temp.add(new HashMap<String, String>() {
            {
                put("key", "Arrival");
                put("value", t.getArrName());
            }
        });
        temp.add(new HashMap<String, String>() {
            {
                put("key", "Departure");
                put("value", t.getDepName());
            }
        });
        temp.add(new HashMap<String, String>() {
            {
                put("key", "Date");
                Date date = t.getDate();
                SimpleDateFormat ft = new SimpleDateFormat ("E MMM dd yyyy 'at' HH:mm:ss");
                //put("value", String.valueOf(t.getDate()));
                put("value", ft.format(date));
            }
        });
        temp.add(new HashMap<String, String>() {
            {
                put("key", "DSI");
                put("value", String.valueOf(t.getFinalDSI()));
            }
        });
        temp.add(new HashMap<String, String>() {
            {
                put("key", "KM");
                put("value", String.valueOf(t.getKm()));
            }
        });
        temp.add(new HashMap<String, String>() {
            {
                put("key", "Duration");
                put("value", String.valueOf(t.getTimeDuration()));
            }
        });
        return temp;
    }
}
