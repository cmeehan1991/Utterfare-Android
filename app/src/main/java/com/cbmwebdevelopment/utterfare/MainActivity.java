package com.cbmwebdevelopment.utterfare;

import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cbmwebdevelopment.utterfare.R;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = getClass().toString();
    private TextView locationField;
    private Spinner distanceSpinner;
    private InputMethodManager imm;
    private EditText terms, locationInput;
    private ViewSwitcher viewSwitcher;
    private String userLocation, searchTerms, searchDistance;
    public ProgressBar progressBar;
    public LinearLayout searchLayout;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private String latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get all of the UI input fields
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.location_switcher);
        locationField = (TextView) findViewById(R.id.location);
        locationInput = (EditText) findViewById(R.id.location_input);
        distanceSpinner = (Spinner) findViewById(R.id.distance_spinner);
        terms = (EditText) findViewById(R.id.terms);
        searchLayout = (LinearLayout) findViewById(R.id.searchLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Get the user's location
        getUserLocation();

        // Request focus on the location field for now. Eventually this will be replaced with
        // automatically loading the user's location.
        locationInput.requestFocus();
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
        }
    }

    /**
     * Submits the search for results
     *
     * @param view
     */
    public void submitSearch(View view) {
        if (latitude == null || longitude == null) {
            userLocation = parseLocation(locationInput.getText().toString());
        }else{
            userLocation = latitude + ":" + longitude;
        }

        // Hide the keyboard
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        searchTerms = terms.getText().toString();
        searchDistance = parseDistance(distanceSpinner.getSelectedItem().toString());
        progressBar.setVisibility(View.VISIBLE);
        boolean valid = isValid();
        Log.i(TAG, String.valueOf(valid));
        // Validate the inputs.
        // Looking for search terms, distance, and location.
        if (valid) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            new Search(this).execute(searchTerms, userLocation, searchDistance, "0", "1");
        } else {
            locationInput.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            terms.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(this, "Please be sure to input the location and some search terms!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValid() {
        boolean isValid = false;
        Log.i(TAG, userLocation);
        if ((userLocation.length() > 1 && !userLocation.equals("BAD LOCATION")) && searchTerms.length() > 1) {
            isValid = true;
        }
        if (userLocation.equals("BAD LOCATION")) {
            locationInput.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }
        return isValid;
    }

    private String parseDistance(String distance) {
        String parseDistance = null;
        String[] distArr = distance.split(" ");
        parseDistance = distArr[0];
        return parseDistance;
    }

    private String parseLocation(String location) {
        String parseLocation = null;
        if (location.matches("\\d+")) {
            parseLocation = location;
        } else if (location.matches("[a-zA-Z ,.-_]+")) {
            parseLocation = location;
        } else {
            parseLocation = "BAD LOCATION";
        }
        return parseLocation;
    }

    public void ManualLocationInput(View view) {
        viewSwitcher.showNext();
        mGoogleApiClient.disconnect();
        latitude = null;
        longitude = null;
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
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

    /**
     * Handle on connected for location
     *
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Information services turned off");
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(results -> {
                Log.i(TAG, "Results: " + results.getStatus());
                final Status status = results.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        mGoogleApiClient.connect();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, 0X1);
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
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
            Log.i(TAG, "LATITUDE: " + latitude + "\nLONGITUDE: " + longitude);
            setAddress(Double.parseDouble(latitude), Double.parseDouble(longitude));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Sets the address on the front end to the city, state, and zip code
     *
     * @param lat
     * @param lng
     */
    private void setAddress(double lat, double lng) {
        Geocoder mGeocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses = mGeocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                locationField.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + " " + addresses.get(0).getPostalCode());
            }
        } catch (IOException ex) {
            Log.e(TAG, "Error getting address");
            locationField.setText("Error finding location");
        }
    }
}
