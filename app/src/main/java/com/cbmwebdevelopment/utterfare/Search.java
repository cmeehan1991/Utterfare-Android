package com.cbmwebdevelopment.utterfare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by cmeehan on 12/5/16.
 */

public class Search extends AsyncTask<String, String, String> {
    private final Context CONTEXT;
    private final String TAG = getClass().toString();
    private String terms, location, distance, offset, page;


    public Search(Context context) {
        this.CONTEXT = context;
    }

    /**
     * This is the primary search function. This will take the user input data, send it to android-search.php and then return the results
     *
     * @params Strings terms, location, distance     These are taken from the user on the front end and passed to this method
     * @returns String results                       This is a json array that will be parsed by the onPostExecute method
     */
    @Override
    protected String doInBackground(String... args) {
        terms = args[0];
        location = args[1];
        distance = args[2];
        offset = args[3];
        page = args[4];
        Log.i(TAG, location);
        String limit = "10"; // Always limit to 25 per page
        String link = "https://www.utterfare.com/includes/php/android-search.php";
        try {
            // The data to be passed to the android-search.php file
            String data = URLEncoder.encode("terms", "UTF-8") + "=" + URLEncoder.encode(terms, "UTF-8");
            data += "&" + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8");
            data += "&" + URLEncoder.encode("distance", "UTF-8") + "=" + URLEncoder.encode(distance, "UTF-8");
            data += "&" + URLEncoder.encode("limit", "UTF-8") + "=" + URLEncoder.encode(limit, "UTF-8");
            data += "&" + URLEncoder.encode("offset", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(offset), "UTF-8");
            data += "&" + URLEncoder.encode("page", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(page), "UTF-8");

            Log.i(TAG, data);
            // Establish and open the connection with the URL
            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            // Get the stream for writing the data
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            // Write the data
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            // This string builder will be used to concat all of the results and then converted into a String for the return value
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read the server response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }

            return sb.toString();

        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    /**
     * After the task has been executed we are going to send the results to the showItems class
     *
     * @param result
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPostExecute(String result) {
        // Get the current class so we can determine how to proceed
        String currentClass = CONTEXT.getClass().toString();
        // Check for results.
        // If there aren't any results then we are going to handle that.
        Log.i(TAG, result);
        if(result.equals("No Results") || result.isEmpty() || result.equals("<br />")){

            // Ask the user to try a different search.
            if(currentClass.contains("MainActivity")){
                Toast.makeText(CONTEXT, "No results. Try a different search.", Toast.LENGTH_SHORT).show();
                ProgressBar progressBar = ((MainActivity) CONTEXT).progressBar;
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.setProgress(0);
            }else{
                // This is for the feed only.
                Toast.makeText(CONTEXT, "End of feed.", Toast.LENGTH_SHORT).show();
            }
        }else {
            // If we are running a new search from the main activity
            if (currentClass.contains("MainActivity")) {

                if(!result.isEmpty()) {

                    // Hide the progress bar
                    ProgressBar progressBar = ((MainActivity) CONTEXT).progressBar;
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBar.setProgress(0);

                    // Get the results activity, which is where we are heading next.
                    Activity resultsActivity = (Activity) CONTEXT;

                    // Start the new activity
                    Intent intent = new Intent(CONTEXT, ResultsActivity.class);
                    intent.putExtra("results", result);
                    intent.putExtra("page", page);
                    intent.putExtra("terms", terms);
                    intent.putExtra("location", location);
                    intent.putExtra("distance", distance);
                    resultsActivity.startActivity(intent);
                }
            }
        }
    }

}