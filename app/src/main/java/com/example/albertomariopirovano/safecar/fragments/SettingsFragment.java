package com.example.albertomariopirovano.safecar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.View;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.activity.MainActivity;
import com.example.albertomariopirovano.safecar.inner.fragments.TAGInterface;
import com.example.albertomariopirovano.safecar.settings.fragments.ManageplugsFragment;
import com.example.albertomariopirovano.safecar.settings.fragments.PairPlugFragment;
import com.example.albertomariopirovano.safecar.settings.fragments.SettingsInfoFeedFragment;
import com.example.albertomariopirovano.safecar.settings.fragments.SettingsNotificationFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattiacrippa on 14/03/17.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements TAGInterface {

    private static final String TAG = "SettingsFragment";
    View v;
    List<Fragment> listFragments;
    FragmentManager fragmentManager;

    public SettingsFragment() {
    }

    public String getAssignedTag() {
        return TAG;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        listFragments = new ArrayList<>();
        listFragments.add(new SettingsNotificationFragment());
        listFragments.add(new PairPlugFragment());
        listFragments.add(new ManageplugsFragment());
        listFragments.add(new SettingsInfoFeedFragment());

        fragmentManager = getActivity().getSupportFragmentManager();

        Preference.OnPreferenceClickListener l = new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference preference) {

                getActivity().setTitle(preference.getKey());

                if (preference.getKey().equals(getString(R.string.button_notifications))) {
                    ((MainActivity)getActivity()).setEnabledNavigationDrawer(false);
                    Log.i(TAG, ((TAGInterface) listFragments.get(1)).getAssignedTag());
                    fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(0), ((TAGInterface) listFragments.get(0)).getAssignedTag()).addToBackStack(null).commit();
                } else if (preference.getKey().equals(getString(R.string.button_pair))) {
                    ((MainActivity) getActivity()).setEnabledNavigationDrawer(false);
                    fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(1), ((TAGInterface) listFragments.get(1)).getAssignedTag()).addToBackStack(null).commit();
                } else if (preference.getKey().equals(getString(R.string.button_manage))) {
                    ((MainActivity) getActivity()).setEnabledNavigationDrawer(false);
                    fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(2), ((TAGInterface) listFragments.get(2)).getAssignedTag()).addToBackStack(null).commit();
                } else if (preference.getKey().equals(getString(R.string.button_info))) {
                    ((MainActivity)getActivity()).setEnabledNavigationDrawer(false);
                    Log.i(TAG, ((TAGInterface) listFragments.get(3)).getAssignedTag());
                    fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(3), ((TAGInterface) listFragments.get(3)).getAssignedTag()).addToBackStack(null).commit();
                }

                return true;
            }
        };

        Preference preference_notifications = findPreference(getString(R.string.button_notifications));
        Preference preference_infofeed = findPreference(getString(R.string.button_info));
        Preference preference_pairplug = findPreference(getString(R.string.button_pair));
        Preference preference_manageplugs = findPreference(getString(R.string.button_manage));

        preference_pairplug.setOnPreferenceClickListener(l);
        preference_manageplugs.setOnPreferenceClickListener(l);
        preference_notifications.setOnPreferenceClickListener(l);
        preference_infofeed.setOnPreferenceClickListener(l);

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

}
