package com.example.albertomariopirovano.safecar.concurrency;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albertomariopirovano on 26/04/17.
 */

public class BluetoothSearcher extends AsyncTask<Void, Void, Void> {

    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver receiver;
    private List<String> bagOfDevices;
    private Context context;
    private TextView target;

    public BluetoothSearcher(Context context, TextView target) {
        this.context = context;
        this.target = target;
        this.bagOfDevices = new ArrayList<String>();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        Log.d("ciaooo", "ciaoooooo");

        return null;
    }

    protected void onPostExecute() {
        Log.d("BluetoothSearcher", "onPostExecute");
        /*for(String s:bagOfDevices) {
            Log.d("BluetoothSearcher", s);
        }
        target.setText(bagOfDevices.get(0));*/
    }

    protected void onPreExecute() {
        Log.d("BluetoothSearcher", "onPreExecute");
    }
}