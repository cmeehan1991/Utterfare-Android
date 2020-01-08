package com.cbmwebdevelopment.utterfare.newuser;

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
 * Created by Connor Meehan on 5/17/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class NewUserModel extends AsyncTask<String, Void, String> implements GlobalVariables{
    private final String TAG = getClass().getName();

    @Override
    public String doInBackground(String...args){
        String username = args[0];
        String password = args[1];
        String firstName = args[2];
        String lastName = args[3];
        String city = args[4];
        String state = args[5];
        String email = args[6];
        String phone = args[7];
        String postalCode = args[8];
        String gender = args[9];
        String birthday = args[10];

        String results = null;
        try{
            String link = "https://www.utterfare.com/includes/php/Users.php";
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            // Set the data to be written
            String data = URLEncoder.encode("action", ENCODING) + "=" + URLEncoder.encode("new_user", ENCODING);
            data += "&" + URLEncoder.encode("email", ENCODING) + "=" + URLEncoder.encode(username, ENCODING);
            data += "&" + URLEncoder.encode("password", ENCODING) + "=" + URLEncoder.encode(password, ENCODING);
            data += "&" + URLEncoder.encode("first_name", ENCODING) + "=" + URLEncoder.encode(firstName, ENCODING);
            data += "&" + URLEncoder.encode("last_name", ENCODING) + "=" + URLEncoder.encode(lastName, ENCODING);
            data += "&" + URLEncoder.encode("city", ENCODING) + "=" + URLEncoder.encode(city, ENCODING);
            data += "&" + URLEncoder.encode("state", ENCODING) + "=" + URLEncoder.encode(state, ENCODING);
            data += "&" + URLEncoder.encode("email", ENCODING) + "=" + URLEncoder.encode(email, ENCODING);
            data += "&" + URLEncoder.encode("phone", ENCODING) + "=" + URLEncoder.encode(phone, ENCODING);
            data += "&" + URLEncoder.encode("postal_code", ENCODING) + "=" + URLEncoder.encode(postalCode, ENCODING);
            data += "&" + URLEncoder.encode("gender", ENCODING) + "=" + URLEncoder.encode(gender, ENCODING);
            data += "&" + URLEncoder.encode("birthday", ENCODING) + "=" + URLEncoder.encode(birthday, ENCODING);
            // Set the output stream
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            // Get the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            results = reader.readLine();
        }catch(IOException ex){
            Log.e(TAG, "IOException: " + ex.getMessage());
        }
        return results;
    }
}
