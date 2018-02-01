package com.example.albertomariopirovano.safecar.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;

/**
 * Created by mattiacrippa on 31/01/18.
 */

public class CustomGrid extends BaseAdapter {

    private Context context;
    private final String[] badgesName;
    private final int[] badgesIcon;

    public CustomGrid(Context c, String[] badgesName, int[] badgesIcon) {
        context = c;
        this.badgesIcon = badgesIcon;
        this.badgesName = badgesName;
    }

    @Override
    public int getCount() {
        return badgesName.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View grid;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            grid = new View(context);
            grid = inflater.inflate(R.layout.badge_grid, null);

            TextView textView = (TextView) grid.findViewById(R.id.badge_text);
            ImageView imageView = (ImageView)grid.findViewById(R.id.badge_image);

            textView.setText(badgesName[i]);
            imageView.setImageResource(badgesIcon[i]);
        } else {
            grid = (View) view;
        }

        return grid;
    }
}
