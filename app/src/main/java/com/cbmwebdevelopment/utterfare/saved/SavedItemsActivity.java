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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cbmwebdevelopment.utterfare.profile.UserProfileActivity;
import com.cbmwebdevelopment.utterfare.user.UserLoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class SavedItemsActivity extends Fragment {
    private View v;
    public SharedPreferences sharedPreferences;
    private String TAG = this.getClass().getName();
    private Activity mActivity;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(UF_SHARED_PREFERENCES, getContext().MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("LOGGED_IN", false);

        if(!isLoggedIn){
            UserLoginActivity userLoginActivity = new UserLoginActivity();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.replace(android.R.id.tabcontent, userLoginActivity);
            fragmentTransaction.commit();
        }

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        v = inflater.inflate(R.layout.activity_saved, container, false);

        mContext = getContext();

       // getFragmentManager().beginTransaction().replace(android.R.id.tabcontent, this).commit();
        getItems();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
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

    private void getItems(){
        try {
            GetSavedItemsModel getSavedItemsModel = new GetSavedItemsModel();
            String results = getSavedItemsModel.execute().get();

            if(results != null){
                JSONArray jsonArray = new JSONArray(results);
                showItems(jsonArray);
            }else{
                Snackbar.make(v.findViewById(android.R.id.content), "You don't have any saved items yet.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("SEARCH", (l)->{
                            FragmentTabHost host = (FragmentTabHost) mActivity.findViewById(android.R.id.tabhost);
                            host.setCurrentTab(0);
                        }).show();
            }
        }catch(ExecutionException | InterruptedException | JSONException ex){
            Log.e(TAG, "Results: " + ex.getMessage());
        }
    }

    private void showItems(JSONArray jsonArray){
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(()->{
            SavedItemsItems items = new SavedItemsItems();
            for(int i = 0; i < jsonArray.length(); i++){
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    items.setItemId(jsonObject.getString("ITEM_ID"));
                    items.setItemName(jsonObject.getString("ITEM_NAME"));
                    items.setDataTable(jsonObject.getString("DATA_TABLE"));
                    items.setItemImage(jsonObject.getString("IMAGE_URL"));
                    items.setItemName(jsonObject.getString("ITEM_NAME"));
                }catch(JSONException ex){
                    Log.e(TAG, "JSON Exception: " + ex.getMessage());
                }
            }
        });
    }



}
