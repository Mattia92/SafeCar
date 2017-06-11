package com.example.albertomariopirovano.safecar.inner.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.concurrency.DownloadTask;
import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.firebase_model.map.MapPoint;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class ReportFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "ReportFragment";

    private LinearLayout layout;
    private LinearLayout f1, f2;
    private MapView mapView;
    private GoogleMap map;
    private View v;
    private View cardViewWrapper;
    private ArrayList<CardView> details = new ArrayList<CardView>();
    private Integer cardViewHeight = 352;

    private Trip t;
    private ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    private LocalModel localModel = LocalModel.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.report_fragment, container, false);

        layout = (LinearLayout) v.findViewById(R.id.linlayout);
        f1 = (LinearLayout) v.findViewById(R.id.f1);
        f2 = (LinearLayout) v.findViewById(R.id.f2);

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = layout.getMeasuredWidth();
                int height = layout.getMeasuredHeight();

                ViewGroup.LayoutParams params1 = f1.getLayoutParams();
                ViewGroup.LayoutParams params2 = f2.getLayoutParams();

                Log.d(TAG, String.valueOf(height));
                Log.d(TAG, String.valueOf(width));
                Log.d(TAG, String.valueOf(223 * 8));
                Log.d(TAG, String.valueOf(height));

                params1.height = height;
                params1.width = width;
                f1.requestLayout();

                params2.height = 223 * 8;
                params2.width = width;
                f2.requestLayout();
            }
        });

        Bundle bundle = this.getArguments();
        t = null;
        if(bundle != null) {
            t = (Trip) bundle.getSerializable("key");
            Log.d(TAG, t.toString());
        }
        for (Map<String, String> map : localModel.getValuesToRender(t)) {
            Iterator it = map.entrySet().iterator();
            cardViewWrapper = LayoutInflater.from(getActivity()).inflate(R.layout.cardview, layout, false);
            Map.Entry<String, String> entry1 = (Map.Entry) it.next();
            CardView cardView = (CardView) cardViewWrapper.findViewById(R.id.cardviewelement);
            LinearLayout firstChild = ((LinearLayout) cardView.getChildAt(0));
            TextView tv1 = (TextView) firstChild.getChildAt(0);
            tv1.setText(entry1.getValue());
            Map.Entry<String, String> entry2 = (Map.Entry) it.next();
            TextView tv2 = (TextView) firstChild.getChildAt(1);
            tv2.setText(entry2.getValue());
            details.add(cardView);
        }

        ViewTreeObserver viewTreeObserver = details.get(0).getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    details.get(0).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    Log.d(TAG, String.valueOf(details.get(0).getMeasuredHeight()));
                }
            });
        }

        mapView = (MapView) v.findViewById(R.id.reportMap);
        mapView.onCreate(savedInstanceState);
        if (mapView != null) {
            mapView.getMapAsync(this);
        }

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady");

        map = googleMap;

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        drawTrip(t.getMarkers());
        addDetails();

    }

    private void addDetails() {
        for (CardView cv : details) {
            f2.addView(cv);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");

        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mapView.onPause();
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

        int width = Double.valueOf(0.55 * getActivity().getResources().getDisplayMetrics().widthPixels).intValue();
        int height = getActivity().getResources().getDisplayMetrics().heightPixels;
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
}