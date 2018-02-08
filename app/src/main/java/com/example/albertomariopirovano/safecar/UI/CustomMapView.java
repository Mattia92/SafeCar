package com.example.albertomariopirovano.safecar.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.maps.MapView;

/**
 * Created by albertomariopirovano on 08/02/18.
 */

public class CustomMapView extends MapView {

    public CustomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Disallow ScrollView to intercept touch events.
                this.getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                this.getParent().getParent().getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        // Handle MapView's touch events.
        super.dispatchTouchEvent(ev);
        return true;
    }
}