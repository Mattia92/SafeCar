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
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.firebase_model.map.MapPoint;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.example.albertomariopirovano.safecar.services.SavedStateHandler;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by albertomariopirovano on 26/04/17.
 */

public class TripHandler extends AsyncTask<Void, Void, Void> implements Serializable {

    public final static int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final String TAG = "TripHandler";
    private final static int REQUEST_ENABLE_LOC = 2;
    private final Object lock;
    private Context context;
    private LocalModel localModel = LocalModel.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private SavedStateHandler savedStateHandler = SavedStateHandler.getInstance();

    private LocationManager locationManager;
    private Criteria criteria = new Criteria();

    //private MapPoint startingPoint = new MapPoint();
    //private MapPoint closingPoint = new MapPoint();
    //private List<MapPoint> wayPoints = new ArrayList<MapPoint>();
    private float globalDistance = 0;
    private long tStart;
    private Boolean stopTask = Boolean.FALSE;
    private ViewFlipper viewFlipper;

    private LinearLayout linLayout;
    private LinearLayout f2;
    private GoogleMap map;
    private TableLayout detailsLayout;
    private ArrayList<CardView> details = new ArrayList<CardView>();
    private ArrayList<LatLng> markerPoints;
    private Trip trip;

    private Geocoder gcd;

