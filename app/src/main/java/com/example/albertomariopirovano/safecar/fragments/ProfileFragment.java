package com.example.albertomariopirovano.safecar.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.inner.fragments.TAGInterface;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.example.albertomariopirovano.safecar.utils.Badge;
import com.example.albertomariopirovano.safecar.utils.CircleTransform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

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
    private View v;
    private CardView cardviewelement;
    private ArrayList<Badge> badges;

    private TableLayout detailsLayout;

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
        detailsLayout = (TableLayout) v.findViewById(R.id.table_layout);
        //f2 = (LinearLayout) v.findViewById(R.id.f2);

        imageView = (ImageView) v.findViewById(R.id.imgProfilePic);
        nameTextView = (TextView) v.findViewById(R.id.txtName);
        emailTextView = (TextView) v.findViewById(R.id.txtEmail);
        levelTextView = (TextView) v.findViewById(R.id.level_text_view);

        customProgress = (ProgressBar) v.findViewById(R.id.customProgress);
        progressDisplay = (TextView) v.findViewById(R.id.progressDisplay);

        cw = new ContextWrapper(getActivity().getApplicationContext());
        directory = cw.getDir("safecar", Context.MODE_PRIVATE);
        profilePngFile = new File(directory, "profile.png");

        //TextView cardViewTitle = (TextView) v.findViewById(R.id.cardViewTitle);
        //cardViewTitle.setText("Badges:");

        Log.i(TAG, "customizing user profile");
        Log.i(TAG, localModel.getUser().toString());

        customProgress.setProgress(Integer.parseInt(localModel.getUser().percentage));
        customProgress.setSecondaryProgress(Integer.parseInt(localModel.getUser().percentage) + 1);
        customProgress.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
        progressDisplay.setText(localModel.getUser().percentage + "%");

        levelTextView.setText("Level " + localModel.getUser().level);

        Bitmap b;
        if (localModel.getUser().photoURL != null && !localModel.getUser().photoURL.isEmpty()) {
            Log.i("ProfileFragment", "load profile image");
            //try {
            //    b = BitmapFactory.decodeStream(new FileInputStream(profilePngFile));
            //} catch (FileNotFoundException e) {
            //    e.printStackTrace();
            //}
            Picasso.with(getContext())
                    .load(localModel.getUser().photoURL)
                    .transform(new CircleTransform())
                    .fit()
                    .into(imageView);
        } else {
            Log.i("ProfileFragment", "load standard profile image");
            b = BitmapFactory.decodeResource(getResources(), R.drawable.user);
            RoundedBitmapDrawable img = RoundedBitmapDrawableFactory.create(getResources(), b);
            img.setCircular(true);
            imageView.setImageDrawable(img);
        }

        //RoundedBitmapDrawable img = RoundedBitmapDrawableFactory.create(getResources(), b);
        //img.setCircular(true);
        //imageView.setImageDrawable(img);
 
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

        populateBadgesView();

        return v;
    }

    public String getAssignedTag() {
        return TAG;
    }

    public void populateBadgesView() {
        badges = new ArrayList<>();
        Badge.loadBadges(badges);
        Badge.checkBadges(badges, localModel);

        for (int i = 0; i < detailsLayout.getChildCount(); i++) {
            View child = detailsLayout.getChildAt(i);

            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;
                int rowElem = row.getChildCount();

                for (int x = 0; x < rowElem; x++) {
                    final LinearLayout badge = (LinearLayout) row.getChildAt(x);
                    ImageView badge_icon = (ImageView) badge.getChildAt(0);
                    TextView badge_name = (TextView) badge.getChildAt(1);
                    final Badge b = badges.get(x + i * rowElem);
                    Drawable icon = ContextCompat.getDrawable(getContext(), b.getBadgeIcon());
                    icon = icon.mutate();

                    if (badges.get(x + i * rowElem).isUnlocked()) {
                        icon.setColorFilter(new LightingColorFilter(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorAccent)));
                    } else {
                        icon.setColorFilter(new LightingColorFilter(Color.GRAY, Color.GRAY));
                        icon.setAlpha(30);
                    }
                    badge_icon.setImageDrawable(icon);
                    badge_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            if (b.isUnlocked()) {
                                builder.setMessage("You have already unlocked this badge. Good job !")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                            } else {
                                builder.setMessage("This badge is locked. Work on it !")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                            }
                            builder.setIcon(b.getBadgeIcon());
                            builder.setTitle(b.getBadgeName());

                            AlertDialog alert = builder.create();
                            alert.show();

                        }
                    });
                    badge_name.setText(badges.get(x + i * rowElem).getBadgeName());
                }
            }
        }
    }

    /*private void addDetails() {
        Log.i(TAG, "Adding badges details");
        int i = 0;
        for (Map<String, String> map : localModel.getValuesToRender(t)) {

            TableRow row = (TableRow) detailsLayout.getChildAt(i);
            Iterator it = map.entrySet().iterator();
            Map.Entry<String, String> entry1 = (Map.Entry) it.next();
            Map.Entry<String, String> entry2 = (Map.Entry) it.next();

            ((TextView) row.getChildAt(0)).setText(entry1.getValue());
            ((TextView) row.getChildAt(1)).setText(entry2.getValue());

            i++;
        }
    }*/

}