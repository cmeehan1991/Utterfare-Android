package com.cbmwebdevelopment.utterfare.user;

import android.os.AsyncTask;
import android.util.Log;

import com.cbmwebdevelopment.utterfare.main.GlobalVariables;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


/**
 * Created by Connor Meehan on 5/15/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class DeleteUserAccountModel extends AsyncTask<String, Void, String> implements GlobalVariables{
    private final String TAG = getClass().getName();
    @Override
    protected String doInBackground(String... args) {
        String results = null;
        String userId = args[0];
        try{
            // Establish connection
            URL url = new URL(USER_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            // Data to be passed to the server.
            String data = URLEncoder.encode("action", ENCODING) + "=" + URLEncoder.encode("remove_user", ENCODING);
            data += "&" + URLEncoder.encode("user_id", ENCODING) + "=" + URLEncoder.encode(userId, ENCODING);

            // Write the data to the server
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            // Get the response from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            //Set the results to be returned
            results = reader.readLine();

            // Close the connection
            writer.close();
            reader.close();
        }catch(IOException ex){
            Log.e(TAG, "IOException: " + ex.getMessage());
            results = "{\"STATUS\":false,\"RESPONSE\":" + ex.getMessage() + "}";
        }
        return results;
    }
}
