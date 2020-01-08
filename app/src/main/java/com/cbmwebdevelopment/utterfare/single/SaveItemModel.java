package com.cbmwebdevelopment.utterfare.single;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static com.cbmwebdevelopment.utterfare.main.GlobalVariables.USER_ITEMS_URL;

/**
 * Created by Connor Meehan on 5/9/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class SaveItemModel extends AsyncTask<String, Void, String> {
    private final String TAG = getClass().getName();
    @Override
    protected String doInBackground(String... args) {

        // The arguements passed by the activity
        String userId = args[0];
        String itemId = args[1];

        // The results returned by the request.
        String results = null;

        try{
            // Encode the data to be passed to the server
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("add_item", "UTF-8");
            data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
            data += "&" + URLEncoder.encode("item_id", "UTF-8") + "=" + URLEncoder.encode(itemId, "UTF-8");

            // Establish connection with server
            String link = "https://www.utterfare.com/includes/php/UsersItems.php";
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            // Write to the stream
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            // Handle the response
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                sb.append(line);
                break;
            }

            // Set the results to the response
            results = sb.toString();
        }catch(IOException ex){
            Log.e(TAG, "Error: " + ex.getMessage());
        }

        return results;
    }
}
