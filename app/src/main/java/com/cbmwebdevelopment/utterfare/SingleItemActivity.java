package com.cbmwebdevelopment.utterfare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 1/9/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */

public class SingleItemActivity extends AppCompatActivity {
    private final String TAG = "SingleItemActivity";
    private TextView itemNameView, restaurantNameView, restPhoneView, restUrlView, itemDescriptionView;
    private ImageView itemImageView;
    private ProgressBar progressBar;
    private String itemName, restaurantAddress, restaurantName, restPhone, restUrl, itemDescription, itemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_item);

        // assign the view objects
        itemNameView = (TextView) findViewById(R.id.single_item_name);
        itemImageView = (ImageView) findViewById(R.id.single_item_image);
        restaurantNameView = (TextView) findViewById(R.id.single_item_restaurant_name);
        restPhoneView = (TextView) findViewById(R.id.single_item_phone);
        restUrlView = (TextView) findViewById(R.id.single_item_link);
        itemDescriptionView = (TextView) findViewById(R.id.single_item_description);
        progressBar = (ProgressBar) findViewById(R.id.singleItemProgressBar);

        // Get the intent
        Intent intent = getIntent();
        String itemId = intent.getStringExtra("itemId");
        String dataTable = intent.getStringExtra("dataTable");

        if (itemId != null) {
            new LoadSingleItem(this).execute(itemId, dataTable);
        }
    }

    public void parseItem(String res) {
        if (!res.equals("No Results")) {
            ExecutorService executor = Executors.newCachedThreadPool();
            executor.submit(() -> {
                try {
                    JSONArray jsonArray = new JSONArray(res);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    itemName = jsonObject.getString("ITEM_NAME");
                    restaurantAddress = jsonObject.getString("ADDRESS");
                    restaurantName = jsonObject.getString("COMPANY_NAME");
                    restPhone = jsonObject.getString("TEL");
                    restUrl = jsonObject.getString("URL");
                    itemDescription = jsonObject.getString("DESCRIPTION");
                    itemImage = jsonObject.getString("IMAGE_URL");
                    runOnUiThread(() -> {
                        updateView();
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                executor.shutdown();
            });
        } else {
            Toast.makeText(this, "Error: There was an error fetching the item. Pleast try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateView() {
        itemNameView.setText(itemName);
        new LoadImages(itemImageView).execute(itemImage);
        restaurantNameView.setText(restaurantName);
        restPhoneView.setText(restPhone);
        restUrlView.setText(restUrl);
        itemDescriptionView.setText(itemDescription);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void openMap(View view) {
        String mapUri = "https://maps.google.com/maps?q=loc:" + restaurantAddress;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

}
