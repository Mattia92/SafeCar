package com.example.albertomariopirovano.safecar.inner.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class ReportFragment extends Fragment implements OnMapReadyCallback, TAGInterface {

    public final static int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final String TAG = "ReportFragment";
    private LinearLayout layout;
    private LinearLayout f1, f2;
    private MapView mapView;
    private GoogleMap map;
    private TableLayout detailsLayout;
    private View v;

    private Trip t;
    private ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    private LocalModel localModel = LocalModel.getInstance();
    private int oldY = 0;
    private ArrayList<Animation> returnList_tv;
    private ArrayList<Animation> returnList_iv;

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
        FloatingActionButton rescan_vis = (FloatingActionButton) v.findViewById(R.id.rescan_vis);

        getActivity().setTitle("After trip report");

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = layout.getMeasuredWidth();
                int height = layout.getMeasuredHeight();

                ViewGroup.LayoutParams params1 = f1.getLayoutParams();

                params1.height = height;
                params1.width = width;
                f1.requestLayout();
            }
        });

        LinearLayout cv = (LinearLayout) v.findViewById(R.id.details_cardview);
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

        mapView = (MapView) v.findViewById(R.id.reportMap);
        mapView.onCreate(savedInstanceState);
        if (mapView != null) {
            mapView.getMapAsync(this);
        }

        final TextView tv = (TextView) v.findViewById(R.id.scrollFroInfo);
        final ImageView iv = (ImageView) v.findViewById(R.id.scrollIcon);
        final ScrollView scv = (ScrollView) v.findViewById(R.id.scrollContext);

        scv.fullScroll(ScrollView.FOCUS_UP);
        returnList_tv = setUpFadeAnimation(tv);
        returnList_iv = setUpFadeAnimation(iv);

        scv.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (!(getActivity() == null)) {
                    int scrollY = scv.getScrollY();

                    if (oldY < scrollY){
                        scv.fullScroll(ScrollView.FOCUS_DOWN);
                        returnList_tv.get(0).setAnimationListener(null);
                        returnList_tv.get(1).setAnimationListener(null);
                        returnList_iv.get(0).setAnimationListener(null);
                        returnList_iv.get(1).setAnimationListener(null);
                        tv.setVisibility(View.GONE);
                        iv.setVisibility(View.GONE);
                    } else {
                        scv.fullScroll(ScrollView.FOCUS_UP);
                        tv.setVisibility(View.VISIBLE);
                        iv.setVisibility(View.VISIBLE);
                        returnList_tv = setUpFadeAnimation(tv);
                        returnList_iv = setUpFadeAnimation(iv);
                    }

                    oldY = scrollY;
                }
            }
        });
        return v;
    }

    private ArrayList<Animation> setUpFadeAnimation(final View view) {
        // Start from 0.1f if you desire 90% fade animation
        final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(2000);
        fadeIn.setStartOffset(500);
        // End to 0.1f if you desire 90% fade animation
        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(3000);
        fadeOut.setStartOffset(1000);

        fadeIn.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeOut when fadeIn ends (continue)
                view.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeIn when fadeOut ends (repeat)
                view.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        view.startAnimation(fadeOut);
        ArrayList<Animation> returnList = new ArrayList<Animation>();
        returnList.add(fadeIn);
        returnList.add(fadeOut);

        return returnList;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.i(TAG, "The map is ready");
        map = googleMap;

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        drawTrip(t.getMarkers());
        LinearLayout cv = (LinearLayout) v.findViewById(R.id.details_cardview);
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
        popolate_report();
    }


    private void popolate_report() {
        addDetails();
        /*
        ViewTreeObserver viewTreeObserver = f2.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    f2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    ViewGroup.LayoutParams params3 = f2.getLayoutParams();
                    params3.height = f2.getMeasuredHeight();
                    params3.width = f2.getMeasuredWidth();
                    f2.requestLayout();
                }
            });
        }*/
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