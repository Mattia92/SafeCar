package com.example.albertomariopirovano.safecar.services;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.activity.MainActivity;
import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.inner.fragments.ReportFragment;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class FetchService {

    private static final String TAG = "FetchService";
    private static FetchService ourInstance = new FetchService();
    private LocalModel localModel = LocalModel.getInstance();
    private FirebaseAuth auth;
    private Integer numTask = 0;

    public FetchService() {

        this.auth = FirebaseAuth.getInstance();
    }

    public static FetchService getInstance() {
        return ourInstance;
    }

    public void insertTrips(final String attributeToShow, final View v, final Comparator c, final ListView l, int startRange, int endRange) {

        numTask++;
        Log.i(TAG, "Asynchronous trip loading task id is: " + String.valueOf(numTask));
        new PopolateTrips(c, attributeToShow, v, l, startRange, endRange).execute();

    }

    private class PopolateTrips extends AsyncTask<Void, Void, List<Map<String, String>>> {

        private final List<Trip> userTrips = new ArrayList<Trip>();
        private Comparator c;
        private String attributeToShow;
        private View v;
        private ListView l;
        private int startRange;
        private int endRange;

        public PopolateTrips(Comparator c, String attributeToShow, View v, ListView l, int startRange, int endRange) {
            this.c = c;
            this.attributeToShow = attributeToShow;
            this.v = v;
            this.l = l;
            this.endRange = endRange;
            this.startRange = startRange;
        }

        @Override
        protected List<Map<String, String>> doInBackground(Void... voids) {

            for (Trip t : localModel.getTrips()) {
                if (t.getDropped().equals(Boolean.FALSE)) {
                    userTrips.add(t);
                }
            }

            Collections.sort(userTrips, c);

            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            for (Trip t : userTrips) {
                Map<String, String> datum = new HashMap<String, String>(2);
                datum.put("title", t.depName + " - " + t.arrName);
                datum.put("date", String.valueOf(t.getAttr(attributeToShow)));
                data.add(datum);
            }

            return data;
        }

        @Override
        protected void onPostExecute(List<Map<String, String>> data) {
            super.onPostExecute(data);

            SimpleAdapter adapter = new SimpleAdapter(v.getContext(), data,
                    android.R.layout.simple_list_item_2,
                    new String[]{"title", "date"},
                    new int[]{android.R.id.text1, android.R.id.text2});
            l.setAdapter(adapter);

            final FragmentManager fragmentManager = ((MainActivity) v.getContext()).getSupportFragmentManager();

            l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Trip t = userTrips.get(i);

                    ReportFragment rf = new ReportFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("key", t);
                    rf.setArguments(bundle);
                    ((MainActivity) v.getContext()).setEnabledNavigationDrawer(false);
                    fragmentManager.beginTransaction().replace(R.id.main_content, rf, rf.getAssignedTag()).addToBackStack(null).commit();
                }
            });
        }
    }
}