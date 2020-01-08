package com.cbmwebdevelopment.utterfare.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

/**
 * Created by Connor Meehan on 2020-01-07.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class UserProfileActivity extends Fragment {

    public EditText firstNameInput, lastNameInput, emailInput, primaryAddressInput, secondaryAddressInput, cityInput, stateInput, postalCodeInput, cellPhoneNumberInput;
    public Spinner genderSpinner;
    public DatePicker birthdayDatePicker;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState){
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState);

        return null;
    }
}
