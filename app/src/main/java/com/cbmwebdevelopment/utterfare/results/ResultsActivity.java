package com.cbmwebdevelopment.utterfare.results;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cbmwebdevelopment.utterfare.search.Search;

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
public class ResultsActivity extends Fragment implements RecyclerView.OnScrollChangeListener {
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
    private Parcelable recyclerViewState;
    private View v;
    private Context mContext;
    private Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_results, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        // Initialize the view elements
        initializeView();

        // Assign the passed arguments to local variables
        parseArguments();

        // Set the results recycler
        setRecycler();

    }
    /**
     * Initialize the view elements
     */
    private void initializeView(){

        mContext = v.getContext();
        mActivity =  ((AppCompatActivity)getActivity());
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = (ProgressBar) v.findViewById(R.id.loadMoreProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        resultItemsList = new ArrayList<>();
    }

    /**
     * Parse the arguments passed from the Search class
     */
    private void parseArguments(){
        results = getArguments().getString("results");//intent.getStringExtra("results");
        page =  getArguments().getString("page") == null ? page : Integer.parseInt(getArguments().getString("page"));//intent.getStringExtra("page") == null ? page : Integer.parseInt(intent.getStringExtra("page"));
        offset =  getArguments().getInt("offset", 0);//intent.getIntExtra("offset", 0);
        searchTerms =  getArguments().getString("terms");//intent.getStringExtra("terms");
        location = getArguments().getString("location"); //intent.getStringExtra("location");
        distance =  getArguments().getString("distance");//intent.getStringExtra("distance");
    }

    /**
     * Set up the recycler view and adapter.
     */
    private void setRecycler(){

        // Set the scroll listener on the recycler view
        recyclerView.setOnScrollChangeListener(this);

        // initializing the results adapter
        adapter = new ResultAdapter(resultItemsList, mContext);

        // Set the adapter for the recycler view to our result adapter
        recyclerView.setAdapter(adapter);

        // show the items
        showItems(results);
    }


    /**
     * Parse the returned JSON Array
     *
     * @param res
     * @throws JSONException
     * @throws IOException
     */
    public void showItems(String res) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            try {
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
                        resultItems.setCompanyName(jsonObj.getString("COMPANY"));
                        resultItems.setItemImage(jsonObj.getString("IMAGE_URL"));
                        resultItems.setItemName(jsonObj.getString("NAME"));
                    } catch (JSONException ex) {
                        Log.e(TAG, "JSON Exception: " + ex.getMessage());
                    }

                    resultItemsList.add(resultItems);
                }
            } catch (JSONException ex) {
                Log.e(TAG, "JSON Exception: " + ex.getMessage());
            }
            adapter.notifyDataSetChanged();
            executor.shutdown();
        });

    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 5) {
                Log.i(TAG, "True");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
        if (isLastItemDisplaying(recyclerView)) {
            page++;
            offset += 10;
            try {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                String data = new Search(mContext).execute(searchTerms, location, distance, String.valueOf(offset), String.valueOf(page)).get();

                if (data.trim().isEmpty() || data == null || data.equals("No Results")) {
                    Toast.makeText(mContext, "There are no more items. Try expanding your search.", Toast.LENGTH_SHORT).show();
                } else {
                    showItems(data);
                }
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.setIndeterminate(false);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
