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
import android.widget.ImageView;
import android.widget.ProgressBar;
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

    private File directory;

    private File profilePngFile;

    private ProgressBar customProgress;
    private TextView progressDisplay;

    private ContextWrapper cw;
    private FirebaseUser currentUser;
    private TextView nameTextView;
    private TextView emailTextView;
    private ImageView imageView;
    private View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_profile, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        imageView = (ImageView) v.findViewById(R.id.imgProfilePic);
        nameTextView = (TextView) v.findViewById(R.id.txtName);
        emailTextView = (TextView) v.findViewById(R.id.txtEmail);

        customProgress = (ProgressBar) v.findViewById(R.id.customProgress);
        progressDisplay = (TextView) v.findViewById(R.id.progressDisplay);

        customProgress.setProgress(70);
        customProgress.setSecondaryProgress(71);
        progressDisplay.setText(String.valueOf(70) + "%");

        cw = new ContextWrapper(getActivity().getApplicationContext());
        directory = cw.getDir("safecar", Context.MODE_PRIVATE);
        profilePngFile = new File(directory, "profile.png");

        if (currentUser.getPhotoUrl() != null && !profilePngFile.exists()) {
            Log.d("ProfileFragment", "download profile image");
            new DownloadImage(profilePngFile).execute(currentUser.getPhotoUrl().toString());
        }

        if (currentUser.getPhotoUrl() != null) {
            Log.d("ProfileFragment", "load profile image");
            try {
                imageView.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(profilePngFile)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("ProfileFragment", "load standard profile image");
            imageView.setImageResource(R.drawable.user);
        }

        if (!TextUtils.isEmpty(currentUser.getDisplayName())) {
            nameTextView.setText(currentUser.getDisplayName());
        } else {
            nameTextView.setText("No name provided");
        }

        if (!TextUtils.isEmpty(currentUser.getEmail())) {
            emailTextView.setText(currentUser.getEmail());
        } else {
            emailTextView.setText("No email provided");
        }
        return v;
    }
}