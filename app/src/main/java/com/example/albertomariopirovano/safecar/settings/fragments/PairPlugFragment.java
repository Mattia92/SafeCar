package com.example.albertomariopirovano.safecar.settings.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.firebase_model.Plug;
import com.example.albertomariopirovano.safecar.inner.fragments.TAGInterface;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.example.albertomariopirovano.safecar.services.SavedStateHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by albertomariopirovano on 16/06/17.
 */

public class PairPlugFragment extends Fragment implements TAGInterface {

    private static final String TAG = "PairPlugFragment";
    private final static int REQUEST_ENABLE_BT = 1;
    private View v;
    private ViewFlipper viewFlipper;
    private ProgressBar progressBar;
    private Button scanButton;
    private ListView listDevices;
    private TextView entry_text_home;
    private SavedStateHandler savedStateHandler = SavedStateHandler.getInstance();
    private ArrayList<Plug> toBeAdded;
    private ArrayList<Plug> found;
    private Boolean isBluetoothScanning = Boolean.FALSE;
    private Boolean registeredReceiver = false;

    private BluetoothAdapter bluetoothAdapter;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private LocalModel localModel = LocalModel.getInstance();
    private Button reScanButton;

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
                            savedStateHandler.setTargetPlug(clicked);
                            localModel.setActivePlug(clicked.getPlugId());

                            viewFlipper.setDisplayedChild(0);
                        }
                    });

                    viewFlipper.setDisplayedChild(1);

                } else {

                    for (Plug p1 : found) {
                        for (Plug p2 : localModel.getPlugs()) {
                            if (p2.getPlugId().equals(p1.getPlugId())) {
                                savedStateHandler.setTargetPlug(p1);
                                localModel.setActivePlug(p1.getPlugId());
                            }
                        }
                    }
                    viewFlipper.setDisplayedChild(0);
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

    public String getAssignedTag() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance().getReference();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        v = inflater.inflate(R.layout.bluetoothpair, container, false);
        viewFlipper = (ViewFlipper) v.findViewById(R.id.flipper);
        entry_text_home = (TextView) v.findViewById(R.id.entry_text_home);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBarHome);
        progressBar.setVisibility(View.GONE);
        scanButton = (Button) v.findViewById(R.id.scanButton);
        listDevices = (ListView) v.findViewById(R.id.listDevices);
        reScanButton = (Button) v.findViewById(R.id.rescan_button);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBluetoothScan();
            }
        });

        reScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toBeAdded = new ArrayList<Plug>();
                found = new ArrayList<Plug>();
                scanButton.setVisibility(View.VISIBLE);
                entry_text_home.setVisibility(View.VISIBLE);
                viewFlipper.setDisplayedChild(0);
            }
        });

        loadStateIfNeeded();

        return v;
    }

    private void startBluetoothScan() {
        toBeAdded = new ArrayList<Plug>();
        found = new ArrayList<Plug>();
        if (bluetoothAdapter != null) {

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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (registeredReceiver) {
            //Log.d(TAG, "Receiver unregistered");
            getActivity().unregisterReceiver(receiver);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        synchronized (localModel) {
            if (localModel.getDropped() == Boolean.FALSE) {
                Log.d(TAG, "onPause - building bundle for saving the current state");

                Bundle state = new Bundle();

                state.putBoolean("isBluetoothScanning", isBluetoothScanning);
                Log.d(TAG, String.valueOf(state.getBoolean("isBluetoothScanning")));

                savedStateHandler.addState("PairPlugFragment", state);
                Log.d(TAG, String.valueOf(savedStateHandler.hasTag("PairPlugFragment")));
                bluetoothAdapter.cancelDiscovery();
            }
        }
    }

    private void loadStateIfNeeded() {
        if (savedStateHandler.hasTag("PairPlugFragment")) {
            Log.d(TAG, "loading previous state");
            Bundle oldState = savedStateHandler.retrieveState("PairPlugFragment");

            scanButton.setVisibility(View.VISIBLE);
            entry_text_home.setVisibility(View.VISIBLE);
            if (oldState.getBoolean("isBluetoothScanning")) {
                startBluetoothScan();
            }
        }
    }
}
