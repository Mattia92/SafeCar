package com.example.albertomariopirovano.safecar.realm_model;

import com.example.albertomariopirovano.safecar.firebase_model.Plug;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by albertomariopirovano on 28/04/17.
 */

public class LocalPlug extends RealmObject implements Serializable {

    private String address_MAC;
    private String name;

    public LocalPlug() {

    }

    public LocalPlug(Plug plug) {
        this.address_MAC = plug.getAddress_MAC();
        this.name = plug.getName();
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
}