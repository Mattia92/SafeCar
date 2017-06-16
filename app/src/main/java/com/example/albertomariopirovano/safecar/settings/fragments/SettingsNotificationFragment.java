package com.example.albertomariopirovano.safecar.settings.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.inner.fragments.TAGInterface;

/**
 * Created by mattiacrippa on 15/03/17.
 */

public class SettingsNotificationFragment extends PreferenceFragmentCompat implements TAGInterface {

    private static final String TAG = "SettingsNotificationFragment";

    public SettingsNotificationFragment() {
    }

    public String getAssignedTag() {
        return TAG;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_notifications);

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }
}
