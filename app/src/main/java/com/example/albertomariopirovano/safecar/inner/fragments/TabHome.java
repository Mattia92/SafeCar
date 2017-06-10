package com.example.albertomariopirovano.safecar.inner.fragments;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.activity.MainActivity;
import com.example.albertomariopirovano.safecar.concurrency.DSIEvaluator;
import com.example.albertomariopirovano.safecar.concurrency.TripHandler;
import com.example.albertomariopirovano.safecar.firebase_model.Plug;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class TabHome extends Fragment implements TabFragment, OnMapReadyCallback {

    private static final String TAG = MainActivity.class.getSimpleName() + " | TabHome";
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_ENABLE_LOC = 2;
    private String name = "Home";
    private FirebaseAuth auth;

    private LocalModel localModel = LocalModel.getInstance();

    private ViewFlipper viewFlipper;

    private ProgressBar progressBar;
    private Button scanButton;

    private ListView listDevices;
    private Button reScanButton;

    private ImageView currentlyDrivingLogo;
    private ImageView notCurrentlyDrivingLogo;

    private ImageView pause_resumeTrip;
    private ImageView quitTrip;
    private TextView pause_resumeTripTextView;
    private ProgressBar progressBarEndTrip;
    private TextView tripName;
    private TextView dsiEvaluation;

    private MapView mapView;
    private GoogleMap map;

    private ArrayList<Plug> toBeAdded = new ArrayList<Plug>();
    private ArrayList<Plug> found = new ArrayList<Plug>();

    private Plug targetPlug;

    private ListView hintsListView;

    private DatabaseReference database;
    private LocationManager locationManager;

    private Criteria criteria = new Criteria();
    private String bestProvider;

    private View v;

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

                if (found.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "No target device in bluetooth range", Toast.LENGTH_SHORT).show();
                } else if (!found.isEmpty() && (toBeAdded.size() - found.size()) == 0) {

                    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                    for (final Plug plug : toBeAdded) {
                        data.add(new HashMap<String, String>() {
                            {
                                put("name", plug.getName());
                                put("MAC", plug.getAddress_MAC());
                            }
                        });
                    }
                    SimpleAdapter adapter = new SimpleAdapter(v.getContext(), data,
                            android.R.layout.simple_list_item_2,
                            new String[]{"name", "MAC"},
                            new int[]{android.R.id.text1, android.R.id.text2});
                    listDevices.setAdapter(adapter);

                    listDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Plug clicked = toBeAdded.get(i);
                            clicked.setIsnew(Boolean.TRUE);
                            localModel.getPlugs().add(clicked);
                            targetPlug = clicked;
                            localModel.setActivePlug(clicked.getPlugId());

                            viewFlipper.setDisplayedChild(2);
                        }
                    });

                    viewFlipper.setDisplayedChild(1);

                } else {

                    for (Plug p1 : found) {
                        for (Plug p2 : localModel.getPlugs()) {
                            if (p2.getPlugId().equals(p1.getPlugId())) {
                                targetPlug = p1;
                                localModel.setActivePlug(p1.getPlugId());
                            }
                        }
                    }

                    viewFlipper.setDisplayedChild(2);
                }

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.d(TAG, "Found device: NAME: " + device.getName() + " - MAC_ADDRESS" + device.getAddress());

                for (Plug plug : localModel.getPlugs()) {
                    if (plug.address_MAC.equals(device.getAddress()) && !plug.getIsDropped()) {
                        found.add(plug);
                        return;
                    }
                }

                Plug newPlug = new Plug(auth.getCurrentUser().getUid(), device.getAddress(), "Anonymous");
                newPlug.setPlugId(database.child("plugs").push().getKey());

                if (!(device.getName() == null)) {
                    newPlug.setName(device.getName());
                }

                found.add(newPlug);
                toBeAdded.add(newPlug);

            } else {
                Log.d(TAG, "I really don't know why you are here");
            }
        }
    };

    private Boolean registeredReceiver = false;
    private Boolean stopTask = Boolean.FALSE;
    private BluetoothAdapter bluetoothAdapter;
    private TripHandler tripHandler;
    private DSIEvaluator dsiEvaluator;
    private FragmentManager fm;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        googleMapsHandler(savedInstanceState);

    }

    public String getName() {
        return name;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance().getReference();

        fm = getActivity().getSupportFragmentManager();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");

        v = inflater.inflate(R.layout.test, container, false);

        viewFlipper = (ViewFlipper) v.findViewById(R.id.flipper);
        viewFlipper.setDisplayedChild(0);

        //child 0 elements
        progressBar = (ProgressBar) v.findViewById(R.id.progressBarHome);
        progressBar.setVisibility(View.GONE);
        scanButton = (Button) v.findViewById(R.id.scanButton);

        //child 0_bis elements
        listDevices = (ListView) v.findViewById(R.id.listDevices);
        reScanButton = (Button) v.findViewById(R.id.rescan_button);

        //child 1 elements
        currentlyDrivingLogo = (ImageView) v.findViewById(R.id.currentlyDrivingLogo);
        notCurrentlyDrivingLogo = (ImageView) v.findViewById(R.id.notCurrentlyDrivingLogo);
        progressBarEndTrip = (ProgressBar) v.findViewById(R.id.progressBarEndTrip);
        hintsListView = (ListView) v.findViewById(R.id.hint_list_view);
        progressBarEndTrip.setVisibility(View.GONE);

        //child 2 elements
        pause_resumeTrip = (ImageView) v.findViewById(R.id.pause_resumeTrip);
        quitTrip = (ImageView) v.findViewById(R.id.stopTrip);
        pause_resumeTripTextView = (TextView) v.findViewById(R.id.pause_resumeTripTextView);
        tripName = (TextView) v.findViewById(R.id.tripNameVisualization);
        dsiEvaluation = (TextView) v.findViewById(R.id.dsiEvaluationVisualization);

        quitTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarEndTrip.setVisibility(View.VISIBLE);
                tripHandler.stopTask();
                dsiEvaluator.stopTask();
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

        reScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toBeAdded = new ArrayList<Plug>();
                found = new ArrayList<Plug>();
                viewFlipper.setDisplayedChild(0);
            }
        });

        currentlyDrivingLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "click on currently driving");
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    Log.d(TAG, "location not enabled");
                    displayLocationSettingsRequest(getActivity());

                } else {

                    Log.d(TAG, "location enabled");

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Log.d(TAG, String.valueOf(locationManager.getAllProviders().size()));
                    tripHandler = new TripHandler(view.getContext(), viewFlipper, tripName, dsiEvaluation, map);
                    dsiEvaluator = new DSIEvaluator(getActivity(), targetPlug, hintsListView);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        tripHandler.executeOnExecutor(TripHandler.THREAD_POOL_EXECUTOR);
                    } else {
                        tripHandler.execute();
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        dsiEvaluator.executeOnExecutor(DSIEvaluator.THREAD_POOL_EXECUTOR);
                    } else {
                        dsiEvaluator.execute();
                    }

                    viewFlipper.setDisplayedChild(3);

                }
            }
        });

        notCurrentlyDrivingLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(0);
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toBeAdded = new ArrayList<Plug>();
                found = new ArrayList<Plug>();
                if (bluetoothAdapter != null) {

                    Log.d(TAG, "Bluetooth supported");

                    //int permissionCheck = getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
                    //permissionCheck += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
                    //Log.d(TAG, String.valueOf(permissionCheck));
                    //if (permissionCheck != 0) {
                    //    Log.d(TAG, "permissionCheck != 0 (!!!!!!)");
                    //    getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
                    //}

                    IntentFilter filter = new IntentFilter();
                    filter.addAction(BluetoothDevice.ACTION_FOUND);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

                    getActivity().registerReceiver(receiver, filter);

                    registeredReceiver = true;

                    if (!bluetoothAdapter.isEnabled()) {

                        Log.d(TAG, "bluetooth not enabled");
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                    } else {

                        Log.d(TAG, "bluetooth enabled");
                        bluetoothSearchPairedDevices();

                        bluetoothAdapter.cancelDiscovery();
                        bluetoothAdapter.startDiscovery();

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
            }
        });

        return v;
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

    private void googleMapsHandler(Bundle savedInstanceState) {

        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (mapView != null) {
            mapView.getMapAsync(this);
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, String.valueOf(requestCode));

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                Log.d(TAG, "bluetooth activated");
                Toast.makeText(getActivity().getApplicationContext(), "Bluetooth activated !", Toast.LENGTH_SHORT).show();
            } else {

                Log.d(TAG, "bluetooth not activated even if asked");
                Toast.makeText(getActivity().getApplicationContext(), "Bluetooth not activated even if asked. Activate it for using the service !", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == 2) {
            if (resultCode == RESULT_OK) {

                Log.d(TAG, "location activated");
                Toast.makeText(getActivity().getApplicationContext(), "Location activated !", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            } else {

                Log.d(TAG, "location not activated even if asked");
                Toast.makeText(getActivity().getApplicationContext(), "Location not activated even if asked. Activate it for using the service !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");

        mapView.onDestroy();

        if (registeredReceiver) {
            Log.d(TAG, "unregister receiver");
            getActivity().unregisterReceiver(receiver);
        }
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
    }

}