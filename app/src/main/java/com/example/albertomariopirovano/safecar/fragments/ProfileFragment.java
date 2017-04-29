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
import com.example.albertomariopirovano.safecar.activity.MainActivity;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by albertomariopirovano on 03/04/17.
 */

public class ProfileFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName() + " | ProfileFragment";

    private LocalModel localModel = LocalModel.getInstance();
    private File directory;

    private File profilePngFile;

    private ProgressBar customProgress;
    private TextView progressDisplay;

    private ContextWrapper cw;
    private FirebaseUser currentUser;
    private TextView nameTextView;
    private TextView emailTextView;
    private ImageView imageView;
    private TextView levelTextView;
    private View v;

    private DatabaseReference database;
    private FirebaseAuth auth;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        currentUser = auth.getCurrentUser();

        imageView = (ImageView) v.findViewById(R.id.imgProfilePic);
        nameTextView = (TextView) v.findViewById(R.id.txtName);
        emailTextView = (TextView) v.findViewById(R.id.txtEmail);
        levelTextView = (TextView) v.findViewById(R.id.level_text_view);

        customProgress = (ProgressBar) v.findViewById(R.id.customProgress);
        progressDisplay = (TextView) v.findViewById(R.id.progressDisplay);

        cw = new ContextWrapper(getActivity().getApplicationContext());
        directory = cw.getDir("safecar", Context.MODE_PRIVATE);
        profilePngFile = new File(directory, "profile.png");


        Log.d(TAG, "customizing user profile");
        Log.d(TAG, localModel.getUser().toString());

        customProgress.setProgress(Integer.parseInt(localModel.getUser().percentage));
        customProgress.setSecondaryProgress(Integer.parseInt(localModel.getUser().percentage) + 1);
        progressDisplay.setText(localModel.getUser().percentage + "%");

        levelTextView.setText("Level " + localModel.getUser().level);

        if (localModel.getUser().photoURL != null) {
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

        if (!TextUtils.isEmpty(localModel.getUser().name)) {
            nameTextView.setText(localModel.getUser().name);
        } else {
            nameTextView.setText("No name provided");
        }

        if (!TextUtils.isEmpty(localModel.getUser().email)) {
            emailTextView.setText(localModel.getUser().email);
        } else {
            emailTextView.setText("No email provided");
        }

        return v;
    }
}