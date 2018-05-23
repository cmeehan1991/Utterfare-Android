package com.cbmwebdevelopment.utterfare.passwordreset;

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
 * Created by Connor Meehan on 5/18/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class RequestCodeModel extends AsyncTask<String, Void, String> implements GlobalVariables{
    private final String TAG  = getClass().getName();

    @Override
    protected String doInBackground(String... args) {
        String email = args[0];
        String results = null;
        try{
            URL url = new URL(USER_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            // Set the data
            String data = URLEncoder.encode("action", ENCODING) + "=" + URLEncoder.encode("reset_password_request", ENCODING);
            data += "&" + URLEncoder.encode("email", ENCODING) + "=" + URLEncoder.encode(email, ENCODING);

            // Write the data
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String read = reader.readLine();
            if(read != null){
                results = read;
            }
        }catch(IOException ex){
            Log.e(TAG, "IOException: " + ex.getMessage());
        }
        return results;
    }
}
