package com.cbmwebdevelopment.utterfare.passwordreset;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 5/18/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class ResetPasswordActivity extends Fragment {

    private View v;
    private Context mContext;
    private Activity mActivity;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mContext = getContext();
        v = inflater.inflate(R.layout.fragment_password_reset_request, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

}
