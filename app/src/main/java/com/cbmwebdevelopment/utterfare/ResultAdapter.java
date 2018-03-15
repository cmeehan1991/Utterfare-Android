package com.cbmwebdevelopment.utterfare;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 1/7/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 *
 * Credit: https://www.simplifiedcoding.net/android-feed-example-using-php-mysql-volley/
 */

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    private final String TAG = ResultAdapter.class.getName();
    private Context context;
    List<ResultItems> results;

    public ResultAdapter(List<ResultItems> results, Context context) {
        super();
        this.results = results;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ResultItems resultItems = results.get(position);

        holder.itemView.setOnClickListener((listener)->{
            Intent intent = new Intent(context, SingleItemActivity.class);
            intent.putExtra("itemId", resultItems.getItemId());
            intent.putExtra("dataTable", resultItems.getDataTable());
            context.startActivity(intent);
        });

        holder.itemName.setText(resultItems.getItemName());
        new LoadImages((ImageView) holder.itemView.findViewById(R.id.item_image)).execute(resultItems.getItemImage());
        holder.itemRestaurantName.setText(resultItems.getCompanyName());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, itemDescription,  phone, url, itemRestaurantName;
        public ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image);
            itemRestaurantName = (TextView) itemView.findViewById(R.id.item_restaurant_name);
        }
    }
}
