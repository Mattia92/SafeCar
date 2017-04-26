package com.example.albertomariopirovano.safecar.inner.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.albertomariopirovano.safecar.R;

/**
 * Created by albertomariopirovano on 27/04/17.
 */

public class Tab_home_step_two extends Fragment implements TabFragment {

    private static final String TAG = "Tab_home_step_two";
    private String name = "Home";

    @Override
    public String getName() {
        return name;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_home_step_two, container, false);

        Log.d(TAG, "tab_home_step_two onCreate");

        return v;
    }
}