package com.cbmwebdevelopment.utterfare.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.cbmwebdevelopment.utterfare.main.MainActivity;
import com.cbmwebdevelopment.utterfare.newuser.NewUserActivity;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cbmwebdevelopment.utterfare.R;

import static android.view.View.VISIBLE;
import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by Connor Meehan on 2020-01-08.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class UserSignIn {

    private final ViewPager VIEW_PAGER;
    private final Context CONTEXT;
    private final Activity ACTIVITY;
    private EditText usernameInput, passwordInput;
    private Button signInButton, cancelSignInButton, signUpButton;
    private ProgressBar signInProgressBar;
    private AlertDialog signInDialog;


    public UserSignIn(ViewPager viewPager, Context context, Activity activity){
        this.VIEW_PAGER = viewPager;
        this.CONTEXT = context;
        this.ACTIVITY = activity;
    }

    public void signInDialog(){
        //Instantiate the alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(ACTIVITY);

        // Get the layout inflater

        View signInView = LayoutInflater.from(CONTEXT).inflate(R.layout.view_login_dialog, null);

        usernameInput = (EditText) signInView.findViewById(R.id.username_input);
        passwordInput = (EditText) signInView.findViewById(R.id.password_input);
        signInButton = (Button) signInView.findViewById(R.id.sign_in_button);
        cancelSignInButton = (Button) signInView.findViewById(R.id.cancel_sign_in_button);
        signUpButton = (Button) signInView.findViewById(R.id.sign_up_button);
        signInProgressBar = (ProgressBar) signInView.findViewById(R.id.sign_in_progress);

        // Add the content to the dialog
        builder.setTitle("Sign In")
                .setView(signInView);


        signInButton.setOnClickListener((listener) -> {
            signInProgressBar.setVisibility(VISIBLE);
            boolean signedIn = false;
            if (!usernameInput.getText().toString().isEmpty() && !passwordInput.getText().toString().isEmpty()) {
                signedIn = signUserIn(usernameInput.getText().toString(), passwordInput.getText().toString());
                MainActivity.sharedPreferences.edit().putBoolean("LOGGED_IN", signedIn).commit();
            }

            Log.d(TAG, String.valueOf(signedIn));

            if (signedIn) {
                signInDialog.cancel();
            }

        });

        cancelSignInButton.setOnClickListener((listener) -> {
            signInDialog.cancel();
            VIEW_PAGER.setCurrentItem(0, false);
        });

        signUpButton.setOnClickListener((l)->{
            NewUserActivity newUserActivity = new NewUserActivity();

            FragmentActivity activity = (FragmentActivity) CONTEXT;
            FragmentManager fm = activity.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.replace(R.id.switch_fragment, newUserActivity);

            ft.addToBackStack("Log In");
            ft.commit();
        });

        // Create the alert dialog
        signInDialog = builder.create();
        signInDialog.show();
    }

    private boolean signUserIn(String username, String password) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Boolean> signIn = executor.submit(() -> {

            // Instantiate the sign in model
            SignInModel signInModel = new SignInModel();

            // Execute the sign in
            signInModel.execute(username, password);

            // Get the response
            String response = signInModel.get();

            // Convert the response from a string to a JSON Object for parsing
            JSONObject jsonObj = new JSONObject(response);

            boolean success = false;

            if (jsonObj.getString("response").equals("SUCCESS")) {
                success = true;
                MainActivity.sharedPreferences.edit().putString("USER_ID", jsonObj.getString("user_id")).commit();
            }

            executor.shutdown();
            return success;
        });

        // Try to get the response from the task
        try {
            return signIn.get();
        } catch (ExecutionException | InterruptedException ex) {
            return false;
        }

    }
}
