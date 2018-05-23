package com.cbmwebdevelopment.utterfare.newuser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cbmwebdevelopment.utterfare.main.GlobalVariables;
import com.cbmwebdevelopment.utterfare.main.MainActivity;
import com.cbmwebdevelopment.utterfare.saved.SavedItemsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 5/16/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class NewUserActivity extends Fragment implements GlobalVariables {
    private final String TAG = getClass().getName();
    private View v;
    private Context mContext;
    private Activity mActivity;
    private EditText firstNameEditText, lastNameEditText, cityEditText, phoneNumberEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Spinner stateSpinner;
    private TextView confirmPasswordTextView, passwordMessageTextView, confirmPasswordMessageTextView;
    private Button signUpButton;
    private ArrayList<EditText>  missingItems;
    private InputMethodManager imm;
    private SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mContext = getContext();
        v = inflater.inflate(R.layout.fragment_new_user, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        mSharedPreferences = mActivity.getSharedPreferences(MainActivity.UF_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        initializeViewItems();
    }

    /**
     * Initialize all gthe view items
     */
    private void initializeViewItems(){

        // Instantiate the inputs
        firstNameEditText = (EditText) v.findViewById(R.id.signup_first_name_edit_text);
        lastNameEditText = (EditText) v.findViewById(R.id.signup_last_name_edit_text);
        cityEditText = (EditText) v.findViewById(R.id.signup_city_edit_text);
        stateSpinner = (Spinner) v.findViewById(R.id.signup_state_spinner);
        phoneNumberEditText = (EditText) v.findViewById(R.id.signup_phone_number_edittext);
        emailEditText = (EditText) v.findViewById(R.id.signup_email_edit_text);
        passwordEditText = (EditText) v.findViewById(R.id.signup_password_edit_text);
        passwordMessageTextView = (TextView) v.findViewById(R.id.password_message_text_view);
        confirmPasswordTextView = (TextView) v.findViewById(R.id.confirm_password_text_view);
        confirmPasswordMessageTextView = (TextView) v.findViewById(R.id.confirm_password_message_text_view);
        confirmPasswordEditText = (EditText) v.findViewById(R.id.signup_confirm_password_edit_text);
        signUpButton = (Button) v.findViewById(R.id.sign_up_button);

        // Instantiate the input method manager (keyboard)
        imm = (InputMethodManager)mActivity.getSystemService(mContext.INPUT_METHOD_SERVICE);

        // Set the liseteners
        setClickListeners();
        setKeyListeners();

        // Set the first name edit text to request the focus.
        firstNameEditText.requestFocus();

        // Hide the keyboard when the state spinner is selected
        stateSpinner.setOnFocusChangeListener((view, hasFocus)->{
            if(hasFocus){
                imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
            }
        });
    }

    /**
     * Set any necessary click listeners
     */
    private void setClickListeners(){
        signUpButton.setOnClickListener((l)->{
            imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
            signUpAction(v);
        });

        confirmPasswordEditText.setOnEditorActionListener((textView, actionId, event)->{
            if(actionId == EditorInfo.IME_ACTION_GO){
                signUpAction(v);
            }
            return true;
        });
    }

    private void setKeyListeners(){

        phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher("+1"));

        passwordEditText.setOnKeyListener((view, keyCode, event)->{
            String text = passwordEditText.getText().toString();
            if(text.length() < 8){
                passwordMessageTextView.setVisibility(View.VISIBLE);
                confirmPasswordTextView.setVisibility(View.INVISIBLE);
                confirmPasswordEditText.setVisibility(View.INVISIBLE);
                confirmPasswordEditText.setText("");
                confirmPasswordMessageTextView.setVisibility(View.INVISIBLE);
                signUpButton.setEnabled(false);
                signUpButton.setVisibility(View.INVISIBLE);
            }else{
                passwordMessageTextView.setVisibility(View.INVISIBLE);
                confirmPasswordTextView.setVisibility(View.VISIBLE);
                confirmPasswordEditText.setVisibility(View.VISIBLE);
            }
            return false;
        });

        confirmPasswordEditText.setOnKeyListener((view, keyCode, event)->{
            String password = passwordEditText.getText().toString();
            String confirmText = confirmPasswordEditText.getText().toString();
            if(!confirmText.equals(password)){
                confirmPasswordMessageTextView.setVisibility(View.VISIBLE);
                signUpButton.setEnabled(false);
                signUpButton.setVisibility(View.INVISIBLE);
            }else{
                confirmPasswordMessageTextView.setVisibility(View.INVISIBLE);
                signUpButton.setEnabled(true);
                signUpButton.setVisibility(View.VISIBLE);
            }
            return false;
        });
    }

    /**
     * Fires on signupButton clicked
     * @param view
     */
    private void signUpAction(View view){
        if(validateInputs()){
            String username = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String city = cityEditText.getText().toString();
            String state = stateSpinner.getSelectedItem().toString();
            String email = emailEditText.getText().toString();
            String phone = phoneNumberEditText.getText().toString();

            try {
                String results = new NewUserModel().execute(username, password, firstName, lastName, city, state, email, phone).get();
                Log.i(TAG, results);
                JSONObject jsonObject = new JSONObject(results);
                boolean success = jsonObject.getBoolean("SUCCESS");
                if(success){
                    String id = jsonObject.getString("ID");
                    goToSavedItems(id);
                }else{
                    String response = jsonObject.getString("RESPONSE");
                    notifyUser("Failed", response);
                }
            }catch(InterruptedException | ExecutionException | JSONException ex){
                Log.e(TAG, "Error with results: " + ex.getMessage());
            }
            Log.i(TAG, "Success");
        }else{
            notifyUser("Sign Up Failed", "You are missing some required fields. Please go back and check that all of the required fields have been completed.");
            for(EditText item : missingItems){
                item.setBackgroundResource(R.drawable.error_outline);
            }
            missingItems.get(0).requestFocus();
        }
    }

    private void goToSavedItems(String id){
        mSharedPreferences.edit().putString("USER_ID", id).commit();
        mSharedPreferences.edit().putBoolean("LOGGED_IN", true).commit();

        SavedItemsActivity savedItemsActivity = new SavedItemsActivity();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(android.R.id.tabcontent, savedItemsActivity)
                .commit();
    }

    private void notifyUser(String title, String message){
        AlertDialog.Builder alertBuilder =  new AlertDialog.Builder(mActivity)
                .setTitle(title)
                .setMessage(message).setPositiveButton("OK", (dialog, id)->{});
        AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }

    private boolean validateInputs(){
        missingItems = new ArrayList<>();
        if(firstNameEditText.getText().toString() == null || firstNameEditText.getText().toString().trim().isEmpty()){
            missingItems.add(firstNameEditText);
        }

        if(lastNameEditText.getText().toString() == null || lastNameEditText.getText().toString().trim().isEmpty()){
            missingItems.add(lastNameEditText);
        }

        if(cityEditText.getText().toString() == null || cityEditText.getText().toString().trim().isEmpty()){
            missingItems.add(cityEditText);
        }

        if(phoneNumberEditText.getText().toString() == null || phoneNumberEditText.getText().toString().trim().isEmpty()){
            missingItems.add(phoneNumberEditText);
        }

        if(emailEditText.getText().toString() == null || emailEditText.getText().toString().trim().isEmpty()){
            missingItems.add(emailEditText);
        }

        if(passwordEditText.getText().toString() == null || passwordEditText.getText().toString().trim().isEmpty() || passwordEditText.getText().toString().length() < 8){
            missingItems.add(passwordEditText);
        }

        if(confirmPasswordEditText.getText().toString() == null || confirmPasswordEditText.getText().toString().trim().isEmpty() || !confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString())){
            missingItems.add(confirmPasswordEditText);
        }

        return missingItems.isEmpty();
    }
}
