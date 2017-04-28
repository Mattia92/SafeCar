package com.example.albertomariopirovano.safecar.realm_model;

import io.realm.RealmObject;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class Badge extends RealmObject {

    private String name;
    private String sentence;

    public Badge(){}

    public void setBadge(String name, String sentence) {
        this.name = name;
        this.sentence = sentence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
}
