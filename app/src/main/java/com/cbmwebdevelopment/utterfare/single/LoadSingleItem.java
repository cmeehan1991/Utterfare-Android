package com.cbmwebdevelopment.utterfare.single;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Connor Meehan on 1/9/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */

public class LoadSingleItem extends AsyncTask<String, Integer, String> {
    private final SingleItemActivity activity;
    private final String LINK = "https://www.utterfare.com/includes/php/single-item.php";
    private final String TAG = this.getClass().getName();
    private final Context CONTEXT;

    public LoadSingleItem(SingleItemActivity activity, Context context) {

        this.CONTEXT = context;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... args) {
        String itemId = args[0];
        String dataTable = args[1];
        String result = null;
        try{
            String data = URLEncoder.encode("item_id", "UTF-8") + "=" + URLEncoder.encode(itemId, "UTF-8");
            data += "&" + URLEncoder.encode("data_table", "UTF-8") + "=" + URLEncoder.encode(dataTable, "UTF-8");

            URL url = new URL(LINK);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                sb.append(line);
                break;
            }
            result = sb.toString();
            wr.close();
            reader.close();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result){
        activity.parseItem(result);
    }
}
