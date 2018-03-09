package com.cbmwebdevelopment.utterfare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by cmeehan on 12/6/16.
 */

public class LoadImages extends AsyncTask<String, Void, Bitmap>{
    private ImageView bmImage;
    private InputStream url;
    private final String TAG = getClass().toString();

    public LoadImages(ImageView bmImage){
        this.bmImage = bmImage;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String urlDisplay = params[0];
        Bitmap bmp = null;
        try {
            url = new URL(params[0]).openStream();
            bmp = BitmapFactory.decodeStream(url);
            url.close();
        } catch (IOException e) {
            Log.e(TAG, "Image error: " +  e.getMessage());
        }finally{
        }
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap bmpResults){
        bmImage.setImageBitmap(bmpResults);
    }
}
