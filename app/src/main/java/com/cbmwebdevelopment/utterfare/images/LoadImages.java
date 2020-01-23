package com.cbmwebdevelopment.utterfare.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by cmeehan on 12/6/16.
 */

public class LoadImages extends AsyncTask<String, Bitmap, Bitmap> {
    private ImageView bmImage;
    private final String TAG = getClass().toString();

    public LoadImages(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imageLink = params[0];

        if (imageLink.contains("http://")) {
            imageLink = imageLink.replace("http://", "https://");
        }

        Bitmap bmp = null;
        try {
            URL url = new URL(imageLink);

            URLConnection conn = url.openConnection();
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            // Check to make sure the header response is 200
            if(conn.getHeaderField(null).contains("200")) {
                bmp = BitmapFactory.decodeStream(bufferedInputStream);
            }

            // Close everything
            bufferedInputStream.close();
            inputStream.close();

        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return bmp;

    }

    @Override
    protected void onPostExecute(Bitmap bmpResults) {
        if (bmpResults != null){
            bmImage.setImageBitmap(bmpResults);
        }else{

            Uri uri = Uri.parse("android.resource://com." + R.class.getPackage().getName() + "/" + R.drawable.ic_logo_no_background);

            bmImage.setImageURI(uri);
        }
    }
}
