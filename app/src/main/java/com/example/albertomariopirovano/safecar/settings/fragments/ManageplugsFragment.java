package com.example.albertomariopirovano.safecar.settings.fragments;

import android.content.Context;
import android.graphics.Typeface;
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
import android.widget.Button;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.adapters.RecyclerAdapter;
import com.example.albertomariopirovano.safecar.firebase_model.Plug;
import com.example.albertomariopirovano.safecar.inner.fragments.TAGInterface;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albertomariopirovano on 16/06/17.
 */

public class ManageplugsFragment extends Fragment implements TAGInterface {

    private static final String TAG = "ManageplugsFragment";
    private View v;
    private Button showPlugs;
    private TextView devices;
    private View divider;
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

    public String getAssignedTag() {
        return TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        v = inflater.inflate(R.layout.manageplugs, container, false);
        showPlugs = (Button) v.findViewById(R.id.toggleplugs);
        devices = (TextView) v.findViewById(R.id.devices);
        divider = v.findViewById(R.id.divider);
        devices.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Noteworthy-Bold.ttf"));
        showPlugs.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Noteworthy-Bold.ttf"));

        setupList(v);

        showPlugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerView.isShown()) {
                    slide_up(getActivity(), recyclerView);
                    showPlugs.setText("Show plugs");
                    recyclerView.setVisibility(View.GONE);
                    devices.setVisibility(View.GONE);
                    divider.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    devices.setVisibility(View.VISIBLE);
                    divider.setVisibility(View.VISIBLE);
                    showPlugs.setText("Hide plugs");
                    slide_down(getActivity(), recyclerView);
                }
            }
        });
        // hide until its title is clicked
        recyclerView.setVisibility(View.GONE);
        devices.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);

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
