package com.cbmwebdevelopment.utterfare.passwordreset;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 5/18/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class PasswordResetCodeActivity extends Fragment {
    private final String TAG = getClass().getName();
    private View v;
    private Context mContext;
    private Activity mActivity;
    private EditText passwordResetCodeEditText;
    private Button submitCodeButton;
    private SharedPreferences mSharedPreferences;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mContext = getContext();
        v = inflater.inflate(R.layout.fragment_password_reset_code, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        initializeView();
    }

    private void initializeView(){
        mSharedPreferences = mActivity.getSharedPreferences("", mContext.MODE_PRIVATE);
        passwordResetCodeEditText = (EditText) v.findViewById(R.id.password_reset_code);
        submitCodeButton = (Button) v.findViewById(R.id.submit_reset_code_button);
        Log.i(TAG, String.valueOf(submitCodeButton));
        // Add click listeners
        setClickListeners();
    }

    private void setClickListeners(){
        submitCodeButton.setOnClickListener((listener)->{
            submitCode(v);
        });
    }

    private void submitCode(View view){
        String code = passwordResetCodeEditText.getText().toString();
        String email = mSharedPreferences.getString("USERNAME", null);
        if(code.length() == 4 && !code.contains("[a-zA-Z]+]")){
            try {
                String results = new VerifyCodeModel().execute(code, email).get();
                JSONObject jsonObject = new JSONObject(results);
                boolean response = jsonObject.getBoolean("RESPONSE");

                if(response){
                    String userId = jsonObject.getString("ID");
                    mSharedPreferences.edit().putString("USER_ID", userId).commit();
                    goToResetFragment();
                }
            }catch(InterruptedException | ExecutionException | JSONException ex){

            }
        }
    }


    private void goToResetFragment(){
        PasswordResetFragment passwordResetFragment = new PasswordResetFragment();
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(android.R.id.tabcontent, passwordResetFragment)
                .commit();
    }

}
