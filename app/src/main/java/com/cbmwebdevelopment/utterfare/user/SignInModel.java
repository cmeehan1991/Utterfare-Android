package com.cbmwebdevelopment.utterfare.user;

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
 * Created by Connor Meehan on 5/8/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class SignInModel extends AsyncTask<String, Void, String> {
    private final String TAG  = this.getClass().getName();
    private UserLoginActivity userLoginActivity;

    @Override
    protected String doInBackground(String... args) {
        String username = args[0];
        String password = args[1];

        String link = "https://www.utterfare.com/includes/php/Users.php";
        String results = null;

        try{

            // Add the arguments
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("log_in", "UTF-8");
            data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            // Initialize the connection
            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);

            // Set the writer and write the data
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null){
                sb.append(line);
                break;
            }

            // Close the connections
            reader.close();
            wr.close();

            // Set the results to be returned
            results = sb.toString();

        }catch(IOException ex){
            Log.i(TAG, "ERROR: " + ex.getMessage());
            results = null;
        }

        return results;
    }

    @Override
    protected void onPostExecute(String results){

    }
}
