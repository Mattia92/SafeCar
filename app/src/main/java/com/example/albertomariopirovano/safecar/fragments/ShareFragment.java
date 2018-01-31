package com.example.albertomariopirovano.safecar.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.inner.fragments.TAGInterface;

/**
 * Created by mattiacrippa on 14/03/17.
 */

public class ShareFragment extends Fragment implements TAGInterface {

    private static final String TAG = "ShareFragment";
    private Intent shareIntent;
    private String shareBody = "This is a great app. You should try !";

    public String getAssignedTag() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_share, container, false);

        Button shareButton = (Button) v.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MyApp");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
        
        return v;
    }
}
