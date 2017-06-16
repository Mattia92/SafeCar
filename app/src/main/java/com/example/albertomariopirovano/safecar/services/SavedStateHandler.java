package com.example.albertomariopirovano.safecar.services;

import android.os.Bundle;

import com.example.albertomariopirovano.safecar.firebase_model.Plug;

import java.util.HashMap;

/**
 * Created by albertomariopirovano on 10/06/17.
 */

public class SavedStateHandler {

    private static final String TAG = "SavedStateHandler";
    private static SavedStateHandler ourInstance = new SavedStateHandler();
    private final Object lock = new Object();
    private HashMap<String, Bundle> savedStates;
    private Plug targetPlug;

    public SavedStateHandler() {
        this.savedStates = new HashMap<String, Bundle>();
    }

    public static SavedStateHandler getInstance() {
        return ourInstance;
    }

    public Plug getTargetPlug() {
        return targetPlug;
    }

    public void setTargetPlug(Plug targetPlug) {
        this.targetPlug = targetPlug;
    }

    public void waitOnLock(Integer time) {
        synchronized (lock) {
            try {
                lock.wait(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyLock() {
        synchronized (lock) {
            lock.notifyAll();
        }
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

    public void removeState(String id) {
        savedStates.remove(id);
    }

    public Boolean isEmpty() {
        return savedStates.isEmpty();
    }
}
