package com.example.albertomariopirovano.safecar.settings.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.inner.fragments.TAGInterface;

/**
 * Created by mattiacrippa on 15/03/17.
 */

public class SettingsShareFragment extends Fragment implements TAGInterface {

    private static final String TAG = "SettingsShareFragment";
    View v;

    public String getAssignedTag() {
        return TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        v = inflater.inflate(R.layout.settings_merge_accounts, container, false);

        return v;
    }
}
