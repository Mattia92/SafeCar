package com.example.albertomariopirovano.safecar.inner.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.firebase_model.Trip;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class ReportFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.report_fragment, container, false);
        TextView tv = (TextView) v.findViewById(R.id.testTextView);

        Bundle bundle = this.getArguments();
        Trip t = null;
        if(bundle != null) {
            t = (Trip) bundle.getSerializable("key");
        }

        tv.setText(t.getDeparture() + " - " + t.getArrival() + " | " +  t.getDate());

        return v;
    }
}