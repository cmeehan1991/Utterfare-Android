package com.cbmwebdevelopment.utterfare.grid;

import android.annotation.TargetApi;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cbmwebdevelopment.utterfare.grid.GridResultAdapter;
import com.cbmwebdevelopment.utterfare.grid.GridResultItems;
import com.cbmwebdevelopment.utterfare.grid.GridViewSearch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 1/23/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */

@TargetApi(Build.VERSION_CODES.M)
public class GridViewActivity extends AppCompatActivity implements RecyclerView.OnScrollChangeListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ImageView gridItemImage;
    private ProgressBar loadingIndicator;

    private final String TAG = this.getClass().toString();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private GoogleApiClient.ConnectionCallbacks mLocationCallback;
    private boolean mRequestingLocationUpdates = true;
    private Location mLastLocation;
    private String latitude, longitude;
    private int offset = 0;
    private List<GridResultItems> gridResultItemsList;
    private boolean canLoadMore;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        mRecyclerView = (RecyclerView) findViewById(R.id.gridRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        gridItemImage = (ImageView) findViewById(R.id.grid_item_image);
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        // Initialize the result items list
        gridResultItemsList = new ArrayList<>();

        // Scroll change listener
        mRecyclerView.setOnScrollChangeListener(this);

        // Initialize the adapter
        mAdapter = new GridResultAdapter(gridResultItemsList, this);

        // Set the adapter
        mRecyclerView.setAdapter(mAdapter);


        // show the loading indicator if it is not already
        if (loadingIndicator.getVisibility() == View.INVISIBLE) {
            loadingIndicator.setVisibility(View.VISIBLE);
        }
        // Get the user's location then get the grid items
        getUserLocation();
        canLoadMore = true;

        getItems(latitude, longitude, 0);
    }

    /**
     * Get the user's location
     * Will instantiate the GoogleAPIClient if it is not already.
     */
    private void getUserLocation() {
        // Create an instance of GoogleAPIClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    private boolean isLastItemDisplayed(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0 && canLoadMore) {
            int lastVisiblItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisiblItemPosition != RecyclerView.NO_POSITION && lastVisiblItemPosition == recyclerView.getAdapter().getItemCount() - 6) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
        if (isLastItemDisplayed(mRecyclerView)) {
            loadingIndicator.setVisibility(View.VISIBLE);
            loadingIndicator.bringToFront();
            offset += 24;
            getItems(latitude, longitude, offset);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(results -> {
                final Status status = results.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "CONNECTED");
                        mGoogleApiClient.connect();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(this, 0X1);
                        } catch (IntentSender.SendIntentException ex) {
                            Log.i(TAG, "Error: " + ex.getMessage());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate");
                        return;
                    default:
                        return;
                }
            });
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.i(TAG, String.valueOf(mLastLocation));
        if (mLastLocation != null) {
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
            getItems(latitude, longitude, offset);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLastLocation == null) {
            getUserLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void showItems(String res) {
        JSONArray jsonArray = parseResults(res);
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                GridResultItems gridResultItems = new GridResultItems();
                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    gridResultItems.setItemId(jsonObject.getString("ITEM_ID"));
                    gridResultItems.setCompanyId(jsonObject.getString("COMPANY_ID"));
                    gridResultItems.setDataTable(jsonObject.getString("DATA_TABLE"));
                    gridResultItems.setItemImage(jsonObject.getString("IMAGE_URL"));
                } catch (JSONException ex) {
                    Log.e(TAG, ex.getMessage());
                }
                gridResultItemsList.add(gridResultItems);
            }
            Log.i(TAG, "Notify data set changed");
            mAdapter.notifyDataSetChanged();
            loadingIndicator.setVisibility(View.INVISIBLE);
        } else {
            displayNoResults();
            runOnUiThread(() -> {
                Toast.makeText(this, "Nothing else to show", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void displayNoResults() {
        canLoadMore = false;
    }

    private JSONArray parseResults(String res) {
        try {
            return new JSONArray(res);
        } catch (JSONException ex) {
            return null;
        }
    }

    private void getItems(String lat, String lng, int currOffset) {
        String location = lat + ":" + lng;
        new GridViewSearch(this).execute(location, String.valueOf(offset));

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        latitude = String.valueOf(mLastLocation.getLatitude());
        longitude = String.valueOf(mLastLocation.getLongitude());
        getItems(latitude, longitude, offset);
    }
}
