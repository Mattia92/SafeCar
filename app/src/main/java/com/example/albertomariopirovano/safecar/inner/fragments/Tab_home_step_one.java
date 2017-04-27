package com.example.albertomariopirovano.safecar.inner.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;

/**
 * Created by albertomariopirovano on 27/04/17.
 */

public class Tab_home_step_one extends Fragment implements TabFragment {

    private static final String TAG = "Tab_home_step_one";
    private String name = "Home";

    private FragmentManager fm;

    private ImageView currentlyDrivingLogo;
    private ImageView notCurrentlyDrivingLogo;
    private TextView titleBluetoothTriggered;
    private TextView devices;

    @Override
    public String getName() {
        return name;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_home_step_one, container, false);

        Log.d(TAG, "tab_home_step_one onCreate");

        currentlyDrivingLogo = (ImageView) v.findViewById(R.id.currentlyDrivingLogo);
        notCurrentlyDrivingLogo = (ImageView) v.findViewById(R.id.notCurrentlyDrivingLogo);
        titleBluetoothTriggered = (TextView) v.findViewById(R.id.entry_text_home);
        devices = (TextView) v.findViewById(R.id.devices);

        String dataFromPrevious = "ANYTHING";
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            dataFromPrevious = bundle.getString("devices");
        }

        devices.setText(dataFromPrevious);

        fm = getActivity().getSupportFragmentManager();

        notCurrentlyDrivingLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.tab_home_one, new TabHome());
                fragmentTransaction.commit();
            }
        });

        currentlyDrivingLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.tab_home_one, new Tab_home_step_two());
                fragmentTransaction.commit();
            }
        });

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "Destroy TabHomeOne");

    }
}
