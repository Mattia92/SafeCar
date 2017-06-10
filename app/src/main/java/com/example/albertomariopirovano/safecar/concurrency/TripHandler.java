package com.example.albertomariopirovano.safecar.concurrency;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.firebase_model.map.MapPoint;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by albertomariopirovano on 26/04/17.
 */

public class TripHandler extends AsyncTask<Void, Void, Void> implements Serializable {

    private static final String TAG = "TripHandler";
    private final static int REQUEST_ENABLE_LOC = 2;
    private final Object lock;
    private Context context;
    private LocalModel localModel = LocalModel.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private LocationManager locationManager;
    private Criteria criteria = new Criteria();

    //private MapPoint startingPoint = new MapPoint();
    //private MapPoint closingPoint = new MapPoint();
    //private List<MapPoint> wayPoints = new ArrayList<MapPoint>();
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
    private String bestProvider;
    private Boolean viewAvailable = Boolean.TRUE;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
            locationManager.removeUpdates(this);
            trip.getMarkers().add(new MapPoint(location.getLatitude(), location.getLongitude()));

            //startingPoint.setLng(location.getLongitude());
            //startingPoint.setLat(location.getLatitude());

            MapPoint mp = trip.getMarkers().get(trip.getMarkers().size() - 1);
            Log.d(TAG, mp.toString());
            Log.d(TAG, "Markers size: " + String.valueOf(trip.getMarkers().size()));
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
    };

    public TripHandler(Context context, ViewFlipper viewFlipper, TextView tripName, TextView dsiEvaluation, GoogleMap map, Object lock) {
        this.context = context;
        this.lock = lock;
        setViewElements(viewFlipper, tripName, dsiEvaluation, map);
        this.trip = new Trip();
        this.markerPoints = new ArrayList<LatLng>();
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.gcd = new Geocoder(context, Locale.getDefault());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground");

        startJob();

        tStart = System.currentTimeMillis();

        while (!stopTask) {

            if ((trip.getMarkers().size() - 2) < 8 && trip.getMarkers().size() > 0) {
                Log.d(TAG, "Markers size: " + String.valueOf(trip.getMarkers().size()));

                //Location wayLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                //MapPoint wayPoint = new MapPoint(wayLocation.getLatitude(), wayLocation.getLongitude());

                //try {
                //    Log.d(TAG, gcd.getFromLocation(wayPoint.getLat(), wayPoint.getLng(), 1).get(0).getLocality());
                //} catch (IOException e) {
                //    e.printStackTrace();
                //}

                //wayPoints.add(wayPoint);

                //getLocation();
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "TripHandler task has been stopped !");

        finishJob();

        return null;
    }

    private void startJob() {
        Log.d(TAG, "startJob");
        getLocation();
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
        trip.getMarkers().add(new MapPoint(45.578680, 9.269462));
        Log.d(TAG, String.valueOf("Markers size: " + trip.getMarkers().size()));

        //startingPoint.setLng(location.getLongitude());
        //startingPoint.setLat(location.getLatitude());

        trip.setUserId(auth.getCurrentUser().getUid());
        trip.setDate(new Date());
        trip.setFinalDSI(fakeDSI);
        trip.setTimeDuration(elapsedMinutes);
        trip.setTripId(database.child("users").push().getKey());
        trip.setIsnew(Boolean.TRUE);

        //trip.getMarkers().add(startingPoint);

        /*for (MapPoint wayPoint : wayPoints) {
            trip.getMarkers().add(wayPoint);
        }*/

        //trip.getMarkers().add(closingPoint);

        for (MapPoint t : trip.getMarkers()) {
            try {
                Log.d(TAG, gcd.getFromLocation(t.getLat(), t.getLng(), 1).get(0).getLocality());
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
            addresses_start = gcd.getFromLocation(trip.getMarkers().get(0).getLat(), trip.getMarkers().get(0).getLng(), 1);
            addresses_close = gcd.getFromLocation(trip.getMarkers().get(trip.getMarkers().size() - 1).getLat(), trip.getMarkers().get(trip.getMarkers().size() - 1).getLng(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        trip.setDepName(addresses_start.get(0).getLocality());
        trip.setArrName(addresses_close.get(0).getLocality());

        Log.d(TAG, trip.toString());

        localModel.getTrips().add(trip);
    }

    public void stopTask() {
        this.stopTask = Boolean.TRUE;
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void getLocation() {
        if (isLocationEnabled()) {
            Log.d(TAG, "location is enabled");
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));

            //You can still do this if you like, you might get lucky:
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                Log.d(TAG, "location is enabled 2");
                trip.getMarkers().add(new MapPoint(location.getLatitude(), location.getLongitude()));
                Log.d(TAG, trip.getMarkers().get(trip.getMarkers().size() - 1).toString());
                Log.d(TAG, "Markers size: " + String.valueOf(trip.getMarkers().size()));
            } else {
                Log.d(TAG, "location is not enabled 2");
                //This is what you need:
                Looper.myLooper().prepare();
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, locationListener, Looper.getMainLooper());
            }
        } else {
            Log.d(TAG, "location not enabled");
            displayLocationSettingsRequest(context);
        }
    }

    private void displayLocationSettingsRequest(final Context context) {
        Log.d(TAG, "- displayLocationSettingsRequest");
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final com.google.android.gms.common.api.Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult((Activity) context, REQUEST_ENABLE_LOC);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
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
            Log.d(TAG, String.valueOf(p));
            LatLng point = new LatLng(p.getLat(), p.getLng());
            Log.d(TAG, String.valueOf(point.latitude));
            Log.d(TAG, String.valueOf(point.longitude));
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

        int width = Double.valueOf(0.55 * context.getResources().getDisplayMetrics().widthPixels).intValue();
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

        synchronized (lock) {
            if (viewAvailable) {
                updateAfterTripVisualization();
            } else {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        viewFlipper.setDisplayedChild(4);
    }

    public void reloadTaskState() {
        Log.d(TAG, "reloadTaskState");
        viewAvailable = Boolean.TRUE;
    }

    public void viewNotAvailable() {
        Log.d(TAG, "viewNotAvailable");
        viewAvailable = Boolean.FALSE;
    }

    public void setViewElements(ViewFlipper viewFlipper, TextView tripName, TextView dsiEvaluation, GoogleMap map) {
        this.tripName = tripName;
        this.dsiEvaluation = dsiEvaluation;
        this.map = map;
        this.viewFlipper = viewFlipper;
    }
}