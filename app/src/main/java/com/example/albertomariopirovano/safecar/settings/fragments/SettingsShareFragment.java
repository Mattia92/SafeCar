package com.example.albertomariopirovano.safecar.settings.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.activity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

/**
 * Created by mattiacrippa on 15/03/17.
 */

public class SettingsShareFragment extends Fragment {

    View v;
    Button b;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        b = (Button) v.findViewById(R.id.merge_account);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        for(UserInfo profile: user.getProviderData()) {
            String providerId = profile.getProviderId();
            String uid = profile.getUid();
            String name = profile.getDisplayName();
            String email = profile.getEmail();
            Uri photoUrl = profile.getPhotoUrl();
            Toast.makeText(getActivity(), providerId + " " + uid + " " + name + " " + email,
                    Toast.LENGTH_SHORT).show();
        }

        v = inflater.inflate(R.layout.settings_smartobj_layout, container, false);

        return v;
    }

}
