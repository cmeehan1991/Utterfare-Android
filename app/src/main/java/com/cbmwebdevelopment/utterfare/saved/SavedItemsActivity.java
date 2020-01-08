package com.cbmwebdevelopment.utterfare.saved;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.cbmwebdevelopment.utterfare.main.MainActivity;
import com.cbmwebdevelopment.utterfare.user.UserSignIn;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cbmwebdevelopment.utterfare.R;

import static android.view.View.VISIBLE;

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
    private RecyclerView savedItemsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ProgressBar progressBar;
    private List<SavedItems> itemsList;
    private boolean isLoggedIn;
    private ViewPager vp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = MainActivity.sharedPreferences;
        isLoggedIn = sharedPreferences.getBoolean("LOGGED_IN", false);

        setHasOptionsMenu(isLoggedIn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_saved, container, false);

        vp = (ViewPager) container;
        mContext = getContext();

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && !isLoggedIn) {
            setHasOptionsMenu(false);
            new UserSignIn(vp, mContext, getActivity()).signInDialog();
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        initializeView();
    }

    private void initializeView() {
        savedItemsRecyclerView = (RecyclerView) v.findViewById(R.id.savedItemsRecyclerView);
        savedItemsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        savedItemsRecyclerView.setLayoutManager(layoutManager);
        progressBar = (ProgressBar) v.findViewById(R.id.loadMoreProgressBar);

        itemsList = new ArrayList<>();

        setRecycler();
    }

    private void setRecycler() {
        adapter = new SavedItemsAdapter(itemsList, mContext);

        savedItemsRecyclerView.setAdapter(adapter);
        getItems();
    }

    private void getItems() {
        progressBar.setVisibility(VISIBLE);
        progressBar.bringToFront();
        try {
            GetSavedItemsModel getSavedItemsModel = new GetSavedItemsModel();
            String userId = sharedPreferences.getString("USER_ID", null);
            if (userId != null) {
                String results = getSavedItemsModel.execute(userId).get();

                if (results != null) {
                    JSONArray jsonArray = new JSONArray(results);
                    showItems(jsonArray);
                } else {
                    Snackbar.make(mActivity.findViewById(android.R.id.tabcontent), "You don't have any saved items yet.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("SEARCH", (l) -> {
                                FragmentTabHost host = (FragmentTabHost) mActivity.findViewById(android.R.id.tabhost);
                                host.setCurrentTab(0);
                            }).show();
                }
            }
        } catch (ExecutionException | InterruptedException | JSONException ex) {
            Log.e(TAG, "Results: " + ex.getMessage());
        }
    }

    private void showItems(JSONArray jsonArrResponse) {

        ExecutorService executor = Executors.newCachedThreadPool();

        executor.submit(() -> {

            for (int i = 0; i < jsonArrResponse.length(); i++) {

                SavedItems items;

                try {

                    JSONObject jsonObject = jsonArrResponse.getJSONObject(i);

                    items = new SavedItems(jsonObject.getString("item_id"), jsonObject.getString("vendor_name"), jsonObject.getString("primary_image"), jsonObject.getString("item_name"), jsonObject.getString("item_short_description"));

                    itemsList.add(items);

                } catch (JSONException ex) {

                    Log.e(TAG, "JSON Exception: " + ex.getMessage());

                }
            }

            adapter.notifyDataSetChanged();

            progressBar.setVisibility(View.INVISIBLE);

            progressBar.setProgress(0);

            executor.shutdown();
        });
    }


}
