package com.example.albertomariopirovano.safecar.fragments;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import android.support.v7.preference.Preference.OnPreferenceClickListener;

import com.example.albertomariopirovano.safecar.settings.fragments.*;
import com.example.albertomariopirovano.safecar.R;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by mattiacrippa on 14/03/17.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    View v;
    List<Fragment> listFragments;
    FragmentManager fragmentManager;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        listFragments = new ArrayList<Fragment>();
        listFragments.add(new SettingsShareFragment());
        listFragments.add(new SettingsNotificationFragment());
        listFragments.add(new SettingsSmartObjectsFragment());

        fragmentManager = getFragmentManager();

        OnPreferenceClickListener preferenceListener = new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference preference) {
                /*switch (preference.getKey()) {
                    case R.string.button_sharing:
                        fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(0)).commit();
                        break;
                    case R.id.nofitications:
                        fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(1)).commit();
                        break;
                    case R.id.smart_objects:
                        fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(2)).commit();
                        break;
                }*/
                System.out.println("ciao");
                return true;
            }
        };


        Preference button = findPreference(getString(R.string.button_sharing));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference preference) {
                /*switch (preference.getKey()) {
                    case R.string.button_sharing:
                        fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(0)).commit();
                        break;
                    case R.id.nofitications:
                        fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(1)).commit();
                        break;
                    case R.id.smart_objects:
                        fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(2)).commit();
                        break;
                }*/
                System.out.println("ciao");
                return true;
            }
        });

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

}
