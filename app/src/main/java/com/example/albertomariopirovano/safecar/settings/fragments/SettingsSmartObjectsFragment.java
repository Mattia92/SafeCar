package com.example.albertomariopirovano.safecar.settings.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.adapters.RecyclerAdapter;
import com.example.albertomariopirovano.safecar.firebase_model.Plug;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattiacrippa on 15/03/17.
 */

public class SettingsSmartObjectsFragment extends Fragment {

    View v;
    private TextView clickableSpeaker, clickablePlug;
    private TextView hiddenSpeaker;
    private RecyclerView recyclerView;
    private RecyclerAdapter plugAdapter;
    private LocalModel localModel = LocalModel.getInstance();

    public static void slide_up(Context ctx, View v) {

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_down(Context ctx, View v) {

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        v = inflater.inflate(R.layout.settings_smartobj_layout, container, false);

        clickableSpeaker = (TextView) v.findViewById(R.id.pairing_speakers);
        clickablePlug = (TextView) v.findViewById(R.id.gestione_plug);
        hiddenSpeaker = (TextView) v.findViewById(R.id.pairing_speakers_hidden);
        setupList(v);
        //plugList = new ArrayList<String>();

        //TODO(Fetch plug for user)
        // fatto nel metodo createList()
        //for(int i = 0; i < 3; i++) {
        //    plugList.add("Plug " + i);
        //}


        clickableSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hiddenSpeaker.isShown()){
                    slide_up(getActivity(), hiddenSpeaker);
                    hiddenSpeaker.setVisibility(View.GONE);
                }
                else{
                    hiddenSpeaker.setVisibility(View.VISIBLE);
                    slide_down(getActivity(), hiddenSpeaker);
                }
            }
        });
        // hide until its title is clicked
        hiddenSpeaker.setVisibility(View.GONE);

        clickablePlug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recyclerView.isShown()){
                    slide_up(getActivity(), recyclerView);
                    recyclerView.setVisibility(View.GONE);
                }
                else{
                    recyclerView.setVisibility(View.VISIBLE);
                    slide_down(getActivity(), recyclerView);
                }
            }
        });
        // hide until its title is clicked
        recyclerView.setVisibility(View.GONE);

        return v;
    }

    private void setupList(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        plugAdapter = new RecyclerAdapter(getActivity(), createList());
        recyclerView.setAdapter(plugAdapter);
    }

    private List<String> createList() {
        List<String> list = new ArrayList<>();

        for (Plug plug : localModel.getPlugs()) {
            if (plug.getIsDropped().equals(Boolean.FALSE)) {
                list.add(plug.name + plug.address_MAC);
            }
        }

        return list;
    }
}