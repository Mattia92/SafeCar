package com.example.albertomariopirovano.safecar.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class User extends RealmObject {

    public String name;
    public String surname;
    public String driverLevel;
    public String DSI;
    public RealmList<Badge> unlockedBadges;
    public RealmList<Trip> trips;

    public User(){}

    public void setUser(String name, String surname, String driverLevel, String DSI) {
        this.name = name;
        this.surname = surname;
        this.driverLevel = driverLevel;
        this.DSI = DSI;
        this.unlockedBadges = new RealmList<Badge>();
        this.trips = new RealmList<Trip>();
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", driverLevel='" + driverLevel + '\'' +
                ", DSI='" + DSI + '\'' +
                '}';
    }

    public RealmList<Trip> getTrips() {
        return trips;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDriverLevel(String driverLevel) {
        this.driverLevel = driverLevel;
    }

    public void setDSI(String DSI) {
        this.DSI = DSI;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getDriverLevel() {
        return driverLevel;
    }

    public String getDSI() {
        return DSI;
    }

    public RealmList<Badge> getUnlockedBadges() {
        return unlockedBadges;
    }
}