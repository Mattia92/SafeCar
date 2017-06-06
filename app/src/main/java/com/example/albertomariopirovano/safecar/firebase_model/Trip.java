package com.example.albertomariopirovano.safecar.firebase_model;

import com.example.albertomariopirovano.safecar.firebase_model.map.MapPoint;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by albertomariopirovano on 04/04/17.
 */

@IgnoreExtraProperties
public class Trip implements Serializable {

    public Date date;
    public Integer finalDSI;
    public Float km;
    public Double timeDuration;

    //public Double departure_latitude;
    //public Double departure_longitude;
    //public Double arrival_latitude;
    //public Double arrival_longitude;

    public List<MapPoint> markers = new ArrayList<MapPoint>();

    public String userId;
    public String depName;
    public String arrName;

    public Boolean isnew = Boolean.FALSE;
    public String tripId;

    public Trip() {
    }

    public Trip(String userId,
                Date date,
                Integer finalDSI,
                Float km,
                Double timeDuration,
                //Double arrival_latitude,
                //Double arrival_longitude,
                //Double departure_latitude,
                //Double departure_longitude,
                String depName,
                String arrName) {
        this.date = date;
        this.finalDSI = finalDSI;
        this.km = km;
        this.timeDuration = timeDuration;
        //this.arrival_latitude = arrival_latitude;
        //this.arrival_longitude = arrival_longitude;
        //this.departure_latitude = departure_latitude;
        //this.departure_longitude = departure_longitude;
        this.userId = userId;
        this.depName = depName;
        this.arrName = arrName;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getAttr(String attributeToShow) {
        switch (attributeToShow) {
            case "duration":
                return String.valueOf(timeDuration);
            case "date":
                return String.valueOf(date);
            case "DSI":
                return String.valueOf(finalDSI);
            case "KM":
                return String.valueOf(km);
            default:
                return "error";
        }
    }

    public Boolean getIsnew() {
        return isnew;
    }

    public void setIsnew(Boolean isnew) {
        this.isnew = isnew;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getFinalDSI() {
        return finalDSI;
    }

    public void setFinalDSI(Integer finalDSI) {
        this.finalDSI = finalDSI;
    }

    public Float getKm() {
        return km;
    }

    public void setKm(Float km) {
        this.km = km;
    }

    public Double getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(Double timeDuration) {
        this.timeDuration = timeDuration;
    }

    public List<MapPoint> getMarkers() {
        return markers;
    }

    public void setMarkers(List<MapPoint> markers) {
        this.markers = markers;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public String getArrName() {
        return arrName;
    }

    public void setArrName(String arrName) {
        this.arrName = arrName;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "date=" + date +
                ", finalDSI=" + finalDSI +
                ", km=" + km +
                ", timeDuration=" + timeDuration +
                ", markers=" + markers +
                ", userId='" + userId + '\'' +
                ", depName='" + depName + '\'' +
                ", arrName='" + arrName + '\'' +
                ", isnew=" + isnew +
                ", tripId='" + tripId + '\'' +
                '}';
    }
}