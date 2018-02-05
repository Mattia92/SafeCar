package com.example.albertomariopirovano.safecar.settings.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.inner.fragments.TAGInterface;

/**
 * Created by mattiacrippa on 04/02/18.
 */

public class SettingsInfoFeedFragment extends Fragment implements TAGInterface {

    private static final String TAG = "SettingsInfoFeedFragment";
    View v;

    @Override
    public String getAssignedTag() {
        return TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        v = inflater.inflate(R.layout.settings_info_feed, container, false);

        TextView description = (TextView) v.findViewById(R.id.description);
        ImageView image = (ImageView) v.findViewById(R.id.image);

        //description.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Noteworthy-Bold.ttf"));

        final LinearLayout contactUs = (LinearLayout) v.findViewById(R.id.feedback);
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Safe Car App");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"admin@safecar.com"});
                startActivity(Intent.createChooser(intent, "Send mail..."));
            }
        });

        final LinearLayout facebook = (LinearLayout) v.findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager packageManager = getContext().getPackageManager();
                String str;
                try {
                    int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
                    if (versionCode >= 3002850) { //newer versions of fb app
                        str = "fb://facewebmodal/f?href=" + "https://www.facebook.com/CheesyChasing/";
                    } else { //older versions of fb app
                        str = "fb://page/" + "CheesyChasing/";
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    str = "https://www.facebook.com/CheesyChasing/"; //normal web url
                }

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
            }
        });

        final LinearLayout twitter = (LinearLayout) v.findViewById(R.id.twitter);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getContext().getPackageManager().getPackageInfo("com.twitter.android", 0);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=Tia_C__92")));
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/")));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/intent/user?screen_name=Tia_C__92")));
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/")));
                }
            }
        });

        final LinearLayout play_store = (LinearLayout) v.findViewById(R.id.play_store);
        play_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getContext().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://apps/")));
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/")));
                }
            }
        });

        return v;
    }
}
