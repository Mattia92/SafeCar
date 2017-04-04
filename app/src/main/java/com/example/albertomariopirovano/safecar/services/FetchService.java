package com.example.albertomariopirovano.safecar.services;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.activity.MainActivity;
import com.example.albertomariopirovano.safecar.inner.fragments.ReportFragment;
import com.example.albertomariopirovano.safecar.model.Trip;
import com.example.albertomariopirovano.safecar.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by albertomariopirovano on 04/04/17.
 */

public class FetchService {

    private static FetchService ourInstance = new FetchService();
    private List<Trip> tripList;
    FragmentManager fragmentManager;

    public static FetchService getInstance() {
        return ourInstance;
    }

    public void insertTrips(final String attributeToShow, final View v, final Comparator c, Realm r, final ListView l, int startRange, int endRange) {

        System.out.println("------------------ENTER INSERTTRIPS-----------------------------");

        fragmentManager = ((MainActivity)v.getContext()).getSupportFragmentManager();

        r.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {

                System.out.println("------------------ATTR-----------------------------");

                System.out.println(attributeToShow);

                User u = realm.where(User.class).equalTo("name", "Alberto").findFirst();

                System.out.println("--------------------LIST BEFORE ORDER---------------------------");

                tripList = u.getTrips();

                for(Trip t:tripList) {
                    System.out.println(t);
                }

                Collections.sort(tripList,c);

                System.out.println("-----------------------LIST AFTER ORDER------------------------");

                for(Trip t:tripList) {
                    System.out.println(t);
                }

                List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                for(Trip t:tripList) {
                    Map<String, String> datum = new HashMap<String, String>(2);
                    datum.put("title", t.getDeparture() + " - " + t.getArrival());
                    datum.put("date", String.valueOf(t.getAttr(attributeToShow)));
                    data.add(datum);
                }

                SimpleAdapter adapter = new SimpleAdapter(v.getContext(), data,
                        android.R.layout.simple_list_item_2,
                        new String[]{"title", "date"},
                        new int[]{android.R.id.text1, android.R.id.text2});

                l.setAdapter(adapter);

            }
        });

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                System.out.println("------------------ENTER ON CLICK ITEM LISTENER-----------------------------");

                Trip t = tripList.get(i);

                System.out.println("-----------------PRINT LIST INSIDE ON CLICK ITEM LISTENER------------------------------");

                for(Trip t1:tripList) {
                    System.out.println(t1);
                }

                System.out.println("-------------------EXIT ON CLICK ITEM LISTENER----------------------------");

                ReportFragment rf = new ReportFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("key", t);
                rf.setArguments(bundle);
                ((MainActivity)v.getContext()).setEnabledNavigationDrawer(false);
                fragmentManager.beginTransaction().replace(R.id.main_content, rf).addToBackStack(null).commit();
            }
        });

        System.out.println("-------------------EXIT ON INSERTTRIPS----------------------------");
    }
}
