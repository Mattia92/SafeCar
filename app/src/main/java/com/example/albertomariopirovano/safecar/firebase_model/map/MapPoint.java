package com.example.albertomariopirovano.safecar.firebase_model.map;

/**
 * Created by albertomariopirovano on 06/06/17.
 */

public class MapPoint {

    public Double lat;
    public Double lng;

    public MapPoint() {
    }

    public MapPoint(Double lat, Double lng) {
        setLat(lat);
        setLng(lng);
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
