package com.example.albertomariopirovano.safecar.services;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.activity.MainActivity;
import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.inner.fragments.ReportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class FetchService {

    private static FetchService ourInstance = new FetchService();
    //private List<Trip> tripList;
    FragmentManager fragmentManager;
    private DatabaseReference database;
    private FirebaseAuth auth;

    public FetchService() {
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    public static FetchService getInstance() {
        return ourInstance;
    }

    public void insertTrips(final String attributeToShow, final View v, final Comparator c, final ListView l, int startRange, int endRange) {

        Log.d("insertTrips", "begin");

        fragmentManager = ((MainActivity)v.getContext()).getSupportFragmentManager();

        database.child("trips").orderByChild("userId").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("insertTrips", "data arrived");
                final List<Trip> parsedList = new ArrayList<Trip>();
                for (DataSnapshot parsedTrip : dataSnapshot.getChildren()) {
                    Trip trip = parsedTrip.getValue(Trip.class);
                    Log.d("insertTrips - t", trip.toString());
                    parsedList.add(trip);
                }

                //tripList = parsedList;
                Collections.sort(parsedList, c);

                List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                for (Trip t : parsedList) {
                    Map<String, String> datum = new HashMap<String, String>(2);
                    datum.put("title", t.getDeparture() + " - " + t.getArrival());
                    datum.put("date", String.valueOf(t.getAttr(attributeToShow)));
                    data.add(datum);
                }

                SimpleAdapter adapter = new SimpleAdapter(v.getContext(), data,
                        android.R.layout.simple_list_item_2,
                        new String[]{"title", "date"},
                        new int[]{android.R.id.text1, android.R.id.text2});
                l.setAdapter(adapter);

                Log.d("insertTrips", "setting listeners");

                l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Trip t = parsedList.get(i);
                        Log.d("insertTrips", view.findViewById(android.R.id.text1).toString());
                        Log.d("insertTrips", view.findViewById(android.R.id.text2).toString());

                        ReportFragment rf = new ReportFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("key", t);
                        rf.setArguments(bundle);
                        ((MainActivity) v.getContext()).setEnabledNavigationDrawer(false);
                        fragmentManager.beginTransaction().replace(R.id.main_content, rf).addToBackStack(null).commit();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}