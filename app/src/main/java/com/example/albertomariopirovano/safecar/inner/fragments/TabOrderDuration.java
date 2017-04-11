package com.example.albertomariopirovano.safecar.inner.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.data_comparators.DSIComparator;
import com.example.albertomariopirovano.safecar.data_comparators.DateComparator;
import com.example.albertomariopirovano.safecar.data_comparators.DurationComparator;
import com.example.albertomariopirovano.safecar.services.FetchService;

import java.util.Comparator;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class TabOrderDuration extends Fragment implements TabFragment{

    private String name = "Duration";
    private Realm realm;
    private ListView listView;
    private Comparator comparator = new DurationComparator();
    private FetchService dataService = FetchService.getInstance();


    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_order_duration, container, false);

        System.out.println("tab order duration");
        listView = (ListView) v.findViewById(R.id.listDuration);

        if(listView == null) {
            System.out.println("Null list view");
        }

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("default2")
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);

        dataService.insertTrips("duration",v, getComparator(), realm, listView, 0, 10);

        return v;
    }

    public Comparator getComparator() {
        return comparator;
    }
}
