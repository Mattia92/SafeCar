package com.example.albertomariopirovano.safecar.inner.fragments;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.albertomariopirovano.safecar.services.DirectionsJSONParser;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class TabHome extends Fragment implements TabFragment, OnMapReadyCallback {

    private static final String TAG = "TabHome";
    private final static int REQUEST_ENABLE_BT = 1;
    private String name = "Home";
    private FirebaseAuth auth;
    private DatabaseReference database;

    private ViewFlipper viewFlipper;

    private ProgressBar progressBar;
    private Button scanButton;

    private ImageView currentlyDrivingLogo;
    private ImageView notCurrentlyDrivingLogo;
    private TextView titleBluetoothTriggered;
    private TextView devicesTextView;

    private ImageView pause_resumeTrip;
    private ImageView quitTrip;
    private TextView pause_resumeTripTextView;

    private MapView mapView;
    private GoogleMap map;

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
        database = FirebaseDatabase.getInstance().getReference();

        fm = getActivity().getSupportFragmentManager();

        viewFlipper = (ViewFlipper) v.findViewById(R.id.flipper);

        //child 0 elements
        progressBar = (ProgressBar) v.findViewById(R.id.progressBarHome);
        progressBar.setVisibility(View.GONE);
        scanButton = (Button) v.findViewById(R.id.scanButton);

        //child 1 elements
        currentlyDrivingLogo = (ImageView) v.findViewById(R.id.currentlyDrivingLogo);
        notCurrentlyDrivingLogo = (ImageView) v.findViewById(R.id.notCurrentlyDrivingLogo);
        titleBluetoothTriggered = (TextView) v.findViewById(R.id.entry_text_home);
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

            Log.d(TAG, "RESULT_OK");

        } else {

            Log.d(TAG, "RESULT_CANCELLED");

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

        // Setting onclick event listener for the map
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                Log.d(TAG, "onMapClick");

                // Already two locations
                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    map.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (markerPoints.size() == 1) {

                    Log.d(TAG, "markerPoints size is 1");

                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {

                    Log.d(TAG, "markerPoints size is 2");

                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                map.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {

                    Log.d(TAG, "init query to google maps web service");

                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

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

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.d(TAG, url);

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            Log.d(TAG, "download JSON");

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            Log.d(TAG, "DownloadTask doInBackground");

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "DownloadTask onPostExecute");

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            Log.d(TAG, "ParserTask doInBackground");

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            Log.d(TAG, "ParserTask onPostExecute");

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }
            Log.d(TAG, lineOptions.toString());

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }

        /*googleMap.addMarker(new MarkerOptions()
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .anchor(0.0f, 1.0f)
                .position(new LatLng(37.782832, -122.393981)))
                .setTitle("Loop AI");
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        MapsInitializer.initialize(this.getActivity());
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(37.782832, -122.393981));
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = 0; // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        googleMap.animateCamera(cu);

        }*/
}