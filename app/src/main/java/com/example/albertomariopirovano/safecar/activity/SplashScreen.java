package com.example.albertomariopirovano.safecar.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.firebase_model.Plug;
import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by mattiacrippa on 13/03/17.
 */

//TODO
// 1.Fetch dati utente + salvataggio dati in locale
// 2.Presi i dati dei trip ordiniamo subito per i diversi criteri (in modo da fare slider senza ricalcolo)
// 3.Quando App si chiude cancelliamo tutti i dati in locale

public class SplashScreen extends Activity {

    private static final String TAG = SplashScreen.class.getSimpleName();
    private static int SPLASH_TIME_OUT = 4000;
    private Typeface font;

    private TextView splash_text;

    private DatabaseReference database;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        font = Typeface.createFromAsset(getAssets(), "fonts/Noteworthy-Bold.ttf");

        splash_text = (TextView) findViewById(R.id.splash_text);
        splash_text.setTypeface(font);

        initRealmLocalDb();

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

                Log.i("> Switching activity <", "Splash Activity -> Login Activity");

                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void initRealmLocalDb() {

        Log.i(TAG, "initRealmLocalDb");

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("default2")
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);
    }

    private void initData() {

        database = FirebaseDatabase.getInstance().getReference();

        database.child("flag").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String id = "36pneb31TEcITVHHaNDm5YVggsq2";

                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                    //addTrip(new Trip(id, new Date(), 100, 26, 57.0, 45.465308, 9.186129, 45.610332, 9.348051, "Milano", "Vimercate"));
                    //addTrip(new Trip(id, new Date(), 300, 77, 64.0, 45.062505, 7.679526, 45.324625, 8.415318, "Torino", "Vercelli"));
                    //addTrip(new Trip(id, new Date(), 340, 4, 8.0, 45.610332, 9.348051, 45.623861, 9.322817, "Vimercate", "Arcore"));
                    //addTrip(new Trip(id, new Date(), 120, 56, 64.0, 45.465308, 9.186129, 45.856695, 9.392471, "Milano", "Lecco"));
                    //addTrip(new Trip(id, new Date(), 1000, 2, 4.0, 45.610332, 9.348051, 45.613372, 9.368208, "Vimercate", "Oreno"));
                    //addTrip(new Trip(id, new Date(), 12, 52, 74.0, 45.465308, 9.186129, 45.804600, 9.089425, "Milano", "Como"));
                    //addTrip(new Trip(id, new Date(), 78, 46, 73.0, 45.465308, 9.186129, 45.184332, 9.167233, "Milano", "Pavia"));
                    //addTrip(new Trip(id, new Date(), 444, 215, 240.0, 45.465308, 9.186129, 46.469777, 10.368755, "Milano", "Bormio"));

                    addPlug(new Plug(id, "98:B8:E3:CF:36:24", "Ipad di Alberto"));
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    public void addTrip(Trip t) {
        String tripID = database.child("trips").push().getKey();
        database.child("trips").child(tripID).setValue(t);
    }

    public void addPlug(Plug p) {
        String plugID = database.child("plugs").push().getKey();
        database.child("plugs").child(plugID).setValue(p);
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
