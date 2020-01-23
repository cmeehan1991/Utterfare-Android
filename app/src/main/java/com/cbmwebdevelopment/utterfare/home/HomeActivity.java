package com.cbmwebdevelopment.utterfare.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cbmwebdevelopment.utterfare.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cbmwebdevelopment.utterfare.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Connor Meehan on 2020-01-03.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class HomeActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View v;
    private RecyclerView homeItemsRV;
    private Context context;
    private final String TAG = this.getTag();
    public List<HomeItems> homeItemsList;
    private RecyclerView.Adapter homeItemsAdapter;
    private RecyclerView.LayoutManager homeFeedLayoutManager;
    private ProgressBar homeItemsProgressBar;
    private String lat, lng, fullAddress;
    private HomeItemsController topItemsController;
    private int page = 1;
    private boolean updating, loading;
    private HomeActivity homeActivity;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


        this.lat = MainActivity.lat;
        this.lng = MainActivity.lng;
        this.fullAddress = MainActivity.fullAddress != null  ? MainActivity.fullAddress : "6 Kent Ct., Hilton Head Island SC, 29926";

        if(isVisibleToUser) {
            this.loadItems();
        }
    }

    private void initFeeds(){

        homeItemsAdapter = new HomeItemsAdapter(homeItemsList, context);
        homeItemsRV.setAdapter(homeItemsAdapter);
        homeItemsRV.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                // Check for scroll down
                if( dy > 0){
                    int visibleItemCount = homeFeedLayoutManager.getChildCount();
                    int totalItemCount = homeFeedLayoutManager.getItemCount();

                    if(totalItemCount == visibleItemCount) {
                        // First we are going to check to make sure we are not updating, loading more items, and that there are enough items to warrant
                        // another load
                        if (updating == false && loading == false && homeItemsList.size() % 25 == 0) {
                            loading = true;
                            page += 1;
                            loadItems();
                        }
                    }
                }
            }
        });
    }

    private void loadItems(){
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(()->{
            topItemsController = new HomeItemsController(this);
            topItemsController.execute(this.fullAddress, "25", "getMobileHomeFeedItems", "25", String.valueOf(page));
            executor.shutdown();
        });
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstance) {
        super.onCreate(savedInstance);

        v = layoutInflater.inflate(R.layout.fragment_home, container, false);
        this.homeActivity = this;
        this.context = v.getContext();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.home_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        homeItemsRV = (RecyclerView) v.findViewById(R.id.home_items_rv);

        homeItemsProgressBar = (ProgressBar) v.findViewById(R.id.home_items_progress_bar);

        homeFeedLayoutManager = new LinearLayoutManager(v.getContext(), RecyclerView.VERTICAL, false);

        homeItemsRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        homeItemsList = new ArrayList<>();

        initFeeds();
    }

    public void showHomeItems(String data) {

        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                String itemId = jsonObj.getString("item_id");
                String itemName = jsonObj.getString("item_name");
                String itemImage = jsonObj.getString("primary_image");

                homeItemsList.add(new HomeItems(itemId, itemName, itemImage));
                homeItemsAdapter.notifyDataSetChanged();
                homeItemsProgressBar.setVisibility(GONE);
                homeItemsRV.setVisibility(VISIBLE);

                updating = false;
                loading = false;
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        } catch (JSONException ex) {
            Log.e(TAG, "JSON Exception");
            Log.e(this.getClass().getName(), "JSON Exception" + ex.getMessage());
            Log.e(TAG, "Result");
            Log.e(this.getClass().getName(), data);
        }
    }

    @Override
    public void onRefresh() {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(()->{
            Log.i(TAG, "Refreshing");
            topItemsController = new HomeItemsController(this);
            topItemsController.execute(this.fullAddress, "25", "getMobileHomeFeedItems", "25", String.valueOf(page));
            executor.shutdown();
            homeItemsList.clear();

        });
    }
}
