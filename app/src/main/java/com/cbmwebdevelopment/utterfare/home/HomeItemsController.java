package com.cbmwebdevelopment.utterfare.home;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Connor Meehan on 2020-01-04.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */

public class HomeItemsController extends AsyncTask<String, String, String> {

    private HomeActivity homeActivity;
    private final String TAG = this.getClass().getName();
    private String action;
    public HomeItemsController(HomeActivity homeActivity){
        this.homeActivity = homeActivity;
    }
    /**
     *
     * @param args String[] {location, distance, action}
     * @return
     */
    @Override
    protected String doInBackground(String... args) {

        String location = args[0];
        String distance = args[1];
        this.action = args[2];

        String link = "https://www.utterfare.com/includes/php/search.php";
        try{

            // Set the arguments
            String data = URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8");
            data += "&" + URLEncoder.encode("distance", "UTF-8") + "=" + URLEncoder.encode(distance, "UTF-8");
            data += "&" + URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");

            // Establish the connection
            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            // Get the stream for writing the data
            conn.setDoOutput(true);

            // Initialize the writer
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            // Write the data
            wr.write(data);
            wr.flush();

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            // Concat the results into a string
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null){
                sb.append(line);
                break;
            }

            reader.close();
            wr.close();
            return sb.toString();

        }catch(IOException ex){
            return ex.getMessage();
        }

    }

    @Override
    protected void onPostExecute(String result){
        homeActivity.showHomeItems(result, this.action);
    }
}
