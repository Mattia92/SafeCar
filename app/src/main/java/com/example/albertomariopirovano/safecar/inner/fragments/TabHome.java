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
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    private BluetoothAdapter bluetoothAdapter;
    private ImageView currentlyDrivingLogo;
    private ImageView notCurrentlyDrivingLogo;
    private TextView titleBluetoothTriggered;
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(TAG, "discovery started");


                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "discovery finished");
                //discovery finishes, dismis progress dialog
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.d(TAG, "Found device: NAME: " + device.getName() + " - MAC_ADDRESS" + device.getAddress());

                Log.d(TAG, "making visible some items");

                currentlyDrivingLogo.setAlpha(0f);
                currentlyDrivingLogo.setVisibility(View.VISIBLE);
                notCurrentlyDrivingLogo.setAlpha(0f);
                notCurrentlyDrivingLogo.setVisibility(View.VISIBLE);
                titleBluetoothTriggered.setAlpha(0f);
                titleBluetoothTriggered.setVisibility(View.VISIBLE);

                int mediumAnimationTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

                currentlyDrivingLogo.animate()
                        .alpha(1f)
                        .setDuration(mediumAnimationTime)
                        .setListener(null);
                notCurrentlyDrivingLogo.animate()
                        .alpha(1f)
                        .setDuration(mediumAnimationTime)
                        .setListener(null);
                titleBluetoothTriggered.animate()
                        .alpha(1f)
                        .setDuration(mediumAnimationTime)
                        .setListener(null);

            } else {
                Log.d(TAG, "I really don't know why you are here");
            }
        }
    };

    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_home_step_one, container, false);

        Log.d(TAG, "onCreate");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        currentlyDrivingLogo = (ImageView) v.findViewById(R.id.currentlyDrivingLogo);
        notCurrentlyDrivingLogo = (ImageView) v.findViewById(R.id.notCurrentlyDrivingLogo);
        titleBluetoothTriggered = (TextView) v.findViewById(R.id.entry_text_home);

        currentlyDrivingLogo.setVisibility(View.GONE);
        notCurrentlyDrivingLogo.setVisibility(View.GONE);
        titleBluetoothTriggered.setVisibility(View.GONE);

        notCurrentlyDrivingLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "hiding some items");

                currentlyDrivingLogo.setAlpha(0f);
                currentlyDrivingLogo.setVisibility(View.GONE);
                notCurrentlyDrivingLogo.setAlpha(0f);
                notCurrentlyDrivingLogo.setVisibility(View.GONE);
                titleBluetoothTriggered.setAlpha(0f);
                titleBluetoothTriggered.setVisibility(View.GONE);

                int mediumAnimationTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

                currentlyDrivingLogo.animate()
                        .alpha(1f)
                        .setDuration(mediumAnimationTime)
                        .setListener(null);
                notCurrentlyDrivingLogo.animate()
                        .alpha(1f)
                        .setDuration(mediumAnimationTime)
                        .setListener(null);
                titleBluetoothTriggered.animate()
                        .alpha(1f)
                        .setDuration(mediumAnimationTime)
                        .setListener(null);
            }
        });

        currentlyDrivingLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_content, new Tab_home_step_two()).commit();*/
                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.tab_home_step_one, new Tab_home_step_two(), "NewFragmentTag");
                ft.commit();
            }
        });

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

            if (!bluetoothAdapter.isEnabled()) {

                Log.d(TAG, "bluetooth not enabled");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            } else {

                Log.d(TAG, "bluetooth enabled");
                bluetoothSearchPairedDevices();

                Log.d(TAG, "search for not paired devices");

                bluetoothAdapter.startDiscovery();

            }

        } else {
            Log.d(TAG, "Bluetooth not supported");
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
            bluetoothSearchPairedDevices();

            Log.d(TAG, "search for not paired devices");

            bluetoothAdapter.startDiscovery();

        } else {

            Log.d(TAG, "RESULT_CANCELLED");

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "destroy receiver");
        // Don't forget to unregister the ACTION_FOUND receiver.
        getActivity().unregisterReceiver(receiver);
    }

}
