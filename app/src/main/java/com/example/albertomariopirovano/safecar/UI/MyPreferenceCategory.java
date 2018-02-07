package com.example.albertomariopirovano.safecar.UI;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;

/**
 * Created by albertomariopirovano on 07/02/18.
 */

public class MyPreferenceCategory extends PreferenceCategory {
    public MyPreferenceCategory(Context context) {
        super(context);
    }
    public MyPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyPreferenceCategory(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        titleView.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryText));
    }
}