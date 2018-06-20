package com.cbmwebdevelopment.utterfare.single;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cbmwebdevelopment.utterfare.images.LoadImages;
import com.cbmwebdevelopment.utterfare.user.UserLoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cbmwebdevelopment.utterfare.R;

import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;
import static android.widget.Toast.LENGTH_SHORT;
import static com.cbmwebdevelopment.utterfare.main.MainActivity.UF_SHARED_PREFERENCES;
import static com.google.android.gms.internal.zzagy.runOnUiThread;

/**
 * Created by Connor Meehan on 1/9/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */

public class SingleItemActivity extends Fragment {
    private static final int REQUEST_PHONE_CALL = 0;
    private final String TAG = "SingleItemActivity";
    private TextView itemNameView, restaurantNameView, itemDescriptionView;
    private Button restPhoneView, restUrlView;
    private ImageView itemImageView;
    private ProgressBar progressBar;
    private FloatingActionButton addItemFab;
    private String itemId, dataTable, itemName, restaurantAddress, restaurantName, restPhone, restUrl, itemDescription, itemImage;
    private View v;
    private Activity mActivity;
    private Context mContext;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.single_item, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeView();
        mActivity = getActivity();
        sharedPreferences = mActivity.getSharedPreferences(UF_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        handleActionItems();

        if (getArguments() != null) {
            parseArguments();
        } else {
            handleEmpty();
        }
    }

    /**
     * Handle any custom action items
     * Items handled: Add Item Floating Action Button
     */
    private void handleActionItems() {

        restaurantNameView.setOnClickListener(l -> {
            openMap(v);
        });

        addItemFab.setOnClickListener((l) -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("LOGGED_IN", false);
            if (isLoggedIn) {
                addItem();
            } else {
                Toast.makeText(mContext, "You must be signed in to save items", Toast.LENGTH_LONG).show();
            }
        });

        restPhoneView.setOnClickListener((l) -> {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + restPhone));

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);

            }else{
                mActivity.startActivity(intent);
            }
        });

        restUrlView.setOnClickListener((l)->{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(restUrl));
            mActivity.startActivity(intent);
        });
    }


    /**
     * Add the item to the users favorites
     * Handle the response, whether positive or negative.
     * Toast is created in other method.
     */
    private void addItem(){
        try {
            SaveItemModel saveItemModel = new SaveItemModel();
            String userId = sharedPreferences.getString("USER_ID", null);
            String results = saveItemModel.execute(userId, itemId, dataTable, itemName, itemImage).get();
            JSONObject jsonObject = new JSONObject(results);

            boolean status = jsonObject.getBoolean("STATUS");
            if(status){
                alertUser("Item saved.");
            }else{
                alertUser("You have already saved this item to your favorites.");
            }
        }catch(ExecutionException | InterruptedException | JSONException ex){
            Log.e(TAG, "Results Error: " + ex.getMessage());
            alertUser("Error: Could not save item. Please try again later.");
        }
    }

    private void alertUser(String message){
        Toast.makeText(mContext, message, LENGTH_SHORT).show();
    }

    private void initializeView(){
        mContext = v.getContext();
        mActivity = getActivity();

        // assign the view objects
        itemNameView = (TextView) v.findViewById(R.id.single_item_name);
        itemImageView = (ImageView) v.findViewById(R.id.single_item_image);
        restaurantNameView = (TextView) v.findViewById(R.id.single_item_restaurant_name);
        restPhoneView = (Button) v.findViewById(R.id.single_item_phone);
        restUrlView = (Button) v.findViewById(R.id.single_item_link);
        itemDescriptionView = (TextView) v.findViewById(R.id.single_item_description);
        progressBar = (ProgressBar) v.findViewById(R.id.singleItemProgressBar);
        addItemFab = (FloatingActionButton) v.findViewById(R.id.addItemFab);


    }

    private void handleEmpty(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("Error");
        dialog.setMessage("There was an error retrieving the item you selected. Please go back and try again.");
        dialog.setPositiveButton("OK", (dialogInterface, i)->{
            mActivity.onBackPressed();
        });
    }

    private void parseArguments(){
        itemId = getArguments().getString("itemId");
        dataTable = getArguments().getString("dataTable");
        if (itemId != null) {
            new LoadSingleItem(this, mContext).execute(itemId, dataTable);
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
            Toast.makeText(mContext, "Error: There was an error fetching the item. Pleast try again.", LENGTH_SHORT).show();
        }
    }

    private void updateView() {
        itemNameView.setText(itemName);
        new LoadImages(itemImageView).execute(itemImage);
        restaurantNameView.setText(restaurantName);
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
