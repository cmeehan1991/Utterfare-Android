package com.cbmwebdevelopment.utterfare.passwordreset;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
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
 * Created by Connor Meehan on 5/21/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class VerifyCodeModel extends AsyncTask<String, Void, String> {
    private final String TAG = getClass().getName();
    @Override
    protected String doInBackground(String... strings) {
        String username = strings[0];
        String code = strings[1];
        String results = null;
        try{
            String data = URLEncoder.encode("action", ENCODING) + "=" + URLEncoder.encode("verify_reset_code", ENCODING);
            data += "&" + URLEncoder.encode("username", ENCODING) + "=" + URLEncoder.encode(username, ENCODING);
            data += "&" + URLEncoder.encode("reset_code", ENCODING) + "=" + URLEncoder.encode(code, ENCODING);

            URL url = new URL(USER_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            results = reader.readLine();


        }catch(IOException ex){
            Log.e(TAG, "IOException: " + ex.getMessage());
        }
        return results;
    }
}
