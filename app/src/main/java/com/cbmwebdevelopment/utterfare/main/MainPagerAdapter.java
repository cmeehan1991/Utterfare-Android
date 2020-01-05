package com.cbmwebdevelopment.utterfare.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 2020-01-05.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager fm){
        super(fm);

    }

    @Override
    public Fragment getItem(int i) {


        return ArrayListFragment.newInstance(i);
    }

    @Override
    public int getCount() {
        return 0;
    }

    public static class ArrayListFragment extends ListFragment {
        int mNum;

        static ArrayListFragment newInstance(int num){
            ArrayListFragment f = new ArrayListFragment();

            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){

            View v = inflater.inflate(R.layout.activity_main, container, false);

            return v;

        }
    }
}


