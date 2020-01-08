package com.cbmwebdevelopment.utterfare.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cbmwebdevelopment.utterfare.main.MainActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import cbmwebdevelopment.utterfare.R;

public class SearchActivity extends Fragment {
        private static final int ACCESS_LOCATION_PERMISSION = 1;
    private Spinner distanceSpinner;
    private InputMethodManager imm;
    private Button locationButton;
    private EditText terms;
    public ProgressBar progressBar;
    public LinearLayout searchLayout;
    public Button submitSearchButton;
    private final String TAG = getClass().toString();
    private String location, searchTerms, searchDistance;
    private String latitude, longitude;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private boolean mRequestingLocationUpdates;
    private boolean hasPermission;
    private LocationRequest mLocationRequest;
    private Context mContext;
    private Activity mActivity;
    private View v;
    private GoogleApiClient mGoogleApiClient;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    /**
     * Create the view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = inflater.inflate(R.layout.fragment_search, container, false);
        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){
            locationButton.setText(MainActivity.fullAddress);
        }

    }


    @Override
    public void onActivityCreated(Bundle savedBundleInstance) {
        super.onActivityCreated(savedBundleInstance);

        // Get instance of activity
        mActivity = getActivity();

        // Initialize the inputs
        initializeInputs();

        // Create the input listeners
        createInputListeners();


    }

    /**
     * This is where action listeners are created.
     *
     */
    private void createInputListeners(){
        // Handle manual location input
        locationButton.setOnClickListener((View view) -> {
            manualLocationInput(v);
        });

        // Submit the search
        submitSearchButton.setOnClickListener((View view) -> {
            submitSearch(v);
        });
    }

    /**
     * Creating a location request with an interval of 5 seconds.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
        //loadingIndicator = (ImageView) v.findViewById(R.id.loading_indicator);
        locationButton = (Button) v.findViewById(R.id.location);
        submitSearchButton = (Button) v.findViewById(R.id.submitSearchButton);

    }


    /**
     * Submits the search for results
     *
     * @param view
     */
    public void submitSearch(View view) {
        location = locationButton.getText().toString();
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
            progressBar.setVisibility(View.INVISIBLE);
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



    public void manualLocationInput(View view) {
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

}