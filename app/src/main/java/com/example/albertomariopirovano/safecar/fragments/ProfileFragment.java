package com.example.albertomariopirovano.safecar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.model.Dog;
import com.example.albertomariopirovano.safecar.model.Trip;
import com.example.albertomariopirovano.safecar.model.User;

import io.realm.Realm;

/**
 * Created by albertomariopirovano on 03/04/17.
 */

public class ProfileFragment extends Fragment {

    private Realm realm = Realm.getDefaultInstance();;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        Button insertButton = (Button) v.findViewById(R.id.insert);
        insertButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Dog myDog = realm.createObject(Dog.class);
                        myDog.setName("Fido");
                        myDog.setAge(1);
                    }
                });

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Dog myPuppy = realm.where(Dog.class).equalTo("age", 1).findFirst();
                        myPuppy.setAge(2);
                    }
                });
                Dog myDog = realm.where(Dog.class).equalTo("age", 2).findFirst();
                Toast.makeText(getActivity(), myDog.toString(),Toast.LENGTH_LONG).show();
            }
        });

        Button testInsertButton = (Button) v.findViewById(R.id.testInsert);
        testInsertButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                User u = realm.where(User.class).equalTo("name", "Alberto").findFirst();

                System.out.println(u.getUnlockedBadges().get(0).toString());
                for(Trip t : u.getTrips()) {
                    System.out.println(t.toString());
                }
                System.out.println(u.toString());

                Toast.makeText(getActivity(), u.getUnlockedBadges().get(0).toString(),Toast.LENGTH_LONG).show();
            }
        });
        return v;
    }

}
