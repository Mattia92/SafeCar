package com.example.albertomariopirovano.safecar.concurrency;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.albertomariopirovano.safecar.activity.MainActivity;
import com.example.albertomariopirovano.safecar.services.SavedStateHandler;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by albertomariopirovano on 10/06/17.
 */

public class DSIEvaluator extends AsyncTask<Void, Void, Void> implements Serializable {

    private static final String TAG = "DSIEvaluator";
    private final Activity activity;
    private final Object lock;
    private Boolean stopTask = Boolean.FALSE;
    private Integer currentDSI;
    private ArrayList<String> hintsList;
    private Boolean rebootImageview = Boolean.FALSE;
    private Boolean viewAvailable = Boolean.TRUE;
    private SavedStateHandler savedStateHandler = SavedStateHandler.getInstance();

    private int counter = 0;

    private ArrayAdapter<String> hintsAdapter;

    public DSIEvaluator(Activity activity, ListView hintsListView, Object lock) {
        this.activity = activity;
        this.currentDSI = 0;
        this.lock = lock;
        this.hintsList = new ArrayList<String>();
        setViewElements(hintsListView);
    }

    public void reloadTaskState() {
        Log.d(TAG, "reloadTaskState");
        this.rebootImageview = Boolean.TRUE;
        this.viewAvailable = Boolean.TRUE;
    }

    public void viewNotAvailable() {
        Log.d(TAG, "viewNotAvailable");
        viewAvailable = Boolean.FALSE;
    }

    private void addHint(final String hint) {
        Log.d(TAG, "addHint");
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "runOnUiThread");
                hintsList.add(hint);
                if (viewAvailable) {
                    if (rebootImageview) {
                        hintsAdapter.clear();
                        hintsAdapter.addAll(hintsList);
                        rebootImageview = Boolean.FALSE;
                    } else {
                        hintsAdapter.add(hint);
                    }
                }
            }
        });
    }


    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground");

        connectWithPlug();

        while (!stopTask) {

            updateDSI();

            /*
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */

            Log.d(TAG, "wait on lock");
            long s = System.currentTimeMillis();
            savedStateHandler.waitOnLock(10000);
            long f = System.currentTimeMillis();
            Log.d(TAG, "awakened from lock t = " + String.valueOf(f - s));
        }
        Log.d(TAG, "DSIEvaluation task has been stopped ! ");

        return null;
    }

    private void connectWithPlug() {
        Log.d(TAG, "connectWithPlug");
        Log.d(TAG, savedStateHandler.getTargetPlug().toString());
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

        if(MainActivity.isApplicationSentToBackground(activity)) {
            Log.d(TAG, "App in background");
            MainActivity.addNotification(activity, hint);
        }
    }

    private String fakeHint() {
        String string_hint = String.valueOf(counter);
        counter++;
        return "Hint " + String.valueOf(string_hint);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        synchronized (lock) {
            if (!viewAvailable) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopTask() {
        this.stopTask = Boolean.TRUE;
    }

    public void setViewElements(ListView hintsListView) {
        this.hintsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, new ArrayList<String>());
        this.hintsAdapter.setNotifyOnChange(true);
        hintsListView.setAdapter(hintsAdapter);
    }


}
