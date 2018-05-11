package com.cbmwebdevelopment.utterfare.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by Connor Meehan on 3/8/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */

public class UserLocation extends AsyncTask<Void, Void, Location> implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = this.getClass().getName();
    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 1;
    private final Context CONTEXT;
    public static Location userLocation;
    private Location lastLocation = null;
    private FusedLocationProviderClient mFusedLocationClient;
    private String latitude, longitude;

    private LocationCallback mLocationCallback;
    private boolean mRequestingLocationUpdates = true, hasPermissions = false, hasLocation = false;
    private LocationRequest mLocationRequest;

    public UserLocation(Context context) {
        this.CONTEXT = context;
    }

    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CONTEXT, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) CONTEXT, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions((Activity) CONTEXT, permissions, LOCATION_PERMISSIONS_REQUEST_CODE);
            }

        }else{
            hasPermissions = true;
        }
    }

    public void startLocationUpdates() {
        checkPermission();
        // Set location callbacks
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                if(locationResult == null){
                    startLocationUpdates();
                    return;
                }
                for(Location location : locationResult.getLocations()){
                    userLocation = location;
                }
                Log.i(TAG, "Callback: " + userLocation);
            };
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    public void cancelLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    public Location getUserLastLocation() {
        checkPermission();
        mFusedLocationClient.getLastLocation().addOnSuccessListener((Activity) CONTEXT, (OnSuccessListener<Location>) location -> {
            if (location != null) {
                hasLocation = true;
                userLocation = location;
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
            }
        });
        return userLocation;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(CONTEXT);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener((Activity) CONTEXT, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but CONTEXT can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult((Activity) CONTEXT,
                                LOCATION_PERMISSIONS_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        Log.e(TAG, "Location task failure listener: " + sendEx.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

    }

    public String getLatitude(){
        return latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    @Override
    protected Location doInBackground(Void... voids) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(CONTEXT);

        // Check to confirm permissions
        checkPermission();
        if(hasPermissions) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener((Activity) CONTEXT, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        hasLocation = true;
                        userLocation = location;
                    } else {
                        startLocationUpdates();
                    }
                }
            });
        }
        createLocationRequest();
        return userLocation;
    }
}
