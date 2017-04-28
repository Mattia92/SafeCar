package com.example.albertomariopirovano.safecar.firebase_model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by albertomariopirovano on 28/04/17.
 */

@IgnoreExtraProperties
public class Plug {

    private String userId;
    private String address_MAC;
    private String name;

    public Plug() {

    }

    public Plug(String authUID, String address_MAC, String name) {
        this.userId = authUID;
        this.address_MAC = address_MAC;
        this.name = name;
    }

    public String getAddress_MAC() {
        return address_MAC;
    }

    public void setAddress_MAC(String address_MAC) {
        this.address_MAC = address_MAC;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthUID() {
        return userId;
    }

    public void setAuthUID(String authUID) {
        this.userId = authUID;
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
