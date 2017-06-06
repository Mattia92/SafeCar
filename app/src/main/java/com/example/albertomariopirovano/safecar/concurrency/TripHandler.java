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

import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.firebase_model.map.MapPoint;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * Created by albertomariopirovano on 26/04/17.
 */

public class TripHandler extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "TripHandler";
    private Context context;
    private LocalModel localModel = LocalModel.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private LocationManager locationManager;
    private Criteria criteria = new Criteria();

    private MapPoint startingPoint = new MapPoint();
    private MapPoint closingPoint = new MapPoint();
    private List<MapPoint> wayPoints = new ArrayList<MapPoint>();
    private Float globalDistances = new Float(0.0);
    private long tStart;

    private Geocoder gcd;

    private Integer fakeDSI = 1000;

    public TripHandler(Context context) {
        this.context = context;
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

        while (true) {

            if (wayPoints.size() < 8) {
                Log.d(TAG, String.valueOf(wayPoints.size()));
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
            if (isCancelled()) {
                Log.d(TAG, "cancelled now");
                break;
            }

        }
        try {
            finishJob();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        Log.d(TAG, startingPoint.getLat().toString());
        Log.d(TAG, startingPoint.getLng().toString());
        try {
            Log.d(TAG, gcd.getFromLocation(startingPoint.getLat(), startingPoint.getLng(), 1).get(0).getLocality().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void finishJob() throws IOException {
        Log.d(TAG, "finishJob");

        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "permission");
            return;
        }
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedMinutes = (tDelta / 1000.0) / 60;

        //Location closingLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        //closingPoint.setLng(closingLocation.getLongitude());
        //closingPoint.setLat(closingLocation.getLatitude());

        //sample vimercate
        closingPoint.setLng(45.6154);
        closingPoint.setLat(9.4680);

        Trip trip = new Trip();
        trip.setUserId(auth.getCurrentUser().getUid());
        trip.setDate(new Date());
        trip.setFinalDSI(fakeDSI);
        trip.setTimeDuration(new Double(elapsedMinutes));

        trip.getMarkers().add(startingPoint);

        for (MapPoint wayPoint : wayPoints) {
            trip.getMarkers().add(wayPoint);
        }

        trip.getMarkers().add(closingPoint);

        for (MapPoint t : trip.getMarkers()) {
            Log.d(TAG, t.toString());
            Log.d(TAG, gcd.getFromLocation(t.getLat(), t.getLng(), 1).get(0).getLocality().toString());
        }

        ListIterator iter = trip.getMarkers().listIterator();

        while (iter.hasNext()) {
            if (iter.nextIndex() != trip.getMarkers().size()) {
                float[] distances = new float[1];
                Location.distanceBetween(trip.getMarkers().get(iter.nextIndex()).getLat(),
                        trip.getMarkers().get(iter.nextIndex()).getLng(),
                        trip.getMarkers().get(iter.nextIndex() + 1).getLat(),
                        trip.getMarkers().get(iter.nextIndex() + 1).getLng(), distances);
                globalDistances = globalDistances + new Float(distances[0]);
            }
        }
        Log.d(TAG, String.valueOf(globalDistances));
        trip.setKm(globalDistances);

        List<Address> addresses_start = gcd.getFromLocation(startingPoint.getLat(), startingPoint.getLng(), 1);
        List<Address> addresses_close = gcd.getFromLocation(closingPoint.getLat(), closingPoint.getLng(), 1);

        trip.setDepName(addresses_start.get(0).getLocality());
        trip.setArrName(addresses_close.get(0).getLocality());

        Log.d(TAG, "ciao");

        Log.d(TAG, trip.toString());

        localModel.getTrips().add(trip);
    }

    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");

    }
}