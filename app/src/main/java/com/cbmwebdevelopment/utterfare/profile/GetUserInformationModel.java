package com.cbmwebdevelopment.utterfare.profile;

import android.os.AsyncTask;
import android.util.Log;

import com.cbmwebdevelopment.utterfare.main.GlobalVariables;

import java.io.BufferedReader;
import java.io.IOException;
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
public class GetUserInformationModel extends AsyncTask<String, Void, String> implements GlobalVariables {
    private final String TAG = getClass().getName();
    @Override
    protected String doInBackground(String... args) {
        String userId = args[0];
        String results = null;
        try{

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_user", "UTF-8");
            data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");

            // Establish the connection
            URL url = new URL(USER_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

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

            results = sb.toString();


        }catch(IOException ex){
            Log.e(TAG, "Error: " + ex.getMessage());
        }
        return results;
    }
}
