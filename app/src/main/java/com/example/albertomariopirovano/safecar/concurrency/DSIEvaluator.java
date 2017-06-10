package com.example.albertomariopirovano.safecar.concurrency;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.albertomariopirovano.safecar.firebase_model.Plug;

import java.util.ArrayList;

/**
 * Created by albertomariopirovano on 10/06/17.
 */

public class DSIEvaluator extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "DSIEvaluator";
    private final Activity activity;
    private final Plug targetPlug;
    private Boolean stopTask = Boolean.FALSE;
    private Integer currentDSI;

    private int counter = 0;

    private ArrayAdapter<String> hintsAdapter;

    public DSIEvaluator(Activity activity, Plug targetPlug, ListView hintsListView) {
        this.targetPlug = targetPlug;
        this.activity = activity;
        this.currentDSI = 0;
        this.hintsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, new ArrayList<String>());
        this.hintsAdapter.setNotifyOnChange(true);
        hintsListView.setAdapter(hintsAdapter);
    }

    private void addHint(final String hint) {
        Log.d(TAG, "addHint");
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "runOnUiThread");
                hintsAdapter.add(hint);
            }
        });
    }


    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground");

        connectWithPlug();

        while (!stopTask) {

            updateDSI();

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "DSIEvaluation task has been stopped ! ");

        return null;
    }

    private void connectWithPlug() {
        Log.d(TAG, "connectWithPlug");
        Log.d(TAG, targetPlug.toString());
        //modify the state of the asynctask
    }

    private void computeDSI() {
        Log.d(TAG, "computeDSI");
        currentDSI = 1000;
    }

    private void updateDSI() {
        Log.d(TAG, "updateDSI");
        computeDSI();
        String hint = "";
        if (currentDSI >= 100) {
            hint = "";
        } else if (currentDSI >= 50) {
            hint = "";
        } else if (currentDSI >= 20) {
            hint = "";
        } else if (currentDSI >= 10) {
            hint = "";
        } else {
            hint = "";
        }

        hint = fakeHint();

        addHint(hint);
        //update the state of the asynctask basing on the state modified by connectWithPlug
    }

    private String fakeHint() {
        String string_hint = String.valueOf(counter);
        counter++;
        return "Hint " + String.valueOf(string_hint);
    }

    public void stopTask() {
        this.stopTask = Boolean.TRUE;
    }
}
