package com.example.albertomariopirovano.safecar.settings.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;

import java.util.ArrayList;
import java.util.List;

import com.podcopic.animationlib.library.AnimationType;
import com.podcopic.animationlib.library.StartSmartAnimation;

/**
 * Created by mattiacrippa on 15/03/17.
 */

public class SettingsSmartObjectsFragment extends Fragment {

    private TextView clickableSpeaker, clickablePlug;
    private TextView hiddenSpeaker;
    private ListView hiddenPlug;

    private List<String> plugList;
    PlugListAdapter plugListAdapter;

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        v = inflater.inflate(R.layout.settings_smartobj_layout, container, false);

        clickableSpeaker = (TextView) v.findViewById(R.id.pairing_speakers);
        clickablePlug = (TextView) v.findViewById(R.id.gestione_plug);
        hiddenSpeaker = (TextView) v.findViewById(R.id.pairing_speakers_hidden);
        hiddenPlug = (ListView) v.findViewById(R.id.gestione_plug_hidden);
        plugList = new ArrayList<String>();

        //TODO(Fetch plug for user)
        for(int i = 0; i < 3; i++) {
            plugList.add("Plug " + i);
        }

        plugListAdapter = new PlugListAdapter();
        hiddenPlug.setAdapter(plugListAdapter);

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
                if(hiddenPlug.isShown()){
                    slide_up(getActivity(), hiddenPlug);
                    hiddenPlug.setVisibility(View.GONE);
                }
                else{
                    hiddenPlug.setVisibility(View.VISIBLE);
                    slide_down(getActivity(), hiddenPlug);
                }
            }
        });
        // hide until its title is clicked
        hiddenPlug.setVisibility(View.GONE);

        return v;
    }

    public static void slide_up(Context ctx, View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_down(Context ctx, View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }



    class PlugListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return plugList.size();
        }

        @Override
        public Object getItem(int position) {
            return plugList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.plug_list, null);
                new ViewHolder(convertView);
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.name.setText(plugList.get(position));
            holder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StartSmartAnimation.startAnimation(holder.item, AnimationType.SlideOutRight, 1000, 0, true);
                    plugList.remove(position);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hiddenPlug.setAdapter(plugListAdapter);
                        }
                    }, 1000);
                }
            });

            return convertView;
        }


        class ViewHolder {
            private TextView name;
            private Button del;
            private View item;

            public ViewHolder(View view) {
                name = (TextView) view.findViewById(R.id.name);
                del = (Button) view.findViewById(R.id.del);
                item = view.findViewById(R.id.item);

                view.setTag(this);
            }
        }
    }

}
