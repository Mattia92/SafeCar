package com.example.albertomariopirovano.safecar.inner.fragments;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Set;

import static android.app.Activity.RESULT_OK;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class TabHome extends Fragment implements TabFragment {

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
    private BluetoothAdapter bluetoothAdapter;
    private FragmentManager fm;

    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.test, container, false);

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

        if (bluetoothAdapter != null) {
            Log.d(TAG, "unregister receiver");
            getActivity().unregisterReceiver(receiver);
        }
    }
}