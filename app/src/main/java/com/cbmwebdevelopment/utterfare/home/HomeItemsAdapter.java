package com.cbmwebdevelopment.utterfare.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.cbmwebdevelopment.utterfare.images.LoadImages;
import com.cbmwebdevelopment.utterfare.single.SingleItemActivity;

import java.util.List;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 2020-01-04.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class HomeItemsAdapter extends RecyclerView.Adapter<HomeItemsAdapter.ViewHolder> {

    private Context context;
    private final String TAG = getClass().toString();
    List<HomeItems> listItems;
    CardView cv;
    TextView itemName;
    ImageView itemImage;

    public HomeItemsAdapter(List<HomeItems> homeItems, Context context){
        super();
        this.context = context;
        this.listItems = homeItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_feed_recycler_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        HomeItems homeItems = listItems.get(i);

        viewHolder.itemName.setText(homeItems.getItemName());
        new LoadImages((ImageView) viewHolder.itemView.findViewById(R.id.home_item_photo)).execute(homeItems.getItemImageUrl());

        viewHolder.cardView.setOnClickListener((listener)->{
            Bundle bundle = new Bundle();
            bundle.putString("itemId", homeItems.getItemId());

            Log.i(TAG, homeItems.getItemName());
            Log.i(TAG, homeItems.getItemId());

            goToSingleItemView(bundle);
        });
    }

    private void goToSingleItemView(Bundle bundle){
        SingleItemActivity singleItemActivity = new SingleItemActivity();
        singleItemActivity.setArguments(bundle);

        FragmentActivity fragmentActivity = (FragmentActivity) context;

        FragmentManager fm = fragmentActivity.getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        ft.replace(R.id.switch_fragment, singleItemActivity);

        ft.addToBackStack("Home");
        ft.commit();


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public ImageView itemImage;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cv);
            itemName = (TextView) itemView.findViewById(R.id.home_item_title);
            itemImage = (ImageView) itemView.findViewById(R.id.home_item_photo);
        }
    }

}

