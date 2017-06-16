package com.example.albertomariopirovano.safecar.inner.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.data_comparators.DateComparator;
import com.example.albertomariopirovano.safecar.services.FetchService;

import java.util.Comparator;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class TabOrderDate extends Fragment implements TabFragment, TAGInterface {

    private static final String TAG = "TabOrderDate";
    private String name = "Date";
    private ListView listView;
    private Comparator comparator = new DateComparator();
    private FetchService dataService = FetchService.getInstance();

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

        View v = inflater.inflate(R.layout.tab_order_date, container, false);

        listView = (ListView) v.findViewById(R.id.listDate);

        if(listView == null) {
            System.out.println("Null list view");
        }

        Log.d("TabOrderDate", "insertTrips");
        dataService.insertTrips("date", v, getComparator(), listView, 0, 10);

        return v;
    }

    public Comparator getComparator() {
        return comparator;
    }
}
