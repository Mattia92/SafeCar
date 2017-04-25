package com.example.albertomariopirovano.safecar.fragments;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.concurrency.DownloadImage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by albertomariopirovano on 03/04/17.
 */

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ImageButton imageButton = (ImageButton) v.findViewById(R.id.imgProfilePic);
        TextView nameTextView = (TextView) v.findViewById(R.id.txtName);
        TextView emailTextView = (TextView) v.findViewById(R.id.txtEmail);

        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("safecar", Context.MODE_PRIVATE);
        File profilePngFile = new File(directory, "profile.png");

        if (currentUser.getPhotoUrl() != null && !profilePngFile.exists()) {
            Log.d("ProfileFragment", "preDownload");
            new DownloadImage(getActivity().getApplicationContext(), imageButton).execute(currentUser.getPhotoUrl().toString());

        } else if (profilePngFile.exists()) {
            Log.d("ProfileFragment", "preLoad");
            try {
                imageButton.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(profilePngFile)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(currentUser.getDisplayName())) {
            nameTextView.setText(currentUser.getDisplayName());
        }
        if (!TextUtils.isEmpty(currentUser.getEmail())) {
            emailTextView.setText(currentUser.getEmail());
        }
        return v;
    }
}