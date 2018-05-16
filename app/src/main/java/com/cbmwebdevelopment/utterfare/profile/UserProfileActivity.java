package com.cbmwebdevelopment.utterfare.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cbmwebdevelopment.utterfare.user.UserLoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import cbmwebdevelopment.utterfare.R;

import static com.cbmwebdevelopment.utterfare.main.MainActivity.UF_SHARED_PREFERENCES;

/**
 * Created by Connor Meehan on 5/9/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class UserProfileActivity extends Fragment {
    private final String TAG = getClass().getName();
    private View v;
    private Activity mActivity;
    private Context mContext;
    private EditText firstNameEditText, lastNameEditText, emailAddressEditText, cityEditText, stateEditText;
    private TextView signOutTextView, changePasswordTextView, deleteAccountTextView;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Has options menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState ){
        // Inflate the view
        v = inflater.inflate(R.layout.activity_user_information, container, false);

        // Get the context
        mContext = getContext();


        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        mSharedPreferences = mActivity.getSharedPreferences(UF_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        instantiateInputs();

        setClickListeners();

        // Get and display the user information
        getUserInformation();
    }


    /**
     * Get the user's information based on the saved user ID in the shared preferences.
     * Parsing the information will take place in another method.
     */
    private void getUserInformation(){
        GetUserInformationModel getUserInformationModel = new GetUserInformationModel();
        String userId = mSharedPreferences.getString("USER_ID", null);
        try {
            String results = getUserInformationModel.execute(userId).get();
            parseUserData(results);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            Toast.makeText(mContext, "There was an error retrieving your information. Please try again.", Toast.LENGTH_SHORT).show();
           Log.e(TAG, "Error getting information: " + e.getMessage());
        }
    }

    /**
     * Parse the user information.
     * Notify the user if nothing was returned, otherwise fill in the input objects.
     * @param results
     * @throws JSONException
     */
    private void parseUserData(String results) throws JSONException {
        JSONObject jsonObject = new JSONObject(results);
        boolean success = jsonObject.getBoolean("SUCCESS");
        if(success){
            Log.i(TAG, jsonObject.toString());
            firstNameEditText.setText(jsonObject.getString("FIRST_NAME"));
            lastNameEditText.setText(jsonObject.getString("LAST_NAME"));
            cityEditText.setText(jsonObject.getString("CITY"));
            stateEditText.setText(jsonObject.getString("STATE"));
            emailAddressEditText.setText(jsonObject.getString("EMAIL"));
        }else{
            String response = jsonObject.getString("RESPONSE");
            Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Create the custom options menu
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_information_menu, menu);
        menu.getItem(0).setOnMenuItemClickListener((l)->{
            if(updateUserInformation()){
                Toast.makeText(mContext, "Your information was successfully updated", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(mContext, "There was an error updating your information. Please try again.", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    /**
     * Update the user's information
     * Get the values from the inputs and pass to the update model.
     * Return a booelan value based on the response.
     * @return
     */
    private boolean updateUserInformation(){
        String userId = mSharedPreferences.getString("USER_ID", null);
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String state = stateEditText.getText().toString();
        String email = emailAddressEditText.getText().toString();

        UpdateUserInformationModel updateUserInformationModel = new UpdateUserInformationModel();
        boolean updated = false;
        try {
            String results = updateUserInformationModel.execute(userId, firstName, lastName, city, state, email).get();
            JSONObject jsonObject = new JSONObject(results);
            updated = jsonObject.getBoolean("SUCCESS");
        }catch(InterruptedException | JSONException | ExecutionException ex){
            Log.e(TAG, "Error retrieving data: " + ex.getMessage());
        }

        return updated;
    }

    /**
     * Instantiate the input fields
     */
    private void instantiateInputs(){
        firstNameEditText = (EditText) mActivity.findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) mActivity.findViewById(R.id.lastNameEditText);
        emailAddressEditText = (EditText) mActivity.findViewById(R.id.emailAddressEditText);
        cityEditText = (EditText) mActivity.findViewById(R.id.cityEditText);
        stateEditText = (EditText) mActivity.findViewById(R.id.stateEditText);

        signOutTextView = (TextView) mActivity.findViewById(R.id.signOutTextView);
        changePasswordTextView = (TextView) mActivity.findViewById(R.id.changePasswordTextView);
        deleteAccountTextView = (TextView) mActivity.findViewById(R.id.deleteAccountTextView);
    }

    /**
     * Create the click listeners for the buttons and other clickable objects
     */
    private void setClickListeners(){
        signOutTextView.setOnClickListener((l)->{
            goToUserLogin();
        });

        changePasswordTextView.setOnClickListener((l) ->{
            UpdatePasswordActivity updatePasswordActivity = new UpdatePasswordActivity();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(android.R.id.tabcontent, updatePasswordActivity)
                    .addToBackStack(null);
            fragmentTransaction.commit();

        });

        deleteAccountTextView.setOnClickListener((l)->{
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This will remove all of your saved items and cannot be recovered.")
                    .setPositiveButton("Delete Account", (dialog, listener)->{
                        String userId = mSharedPreferences.getString("USER_ID", null);
                        try {
                            DeleteUserAccountModel deleteUserAccountModel = new DeleteUserAccountModel();
                            String res = deleteUserAccountModel.execute(userId).get();
                            JSONObject jsonObject = new JSONObject(res);
                            String response = jsonObject.getString("RESPONSE");
                            boolean status = jsonObject.getBoolean("STATUS");
                            if(status){
                                goToUserLogin();
                            }else{
                                Toast.makeText(mContext, "Failed to delete account. Please try again.", Toast.LENGTH_LONG).show();
                            }
                        }catch(InterruptedException | ExecutionException | JSONException ex){
                            Log.e(TAG, "IO Exception: " + ex.getMessage());
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, listener)->{});
            AlertDialog dialog = builder.create();
            dialog.setOnShowListener((listener)->{
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            });

            dialog.show();
        });
    }

    private void goToUserLogin(){
        mSharedPreferences.edit().putBoolean("LOGGED_IN", false).commit();
        mSharedPreferences.edit().putString("USER_ID", null).commit();
        UserLoginActivity userLoginActivity = new UserLoginActivity();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(android.R.id.tabcontent, userLoginActivity);
        fragmentManager.popBackStack();
        fragmentTransaction.commit();
    }

}
