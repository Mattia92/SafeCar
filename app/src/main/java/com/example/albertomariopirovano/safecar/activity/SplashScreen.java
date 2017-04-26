package com.example.albertomariopirovano.safecar.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Created by mattiacrippa on 13/03/17.
 */

//TODO
// 1.Fetch dati utente + salvataggio dati in locale
// 2.Presi i dati dei trip ordiniamo subito per i diversi criteri (in modo da fare slider senza ricalcolo)
// 3.Quando App si chiude cancelliamo tutti i dati in locale

public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 4000;

    private Typeface font;

    private TextView splash_text;

    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");

        splash_text = (TextView) findViewById(R.id.splash_text);
        splash_text.setTypeface(font);

        initData();

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void initData() {

        database = FirebaseDatabase.getInstance().getReference();

        database.child("flag").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String id = "PUyyqvKwsaevqS399UO8DV2PH1D2";

                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                    addTrip(new com.example.albertomariopirovano.safecar.firebase_model.Trip(id, new Date(), 100, 26, 57, "Milano", "Vimercate"));
                    addTrip(new com.example.albertomariopirovano.safecar.firebase_model.Trip(id, new Date(), 300, 77, 64, "Torino", "Vercelli"));
                    addTrip(new com.example.albertomariopirovano.safecar.firebase_model.Trip(id, new Date(), 340, 4, 8, "Vimercate", "Arcore"));
                    addTrip(new com.example.albertomariopirovano.safecar.firebase_model.Trip(id, new Date(), 120, 56, 64, "Milano", "Lecco"));
                    addTrip(new com.example.albertomariopirovano.safecar.firebase_model.Trip(id, new Date(), 1000, 2, 4, "Vimercate", "Oreno"));
                    addTrip(new com.example.albertomariopirovano.safecar.firebase_model.Trip(id, new Date(), 12, 52, 74, "Milano", "Como"));
                    addTrip(new com.example.albertomariopirovano.safecar.firebase_model.Trip(id, new Date(), 78, 46, 73, "Milano", "Pavia"));
                    addTrip(new com.example.albertomariopirovano.safecar.firebase_model.Trip(id, new Date(), 444, 215, 240, "Milano", "Bormio"));
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    public void addTrip(com.example.albertomariopirovano.safecar.firebase_model.Trip t) {
        String tripID = database.child("trips").push().getKey();
        database.child("trips").child(tripID).setValue(t);
    }

    private void resizeFragment(Fragment f, int newWidth, int newHeight) {
        if (f != null) {
            View view = f.getView();
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(newWidth, newHeight);
            view.setLayoutParams(p);
            view.requestLayout();
        }
    }
}
