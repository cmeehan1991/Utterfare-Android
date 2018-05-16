package com.cbmwebdevelopment.utterfare.saved;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cbmwebdevelopment.utterfare.images.LoadImages;
import com.cbmwebdevelopment.utterfare.results.ResultItems;
import com.cbmwebdevelopment.utterfare.single.SingleItemActivity;

import java.util.List;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 1/7/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 *
 * Credit: https://www.simplifiedcoding.net/android-feed-example-using-php-mysql-volley/
 */

public class SavedItemsAdapter extends RecyclerView.Adapter<SavedItemsAdapter.ViewHolder> {
    private final String TAG = SavedItemsAdapter.class.getName();
    private Context context;
    List<SavedItems> results;

    public SavedItemsAdapter(List<SavedItems> results, Context context) {
        super();
        this.results = results;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_items_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SavedItems savedItems = results.get(position);

        holder.itemView.setOnClickListener((listener)->{
            Bundle bundle = new Bundle();
            bundle.putString("itemId", savedItems.getItemId());
            bundle.putString("dataTable", savedItems.getDataTable());

            goToSingleItemView(bundle);

        });

        holder.savedItemName.setText(savedItems.getItemName());
        new LoadImages((ImageView) holder.itemView.findViewById(R.id.saved_item_image)).execute(savedItems.getItemImage());
        holder.itemRestaurantName.setText(savedItems.getCompanyName());
    }

    private void goToSingleItemView(Bundle bundle){
        SingleItemActivity singleItemActivity = new SingleItemActivity();

        singleItemActivity.setArguments(bundle);
        Log.i(TAG, bundle.toString());
        FragmentActivity activity = (FragmentActivity) context;

        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        fragmentTransaction.replace(android.R.id.tabcontent, singleItemActivity);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    @Override
    public int getItemCount() {
        return results.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView savedItemName, itemDescription,  phone, url, itemRestaurantName;
        public ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            savedItemName = (TextView) itemView.findViewById(R.id.saved_item_name);
            itemImage = (ImageView) itemView.findViewById(R.id.saved_item_image);
            itemRestaurantName = (TextView) itemView.findViewById(R.id.saved_item_restaurant_name);
        }
    }
}
