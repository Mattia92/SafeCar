package com.example.albertomariopirovano.safecar.settings.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
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

    public final static int REQUEST_ACCESS_COARSE_LOCATION = 1;
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
    final private LocalModel localModel = LocalModel.getInstance();
    private TableLayout targetPlugTable;
    private LinearLayout targetPlugLinLay;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.i(TAG, "discovery started");

                isBluetoothScanning = Boolean.TRUE;

                progressBar.setVisibility(View.VISIBLE);
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG, "discovery finished");

                progressBar.setVisibility(View.GONE);

                if (found.isEmpty()) {
                    entry_text_home.setText(R.string.initPairPlugTA);
                    Toast.makeText(getActivity().getApplicationContext(), R.string.noBluetoothDevices, Toast.LENGTH_SHORT).show();
                } else if (!found.isEmpty() && (toBeAdded.size() - found.size()) == 0) {

                    List<Map<String, String>> data = new ArrayList<>();
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

                            scanButton.setText(R.string.changePairPlugB);
                            entry_text_home.setText(R.string.justPairedPairPlugTA);

                            TableRow row1 = (TableRow) targetPlugTable.getChildAt(0);
                            ((TextView) row1.getChildAt(0)).setText(R.string.Name);
                            ((TextView) row1.getChildAt(1)).setText(savedStateHandler.getTargetPlug().getName());
                            TableRow row2 = (TableRow) targetPlugTable.getChildAt(1);
                            ((TextView) row2.getChildAt(0)).setText(R.string.MACAddress);
                            ((TextView) row2.getChildAt(1)).setText(savedStateHandler.getTargetPlug().getAddress_MAC());

                            targetPlugLinLay.setVisibility(View.VISIBLE);

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
                    scanButton.setText(R.string.changePairPlugB);
                    entry_text_home.setText(R.string.justPairedPairPlugTA);

                    TableRow row1 = (TableRow) targetPlugTable.getChildAt(0);
                    ((TextView) row1.getChildAt(0)).setText(R.string.Name);
                    ((TextView) row1.getChildAt(1)).setText(savedStateHandler.getTargetPlug().getName());
                    TableRow row2 = (TableRow) targetPlugTable.getChildAt(1);
                    ((TextView) row2.getChildAt(0)).setText(R.string.MACAddress);
                    ((TextView) row2.getChildAt(1)).setText(savedStateHandler.getTargetPlug().getAddress_MAC());

                    targetPlugLinLay.setVisibility(View.VISIBLE);

                    viewFlipper.setDisplayedChild(0);
                }

                isBluetoothScanning = Boolean.FALSE;

                getActivity().unregisterReceiver(receiver);
                registeredReceiver = false;

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.i(TAG, "Found device: NAME: " + device.getName() + " - MAC_ADDRESS" + device.getAddress());

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
                Log.i(TAG, "I really don't know why you are here");
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
        Button reScanButton = (Button) v.findViewById(R.id.rescan_button);
        targetPlugTable = (TableLayout) v.findViewById(R.id.targetPlug);
        targetPlugLinLay = (LinearLayout) v.findViewById(R.id.targetPlugLinLay);
        targetPlugLinLay.setVisibility(View.GONE);

        if (savedStateHandler.getTargetPlug() != null) {
            scanButton.setText(R.string.changePairPlugB);
            entry_text_home.setText(R.string.justPairedPairPlugTA);

            TableRow row1 = (TableRow) targetPlugTable.getChildAt(0);
            ((TextView) row1.getChildAt(0)).setText(R.string.Name);
            ((TextView) row1.getChildAt(1)).setText(savedStateHandler.getTargetPlug().getName());
            TableRow row2 = (TableRow) targetPlugTable.getChildAt(1);
            ((TextView) row2.getChildAt(0)).setText(R.string.MACAddress);
            ((TextView) row2.getChildAt(1)).setText(savedStateHandler.getTargetPlug().getAddress_MAC());

            targetPlugLinLay.setVisibility(View.VISIBLE);
        } else {
            entry_text_home.setText(R.string.initPairPlugTA);
            scanButton.setText(R.string.initPairPlugB);
            targetPlugLinLay.setVisibility(View.GONE);
        }

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedStateHandler.removeTargetPlug();
                targetPlugLinLay.setVisibility(View.GONE);
                startBluetoothScan();
            }
        });

        reScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entry_text_home.setText(R.string.initPairPlugTA);
                scanButton.setText(R.string.initPairPlugB);
                targetPlugLinLay.setVisibility(View.GONE);
                toBeAdded = new ArrayList<>();
                found = new ArrayList<>();
                scanButton.setVisibility(View.VISIBLE);
                entry_text_home.setVisibility(View.VISIBLE);
                viewFlipper.setDisplayedChild(0);
            }
        });

        loadStateIfNeeded();

        return v;
    }

    private void startBluetoothScan() {
        toBeAdded = new ArrayList<>();
        found = new ArrayList<>();
        if (bluetoothAdapter != null) {

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

            getActivity().registerReceiver(receiver, filter);

            registeredReceiver = true;

            if (!bluetoothAdapter.isEnabled()) {

                //Log.i(TAG, "Bluetooth not enabled");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            } else {

                //Log.i(TAG, "Bluetooth enabled");
                //bluetoothSearchPairedDevices();

                switch (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    case PackageManager.PERMISSION_DENIED:
                        Log.i(TAG, "ACCESS COARSE LOCATION denied");
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                REQUEST_ACCESS_COARSE_LOCATION);
                        break;
                    case PackageManager.PERMISSION_GRANTED:

                        entry_text_home.setText(R.string.searchPairPlugB);
                        bluetoothAdapter.cancelDiscovery();
                        bluetoothAdapter.startDiscovery();
                        break;
                }

            }

        } else {
            //Log.i(TAG, "Bluetooth not supported");
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
            Log.i(TAG, "Receiver unregistered");
            getActivity().unregisterReceiver(receiver);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        synchronized (localModel) {
            if (localModel.getDropped() == Boolean.FALSE) {
                Log.i(TAG, "onPause - building bundle for saving the current state");

                Bundle state = new Bundle();

                state.putBoolean("isBluetoothScanning", isBluetoothScanning);
                Log.i(TAG, "Bluetooth is scanning ? " + String.valueOf(state.getBoolean("isBluetoothScanning")));

                savedStateHandler.addState("PairPlugFragment", state);
                if(bluetoothAdapter != null)
                    bluetoothAdapter.cancelDiscovery();
            }
        }
    }

    private void loadStateIfNeeded() {
        if (savedStateHandler.hasTag("PairPlugFragment")) {
            Log.i(TAG, "loading previous state");

            if (savedStateHandler.getTargetPlug() != null) {
                for (int i=0; i < localModel.getPlugs().size(); i++) {
                    Plug p = localModel.getPlugs().get(i);
                    if (p != null) {
                        if (p.getName().equals(savedStateHandler.getTargetPlug().getName()) &&
                                p.getAddress_MAC().equals(savedStateHandler.getTargetPlug().getAddress_MAC()) &&
                                p.getIsDropped()) {
                            Log.i(TAG, "The plug in the state has been dropped");
                            savedStateHandler.setTargetPlug(null);
                        }
                    }
                }
            }

            if (savedStateHandler.getTargetPlug() != null) {
                scanButton.setText(R.string.changePairPlugB);
                entry_text_home.setText(R.string.justPairedPairPlugTA);

                TableRow row1 = (TableRow) targetPlugTable.getChildAt(0);
                ((TextView) row1.getChildAt(0)).setText(R.string.Name);
                ((TextView) row1.getChildAt(1)).setText(savedStateHandler.getTargetPlug().getName());
                TableRow row2 = (TableRow) targetPlugTable.getChildAt(1);
                ((TextView) row2.getChildAt(0)).setText(R.string.MACAddress);
                ((TextView) row2.getChildAt(1)).setText(savedStateHandler.getTargetPlug().getAddress_MAC());

                targetPlugLinLay.setVisibility(View.VISIBLE);
            } else {
                entry_text_home.setText(R.string.initPairPlugTA);
                scanButton.setText(R.string.initPairPlugB);
                targetPlugLinLay.setVisibility(View.GONE);
            }

            Bundle oldState = savedStateHandler.retrieveState("PairPlugFragment");

            scanButton.setVisibility(View.VISIBLE);
            entry_text_home.setVisibility(View.VISIBLE);
            if (oldState.getBoolean("isBluetoothScanning")) {
                startBluetoothScan();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_COARSE_LOCATION) {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                bluetoothAdapter.cancelDiscovery();
                bluetoothAdapter.startDiscovery();
            }
            /*else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }*/
        }
    }
}
