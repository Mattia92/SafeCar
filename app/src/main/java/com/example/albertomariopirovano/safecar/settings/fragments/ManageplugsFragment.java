package com.example.albertomariopirovano.safecar.settings.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private LocalModel localModel = LocalModel.getInstance();

    public String getAssignedTag() {
        return TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View v;
        v = inflater.inflate(R.layout.manageplugs, container, false);
        setupList(v);

        return v;
    }

    private void setupList(View v) {

        RecyclerView recyclerView;
        RecyclerAdapter plugAdapter;
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
