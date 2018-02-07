package com.example.albertomariopirovano.safecar.concurrency;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.activity.MainActivity;
import com.example.albertomariopirovano.safecar.services.SavedStateHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by albertomariopirovano on 10/06/17.
 */

public class DSIEvaluator extends AsyncTask<Void, Void, Void> implements Serializable {

    private static final String TAG = "DSIEvaluator";
    private final Activity activity;
    private final Object lock;
    private Boolean stopTask = Boolean.FALSE;
    private Boolean pauseTask = Boolean.FALSE;
    private Integer currentDSI;
    private ArrayList<String> hintsList;
    private Boolean rebootImageview = Boolean.FALSE;
    private Boolean viewAvailable = Boolean.TRUE;
    private SavedStateHandler savedStateHandler = SavedStateHandler.getInstance();
    SharedPreferences sharedPreferences;
    boolean isChecked;

    private int counter = 0;

    private ArrayAdapter<String> hintsAdapter;

    public DSIEvaluator(Activity activity, ListView hintsListView, Object lock) {
        this.activity = activity;
        this.currentDSI = 0;
        this.lock = lock;
        this.hintsList = new ArrayList<String>();
        setViewElements(hintsListView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        isChecked = sharedPreferences.getBoolean("notifiche", false);
    }

    public void reloadTaskState() {
        Log.i(TAG, "reloadTaskState");
        this.rebootImageview = Boolean.TRUE;
        this.viewAvailable = Boolean.TRUE;
    }

    public void viewNotAvailable() {
        Log.i(TAG, "viewNotAvailable");
        viewAvailable = Boolean.FALSE;
    }

    private void addHint(final String hint) {
        Log.i(TAG, "addHint");
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Log.i(TAG, "runOnUiThread");
                hintsList.add(0, hint);
                if (viewAvailable || MainActivity.isApplicationSentToBackground(activity)) {
                    if (rebootImageview) {
                        hintsAdapter.clear();
                        hintsAdapter.addAll(hintsList);
                        rebootImageview = Boolean.FALSE;
                    } else {
                        hintsAdapter.insert(hint, 0);
                    }
                }
            }
        });
    }


    @Override
    protected Void doInBackground(Void... voids) {
        Log.i(TAG, "doInBackground");

        connectWithPlug();

        do {
            while (!stopTask && !pauseTask) {

                updateDSI();

            /*
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */

                Log.i(TAG, "wait on lock");
                long s = System.currentTimeMillis();
                savedStateHandler.waitOnLock(10000);
                long f = System.currentTimeMillis();
                Log.i(TAG, "awakened from lock t = " + String.valueOf(f - s));
            }

            while (pauseTask) {
                //wait until resume trip
            }

        } while (!stopTask);

        Log.i(TAG, "DSIEvaluation task has been stopped ! ");

        return null;
    }

    private void connectWithPlug() {
        Log.i(TAG, "connectWithPlug");
        Log.i(TAG, savedStateHandler.getTargetPlug().toString());
        //modify the state of the asynctask
    }

    private void computeDSI() {
        Log.i(TAG, "computeDSI");
        Random r = new Random();
        int Low = 0;
        int High = 1000;
        currentDSI = r.nextInt(High-Low) + Low;;
    }

    private void updateDSI() {
        Log.i(TAG, "updateDSI");
        computeDSI();
        String hint = "";
        if (currentDSI >= 0 && currentDSI < 50) {
            hint = "Wake up man !";
        } else if (currentDSI >= 50 && currentDSI < 100) {
            hint = "Be careful !";
        } else if (currentDSI >= 100 && currentDSI < 150) {
            hint = "Please, tell me you are still alive !";
        } else if (currentDSI >= 150 && currentDSI < 200) {
            hint = "I don't have all the day !";
        } else if (currentDSI >= 200 && currentDSI < 250) {
            hint = "This is an ECO drive style !";
        } else if (currentDSI >= 250 && currentDSI < 300) {
            hint = "Maybe we shuld go to the mechanic";
        } else if (currentDSI >= 300 && currentDSI < 350) {
            hint = "Speed up !";
        } else if (currentDSI >= 350 && currentDSI < 400) {
            hint = "Don't brake that often !";
        } else if (currentDSI >= 400 && currentDSI < 450) {
            hint = "Come on, you can do better !";
        } else if (currentDSI >= 450 && currentDSI < 500) {
            hint = "Come on enjoy your ride !";
        } else if (currentDSI >= 500 && currentDSI < 550) {
            hint = "Regular drive style, Ok !";
        } else if (currentDSI >= 550 && currentDSI < 600) {
            hint = "Ok man, maybe you should calm down";
        } else if (currentDSI >= 600 && currentDSI < 650) {
            hint = "Are you crazy ? Slow down !";
        } else if (currentDSI >= 650 && currentDSI < 700) {
            hint = "This is not a good driving style !";
        } else if (currentDSI >= 700 && currentDSI < 750) {
            hint = "Please stop the car and take a breath";
        } else if (currentDSI >= 750 && currentDSI < 800) {
            hint = "Take a break, let's have a tea";
        } else if (currentDSI >= 800 && currentDSI < 850) {
            hint = "Are you crazy ? Slow down !";
        } else if (currentDSI >= 850 && currentDSI < 900) {
            hint = "I am going to call the police !";
        } else if (currentDSI >= 900 && currentDSI < 950) {
            hint = "The police is arriving, prepare yourself";
        } else {
            hint = "Are you drunk ?";
        }

        addHint(hint);
        //update the state of the asynctask basing on the state modified by connectWithPlug

        if(isChecked) {
            if(MainActivity.isApplicationSentToBackground(activity)) {
                Log.i(TAG, "App in background");
                MainActivity.addNotification(activity, hint);
            }
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
        this.pauseTask = Boolean.FALSE;
    }

    public void pauseTask() {
        this.pauseTask = Boolean.TRUE;
    }

    public void resumeTask() {
        this.pauseTask = Boolean.FALSE;
    }

    public void setViewElements(ListView hintsListView) {
        this.hintsAdapter = new ArrayAdapter<>(activity, R.layout.white_single_list_item, new ArrayList<String>());
        this.hintsAdapter.setNotifyOnChange(true);
        hintsListView.setAdapter(hintsAdapter);
    }
}
