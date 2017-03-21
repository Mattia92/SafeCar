package com.example.albertomariopirovano.safecar.settings.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;

/**
 * Created by mattiacrippa on 15/03/17.
 */

public class SettingsSmartObjectsFragment extends Fragment {

    private TextView clickableText;
    private TextView hiddenText;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        v = inflater.inflate(R.layout.settings_smartobj_layout, container, false);

        clickableText = (TextView) v.findViewById(R.id.pairing_speakers);
        hiddenText = (TextView) v.findViewById(R.id.pairing_speakers_hidden);

        clickableText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hiddenText.isShown()){
                    slide_up(getActivity(), hiddenText);
                    hiddenText.setVisibility(View.GONE);
                }
                else{
                    hiddenText.setVisibility(View.VISIBLE);
                    slide_down(getActivity(), hiddenText);
                }
            }
        });
        // hide until its title is clicked
        hiddenText.setVisibility(View.GONE);

        return v;
    }

    public static void slide_up(Context ctx, View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_down(Context ctx, View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

}
