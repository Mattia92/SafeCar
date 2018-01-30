package com.example.albertomariopirovano.safecar.inner.fragments;

import android.content.pm.PackageManager;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.concurrency.DownloadTask;
import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.firebase_model.map.MapPoint;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.example.albertomariopirovano.safecar.utils.OnSwipeListener;
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

public class ReportFragment extends Fragment implements OnMapReadyCallback, TAGInterface, View.OnTouchListener{

    public final static int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final String TAG = "ReportFragment";
    private LinearLayout layout;
    private LinearLayout f1, f2;
    private MapView mapView;
    private GoogleMap map;
    private TableLayout detailsLayout;
    private View v;

    private GestureDetector gestureDetector;

    private Trip t;
    private ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    private LocalModel localModel = LocalModel.getInstance();
    private FloatingActionButton rescan_vis;

    public String getAssignedTag() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.report_fragment, container, false);

        layout = (LinearLayout) v.findViewById(R.id.linlayout);
        f1 = (LinearLayout) v.findViewById(R.id.f1);
        f2 = (LinearLayout) v.findViewById(R.id.f2);
        detailsLayout = (TableLayout) v.findViewById(R.id.table_layout);
        rescan_vis = (FloatingActionButton) v.findViewById(R.id.rescan_vis);

        rescan_vis.setImageResource(R.drawable.ic_clear_black_24dp);

        getActivity().setTitle("After trip report");

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = layout.getMeasuredWidth();
                int height = layout.getMeasuredHeight();

                ViewGroup.LayoutParams params1 = f1.getLayoutParams();
                ViewGroup.LayoutParams params2 = f2.getLayoutParams();

                //Log.i(TAG, String.valueOf(height));
                //Log.i(TAG, String.valueOf(width));
                //Log.i(TAG, String.valueOf(223 * 8));
                //Log.i(TAG, String.valueOf(height));

                params1.height = height;
                params1.width = width;
                f1.requestLayout();

                params2.height = 880;
                params2.width = width;
                f2.requestLayout();
            }
        });

        CardView cv = (CardView) v.findViewById(R.id.details_cardview);
        cv.setVisibility(View.GONE);

        Bundle bundle = this.getArguments();
        t = null;
        if(bundle != null) {
            t = (Trip) bundle.getSerializable("key");
            Log.i(TAG, "Trip to be visualized : " + t.toString());
            rescan_vis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    localModel.dropTrip(t.getTripId());
                    getActivity().onBackPressed();
                }
            });
        }

        ViewTreeObserver viewTreeObserver = detailsLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    detailsLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    Log.i(TAG, "ALERT");
                    Log.i(TAG, String.valueOf(detailsLayout.getMeasuredHeight()));
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

        Log.i(TAG, "The map is ready");
        map = googleMap;

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        drawTrip(t.getMarkers());
        addDetails();
        CardView cv = (CardView) v.findViewById(R.id.details_cardview);
        cv.setVisibility(View.VISIBLE);

        switch (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            case PackageManager.PERMISSION_DENIED:
                Log.i(TAG, "ACCESS COARSE LOCATION denied");
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COARSE_LOCATION);
                break;
            case PackageManager.PERMISSION_GRANTED:
                break;
        }
        map.setMyLocationEnabled(true);

    }

    private void addDetails() {
        Log.i(TAG, "Adding trip details");
        int i = 0;
        for (Map<String, String> map : localModel.getValuesToRender(t)) {

            TableRow row = (TableRow) detailsLayout.getChildAt(i);
            Iterator it = map.entrySet().iterator();
            Map.Entry<String, String> entry1 = (Map.Entry) it.next();
            Map.Entry<String, String> entry2 = (Map.Entry) it.next();

            ((TextView) row.getChildAt(0)).setText(entry1.getValue());
            ((TextView) row.getChildAt(1)).setText(entry2.getValue());

            i++;
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
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    private void drawTrip(List<MapPoint> markers) {
        Log.i(TAG, "Drawing trip path");
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

        Log.i(TAG, "Number of markers: " + String.valueOf(markerPoints.size()));
        Log.i(TAG, "First marker: " + markerPoints.get(0).toString());
        Log.i(TAG, "Last marker: " + markerPoints.get(markers.size() - 1).toString());

        LatLng origin = markerPoints.get(0);
        LatLng dest = markerPoints.get(markers.size() - 1);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);
        Log.i(TAG, "Url: " + url);

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
        Log.i(TAG, "Getting google map directions json");
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