package com.example.albertomariopirovano.safecar.firebase_model;


import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by albertomariopirovano on 24/04/17.
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public String email;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
