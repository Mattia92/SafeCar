package com.example.albertomariopirovano.safecar.concurrency;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.firebase_model.map.MapPoint;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by albertomariopirovano on 26/04/17.
 */

public class TripHandler extends AsyncTask<Void, Trip, Void> {

    private static final String TAG = "TripHandler";
    private Context context;
    private LocalModel localModel = LocalModel.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private LocationManager locationManager;
    private Criteria criteria = new Criteria();

    private MapPoint startingPoint = new MapPoint();
    private MapPoint closingPoint = new MapPoint();
    private List<MapPoint> wayPoints = new ArrayList<MapPoint>();
    private float globalDistance = 0;
    private long tStart;
    private Boolean stopTask = Boolean.FALSE;
    private ViewFlipper viewFlipper;

    private TextView tripName;
    private TextView dsiEvaluation;
    private GoogleMap map;
    private ArrayList<LatLng> markerPoints;
    private Trip trip;

    private Geocoder gcd;

    private Integer fakeDSI = 1000;

    public TripHandler(Context context, ViewFlipper viewFlipper, TextView tripName, TextView dsiEvaluation, GoogleMap map) {
        this.context = context;
        this.tripName = tripName;
        this.dsiEvaluation = dsiEvaluation;
        this.map = map;
        this.trip = new Trip();
        this.viewFlipper = viewFlipper;
        this.markerPoints = new ArrayList<LatLng>();
        this.locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        this.gcd = new Geocoder(context, Locale.getDefault());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground");

        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        startJob();

        tStart = System.currentTimeMillis();

        while (!stopTask) {

            if (wayPoints.size() < 8) {
                Log.d(TAG, String.valueOf(stopTask));
                //Location wayLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                //MapPoint wayPoint = new MapPoint(wayLocation.getLatitude(), wayLocation.getLongitude());
                //Log.d(TAG, wayPoint.toString());
                //wayPoints.add(wayPoint);
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        finishJob();

        return null;
    }

    private void startJob() {
        Log.d(TAG, "startJob");
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location startingLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

        startingPoint.setLng(startingLocation.getLongitude());
        startingPoint.setLat(startingLocation.getLatitude());
    }

    public void stopTask() {
        this.stopTask = Boolean.TRUE;
    }

    private void finishJob() {
        Log.d(TAG, "finishJob");

        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedMinutes = (tDelta / 1000.0) / 60;

        //Location closingLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        //closingPoint.setLng(closingLocation.getLongitude());
        //closingPoint.setLat(closingLocation.getLatitude());

        //sample milano
        closingPoint.setLat(45.4629);
        closingPoint.setLng(9.1990);

        trip.setUserId(auth.getCurrentUser().getUid());
        trip.setDate(new Date());
        trip.setFinalDSI(fakeDSI);
        trip.setTimeDuration(new Double(elapsedMinutes));
        trip.setTripId(database.child("users").push().getKey());
        trip.setIsnew(Boolean.TRUE);

        trip.getMarkers().add(startingPoint);

        for (MapPoint wayPoint : wayPoints) {
            trip.getMarkers().add(wayPoint);
        }

        trip.getMarkers().add(closingPoint);

        for (MapPoint t : trip.getMarkers()) {
            try {
                Log.d(TAG, gcd.getFromLocation(t.getLat(), t.getLng(), 1).get(0).getLocality().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int i = 0;
        for (MapPoint mapPoint : trip.getMarkers()) {
            if (i != trip.getMarkers().size() - 1) {
                Log.d(TAG, String.valueOf(i));
                Log.d(TAG, String.valueOf(trip.getMarkers().size()));
                Location loc1 = new Location("");
                loc1.setLatitude(trip.getMarkers().get(i).getLat());
                loc1.setLongitude(trip.getMarkers().get(i).getLng());

                Location loc2 = new Location("");
                loc2.setLatitude(trip.getMarkers().get(i + 1).getLat());
                loc2.setLongitude(trip.getMarkers().get(i + 1).getLng());

                globalDistance = globalDistance + loc1.distanceTo(loc2);
                Log.d(TAG, String.valueOf(globalDistance));
            }
            i++;
        }

        trip.setKm(globalDistance / 1000);

        List<Address> addresses_start = null;
        List<Address> addresses_close = null;
        try {
            addresses_start = gcd.getFromLocation(startingPoint.getLat(), startingPoint.getLng(), 1);
            addresses_close = gcd.getFromLocation(closingPoint.getLat(), closingPoint.getLng(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        trip.setDepName(addresses_start.get(0).getLocality());
        trip.setArrName(addresses_close.get(0).getLocality());

        Log.d(TAG, trip.toString());

        localModel.getTrips().add(trip);
    }

    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");

    }

    private void updateAfterTripVisualization() {
        dsiEvaluation.setText(trip.getFinalDSI().toString());
        tripName.setText(trip.getDepName() + " - " + trip.getArrName());
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        int i = 0;
        for (MapPoint p : trip.getMarkers()) {
            LatLng point = new LatLng(p.getLat(), p.getLng());
            markerPoints.add(point);
            MarkerOptions options = new MarkerOptions();
            options.position(point);
            if (i == 0) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (i == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            } else {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }
            map.addMarker(options);
            i++;
        }

        Log.d(TAG, String.valueOf(markerPoints.size()));
        Log.d(TAG, markerPoints.get(0).toString());
        Log.d(TAG, markerPoints.get(1).toString());

        LatLng origin = markerPoints.get(0);
        LatLng dest = markerPoints.get(1);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask(map);

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : markerPoints) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        map.animateCamera(cu);

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for (int i = 2; i < markerPoints.size(); i++) {
            LatLng point = (LatLng) markerPoints.get(i);
            if (i == 2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d(TAG, "onPostExecute");

        updateAfterTripVisualization();

        viewFlipper.setDisplayedChild(4);
    }
}