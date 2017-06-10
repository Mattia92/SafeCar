package com.example.albertomariopirovano.safecar.firebase_model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by albertomariopirovano on 28/04/17.
 */

@IgnoreExtraProperties
public class Plug implements Serializable {

    public String userId;
    public String address_MAC;
    public String name;
    public String plugId;
    public Boolean isnew = Boolean.FALSE;
    public Boolean activePlug = Boolean.FALSE;
    public Boolean isDropped = Boolean.FALSE;

    public Plug() {

    }

    public Plug(String authUID, String address_MAC, String name) {
        this.userId = authUID;
        this.address_MAC = address_MAC;
        this.name = name;
    }

    public Boolean getIsDropped() {
        return isDropped;
    }

    public void setIsDropped(Boolean dropped) {
        isDropped = dropped;
    }

    public Boolean getActivePlug() {
        return activePlug;
    }

    public void setActivePlug(Boolean activePlug) {
        this.activePlug = activePlug;
    }

    public String getPlugId() {
        return plugId;
    }

    public void setPlugId(String plugId) {
        this.plugId = plugId;
    }

    @Override
    public String toString() {
        return "Plug{" +
                "userId='" + userId + '\'' +
                ", address_MAC='" + address_MAC + '\'' +
                ", name='" + name + '\'' +
                ", plugId='" + plugId + '\'' +
                ", isnew=" + isnew +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Boolean getIsnew() {
        return isnew;
    }

    public void setIsnew(Boolean isnew) {
        this.isnew = isnew;
    }
}
