package com.example.albertomariopirovano.safecar.concurrency;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;

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
    private final ImageButton ref;
    private Context c;

    public DownloadImage(Context c, ImageButton imb) {
        this.c = c;
        this.ref = imb;
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
        if (ref != null && bitmap != null) {
            ref.setImageBitmap(bitmap);
        }
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
        ContextWrapper cw = new ContextWrapper(c);
        File directory = cw.getDir("safecar", Context.MODE_PRIVATE);
        File profilePngFile = new File(directory, "profile.png");
        try {
            out = new FileOutputStream(profilePngFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
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