package com.cbmwebdevelopment.utterfare.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.ListFragment;

import com.cbmwebdevelopment.utterfare.home.HomeActivity;
import com.cbmwebdevelopment.utterfare.saved.SavedItems;
import com.cbmwebdevelopment.utterfare.saved.SavedItemsActivity;
import com.cbmwebdevelopment.utterfare.search.Search;
import com.cbmwebdevelopment.utterfare.search.SearchActivity;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 2020-01-05.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private final String TAG = getClass().getName();

    FragmentManager fm;
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        this.fm = fm;
    }


    @Override
    public Fragment getItem(int i) {

        switch(i){
            case 0:
                HomeActivity tab1 = new HomeActivity();
                return tab1;
            case 1:
                SearchActivity tab2 = new SearchActivity();
                return tab2;
            case 2:
                SavedItemsActivity tab3 = new SavedItemsActivity();
                return tab3;
            default: return null;
        }

        //return ArrayListFragment.newInstance(i);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        Log.i(TAG, String.valueOf(position));
        switch (position) {
            case 0:
                title = "Home";
                break;
            case 1:
                title = "Search";
                break;
            case 2:
                title = "Saved Items";
                break;
            default: break;
        }
        return title;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public static class ArrayListFragment extends Fragment {
        private final String TAG = this.getClass().getName();
        int mNum;

        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();

            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View v = null;

            Log.d(TAG, String.valueOf(mNum));
            switch(mNum){
                case 0:
                    v = inflater.inflate(R.layout.activity_home, container, false);
                    break;
                case 1:
                    v = inflater.inflate(R.layout.activity_search, container, false);
                    break;
                case 2:
                    v = inflater.inflate(R.layout.activity_saved, container, false);
                    break;
                default: break;
            }

            return v;

        }
    }
}


