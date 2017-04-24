package com.example.albertomariopirovano.safecar.settings.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.activity.MainActivity;
import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

/**
 * Created by mattiacrippa on 15/03/17.
 */

public class SettingsShareFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    View v;
    Button b;
    private DatabaseReference tripsNode;
    private FirebaseDatabase firebaseInstance;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        v = inflater.inflate(R.layout.settings_merge_accounts, container, false);

        firebaseInstance = FirebaseDatabase.getInstance();

        tripsNode = firebaseInstance.getReference().child("trips");

        Trip t1 = new Trip(new Date(), 100, 26, 57, "Milano", "Vimercate");
        Trip t2 = new Trip(new Date(), 300, 77, 64, "Torino", "Vercelli");
        Trip t3 = new Trip(new Date(), 340, 4, 8, "Vimercate", "Arcore");
        Trip t4 = new Trip(new Date(), 120, 56, 64, "Milano", "Lecco");
        Trip t5 = new Trip(new Date(), 1000, 2, 4, "Vimercate", "Oreno");
        Trip t6 = new Trip(new Date(), 12, 52, 74, "Milano", "Como");
        Trip t7 = new Trip(new Date(), 78, 46, 73, "Milano", "Pavia");
        Trip t8 = new Trip(new Date(), 444, 215, 240, "Milano", "Bormio");

        Log.d("onCreate", "HERE");

        tripsNode.child(tripsNode.push().getKey()).setValue(t1);
        tripsNode.child(tripsNode.push().getKey()).setValue(t2);
        tripsNode.child(tripsNode.push().getKey()).setValue(t3);
        tripsNode.child(tripsNode.push().getKey()).setValue(t4);
        tripsNode.child(tripsNode.push().getKey()).setValue(t5);
        tripsNode.child(tripsNode.push().getKey()).setValue(t6);
        tripsNode.child(tripsNode.push().getKey()).setValue(t7);
        tripsNode.child(tripsNode.push().getKey()).setValue(t8);

        return v;
    }
}
