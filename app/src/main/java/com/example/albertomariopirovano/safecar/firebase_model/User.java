package com.example.albertomariopirovano.safecar.firebase_model;


import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by albertomariopirovano on 24/04/17.
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public String email;
    public String provider;
    public Boolean active;
    public String photoURL;
    public String authUID;

    public User() {
    }

    //constructor for GOOGLE+ users
    public User(String uid, String name, String email, String photoURL) {
        this.name = name;
        this.email = email;
        this.active = true;
        this.provider = "Google+";
        this.photoURL = photoURL;
        this.authUID = uid;
    }

    //constructor for password/email users
    public User(String uid, String email) {
        this.name = "";
        this.email = email;
        this.active = true;
        this.provider = "password";
        this.photoURL = "";
        this.authUID = uid;
    }
}