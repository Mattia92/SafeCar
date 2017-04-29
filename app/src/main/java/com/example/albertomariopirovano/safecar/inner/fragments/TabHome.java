package com.example.albertomariopirovano.safecar.inner.fragments;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.activity.MainActivity;
import com.example.albertomariopirovano.safecar.concurrency.DownloadTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class TabHome extends Fragment implements TabFragment, OnMapReadyCallback {

    private static final String TAG = MainActivity.class.getSimpleName() + " | TabHome";
    private final static int REQUEST_ENABLE_BT = 1;
    private String name = "Home";
    private FirebaseAuth auth;

    private ViewFlipper viewFlipper;

    private ProgressBar progressBar;
    private Button scanButton;

    private ImageView currentlyDrivingLogo;
    private ImageView notCurrentlyDrivingLogo;
    private TextView devicesTextView;

    private ImageView pause_resumeTrip;
    private ImageView quitTrip;
    private TextView pause_resumeTripTextView;

    private MapView mapView;
    private GoogleMap map;
    private Button btnDraw;

    private View v;

    private Boolean found = false;
    private String devices = "FOUND DEVICES:\n";
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(TAG, "discovery started");

                progressBar.setVisibility(View.VISIBLE);
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "discovery finished");

                progressBar.setVisibility(View.GONE);

                Log.d("WARNING", devices);

                if (found) {

                    devicesTextView.setText(devices);
                    viewFlipper.setDisplayedChild(1);
                    devices = "FOUND DEVICES:\n";

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No target device in bluetooth range", Toast.LENGTH_SHORT).show();
                }

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.d(TAG, "Found device: NAME: " + device.getName() + " - MAC_ADDRESS" + device.getAddress());

                devices = devices + device.getName() + " " + device.getAddress() + "\n";

                //TODO se il device mac è uno dei plugs dell'utente(test con MAC ipad) allora cambia fragment e stoppa discovery
                if (device.getAddress().equals("98:B8:E3:CF:36:24")) {
                    found = true;
                }

            } else {
                Log.d(TAG, "I really don't know why you are here");
            }
        }
    };
    private ArrayList<LatLng> markerPoints;
    private BluetoothAdapter bluetoothAdapter;
    private FragmentManager fm;

    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.test, container, false);

        Log.d(TAG, "onCreate");

        auth = FirebaseAuth.getInstance();

        fm = getActivity().getSupportFragmentManager();

        viewFlipper = (ViewFlipper) v.findViewById(R.id.flipper);
        viewFlipper.setDisplayedChild(0);

        //child 0 elements
        progressBar = (ProgressBar) v.findViewById(R.id.progressBarHome);
        progressBar.setVisibility(View.GONE);
        scanButton = (Button) v.findViewById(R.id.scanButton);

        //child 1 elements
        currentlyDrivingLogo = (ImageView) v.findViewById(R.id.currentlyDrivingLogo);
        notCurrentlyDrivingLogo = (ImageView) v.findViewById(R.id.notCurrentlyDrivingLogo);
        devicesTextView = (TextView) v.findViewById(R.id.devices);

        //child 2 elements
        pause_resumeTrip = (ImageView) v.findViewById(R.id.pause_resumeTrip);
        quitTrip = (ImageView) v.findViewById(R.id.stopTrip);
        pause_resumeTripTextView = (TextView) v.findViewById(R.id.pause_resumeTripTextView);

        quitTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(3);
            }
        });

        pause_resumeTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pause_resumeTripTextView.getText().equals("Take a break")) {
                    pause_resumeTrip.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    pause_resumeTripTextView.setText("Resume Trip");
                } else {
                    pause_resumeTrip.setImageResource(R.drawable.ic_pause_black_24dp);
                    pause_resumeTripTextView.setText("Take a break");
                }

            }
        });

        currentlyDrivingLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(2);
            }
        });

        notCurrentlyDrivingLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(0);
            }
        });

        //child 3 elements
        btnDraw = (Button) v.findViewById(R.id.btn_draw);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        googleMapsHandler(savedInstanceState);

        // Quick permission check
        int permissionCheck = getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        Log.d(TAG, String.valueOf(permissionCheck));
        if (permissionCheck != 0) {
            Log.d(TAG, "permissionCheck != 0 (!!!!!!)");
            getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
        }

        if (bluetoothAdapter != null) {

            Log.d(TAG, "Bluetooth supported");

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

            getActivity().registerReceiver(receiver, filter);

            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothAdapter.startDiscovery();
                    Log.d(TAG, "-------@@@---------");
                    for (int entry = 0; entry < fm.getBackStackEntryCount(); entry++) {
                        Log.d(TAG, "Found fragment: " + fm.getBackStackEntryAt(entry).getId() + fm.getBackStackEntryAt(entry).getName() + fm.getBackStackEntryAt(entry).getClass());
                    }
                    Log.d(TAG, "-------@@@---------");
                }
            });

            if (!bluetoothAdapter.isEnabled()) {

                Log.d(TAG, "bluetooth not enabled");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            } else {

                Log.d(TAG, "bluetooth enabled");
                bluetoothSearchPairedDevices();

            }

        } else {
            Log.d(TAG, "Bluetooth not supported");
            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity().getApplicationContext(), "Your device doesn't support bluetooth!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return v;
    }

    private void googleMapsHandler(Bundle savedInstanceState) {

        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (mapView != null) {
            mapView.getMapAsync(this);
        }

        markerPoints = new ArrayList<LatLng>();

    }

    private void bluetoothSearchPairedDevices() {

        Log.d(TAG, "search for already paired devices");
        //new BluetoothSearcher(getActivity().getApplicationContext(), targetTextView).execute();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {

            Log.d(TAG, "there are paired devices");
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, deviceName + " " + deviceHardwareAddress);
            }
        } else {
            Log.d(TAG, "there are no paired devices");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            Log.d(TAG, "bluetooth activated");

        } else {

            Log.d(TAG, "bluetooth not activated even if asked");

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "Destroy TabHome");

        mapView.onDestroy();

        if (bluetoothAdapter != null) {
            Log.d(TAG, "unregister receiver");
            getActivity().unregisterReceiver(receiver);
        }
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
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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

        // Setting onclick event listener for the map
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                // Already 10 locations with 8 waypoints and 1 start location and 1 end location.
                // Upto 8 waypoints are allowed in a query for non-business users
                if (markerPoints.size() >= 10) {
                    return;
                }

                // Adding new item to the ArrayList
                markerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED and
                 * for the rest of markers, the color is AZURE
                 */
                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                } else {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                }

                // Add new marker to the Google Map Android API V2
                map.addMarker(options);
            }
        });

        // The map will be cleared on long click
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {
                // Removes all the points from Google Map
                map.clear();

                // Removes all the points in the ArrayList
                markerPoints.clear();
            }
        });

        // Click event handler for Button btn_draw
        btnDraw.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask(map);

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }
            }
        });
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