    private Integer fakeDSI = 1000;
    private Boolean viewAvailable = Boolean.TRUE;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
            locationManager.removeUpdates(this);
            trip.getMarkers().add(new MapPoint(location.getLatitude(), location.getLongitude()));

            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, trip.getMarkers().get(trip.getMarkers().size() - 1).toString(), Toast.LENGTH_SHORT).show();
                }
            });

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

    public TripHandler(Context context, ViewFlipper viewFlipper, TableLayout detailsLayout, GoogleMap map, Object lock) {
        this.context = context;
        this.lock = lock;
        setViewElements(viewFlipper, detailsLayout, map);
        this.trip = new Trip();
        this.markerPoints = new ArrayList<LatLng>();
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.gcd = new Geocoder(context, Locale.getDefault());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground");

        //startJob();
        //Log.d(TAG, "after startJob");

        tStart = System.currentTimeMillis();

        while (!stopTask) {

            /*
            try {
                //if the mappoint is very near to the last one picked drop it ( implicit cleaning )
                Log.d(TAG, "sleep this thread for a while");
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */

            if (trip.getMarkers().size() + 1 < 8) {
                //Log.d(TAG, "Markers size: " + String.valueOf(trip.getMarkers().size()));

                //Location wayLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                //MapPoint wayPoint = new MapPoint(wayLocation.getLatitude(), wayLocation.getLongitude());

                /*try {
                    Log.d(TAG, gcd.getFromLocation(wayPoint.getLat(), wayPoint.getLng(), 1).get(0).getLocality());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                //wayPoints.add(wayPoint);

                getLocation();

            } else {
                Log.d(TAG, "No more markers allowed");
            }

            Log.d(TAG, "wait on lock");
            long s = System.currentTimeMillis();
            savedStateHandler.waitOnLock(20000);
            long f = System.currentTimeMillis();
            Log.d(TAG, "awakened from lock t = " + String.valueOf(f - s));
        }
        Log.d(TAG, "TripHandler task has been stopped !");

        finishJob();

        return null;
    }

    /*
    private void startJob() {

        Log.d(TAG, "startJob");
        getLocation();
    }
    */

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
        double d = Math.random();
        Log.d(TAG, String.valueOf(d));

        /*
        if (d <= 0.2) {
            trip.getMarkers().add(new MapPoint(45.622328, 9.319304)); // arcore
            trip.getMarkers().add(new MapPoint(45.578680, 9.269462)); // milano
        } else if (d > 0.2 && d <= 0.4) {
            trip.getMarkers().add(new MapPoint(45.582167, 9.349979)); // agrate
            trip.getMarkers().add(new MapPoint(45.551752, 9.298702)); // brugherio
        } else if (d > 0.4 && d <= 0.6) {
            trip.getMarkers().add(new MapPoint(45.622328, 9.319304)); // arcore
            trip.getMarkers().add(new MapPoint(45.646461, 9.307228)); // lesmo
        } else if (d > 0.6 && d <= 0.8) {
            trip.getMarkers().add(new MapPoint(45.614278, 9.419443)); // bellusco
            trip.getMarkers().add(new MapPoint(45.635639, 9.412620)); // aicurzio
        } else {
            trip.getMarkers().add(new MapPoint(45.599730, 9.357705)); // torri bianche
            trip.getMarkers().add(new MapPoint(45.590635, 9.332360)); // concorezzo
        }
        */

        getLocation();

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
                Location loc1 = new Location("");
                loc1.setLatitude(trip.getMarkers().get(i).getLat());
                loc1.setLongitude(trip.getMarkers().get(i).getLng());

                Location loc2 = new Location("");
                loc2.setLatitude(trip.getMarkers().get(i + 1).getLat());
                loc2.setLongitude(trip.getMarkers().get(i + 1).getLng());

                globalDistance = globalDistance + loc1.distanceTo(loc2);
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

    private void getLocation() {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //Log.d(TAG, "Newtork provider enabled");
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
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                //Log.d(TAG, "Location taken with Newtork Provider is not null");
                trip.getMarkers().add(new MapPoint(location.getLatitude(), location.getLongitude()));

                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, trip.getMarkers().get(trip.getMarkers().size() - 1).toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                Log.d(TAG, trip.getMarkers().get(trip.getMarkers().size() - 1).toString());
                Log.d(TAG, "Markers size: " + String.valueOf(trip.getMarkers().size()));
            } else {
                Log.d(TAG, "Location taken with Newtork Provider is null");
                //This is what you need:
                if (Looper.myLooper() == null) {
                    Log.d(TAG, "looper == null");
                    Looper.myLooper().prepare();
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener, Looper.getMainLooper());
            }
        } else {
            //Log.d(TAG, "Newtork provider NOT enabled");
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //Log.d(TAG, "GPS provider enabled");

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
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    //Log.d(TAG, "Location taken with GPS Provider is not null");
                    trip.getMarkers().add(new MapPoint(location.getLatitude(), location.getLongitude()));

                    ((Activity) context).runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, trip.getMarkers().get(trip.getMarkers().size() - 1).toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.d(TAG, trip.getMarkers().get(trip.getMarkers().size() - 1).toString());
                    Log.d(TAG, "Markers size: " + String.valueOf(trip.getMarkers().size()));
                } else {
                    Log.d(TAG, "Location taken with GPS Provider is null");
                    //This is what you need:
                    if (Looper.myLooper() == null) {
                        Log.d(TAG, "looper == null");
                        Looper.myLooper().prepare();
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener, Looper.getMainLooper());
                }
            } else {
                Log.d(TAG, "GPS provider NOT enabled");
                displayLocationSettingsRequest(context);
            }
        }
    }

    private void displayLocationSettingsRequest(final Context context) {
        Log.d(TAG, "displayLocationSettingsRequest");
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

    private void restoreAfterTripVisualization(Trip trip) {

        int i = 0;
        for (Map<String, String> map : localModel.getValuesToRender(trip)) {

            TableRow row = (TableRow) detailsLayout.getChildAt(i);
            Iterator it = map.entrySet().iterator();
            Map.Entry<String, String> entry1 = (Map.Entry) it.next();
            Map.Entry<String, String> entry2 = (Map.Entry) it.next();

            ((TextView) row.getChildAt(0)).setText(entry1.getValue());
            ((TextView) row.getChildAt(1)).setText(entry2.getValue());

            i++;
        }

        drawTrip(trip.getMarkers());
    }

    private void drawTrip(List<MapPoint> markers) {

        int i = 0;
        for (MapPoint p : markers) {
            LatLng point = new LatLng(p.getLat(), p.getLng());
            markerPoints.add(point);
            MarkerOptions options = new MarkerOptions();
            options.position(point);
            if (i == 0) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (i == (markers.size() - 1)) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            } else {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }
            map.addMarker(options);
            i++;
        }

        Log.d(TAG, String.valueOf(markerPoints.size()));
        //Log.d(TAG, markerPoints.get(0).toString());
        //Log.d(TAG, markerPoints.get(markers.size() - 1).toString());

        LatLng origin = markerPoints.get(0);
        LatLng dest = markerPoints.get(markers.size() - 1);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);
        Log.d(TAG, url);

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
        String waypoints = "waypoints=";
        for (int i = 1; i < markerPoints.size() - 1; i++) {
            LatLng point = (LatLng) markerPoints.get(i);
            //Log.d(TAG, point.toString());
            waypoints += point.latitude + "," + point.longitude + "|";
            //Log.d(TAG, waypoints);
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
                restoreAfterTripVisualization(trip);
            } else {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        viewFlipper.setDisplayedChild(2);
    }

    public void reloadTaskState() {
        Log.d(TAG, "reloadTaskState");
        viewAvailable = Boolean.TRUE;
    }

    public void viewNotAvailable() {
        Log.d(TAG, "viewNotAvailable");
        viewAvailable = Boolean.FALSE;
    }

    public void setViewElements(ViewFlipper viewFlipper, TableLayout detailsLayout, GoogleMap map) {
        this.detailsLayout = detailsLayout;
        this.map = map;
        this.viewFlipper = viewFlipper;
    }
}