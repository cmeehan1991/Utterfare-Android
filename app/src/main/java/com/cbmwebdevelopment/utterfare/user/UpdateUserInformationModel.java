package com.cbmwebdevelopment.utterfare.user;

import android.util.Log;

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
public class UpdateUserInformationModel {
    private final String TAG = getClass().getName();

    protected String updateUserInformation(String... args) {
        String results = null;

        String userId = args[0];
        String firstName = args[1];
        String lastName = args[2];
        String email = args[3];
        String telephone = args[4];
        String primaryAddress = args[5];
        String secondaryAddress = args[6];
        String city = args[7];
        String state = args[8];
        String postalCode = args[9];
        String gender = args[10];
        String birthday = args[11];


        try{
            String link = "https://www.utterfare.com/includes/php/Users.php";

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("set_user", "UTF-8");
            data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
            data += "&" + URLEncoder.encode("first_name", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8");
            data += "&" + URLEncoder.encode("last_name", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8");
            data += "&" + URLEncoder.encode("primary_address", "UTF-8") + "=" + URLEncoder.encode(primaryAddress, "UTF-8");
            data += "&" + URLEncoder.encode("secondary_address", "UTF-8") + "=" + URLEncoder.encode(secondaryAddress, "UTF-8");
            data += "&" + URLEncoder.encode("city", "UTF-8") + "=" + URLEncoder.encode(city, "UTF-8");
            data += "&" + URLEncoder.encode("state", "UTF-8") + "=" + URLEncoder.encode(state, "UTF-8");
            data += "&" + URLEncoder.encode("postal_code", "UTF-8") + "=" + URLEncoder.encode(postalCode, "UTF-8");
            data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("telephone_number", "UTF-8") + "=" + URLEncoder.encode(telephone, "UTF-8");
            data += "&" + URLEncoder.encode("birthday", "UTF-8") + "=" + URLEncoder.encode(birthday, "UTF-8");
            data += "&" + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8");

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
