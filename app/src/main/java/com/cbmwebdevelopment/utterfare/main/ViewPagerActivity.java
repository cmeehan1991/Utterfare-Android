package com.cbmwebdevelopment.utterfare.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import cbmwebdevelopment.utterfare.R;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by Connor Meehan on 2020-01-05.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class ViewPagerActivity extends Fragment {

    MainPagerAdapter mainPagerAdapter;
    ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState){
        return layoutInflater.inflate(R.layout.activity_viewpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        mainPagerAdapter = new MainPagerAdapter(getChildFragmentManager());

        viewPager = view.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(mainPagerAdapter);



        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }
}
