package com.cbmwebdevelopment.utterfare.user;

import android.os.AsyncTask;
import android.util.Log;

import com.cbmwebdevelopment.utterfare.main.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by Connor Meehan on 2020-01-07.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class UserProfileModel extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... args){
        String link = "https://www.utterfare.com/includes/php/Users.php";
        String userId = args[0];

        try{
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_user", "UTF-8");
            data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null){
                sb.append(line);
                break;
            }

            return sb.toString();

        }catch(IOException ex){
            Log.e(TAG, "User Profile Error");
            Log.e(TAG, ex.getMessage());
            return "";
        }
    }
}
