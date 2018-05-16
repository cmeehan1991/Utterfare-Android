package com.cbmwebdevelopment.utterfare.newuser;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import cbmwebdevelopment.utterfare.R;

/**
 * Created by Connor Meehan on 5/16/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class NewUserActivity extends Fragment {
    private View v;
    private Context mContext;
    private Activity mActivity;
    private EditText firstNameEditText, lastNameEditText, cityEditText, stateEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private TextView passwordMessageTextView, confirmPasswordMessageTextView;
    private Button signUpButton;
    private ArrayList<EditText>  missingItems;

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
        initializeViewItems();
    }

    /**
     * Initialize all gthe view items
     */
    private void initializeViewItems(){
        firstNameEditText = (EditText) v.findViewById(R.id.signup_first_name_edit_text);
        lastNameEditText = (EditText) v.findViewById(R.id.signup_last_name_edit_text);
        cityEditText = (EditText) v.findViewById(R.id.signup_city_edit_text);
        stateEditText = (EditText) v.findViewById(R.id.signup_state_edit_text);
        emailEditText = (EditText) v.findViewById(R.id.signup_email_edit_text);
        passwordEditText = (EditText) v.findViewById(R.id.signup_password_edit_text);
        confirmPasswordEditText = (EditText) v.findViewById(R.id.signup_confirm_password_edit_text);
        signUpButton = (Button) v.findViewById(R.id.sign_up_button);

        setClickListeners();
    }

    /**
     * Set any necessary click listeners
     */
    private void setClickListeners(){
        signUpButton.setOnClickListener((l)->{
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
        passwordEditText.setOnKeyListener((view, keyCode, event)->{
            String text = passwordEditText.getText().toString();
            if(text.length() < 8){

            }else{

            }
            return true;
        });
    }

    /**
     * Fires on signupButton clicked
     * @param view
     */
    private void signUpAction(View view){
        if(validateInputs()){

        }else{
            notifyUser();
        }
    }

    private void notifyUser(){

    }

    private boolean validateInputs(){
        missingItems = new ArrayList<>();
        if(firstNameEditText.getText().toString() == null || firstNameEditText.getText().toString().trim().isEmpty()){
            missingItems.add(firstNameEditText);
        }

        if(lastNameEditText.getText().toString() == null || lastNameEditText.getText().toString().trim().isEmpty()){
            missingItems.add(lastNameEditText);
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
