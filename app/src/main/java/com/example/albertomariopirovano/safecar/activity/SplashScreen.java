package com.example.albertomariopirovano.safecar.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.model.Badge;
import com.example.albertomariopirovano.safecar.model.Trip;
import com.example.albertomariopirovano.safecar.model.User;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by mattiacrippa on 13/03/17.
 */

//TODO
// 1.Fetch dati utente + salvataggio dati in locale
// 2.Presi i dati dei trip ordiniamo subito per i diversi criteri (in modo da fare slider senza ricalcolo)
// 3.Quando App si chiude cancelliamo tutti i dati in locale

public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 4000;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("default2")
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);

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

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void initData() {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User u = realm.createObject(User.class);
                Trip t1 = realm.createObject(Trip.class);
                Trip t2 = realm.createObject(Trip.class);
                Trip t3 = realm.createObject(Trip.class);
                Trip t4 = realm.createObject(Trip.class);
                Trip t5 = realm.createObject(Trip.class);
                Trip t6 = realm.createObject(Trip.class);
                Trip t7 = realm.createObject(Trip.class);
                Trip t8 = realm.createObject(Trip.class);
                Badge b1 = realm.createObject(Badge.class);

                u.setUser("Alberto", "Pirovano", "Newbie", "100");
                t1.setTrip(new Date(), 100, 26, 57, "Milano", "Vimercate");
                u.getTrips().add(t1);
                t2.setTrip(new Date(), 300, 77, 64,  "Torino", "Vercelli");
                u.getTrips().add(t2);
                t3.setTrip(new Date(), 340, 4, 8,  "Vimercate", "Arcore");
                u.getTrips().add(t3);
                t4.setTrip(new Date(), 120, 56, 64,  "Milano", "Lecco");
                u.getTrips().add(t4);
                t5.setTrip(new Date(), 1000, 2, 4,  "Vimercate", "Oreno");
                u.getTrips().add(t5);
                t6.setTrip(new Date(), 12, 52, 74, "Milano", "Como");
                u.getTrips().add(t6);
                t7.setTrip(new Date(), 78, 46, 73, "Milano", "Pavia");
                u.getTrips().add(t7);
                t8.setTrip(new Date(), 444, 215, 240,  "Milano", "Bormio");
                u.getTrips().add(t8);

                b1.setBadge("Newbie", "We are happy to have you in our community!");
                u.getUnlockedBadges().add(b1);

                /*System.out.println(u.getUnlockedBadges().get(0).toString());
                for(Trip t : u.getTrips()) {
                    System.out.println(t.toString());
                }
                System.out.println(u.toString());*/
            }
        });
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