package com.cbmwebdevelopment.utterfare.saved;

import android.os.AsyncTask;

import com.cbmwebdevelopment.utterfare.main.GlobalVariables;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Connor Meehan on 5/9/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class GetSavedItemsModel extends AsyncTask<String, Void, String> implements GlobalVariables {
    @Override
    protected String doInBackground(String... args) {
        String results = null;
        try{
            URL url = new URL(USER_ITEMS_URL);
        }catch(IOException ex){

        }
        return null;
    }
}
