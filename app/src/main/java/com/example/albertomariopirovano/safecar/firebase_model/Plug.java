package com.example.albertomariopirovano.safecar.firebase_model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by albertomariopirovano on 28/04/17.
 */

@IgnoreExtraProperties
public class Plug {

    public String userId;
    public String address_MAC;
    public String name;

    public Plug() {

    }

    public Plug(String authUID, String address_MAC, String name) {
        this.userId = authUID;
        this.address_MAC = address_MAC;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Plug{" +
                "userId='" + userId + '\'' +
                ", address_MAC='" + address_MAC + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
