package com.cbmwebdevelopment.utterfare.passwordreset;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static com.cbmwebdevelopment.utterfare.main.GlobalVariables.ENCODING;
import static com.cbmwebdevelopment.utterfare.main.GlobalVariables.USER_LINK;

/**
 * Created by Connor Meehan on 5/24/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class ResetPasswordModel extends AsyncTask<String, Void, String> {
    private final String TAG = getClass().getName();
    @Override
    protected String doInBackground(String...args) {
        String userId = args[0];
        String password = args[1];
        String results = null;
        try{
            URL url = new URL(USER_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", ENCODING) + "=" + URLEncoder.encode("set_new_password", ENCODING);
            data += "&" + URLEncoder.encode("user_id", ENCODING) + "=" + URLEncoder.encode(userId, ENCODING);
            data += "&" + URLEncoder.encode("password", ENCODING) + "=" + URLEncoder.encode(password, ENCODING);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            results = reader.readLine();

        }catch(IOException ex){
            Log.e(TAG, "Error getting data: " + ex.getMessage());
        }
        return results;
    }
}
