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
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.activity.MainActivity;
import com.example.albertomariopirovano.safecar.concurrency.DSIEvaluator;
import com.example.albertomariopirovano.safecar.concurrency.DownloadTask;
import com.example.albertomariopirovano.safecar.concurrency.TripHandler;
import com.example.albertomariopirovano.safecar.data_comparators.DateComparator;
import com.example.albertomariopirovano.safecar.firebase_model.Plug;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private SavedStateHandler savedStateHandler = SavedStateHandler.getInstance();
    private DatabaseReference database;
    private LocationManager locationManager;
    private BluetoothAdapter bluetoothAdapter;
    private Criteria criteria = new Criteria();


    private View v;
    private ViewFlipper viewFlipper;
    private ProgressBar progressBar;
    private Button scanButton;
    private ListView listDevices;
    private ListView hintsListView;
    private Button reScanButton;
    private ImageView currentlyDrivingLogo;
    private ImageView notCurrentlyDrivingLogo;
    private ImageView pause_resumeTrip;
    private ImageView quitTrip;
    private TextView pause_resumeTripTextView;
    private ProgressBar progressBarEndTrip;
    private MapView mapView;
    private GoogleMap map;
    private LinearLayout f1;
    private LinearLayout f2;
    private LinearLayout layout;
    private View cardViewWrapper;
    private ArrayList<CardView> details = new ArrayList<CardView>();


    private ArrayList<Plug> toBeAdded = new ArrayList<Plug>();
    private ArrayList<Plug> found = new ArrayList<Plug>();
    private ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    private Plug targetPlug;


    private Boolean isBluetoothScanning = Boolean.FALSE;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //Log.d(TAG, "discovery started");

                isBluetoothScanning = Boolean.TRUE;
                //Log.d(TAG, String.valueOf(isBluetoothScanning));

                progressBar.setVisibility(View.VISIBLE);
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Log.d(TAG, "discovery finished");

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

                isBluetoothScanning = Boolean.FALSE;
                //Log.d(TAG, String.valueOf(isBluetoothScanning));

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

    private Object lock1 = new Object();
    private Object lock2 = new Object();


    private TripHandler tripHandler;
    private DSIEvaluator dsiEvaluator;

    public String getName() {
        return name;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance().getReference();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");

        v = inflater.inflate(R.layout.test, container, false);

        viewFlipper = (ViewFlipper) v.findViewById(R.id.flipper);

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

        //child 3 elements
        googleMapsHandler(savedInstanceState);
        layout = (LinearLayout) v.findViewById(R.id.linlayout);
        f1 = (LinearLayout) v.findViewById(R.id.f1);
        f2 = (LinearLayout) v.findViewById(R.id.f2);

        setListeners();

        return v;
    }

    private void setListeners() {
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

        quitTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitTrip();
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
                startTrip(view);
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
                startBluetoothScan();
            }
        });
    }

    private void loadStateIfNeeded() {
        if (savedStateHandler.hasTag("TabHome")) {
            Log.d(TAG, "loading previous state");
            Bundle oldState = savedStateHandler.retrieveState("TabHome");

            Log.d(TAG, String.valueOf(oldState.getInt("viewFlipperKey")));

            if (oldState.getInt("viewFlipperKey") == 0) {
                if (oldState.getBoolean("isBluetoothScanning")) {
                    startBluetoothScan();
                }
            } else if (oldState.getInt("viewFlipperKey") == 2) {
                targetPlug = (Plug) oldState.getSerializable("targetPlug");
            } else if (oldState.getInt("viewFlipperKey") == 3) {
                if (oldState.getString("pause_resumeTripTextView").equals("Resume Trip")) {
                    pause_resumeTrip.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    pause_resumeTripTextView.setText("Resume Trip");
                } else {
                    pause_resumeTrip.setImageResource(R.drawable.ic_pause_black_24dp);
                    pause_resumeTripTextView.setText("Take a break");
                }
                tripHandler = (TripHandler) oldState.getSerializable("tripHandler");
                tripHandler.setViewElements(viewFlipper, layout, f2, map);
                dsiEvaluator = (DSIEvaluator) oldState.getSerializable("dsiEvaluator");
                dsiEvaluator.setViewElements(hintsListView);
                synchronized (lock1) {
                    tripHandler.reloadTaskState();
                    lock1.notifyAll();
                }
                synchronized (lock2) {
                    dsiEvaluator.reloadTaskState();
                    lock2.notifyAll();
                }
            } else if (oldState.getInt("viewFlipperKey") == 4) {
                //dsiEvaluation.setText(oldState.getString("dsiEvaluationVisualization"));
                //tripName.setText(oldState.getString("tripNameVisualization"));
                //ArrayList<MapPoint> markers = oldState.getParcelableArrayList("markersToBePlaced");
                //drawTrip(markers);
                Trip trip = (Trip) oldState.getSerializable("trip");
                restoreAfterTripVisualization(trip);
            }
            savedStateHandler.removeState("TabHome");
            // restore the old state
            viewFlipper.setDisplayedChild(oldState.getInt("viewFlipperKey"));

        } else {
            viewFlipper.setDisplayedChild(0);
        }
    }

    private void quitTrip() {
        progressBarEndTrip.setVisibility(View.VISIBLE);
        tripHandler.stopTask();
        dsiEvaluator.stopTask();
    }

    private void startBluetoothScan() {
        toBeAdded = new ArrayList<Plug>();
        found = new ArrayList<Plug>();
        if (bluetoothAdapter != null) {

            //Log.d(TAG, "Bluetooth supported");

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

                //Log.d(TAG, "Bluetooth not enabled");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            } else {

                //Log.d(TAG, "Bluetooth enabled");
                //bluetoothSearchPairedDevices();

                bluetoothAdapter.cancelDiscovery();
                bluetoothAdapter.startDiscovery();

            }

        } else {
            //Log.d(TAG, "Bluetooth not supported");
            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity().getApplicationContext(), "Your device doesn't support bluetooth!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void startTrip(View view) {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            //Log.d(TAG, "Location not enabled");
            displayLocationSettingsRequest(getActivity());

        } else {

            //Log.d(TAG, "Location enabled");

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
            tripHandler = new TripHandler(view.getContext(), viewFlipper, layout, f2, map, lock1);
            dsiEvaluator = new DSIEvaluator(getActivity(), targetPlug, hintsListView, lock2);

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

    private void displayLocationSettingsRequest(final Context context) {
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
                        //Log.d(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //Log.d(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult((Activity) context, REQUEST_ENABLE_LOC);
                        } catch (IntentSender.SendIntentException e) {
                            //Log.d(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Log.d(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private void googleMapsHandler(Bundle savedInstanceState) {

        mapView = (MapView) v.findViewById(R.id.reportMap);
        mapView.onCreate(savedInstanceState);
        if (mapView != null) {
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                //Log.d(TAG, "Bluetooth activated");
                Toast.makeText(getActivity().getApplicationContext(), "Bluetooth activated !", Toast.LENGTH_SHORT).show();
            } else {

                //Log.d(TAG, "Bluetooth not activated even if asked");
                Toast.makeText(getActivity().getApplicationContext(), "Bluetooth not activated even if asked. Activate it for using the service !", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == 2) {
            if (resultCode == RESULT_OK) {

                //Log.d(TAG, "Location activated");
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

                //Log.d(TAG, "Location not activated even if asked");
                Toast.makeText(getActivity().getApplicationContext(), "Location not activated even if asked. Activate it for using the service !", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        mapView.onDestroy();

        if (registeredReceiver) {
            //Log.d(TAG, "Receiver unregistered");
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

        synchronized (localModel) {
            if (localModel != null) {

                Log.d(TAG, "onPause - building bundle for saving the current state");

                Bundle state = new Bundle();

                //viewflipper state
                state.putInt("viewFlipperKey", viewFlipper.getDisplayedChild());
                Log.d(TAG, String.valueOf(state.getInt("viewFlipperKey")));

                if (viewFlipper.getDisplayedChild() == 0) {
                    state.putBoolean("isBluetoothScanning", isBluetoothScanning);
                    Log.d(TAG, String.valueOf(state.getBoolean("isBluetoothScanning")));
                } else if (viewFlipper.getDisplayedChild() == 2) {
                    state.putSerializable("targetPlug", targetPlug); // set bluetooth targetplug
                    Log.d(TAG, String.valueOf((Plug) state.getSerializable("targetPlug")));
                } else if (viewFlipper.getDisplayedChild() == 3) {
                    synchronized (lock1) {
                        tripHandler.viewNotAvailable();
                        lock1.notifyAll();
                    }
                    synchronized (lock2) {
                        dsiEvaluator.viewNotAvailable();
                        lock2.notifyAll();
                    }
                    state.putSerializable("tripHandler", tripHandler); // tripHandler.reloadTaskState();
                    Log.d(TAG, String.valueOf((TripHandler) state.getSerializable("tripHandler")));
                    state.putSerializable("dsiEvaluator", dsiEvaluator); //dsiEvaluator.reloadTaskState();
                    Log.d(TAG, String.valueOf((DSIEvaluator) state.getSerializable("dsiEvaluator")));
                    state.putString("pause_resumeTripTextView", pause_resumeTripTextView.getText().toString());
                    Log.d(TAG, state.getString("pause_resumeTripTextView"));
                } else if (viewFlipper.getDisplayedChild() == 4) {
                    //state.putString("dsiEvaluationVisualization", dsiEvaluation.getText().toString());
                    //Log.d(TAG, state.getString("dsiEvaluationVisualization"));
                    //state.putString("tripNameVisualization", tripName.getText().toString());
                    //Log.d(TAG, state.getString("tripNameVisualization"));

                    details.clear();
                    List<Trip> trips = localModel.getTrips();
                    Collections.sort(trips, new DateComparator());
                    state.putSerializable("trip", trips.get(trips.size() - 1));
                    //state.putParcelableArrayList("markersToBePlaced", (ArrayList<? extends Parcelable>) trips.get(trips.size() - 1).getMarkers());
                    //Log.d(TAG, String.valueOf(state.getParcelableArrayList("markersToBePlaced")));
                }

                savedStateHandler.addState("TabHome", state);
                Log.d(TAG, String.valueOf(savedStateHandler.hasTag("TabHome")));
                bluetoothAdapter.cancelDiscovery();

                mapView.onPause();
            }
        }
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void addDetails() {
        for (CardView cv : details) {
            f2.addView(cv);
        }
    }

    private void restoreAfterTripVisualization(Trip trip) {

        for (Map<String, String> map : localModel.getValuesToRender(trip)) {
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

        drawTrip(trip.getMarkers());
        addDetails();

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
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady");

        map = googleMap;

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        loadStateIfNeeded();
    }

}