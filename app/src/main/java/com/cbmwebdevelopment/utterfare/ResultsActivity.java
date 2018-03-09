package com.cbmwebdevelopment.utterfare;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 1/5/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class ResultsActivity extends AppCompatActivity implements RecyclerView.OnScrollChangeListener {
    public final String TAG = "ResultsActivity.class";
    public boolean loading = true;
    public int offset, page;
    public String searchTerms, location, distance;
    public String results;
    private List<ResultItems> resultItemsList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    public static ProgressBar progressBar;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initializing the views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = (ProgressBar) findViewById(R.id.loadMoreProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        // Initialize the results list
        resultItemsList = new ArrayList<>();

        // Get the intent from the Search activity
        Intent intent = getIntent();
        results = intent.getStringExtra("results");
        page = intent.getStringExtra("page") == null ? page : Integer.parseInt(intent.getStringExtra("page"));
        offset = intent.getIntExtra("offset", 0);
        searchTerms = intent.getStringExtra("terms");
        location = intent.getStringExtra("location");
        distance = intent.getStringExtra("distance");

        // Adding a scroll change listener to recyclerview
        recyclerView.setOnScrollChangeListener(this);

        // initializing our adapter
        adapter = new ResultAdapter(resultItemsList, this);

        recyclerView.setAdapter(adapter);
        try {
            showItems(results);
        } catch (JSONException ex) {
            Log.e(TAG, "Show items exception: " + ex.getMessage());
        }
    }

    /**
     * Parse the returned JSON Array
     *
     * @param res
     * @throws JSONException
     * @throws IOException
     */
    public void showItems(String res) throws JSONException {

        // Get the result and convert it into a JSON Array
        JSONArray jsonArray = new JSONArray(res);

        // Each JSON element is an array of values returned from the server.
        // We are going to loop through each of them and assign the individual value to it's
        // respective variable.
        for (int i = 0; i < jsonArray.length(); i++) {
            ResultItems resultItems = new ResultItems();
            JSONObject jsonObj = null;
            try {
                jsonObj = jsonArray.getJSONObject(i); // Convert each array to an JSON object
                resultItems.setItemId(jsonObj.getString("ITEM_ID"));
                resultItems.setDataTable(jsonObj.getString("DATA_TABLE"));
                resultItems.setCompanyId(jsonObj.getString("COMPANY_ID"));
                resultItems.setItemId(jsonObj.getString("ITEM_ID"));
                resultItems.setItemImage(jsonObj.getString("IMAGE_URL"));
                resultItems.setItemName(jsonObj.getString("NAME"));
                resultItems.setItemDescription(jsonObj.getString("DESCRIPTION"));
                resultItems.setPhone(jsonObj.getString("PHONE"));
                resultItems.setLink(jsonObj.getString("LINK"));
                resultItems.setAddress(jsonObj.getString("ADDRESS") + ", " + jsonObj.getString("CITY") + ", " + jsonObj.getString("STATE"));
            } catch (JSONException ex) {
                Log.e(TAG, "JSON Exception: " + ex.getMessage());
            }
            ExecutorService executor = Executors.newCachedThreadPool();
            executor.submit(() -> {
                resultItemsList.add(resultItems);
                executor.shutdown();
            });
        }
        Log.i(TAG, "Notify data set changed");
        adapter.notifyDataSetChanged();
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
        if (isLastItemDisplaying(recyclerView)) {
            page++;
            offset += 25;
            try {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                String data = new Search(this).execute(searchTerms, location, distance, String.valueOf(offset), String.valueOf(page)).get();
                Log.i(TAG, "Data: " + data);
                if (data.trim().isEmpty() || data == null || data.equals("No Results")) {
                    Toast.makeText(this, "There are no more items. Try expanding your search.", Toast.LENGTH_SHORT).show();
                } else {
                    showItems(data);
                }
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.setIndeterminate(false);
            } catch (InterruptedException | JSONException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
