package com.cbmwebdevelopment.utterfare;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 1/23/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */

class GridResultAdapter extends RecyclerView.Adapter<GridResultAdapter.ViewHolder> {
    private final String TAG = this.getClass().getName();
    private List<GridResultItems> mGridResultItems;
    private Context context;
    public GridResultAdapter(List<GridResultItems> resultItems, Context context){
        this.mGridResultItems = resultItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GridResultItems gridResultItems = mGridResultItems.get(position);
        holder.itemView.setOnClickListener((listener)->{
            Intent intent = new Intent(context, SingleItemActivity.class);
            intent.putExtra("itemId", gridResultItems.getItemId());
            intent.putExtra("dataTable", gridResultItems.getDataTable());
            context.startActivity(intent);
        });

        // Load the image
        new LoadImages((ImageView) holder.itemView.findViewById(R.id.grid_item_image)).execute(gridResultItems.getItemImage());
    }

    @Override
    public int getItemCount() {
        return mGridResultItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.grid_item_image);
        }
    }

}

