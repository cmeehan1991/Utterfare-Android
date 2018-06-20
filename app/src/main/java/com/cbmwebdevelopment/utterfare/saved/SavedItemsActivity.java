package com.cbmwebdevelopment.utterfare.saved;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cbmwebdevelopment.utterfare.profile.UserProfileActivity;
import com.cbmwebdevelopment.utterfare.results.ResultAdapter;
import com.cbmwebdevelopment.utterfare.user.UserLoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cbmwebdevelopment.utterfare.R;

import static com.cbmwebdevelopment.utterfare.main.MainActivity.UF_SHARED_PREFERENCES;

/**
 * Created by Connor Meehan on 5/5/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class SavedItemsActivity extends Fragment{
    private View v;
    public SharedPreferences sharedPreferences;
    private String TAG = this.getClass().getName();
    private Activity mActivity;
    private Context mContext;
    private RecyclerView savedItemsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ProgressBar progressBar;
    private List<SavedItems> itemsList;
    private boolean isLoggedIn;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(UF_SHARED_PREFERENCES, getContext().MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("LOGGED_IN", false);

        setHasOptionsMenu(isLoggedIn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        v = inflater.inflate(R.layout.activity_saved, container, false);

        mContext = getContext();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        if(!isLoggedIn){
            setHasOptionsMenu(false);
            UserLoginActivity userLoginActivity = new UserLoginActivity();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .replace(android.R.id.tabcontent, userLoginActivity);
            fragmentTransaction.commit();
        }

        initializeView();
    }

    private void initializeView(){
        savedItemsRecyclerView = (RecyclerView) v.findViewById(R.id.savedItemsRecyclerView);
        savedItemsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        savedItemsRecyclerView.setLayoutManager(layoutManager);
        progressBar = (ProgressBar) v.findViewById(R.id.loadMoreProgressBar);

        itemsList = new ArrayList<>();

        setRecycler();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_menu, menu);
        menu.getItem(0).setOnMenuItemClickListener((l)->{
            UserProfileActivity userProfileActivity = new UserProfileActivity();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(android.R.id.tabcontent, userProfileActivity)
                    .addToBackStack("SavedItems");
            fragmentTransaction.commit();
           return true;
        });
    }

    private void setRecycler(){
        adapter = new SavedItemsAdapter(itemsList, mContext);

        savedItemsRecyclerView.setAdapter(adapter);
        getItems();
    }

    private void getItems(){
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        try {
            GetSavedItemsModel getSavedItemsModel = new GetSavedItemsModel();
            String userId = sharedPreferences.getString("USER_ID", null);
            if(userId != null) {
                String results = getSavedItemsModel.execute(userId).get();

                if (results != null) {
                    JSONArray jsonArray = new JSONArray(results);
                    showItems(jsonArray);
                } else {
                    Log.i(TAG, "Activity: " + mActivity);
                    Snackbar.make(mActivity.findViewById(android.R.id.tabcontent), "You don't have any saved items yet.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("SEARCH", (l) -> {
                                FragmentTabHost host = (FragmentTabHost) mActivity.findViewById(android.R.id.tabhost);
                                host.setCurrentTab(0);
                            }).show();
                }
            }
        }catch(ExecutionException | InterruptedException | JSONException ex){
            Log.e(TAG, "Results: " + ex.getMessage());
        }
    }

    private void showItems(JSONArray jsonArrResponse){

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(()->{
            for(int i = 0; i < jsonArrResponse.length(); i++){
                SavedItems items = new SavedItems();
                try {
                    JSONObject jsonObject = jsonArrResponse.getJSONObject(i);
                    items.setItemId(jsonObject.getString("ITEM_ID"));
                    items.setItemName(jsonObject.getString("ITEM_NAME"));
                    items.setDataTable(jsonObject.getString("ITEM_DATA_TABLE"));
                    items.setItemImage(jsonObject.getString("ITEM_IMAGE_URL"));
                    items.setItemName(jsonObject.getString("ITEM_NAME"));
                }catch(JSONException ex){
                    Log.e(TAG, "JSON Exception: " + ex.getMessage());
                }
                itemsList.add(items);
            }
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setProgress(0);
            executor.shutdown();
        });
    }



}
