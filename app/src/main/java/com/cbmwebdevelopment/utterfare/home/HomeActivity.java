package com.cbmwebdevelopment.utterfare.home;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbmwebdevelopment.utterfare.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cbmwebdevelopment.utterfare.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Connor Meehan on 2020-01-03.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class HomeActivity extends Fragment {

    private View v;
    private RecyclerView topPicksRV, localPicksRV, personalizedPicksRV;
    private Context context;
    private final String TAG = this.getTag();
    public List<HomeItems> topPicksList, localPicksList, personalizedPicksList;
    private RecyclerView.Adapter topItemsAdapter, localItemsAdapter, personalizedItemsAdapter;
    private RecyclerView.LayoutManager topPicksLayoutManager, localPicksLayoutManager, personalizedPicksLayoutManager;
    private ProgressBar homeProgressBar;
    private LinearLayout homeContentLayout;
    private ViewGroup container;
    private String lat, lng;
    private HomeItemsController topItemsController, localItemsController, personalizedItemsController;
    AsyncTask<String, String, String> topItemsTask, localItemsTask, personalizedItemsTask;


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstance){
        super.onCreate(savedInstance);

        v = layoutInflater.inflate(R.layout.activity_home, container, false);

        this.lat = MainActivity.lat;
        this.lng = MainActivity.lng;

        topItemsController = new HomeItemsController(this);
        localItemsController = new HomeItemsController(this);
        personalizedItemsController = new HomeItemsController(this);

        topItemsTask = topItemsController.execute("", "10", "get_top_items");
        localItemsTask = localItemsController.execute("320 Burlington Ave, Gibsonville, NC 27249", "25", "get_local_items");
        personalizedItemsTask = personalizedItemsController.execute("320 Burlington Ave, Gibsonville, NC 27249", "25", "get_recommendations");

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        topPicksRV = (RecyclerView) v.findViewById(R.id.top_picks_rv);
        localPicksRV = (RecyclerView) v.findViewById(R.id.local_picks_rv);
        personalizedPicksRV = (RecyclerView) v.findViewById(R.id.personalized_picks_rv);

        homeContentLayout = (LinearLayout)v.findViewById(R.id.home_content_layout);
        homeProgressBar = (ProgressBar) v.findViewById(R.id.home_progress_bar);


        topPicksLayoutManager = new LinearLayoutManager(v.getContext(), RecyclerView.HORIZONTAL, false);
        localPicksLayoutManager = new LinearLayoutManager(v.getContext(), RecyclerView.HORIZONTAL, false);
        personalizedPicksLayoutManager = new LinearLayoutManager(v.getContext(), RecyclerView.HORIZONTAL, false);

        topPicksRV.setLayoutManager(topPicksLayoutManager);
        localPicksRV.setLayoutManager(localPicksLayoutManager);
        personalizedPicksRV.setLayoutManager(personalizedPicksLayoutManager);

        context = v.getContext();

        topPicksList = new ArrayList<>();
        localPicksList = new ArrayList<>();
        personalizedPicksList = new ArrayList<>();

        topItemsAdapter = new HomeItemsAdapter(topPicksList, context);
        localItemsAdapter = new HomeItemsAdapter(localPicksList, context);
        personalizedItemsAdapter = new HomeItemsAdapter(personalizedPicksList, context);

        topPicksRV.setAdapter(topItemsAdapter);
        localPicksRV.setAdapter(localItemsAdapter);
        personalizedPicksRV.setAdapter(personalizedItemsAdapter);

    }

    public void showHomeItems(String data, String section){

        try {

            JSONArray jsonArray = new JSONArray(data);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                String itemId = jsonObj.getString("item_id");
                String itemName = jsonObj.getString("item_name");
                String itemImage = jsonObj.getString("primary_image");
                switch(section){
                    case "get_top_items":
                        topPicksList.add(new HomeItems(itemId, itemName, itemImage));
                        break;
                    case "get_local_items":
                        localPicksList.add(new HomeItems(itemId, itemName, itemImage));
                        break;
                    case "get_recommendations":
                        personalizedPicksList.add(new HomeItems(itemId, itemName, itemImage));
                        break;
                    default:break;
                }
            }
            switch(section){
                case "get_top_items":
                    topItemsAdapter.notifyDataSetChanged();
                    break;
                case "get_local_items":
                    localItemsAdapter.notifyDataSetChanged();
                    break;
                case "get_recommendations":
                    personalizedItemsAdapter.notifyDataSetChanged();
                    homeProgressBar.setVisibility(GONE);
                    homeContentLayout.setVisibility(VISIBLE);
                    break;
                default:break;
            }
        }catch(JSONException ex){
            Log.e(TAG, "JSON Exception");
            Log.e(this.getClass().getName(), "JSON Exception" + ex.getMessage());
            Log.e(TAG, "Result");
            Log.e(this.getClass().getName(), data);
        }
    }
}
