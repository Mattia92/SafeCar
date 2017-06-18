package com.example.albertomariopirovano.safecar.inner.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.data_comparators.DurationComparator;
import com.example.albertomariopirovano.safecar.services.FetchService;

import java.util.Comparator;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class TabOrderDuration extends Fragment implements TabFragment, TAGInterface {

    private static final String TAG = "TabOrderDuration";
    private String name = "Duration";
    private ListView listView;
    private Comparator comparator = new DurationComparator();
    private FetchService dataService = FetchService.getInstance();
    private SwipeRefreshLayout swipeRefreshLayout;

    public String getName() {
        return name;
    }

    public String getAssignedTag() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");

        final View v = inflater.inflate(R.layout.tab_order_duration, container, false);

        listView = (ListView) v.findViewById(R.id.listDuration);

        if(listView == null) {
            System.out.println("Null list view");
        }

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefreshHomeDuration);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh");
                dataService.insertTrips("duration", v, getComparator(), listView, 0, 10);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Log.d("TabOrderDuration", "insertTrips");
        dataService.insertTrips("duration", v, getComparator(), listView, 0, 10);

        return v;
    }

    public Comparator getComparator() {
        return comparator;
    }
}
