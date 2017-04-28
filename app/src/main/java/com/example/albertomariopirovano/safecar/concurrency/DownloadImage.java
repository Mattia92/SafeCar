package com.example.albertomariopirovano.safecar.concurrency;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by albertomariopirovano on 24/04/17.
 */

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    private File targetFile;
    private Activity activity;
    private Intent whereToGoNext;

    public DownloadImage(File targetFile, Activity activity, Intent whereToGoNext) {

        this.targetFile = targetFile;
        this.activity = activity;
        this.whereToGoNext = whereToGoNext;

    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        Log.d("DownloadImage", "doInBackground");
        Bitmap image = fetchImage(urls[0]);
        saveBitmap(image);
        return image;
    }

    protected void onPostExecute(Bitmap bitmap) {
        Log.d("DownloadImage", "onPostExecute");
        activity.startActivity(whereToGoNext);
        activity.finish();
    }

    private Bitmap fetchImage(String urlstr) {
        Log.d("DownloadImage", "fetchImage");
        try {
            URL url;
            url = new URL(urlstr);

            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setDoInput(true);
            c.connect();
            InputStream is = c.getInputStream();
            Bitmap img;
            img = BitmapFactory.decodeStream(is);
            return img;
        } catch (MalformedURLException e) {
            Log.d("RemoteImageHandler", "fetchImage passed invalid URL: " + urlstr);
        } catch (IOException e) {
            Log.d("RemoteImageHandler", "fetchImage IO exception: " + e);
        }
        return null;
    }

    public void saveBitmap(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(targetFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            Log.d("DownloadImage", "saveBitmap");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}