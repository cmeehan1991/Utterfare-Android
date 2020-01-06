package com.cbmwebdevelopment.utterfare.main;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import cbmwebdevelopment.utterfare.R;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private final String TAG = this.getClass().getName();
    public static final String UF_SHARED_PREFERENCES = "UF_SHARED_PREFERENCES";
    public SharedPreferences sharedPreferences;
    protected LocationManager locationManager;
    public static String lat, lng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            boolean hasItems = getSupportFragmentManager().getBackStackEntryCount() > 0;
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(hasItems);
        });

    }
    private void initViews(String lat, String lng){

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment pager = new ViewPagerActivity();

        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.add(R.id.switch_fragment, pager, "Pager");
        ft.commit();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
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