package com.example.albertomariopirovano.safecar.realm_model;

import java.util.Comparator;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class Help {

    private int id;
    private Comparator comparator;
    private String tag;

    public Help(int id, Comparator comparator, String tag) {
        this.id = id;
        this.comparator = comparator;
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Comparator getComparator() {
        return comparator;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
