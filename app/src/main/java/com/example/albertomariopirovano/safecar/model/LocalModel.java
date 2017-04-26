package com.example.albertomariopirovano.safecar.model;

import com.example.albertomariopirovano.safecar.firebase_model.User;

/**
 * Created by albertomariopirovano on 26/04/17.
 */

public class LocalModel {
    // TODO use this class for holding the user data and popolate this static object available in
    // the whole code with the currentUser from database . in this way the network calls are done
    // only once.

    private static LocalModel ourInstance = new LocalModel();
    private User user;

    public LocalModel(User user) {
        this.user = user;
    }

    public LocalModel() {
    }

    public static LocalModel getInstance() {
        return ourInstance;
    }

    public User getUser() {
        return user;
    }
}
