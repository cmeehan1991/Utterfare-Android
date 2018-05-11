package com.cbmwebdevelopment.utterfare.profile;

import android.os.AsyncTask;
import android.util.Log;

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
public class UpdateUserInformationModel extends AsyncTask<String, Void, String> {
    private final String TAG = getClass().getName();
    @Override
    protected String doInBackground(String... args) {
        String results = null;
        String userId = args[0];
        String firstName = args[1];
        String lastName = args[2];
        String city = args[3];
        String state = args[4];
        String email = args[5];
        try{
            String link = "https://www.utterfare.com/includes/mobile/users/Users.php";

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("set_user", "UTF-8");
            data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
            data += "&" + URLEncoder.encode("first_name", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8");
            data += "&" + URLEncoder.encode("last_name", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8");
            data += "&" + URLEncoder.encode("city", "UTF-8") + "=" + URLEncoder.encode(city, "UTF-8");
            data += "&" + URLEncoder.encode("state", "UTF-8") + "=" + URLEncoder.encode(state, "UTF-8");
            data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

            // Establish a connection
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            // Initialize the output writer and write to the stream
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response and read the data
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null){
                sb.append(line);
                break;
            }

            results = sb.toString();

        }catch(IOException ex){
            Log.e(TAG, "Error: " + ex.getMessage());
        }
        return results;
    }
}
