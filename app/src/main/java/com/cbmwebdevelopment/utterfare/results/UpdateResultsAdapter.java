package com.cbmwebdevelopment.utterfare.results;

import android.os.AsyncTask;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Connor Meehan on 6/4/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class UpdateResultsAdapter extends AsyncTask<String, Void, Boolean> {

    private final String TAG = getClass().getName();
    private RecyclerView.Adapter recyclerViewAdapter;
    private List<ResultItems> resultItems;

    public UpdateResultsAdapter(List<ResultItems> resultItems, RecyclerView.Adapter recyclerViewAdapter){
        this.resultItems = resultItems;
        this.recyclerViewAdapter = recyclerViewAdapter;
    }

    @Override
    protected Boolean doInBackground(String... results) {
        Boolean success = false;
        List<ResultItems> resultItemsList = resultItems;
        try {
            // Get the result and convert it into a JSON Array
            JSONArray jsonArray = new JSONArray(results[0]);

            // Each JSON element is an array of values returned from the server.
            // We are going to loop through each of them and assign the individual value to it's
            // respective variable.
            for (int i = 0; i < jsonArray.length(); i++) {
                ResultItems resultItems = new ResultItems();
                JSONObject jsonObj = null;
                try {
                    jsonObj = jsonArray.getJSONObject(i); // Convert each array to an JSON object
                    resultItems.setItemId(jsonObj.getString("item_id"));
                    resultItems.setCompanyId(jsonObj.getString("vendor_id"));
                    resultItems.setCompanyName(jsonObj.getString("vendor_name"));
                    resultItems.setItemImage(jsonObj.getString("primary_image"));
                    resultItems.setItemName(jsonObj.getString("item_name"));
                    resultItems.setItemShortDescription(jsonObj.getString("item_short_description"));
                } catch (JSONException ex) {
                    Log.e(TAG, "JSON Exception: " + ex.getMessage());
                }

                resultItemsList.add(resultItems);
            }
            success = true;
        } catch (JSONException ex) {
            Log.e(TAG, "JSON Exception: " + ex.getMessage());
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean success){
        if(success){
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
