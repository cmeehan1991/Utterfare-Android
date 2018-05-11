package com.cbmwebdevelopment.utterfare.main;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TabHost;

import com.cbmwebdevelopment.utterfare.saved.SavedItemsActivity;
import com.cbmwebdevelopment.utterfare.search.SearchActivity;

import cbmwebdevelopment.utterfare.R;
public class MainActivity extends AppCompatActivity{
    private FragmentTabHost mFragmentTabHost;
    private final String TAG = this.getClass().getName();
    public static final String UF_SHARED_PREFERENCES = "UF_SHARED_PREFERENCES";
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(UF_SHARED_PREFERENCES, MODE_PRIVATE);

        // Initialize the fragment tab host
        mFragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mFragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        // Add the tabs
        mFragmentTabHost.addTab(getTabSpec(mFragmentTabHost, "Search", null, getResources().getDrawable(R.drawable.ic_search_dark)), SearchActivity.class, null);
        mFragmentTabHost.addTab(getTabSpec(mFragmentTabHost, "Favorites", null, getResources().getDrawable(R.drawable.ic_favorites)), SavedItemsActivity.class, null);
        mFragmentTabHost.getTabWidget().setStripEnabled(false);
        mFragmentTabHost.setCurrentTab(0);


        // Listen for additional fragments added to the stack to add
        // up navigation.
        getSupportFragmentManager().addOnBackStackChangedListener(()->{
            boolean hasItems = getSupportFragmentManager().getBackStackEntryCount() > 0;
            Log.i(TAG, String.valueOf(mFragmentTabHost.getCurrentTab()));
            //getActionBar().setDisplayHomeAsUpEnabled(true);
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(hasItems);
        });
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
}