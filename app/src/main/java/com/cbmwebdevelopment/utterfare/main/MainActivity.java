package com.cbmwebdevelopment.utterfare.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import com.cbmwebdevelopment.utterfare.home.HomeActivity;
import com.cbmwebdevelopment.utterfare.saved.SavedItemsActivity;
import com.cbmwebdevelopment.utterfare.search.SearchActivity;

import cbmwebdevelopment.utterfare.R;


public class MainActivity extends AppCompatActivity implements LocationListener {
    private FragmentTabHost mFragmentTabHost;
    private final String TAG = this.getClass().getName();
    public static final String UF_SHARED_PREFERENCES = "UF_SHARED_PREFERENCES";
    public SharedPreferences sharedPreferences;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    public static String lat, lng;
    public FloatingActionButton homeButton, searchButton, savedButton, profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(UF_SHARED_PREFERENCES, MODE_PRIVATE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);



        // Listen for additional fragments added to the stack to add
        // up navigation.
        getSupportFragmentManager().addOnBackStackChangedListener(()->{
            boolean hasItems = getSupportFragmentManager().getBackStackEntryCount() > 0;
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(hasItems);
        });

    }

    private void initViews(String lat, String lng){

        // Initialize the fragment tab host
        mFragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mFragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        // Add the tabs
        Bundle bundle = new Bundle();
        bundle.putString("lat", lat);
        bundle.putString("lng", lng);


        mFragmentTabHost.addTab(getTabSpec(mFragmentTabHost,"Home", null, getResources().getDrawable(R.drawable.ic_home)), HomeActivity.class, bundle);
        mFragmentTabHost.addTab(getTabSpec(mFragmentTabHost, "Search", null, getResources().getDrawable(R.drawable.ic_search_dark)), SearchActivity.class, bundle);
        mFragmentTabHost.addTab(getTabSpec(mFragmentTabHost, "Favorites", null, getResources().getDrawable(R.drawable.ic_favorites)), SavedItemsActivity.class, null);
        mFragmentTabHost.getTabWidget().setStripEnabled(false);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mFragmentTabHost = null;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private TabHost.TabSpec getTabSpec(TabHost tabHost, String tabTitle, String tabIndicator, Drawable drawable){
        return tabHost.newTabSpec(tabTitle).setIndicator(tabIndicator,drawable);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.lat = String.valueOf(location.getLatitude());
        this.lng = String.valueOf(location.getLongitude());

        locationManager.removeUpdates(this);
        initViews(this.lat, this.lng);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "Changed");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "Enabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "disabled");
    }
}