package com.example.albertomariopirovano.safecar.fragments;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.inner.fragments.TAGInterface;
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

public class ProfileFragment extends Fragment implements TAGInterface {

    private static final String TAG = "ProfileFragment";

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
    private LinearLayout layout;
    private RelativeLayout f1;
    //private LinearLayout f2;
    private View v;
    private CardView cardviewelement;

    private DatabaseReference database;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        currentUser = auth.getCurrentUser();

        layout = (LinearLayout) v.findViewById(R.id.linlayout);
        f1 = (RelativeLayout) v.findViewById(R.id.f1);
        //f2 = (LinearLayout) v.findViewById(R.id.f2);

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = layout.getWidth();
                int height = layout.getHeight();

                ViewGroup.LayoutParams params1 = f1.getLayoutParams();
                //ViewGroup.LayoutParams params2 = f2.getLayoutParams();

                params1.height = height;
                params1.width = width;
                f1.requestLayout();

                //params2.height = height / 2;
                //params2.width = width;
                //f2.requestLayout();
            }
        });

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

        Bitmap b = null;
        if (localModel.getUser().photoURL != null) {
            Log.d("ProfileFragment", "load profile image");
            //b = BitmapFactory.decodeResource(getResources(), R.drawable.user);
            try {
                b = BitmapFactory.decodeStream(new FileInputStream(profilePngFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("ProfileFragment", "load standard profile image");
            b = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        }
        RoundedBitmapDrawable img = RoundedBitmapDrawableFactory.create(getResources(), b);
        img.setCircular(true);
        imageView.setImageDrawable(img);
 
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

    public String getAssignedTag() {
        return TAG;
    }

}