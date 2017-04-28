package com.example.albertomariopirovano.safecar.realm_model;

import com.example.albertomariopirovano.safecar.firebase_model.User;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by albertomariopirovano on 28/04/17.
 */

public class LocalUser extends RealmObject implements Serializable {

    public String name;
    public String email;
    public String provider;
    public String photoURI;
    public String authUID;
    public String level;
    public String percentage;
    public RealmList<Badge> unlockedBadges;
    public RealmList<LocalTrip> trips;
    public RealmList<LocalPlug> plugs;

    public LocalUser() {
    }

    public void setUserLocal(User u) {
        this.name = u.name;
        this.email = u.email;
        this.level = u.level;
        this.percentage = u.percentage;
        this.provider = u.provider;
        this.photoURI = u.photoURL;
        this.authUID = u.authUID;
        this.unlockedBadges = new RealmList<Badge>();
        this.trips = new RealmList<LocalTrip>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
    }

    public String getAuthUID() {
        return authUID;
    }

    public void setAuthUID(String authUID) {
        this.authUID = authUID;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public RealmList<LocalPlug> getPlugs() {
        return plugs;
    }

    public void setPlugs(RealmList<LocalPlug> plugs) {
        this.plugs = plugs;
    }

    @Override
    public String toString() {
        return "LocalUser{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", provider='" + provider + '\'' +
                ", photoURI='" + photoURI + '\'' +
                ", authUID='" + authUID + '\'' +
                ", level='" + level + '\'' +
                ", percentage='" + percentage + '\'' +
                '}';
    }

    public RealmList<LocalTrip> getTrips() {
        return trips;
    }

    public void setTrips(RealmList<LocalTrip> trips) {
        this.trips = trips;
    }

    public RealmList<Badge> getUnlockedBadges() {
        return unlockedBadges;
    }

    public void setUnlockedBadges(RealmList<Badge> unlockedBadges) {
        this.unlockedBadges = unlockedBadges;
    }
}
