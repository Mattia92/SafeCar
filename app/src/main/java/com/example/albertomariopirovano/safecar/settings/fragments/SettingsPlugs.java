package com.example.albertomariopirovano.safecar.settings.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.activity.MainActivity;
import com.example.albertomariopirovano.safecar.inner.fragments.TAGInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albertomariopirovano on 16/06/17.
 */

public class SettingsPlugs extends PreferenceFragmentCompat implements TAGInterface {

    private static final String TAG = "SettingsPlugs";
    View v;
    List<Fragment> listFragments;
    FragmentManager fragmentManager;

    public String getAssignedTag() {
        return TAG;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencesplugs);

        listFragments = new ArrayList<>();
        listFragments.add(new PairPlugFragment());
        listFragments.add(new ManageplugsFragment());

        fragmentManager = getActivity().getSupportFragmentManager();

        Preference.OnPreferenceClickListener l = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                getActivity().setTitle(preference.getKey());

                if (preference.getKey().equals("Pair plug")) {
                    ((MainActivity) getActivity()).setEnabledNavigationDrawer(false);
                    fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(0), ((TAGInterface) listFragments.get(0)).getAssignedTag()).addToBackStack(null).commit();
                } else if (preference.getKey().equals("Manage plugs")) {
                    ((MainActivity) getActivity()).setEnabledNavigationDrawer(false);
                    fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(1), ((TAGInterface) listFragments.get(1)).getAssignedTag()).addToBackStack(null).commit();
                }

                return true;
            }
        };

        Preference preference_pairplug = findPreference("Pair plug");
        Preference preference_manageplugs = findPreference("Manage plugs");

        preference_pairplug.setOnPreferenceClickListener(l);
        preference_manageplugs.setOnPreferenceClickListener(l);

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }
}
