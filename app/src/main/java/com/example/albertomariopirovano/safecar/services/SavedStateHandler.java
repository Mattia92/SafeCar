package com.example.albertomariopirovano.safecar.services;

import android.os.Bundle;

import java.util.HashMap;

/**
 * Created by albertomariopirovano on 10/06/17.
 */

public class SavedStateHandler {

    private static final String TAG = "SavedStateHandler";
    private static SavedStateHandler ourInstance = new SavedStateHandler();
    private HashMap<String, Bundle> savedStates;

    public SavedStateHandler() {
        this.savedStates = new HashMap<String, Bundle>();
    }

    public static SavedStateHandler getInstance() {
        return ourInstance;
    }

    public void addState(String tag, Bundle state) {
        savedStates.put(tag, state);
    }

    public Bundle retrieveState(String tag) {
        return savedStates.get(tag);
    }

    public Boolean hasTag(String tag) {
        return savedStates.keySet().contains(tag);
    }

}
