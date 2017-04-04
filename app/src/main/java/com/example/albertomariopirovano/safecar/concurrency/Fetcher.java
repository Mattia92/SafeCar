package com.example.albertomariopirovano.safecar.concurrency;

import com.example.albertomariopirovano.safecar.model.Badge;
import com.example.albertomariopirovano.safecar.model.Trip;
import com.example.albertomariopirovano.safecar.model.User;

import java.util.Date;

import io.realm.Realm;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class Fetcher extends Thread {

    public void run() {

        Realm realm = Realm.getDefaultInstance();
        try {
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
                    t1.setTrip(new Date(), 100, 12, 0.5, "Milano", "Vimercate");
                    u.getTrips().add(t1);
                    t2.setTrip(new Date(), 300, 30, 1,  "Torino", "Vercelli");
                    u.getTrips().add(t2);
                    t3.setTrip(new Date(), 340, 5, 1.2,  "Vimercate", "Arcore");
                    u.getTrips().add(t3);
                    t4.setTrip(new Date(), 120, 45, 0.7,  "Milano", "Lecco");
                    u.getTrips().add(t4);
                    t5.setTrip(new Date(), 1000, 2, 4,  "Vimercate", "Oreno");
                    u.getTrips().add(t5);
                    t6.setTrip(new Date(), 12, 67, 0.2, "Milano", "Como");
                    u.getTrips().add(t6);
                    t7.setTrip(new Date(), 78, 43, 0.5, "Milano", "Pavia");
                    u.getTrips().add(t7);
                    t8.setTrip(new Date(), 444, 123, 2,  "Milano", "Bormio");
                    u.getTrips().add(t8);

                    b1.setBadge("Newbie", "We are happy to have you in our community!");
                    u.getUnlockedBadges().add(b1);

                    System.out.println(u.getUnlockedBadges().get(0).toString());
                    for(Trip t : u.getTrips()) {
                        System.out.println(t.toString());
                    }
                    System.out.println(u.toString());
                }
            });

        } finally {
            realm.close();
        }
    }
}