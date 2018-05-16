package com.cbmwebdevelopment.utterfare.search;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cbmwebdevelopment.utterfare.R;

public class SearchActivity extends Fragment {
    private static final int ACCESS_LOCATION_PERMISSION = 1;
    private final int ACCESS_LOCATION = 1;
    // Elements
    private Spinner distanceSpinner;
    private InputMethodManager imm;
    private Button locationButton;
    private EditText terms;
    private ViewSwitcher viewSwitcher;
    public ProgressBar progressBar;
    public LinearLayout searchLayout;
    public BottomNavigationView mBottomNavigationView;
    public Menu bottomNavigationMenu;
    public MenuItem searchActionMenuItem, feedActionMenuItem;
    public Button submitSearchButton;

    // Variables
    private final String TAG = getClass().toString();
    private String location, searchTerms, searchDistance;
    private String latitude, longitude;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mRequestingLocationUpdates = true;
    private LocationCallback mLocationCallback;
    private boolean hasPermission;
    private LocationRequest mLocationRequest;
    private Context mContext;
    private Activity mActivity;
    private View v;
    private ImageView loadingIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = inflater.inflate(R.layout.activity_search, container, false);

        getFragmentManager().beginTransaction().replace(android.R.id.tabcontent, this).commit();

        mContext = v.getContext();
        mActivity = getActivity();

        // Assign and initialize all of the UI input fields
        initializeInputs();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        getLastLocation();

        locationButton.setOnClickListener((View view)->{
            manualLocationInput(v);
        });

        submitSearchButton.setOnClickListener((View view)->{
            submitSearch(v);
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedBundleInstance){
        super.onActivityCreated(savedBundleInstance);

        mActivity = getActivity();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void setLocationCallback() {

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                    setAddress(location.getLatitude(), location.getLongitude());
                }
            }
        };
    }

    private void permissionsCheck() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(mActivity, permissions, ACCESS_LOCATION_PERMISSION);
            }

        } else {
            hasPermission = true;
        }
        if (hasPermission) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(mActivity, (OnSuccessListener<Location>) location -> {
                mRequestingLocationUpdates = true;
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                setAddress(location.getLatitude(), location.getLongitude());
            });
        }
        createLocationRequest();
        setLocationCallback();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true;
                    getLastLocation();
                } else {
                    hasPermission = false;
                }
            }
            return;
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        latitude = null;
        longitude = null;
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(mActivity, permissions, ACCESS_LOCATION_PERMISSION);
            }

        } else {
            hasPermission = true;
        }
        if (hasPermission) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    /**
     * Initiaize the views
     */
    private void initializeInputs() {
        imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        distanceSpinner = (Spinner) v.findViewById(R.id.distance_spinner);
        terms = (EditText) v.findViewById(R.id.terms);
        searchLayout = (LinearLayout) v.findViewById(R.id.searchLayout);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        loadingIndicator = (ImageView) v.findViewById(R.id.loading_indicator);
        locationButton = (Button) v.findViewById(R.id.location);
        submitSearchButton = (Button) v.findViewById(R.id.submitSearchButton);

    }


    /**
     * Submits the search for results
     *
     * @param view
     */
    public void submitSearch(View view) {
        if (latitude == null || longitude == null) {
            location = parseLocation(locationButton.getText().toString());
        } else {
            location = String.valueOf(latitude) + ":" + String.valueOf(longitude);
        }

        // Hide the keyboard
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        searchTerms = terms.getText().toString();
        searchDistance = parseDistance(distanceSpinner.getSelectedItem().toString());
        progressBar.setVisibility(View.VISIBLE);

        // Validate the inputs.
        // Looking for search terms, distance, and location.
        if (isValid()) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            new Search(mContext).execute(searchTerms, location, searchDistance, "0", "1");
        } else {
            terms.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(mContext, "Please be sure to input the location and some search terms!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValid() {
        boolean isValid = false;
        if ((location.length() > 1 && !location.equals("BAD LOCATION")) && searchTerms.length() > 1) {
            isValid = true;
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

    public void manualLocationInput(View view) {
        stopLocationUpdates();
        AlertDialog.Builder locationDialog = new AlertDialog.Builder(mContext);
        locationDialog.setTitle("Set Location");
        locationDialog.setMessage("Set your location");
        final EditText input = new EditText(mContext);
        input.setText(locationButton.getText().toString());

        locationDialog.setView(input);
        locationDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                location = input.getText().toString();
                locationButton.setText(location);
                dialogInterface.dismiss();
                latitude = null;
                longitude = null;
            }
        });
        locationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        locationDialog.show();


    }

    /**
     * Sets the address on the front end to the city, state, and zip code
     *
     * @param lat
     * @param lng
     */
    private void setAddress(double lat, double lng) {
        Geocoder mGeocoder = new Geocoder(mContext, Locale.ENGLISH);
        try {
            List<Address> addresses = mGeocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                locationButton.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + " " + addresses.get(0).getPostalCode());
            }
        } catch (IOException ex) {
            Log.e(TAG, "Error getting address");
            locationButton.setText("Set your location");
        }
    }
}