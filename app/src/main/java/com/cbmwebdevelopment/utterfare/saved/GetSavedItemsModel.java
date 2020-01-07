package com.cbmwebdevelopment.utterfare.saved;

import android.os.AsyncTask;
import android.util.Log;

import com.cbmwebdevelopment.utterfare.main.GlobalVariables;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Connor Meehan on 5/9/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class GetSavedItemsModel extends AsyncTask<String, Void, String> implements GlobalVariables {
    private final String TAG = getClass().getName();
    @Override
    protected String doInBackground(String... args) {
        String userId = args[0];
        String results = null;
        String userItemsUrl = "https://www.utterfare.com/includes/php/UsersItems.php";
        try{
            URL url = new URL(userItemsUrl);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            // Set the parameters to be passed to the server
            String data = URLEncoder.encode("action", "UTF-8")  + "=" + URLEncoder.encode("get_items", "UTF-8");
            data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");

            // Write the data to the stream
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null){
                sb.append(line);
                break;
            }

            // Set the results
            results = sb.toString();
        }catch(IOException ex){
            Log.i(TAG, "Error: " + ex.getMessage());
        }
        return results;
    }
}
