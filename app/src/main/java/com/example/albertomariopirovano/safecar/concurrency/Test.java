package com.example.albertomariopirovano.safecar.concurrency;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by albertomariopirovano on 08/06/17.
 */

public class Test implements LocationListener {

    private LocationManager lm;
    private Context context;

    public Test(Context context) {
        context = context;
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onLocationChanged(Location location) {
        lm.removeUpdates((android.location.LocationListener) this);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
