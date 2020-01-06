package com.cbmwebdevelopment.utterfare.passwordreset;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cbmwebdevelopment.utterfare.notifications.CustomAlerts;
import com.cbmwebdevelopment.utterfare.saved.SavedItemsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 5/18/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class ResetPasswordActivity extends Fragment {

    private final String TAG = getClass().getName();
    private Activity mActivity;
    private Context mContext;
    private View view;
    private EditText newPasswordEditText, confirmNewPasswordEditText;
    private Button setNewPasswordButton;
    String password, confirmPassword;
    private SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_password_reset, group, false);

        mContext = getContext();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        newPasswordEditText = (EditText) view.findViewById(R.id.new_password);
        confirmNewPasswordEditText = (EditText) view.findViewById(R.id.confirm_new_password);
        setNewPasswordButton = (Button) view.findViewById(R.id.setPasswordButton);

        mSharedPreferences = mActivity.getSharedPreferences("", mContext.MODE_PRIVATE);

        setActionListeners();
    }

    private void setActionListeners(){
        newPasswordEditText.setOnKeyListener((view, keyCode, event)->{
            password = newPasswordEditText.getText().toString();
            if(password == null || password.length() < 8 || password.trim().isEmpty()){
                newPasswordEditText.setBackground(mContext.getDrawable(R.drawable.error_outline));
            }else{
                newPasswordEditText.setBackground(mContext.getDrawable(R.drawable.success_outline));
            }
            return false;
        });

        confirmNewPasswordEditText.setOnKeyListener((view, keyCode, event)->{
            confirmPassword = confirmNewPasswordEditText.getText().toString();
            if(confirmPassword == null || confirmPassword.length() < 8 || confirmPassword.trim().isEmpty() || !confirmPassword.equals(newPasswordEditText.getText().toString())){
                confirmNewPasswordEditText.setBackground(mContext.getDrawable(R.drawable.error_outline));
            }else{
                confirmNewPasswordEditText.setBackground(mContext.getDrawable(R.drawable.success_outline));
            }
            return false;
        });

        setNewPasswordButton.setOnClickListener((l)->{
            if(password.equals(confirmPassword)){
                resetPassword();
            }else{
                new CustomAlerts().errorAlert("", "The passwords you entered do not match. Please try again.", mActivity);
            }
        });
    }



    private void resetPassword(){
        String userId = mSharedPreferences.getString("USER_ID", null);
        try {
            String results = new ResetPasswordModel().execute(userId, password).get();
            JSONObject jsonObject = new JSONObject(results);
            boolean success = jsonObject.getBoolean("SUCCESS");
            if(success){
                goToProfile();
            }
        }catch(InterruptedException | ExecutionException | JSONException ex){
            Log.e(TAG, "Exception: " + ex.getMessage());
        }
    }

    private void goToProfile(){
        SavedItemsActivity savedItemsActivity = new SavedItemsActivity();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .remove(savedItemsActivity)
                .commit();
    }

}
