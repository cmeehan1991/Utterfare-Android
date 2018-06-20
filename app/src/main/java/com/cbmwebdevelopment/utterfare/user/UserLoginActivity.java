package com.cbmwebdevelopment.utterfare.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;

import com.cbmwebdevelopment.utterfare.main.MainActivity;
import com.cbmwebdevelopment.utterfare.newuser.NewUserActivity;
import com.cbmwebdevelopment.utterfare.passwordreset.RequestPasswordResetActivity;
import com.cbmwebdevelopment.utterfare.saved.SavedItemsActivity;
import com.cbmwebdevelopment.utterfare.search.SearchActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 5/8/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class UserLoginActivity extends Fragment {

    private String TAG = this.getClass().getName();
    private View v;
    private EditText usernameEditText, passwordEditText;
    private TextView signUpTextView,forgotPasswordTextView;
    private Button signInButton;
    private Activity mActivity;
    private Context mContext;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the view
        v = inflater.inflate(R.layout.activity_sign_in, container, false);

        // Initialize the UI variables
        usernameEditText = (EditText) v.findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) v.findViewById(R.id.passwordEditText);
        signUpTextView = (TextView) v.findViewById(R.id.signUpTextView);
        forgotPasswordTextView = (TextView) v.findViewById(R.id.forgotPasswordTextView);
        signInButton = (Button) v.findViewById(R.id.signInButton);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mActivity = getActivity();
        mContext = getContext();

        // Get the shared preferences
        sharedPreferences = mActivity.getSharedPreferences(MainActivity.UF_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        // Handle when the clickable elements are clicked
        onClickMethods();
    }

    private void onClickMethods(){
        // User Sign In Clicked
        signInButton.setOnClickListener((l)->{
            signIn(v);
        });

        // Forgot password edittext clicked
        forgotPasswordTextView.setOnClickListener((l)->{
            resetPassword(v);
        });

        // sign up edit text clicked
        signUpTextView.setOnClickListener((l)->{
            signUp(v);
        });
    }

    /**
     * Navigate to a new activity to allow a new user to sign up.
     * @param view
     */
    private void signUp(View view){
        NewUserActivity newUserActivity = new NewUserActivity();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(android.R.id.tabcontent, newUserActivity)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Navigate to a new activity to handle the password reset.
     * @param view
     */
    private void resetPassword(View view){
        RequestPasswordResetActivity requestPasswordResetActivity = new RequestPasswordResetActivity();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(android.R.id.tabcontent, requestPasswordResetActivity)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Check if the username and password are both completed
     * If so then handle the sign in action
     * @param view
     */
    private void signIn(View view) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(!username.isEmpty() && !password.isEmpty()){
            SignInModel signInModel = new SignInModel(this);
            signInModel.execute(username, password);
            try {
                String results = signInModel.get();
                handleSignin(results);
            }catch(InterruptedException | ExecutionException ex){
                Log.i(TAG, "Results Exception: " + ex.getMessage());
            }
        }
    }

    /**
     * Handle the response.
     * If the user is real then we are allowing them to log in.
     * Otherwise we are going to display a notification that the user does not exist.
     * @param results
     */
    private void handleSignin(String results){
        try {
            JSONObject jsonObject = new JSONObject(results);
            String userId = String.valueOf(jsonObject.get("ID"));
            String response = String.valueOf(jsonObject.get("RESPONSE"));

            if(response.equals("SUCCESS")){
                sharedPreferences.edit().putString("USER_ID", userId).commit();
                sharedPreferences.edit().putBoolean("LOGGED_IN", true).commit();
                goToSaved();
            }else{
                notifyUser("Login Failed", "The username/password combination you entered to not match anything we have on file. Please try again.");
            }
        }catch(JSONException ex){
            Log.i(TAG, "JSON Exception: " + ex.getMessage());
            notifyUser("Error: Login Failed", "There was an error completing the login. Please wait a moment and try again.");
        }

    }

    private void notifyUser(String title, String message){
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("OK", (dialogInterface, i)->{});
        alert.show();
    }

    /**
     * Go to the saved items.
     */
    private void goToSaved(){
        SavedItemsActivity savedItemsActivity = new SavedItemsActivity();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(android.R.id.tabcontent, savedItemsActivity);
        transaction.commit();
    }


}
