package com.cbmwebdevelopment.utterfare.passwordreset;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cbmwebdevelopment.utterfare.notifications.CustomAlerts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 5/18/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class RequestPasswordResetActivity extends Fragment {

    private View v;
    private Context mContext;
    private Activity mActivity;
    private EditText emailEditText;
    private Button requestPasswordButton;
    private SharedPreferences mSharedPreferences;

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
        initItems();
    }

    private void initItems(){
        mSharedPreferences = mActivity.getSharedPreferences("", mContext.MODE_PRIVATE);
        emailEditText = (EditText) v.findViewById(R.id.recover_pwd_email_address);
        requestPasswordButton = (Button) v.findViewById(R.id.request_code_button);

        requestPasswordButton.setOnClickListener((l)->{
            requestCode(v);
        });
    }

    private void requestCode(String email){
        try {
            String results = new RequestCodeModel().execute(email).get();
            JSONObject jsonObject = new JSONObject(results);
            if(jsonObject.getBoolean("SUCCESS")){
                goToCodeFragment();
            }else{
                new CustomAlerts().errorAlert("Error", jsonObject.getString("RESPONSE"), mActivity);
            }
        }catch(InterruptedException | ExecutionException | JSONException ex){
            new CustomAlerts().errorAlert("Error", "There was an error generating the reset code. Please try again later.", mActivity);
        }
    }

    private void goToCodeFragment(){
        PasswordResetCodeActivity passwordResetCodeActivity = new PasswordResetCodeActivity();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(android.R.id.tabcontent, passwordResetCodeActivity)
                .commit();
    }

    private void requestCode(View view){
        String email = emailEditText.getText().toString();
        if(email != null && !email.trim().isEmpty() && email.contains("@")){
            requestCode(email);
            mSharedPreferences.edit().putString("USERNAME", email).commit();
        }
    }

}
