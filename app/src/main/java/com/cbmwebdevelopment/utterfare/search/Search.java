package com.cbmwebdevelopment.utterfare.search;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cbmwebdevelopment.utterfare.results.ResultsActivity;

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

            reader.close();
            wr.close();
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
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPostExecute(String result) {
        // Get the current class so we can determine how to proceed
        String currentClass = CONTEXT.getClass().toString();

        // Check for results.
        // If there aren't any results then we are going to handle that.
        if(result.equals("No Results") || result.isEmpty() || result.equals("<br />")){

            // Ask the user to try a different search.
            if(currentClass.contains("MainActivity")){
                Toast.makeText(CONTEXT, "No results. Try a different search.", Toast.LENGTH_SHORT).show();
            }else{
                // This is for the feed only.
                Toast.makeText(CONTEXT, "End of feed.", Toast.LENGTH_SHORT).show();
            }
        }else {
            // If we are running a new search from the search activity
            if (Integer.parseInt(offset) == 0) {

                if(!result.isEmpty()) {
                    // Get the results activity, which is where we are heading next.
                    ResultsActivity resultsActivity = new ResultsActivity();

                    // Start the new activity
                    Bundle bundle = new Bundle();
                    bundle.putString("results", result);
                    bundle.putString("page", page);
                    bundle.putString("terms", terms);
                    bundle.putString("location", location);
                    bundle.putString("distance", distance);
                    resultsActivity.setArguments(bundle);

                    FragmentActivity activity = (FragmentActivity) CONTEXT;

                    FragmentManager fragmentManager = activity.getSupportFragmentManager();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                    fragmentTransaction.replace(android.R.id.tabcontent, resultsActivity);


                    fragmentTransaction.addToBackStack("Search");
                    fragmentTransaction.commit();
                }
            }
        }
    }

}
