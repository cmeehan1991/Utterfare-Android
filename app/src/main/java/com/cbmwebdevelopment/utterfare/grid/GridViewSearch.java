package com.cbmwebdevelopment.utterfare.grid;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cbmwebdevelopment.utterfare.grid.GridViewActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Connor Meehan on 1/23/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */

public class GridViewSearch extends AsyncTask<String, Integer, String> {
    private final String TAG = this.getClass().getName();
    private final String link = "https://www.utterfare.com/includes/php/grid-search.php";
    private final GridViewActivity gridViewActivity;


    public GridViewSearch(GridViewActivity gridViewActivity){
        this.gridViewActivity = gridViewActivity;
    }

    /**
     *
     * @param args - lat/lng, offset
     * @return
     */
    @Override
    protected String doInBackground(String... args) {
        String location = "36.100255:-76.500494";//args[0];
        String offset = args[1];
        try{
            String data = URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8");
            data += "&" + URLEncoder.encode("offset", "UTF-8") + "= " + URLEncoder.encode(offset, "UTF-8");
            // Establish a connection
            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            // Get the stream for writing the data
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            // Write the data
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                sb.append(line);
                break;
            }
            wr.close();
            return sb.toString();
        }catch(Exception ex){
            Log.e(TAG, ex.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String results){
        if (results.equals("No Results") || results == null || results.trim().isEmpty() || results.equals("<br />")) {
            Toast.makeText(gridViewActivity, "No more items near your location.", Toast.LENGTH_SHORT).show();
        } else {
            gridViewActivity.showItems(results);
        }
    }
}
