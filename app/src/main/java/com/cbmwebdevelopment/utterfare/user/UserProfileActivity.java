package com.cbmwebdevelopment.utterfare.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.cbmwebdevelopment.utterfare.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cbmwebdevelopment.utterfare.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by Connor Meehan on 2020-01-07.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class UserProfileActivity extends Fragment {

    public EditText firstNameInput, lastNameInput, emailInput, primaryAddressInput, secondaryAddressInput, cityInput, stateInput, postalCodeInput, cellPhoneNumberInput;
    public Spinner genderSpinner;
    public DatePicker birthdayDatePicker;
    public ProgressBar progressBar;
    public Button logOutButton;
    public SharedPreferences sharedPreferences;
    private ViewPager viewPager;
    private ArrayAdapter<CharSequence> genderAdapter;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


        boolean userIsSignedIn = MainActivity.sharedPreferences.getBoolean("LOGGED_IN", false);

        if (isVisibleToUser && userIsSignedIn) {
            progressBar.setVisibility(VISIBLE);
            getUserData();
        }else if(isVisibleToUser && !userIsSignedIn){
            new UserSignIn(viewPager, getContext(), getActivity()).signInDialog();
        }



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);

        menuInflater.inflate(R.menu.save_user_menu, menu);
        menu.getItem(0).setOnMenuItemClickListener((l) -> {
            saveUser();
            return true;
        });
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState);
        View v = layoutInflater.inflate(R.layout.fragment_profile, viewGroup, false);
        viewPager = (ViewPager) viewGroup;

        // Instantiate the inputs
        progressBar = (ProgressBar) v.findViewById(R.id._user_info_progress_bar);
        firstNameInput = (EditText) v.findViewById(R.id.first_name_input);
        lastNameInput = (EditText) v.findViewById(R.id.last_name_input);
        emailInput = (EditText) v.findViewById(R.id.email_address_input);
        primaryAddressInput = (EditText) v.findViewById(R.id.street_address_input);
        secondaryAddressInput = (EditText) v.findViewById(R.id.secondary_address_input);
        cityInput = (EditText) v.findViewById(R.id.city_input);
        stateInput = (EditText) v.findViewById(R.id.state_input);
        postalCodeInput = (EditText) v.findViewById(R.id.postal_code_input);
        cellPhoneNumberInput = (EditText) v.findViewById(R.id.cell_phone_input);
        genderSpinner = (Spinner) v.findViewById(R.id.gender_spinner);
        birthdayDatePicker = (DatePicker) v.findViewById(R.id.birthday_date_picker);
        logOutButton = (Button) v.findViewById(R.id.log_out_button);

        // Set button listener
        logOutButton.setOnClickListener((listener)->{
            MainActivity.sharedPreferences.edit()
                    .putString("USER_ID", "")
                    .putBoolean("LOGGED_IN", false)
                    .commit();

            // Get the viewpager
            viewPager.setCurrentItem(0);

            Toast.makeText(this.getContext(), "Signed Out", Toast.LENGTH_SHORT).show();
        });


        // Create the gender spinner adapter and assign it
        genderAdapter = ArrayAdapter.createFromResource(getContext(), R.array.genders, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        return v;
    }


    /**
     * Save the user information
     */
    public void saveUser() {
        progressBar.setVisibility(VISIBLE);

        ExecutorService executor = Executors.newCachedThreadPool();

        Future<String> saveProfileResults = executor.submit(() -> {
            String results = new String();
            UpdateUserInformationModel updateUserInformationModel = new UpdateUserInformationModel();

            //String birthday = birthdayDatePicker.getYear() + "-" + birthdayDatePicker.getMonth() + "-" + birthdayDatePicker.getDayOfMonth();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            Calendar calendar = Calendar.getInstance();
            calendar.set(birthdayDatePicker.getYear(), birthdayDatePicker.getMonth(), birthdayDatePicker.getDayOfMonth());

            String birthday = df.format(calendar.getTime());

            Log.i(TAG, birthday);

           results = updateUserInformationModel.updateUserInformation(MainActivity.sharedPreferences.getString("USER_ID", ""), firstNameInput.getText().toString(), lastNameInput.getText().toString(), emailInput.getText().toString(), cellPhoneNumberInput.getText().toString(), primaryAddressInput.getText().toString(), secondaryAddressInput.getText().toString(), cityInput.getText().toString(), stateInput.getText().toString(), postalCodeInput.getText().toString(), genderSpinner.getSelectedItem().toString(), birthday);

            return results;
        });
        executor.shutdown();

        try{

            JSONObject jsonObject = new JSONObject(saveProfileResults.get());
            progressBar.setVisibility(GONE);
            if(jsonObject.getBoolean("SUCCESS") == true){
                Toast.makeText(this.getActivity(), "Profile Saved", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this.getActivity(), "Error saving profile", Toast.LENGTH_SHORT).show();
            }
        }catch(JSONException | ExecutionException | InterruptedException ex){
            Log.e(TAG, "Future Error");
            Log.e(TAG, ex.getMessage());
        }
    }


    public void handleResponse(String res) {

    }

    /**
     * Populate the input fields with the user's information
     *
     * @param info
     */
    private void populateInputs(String info) {

        if (info != null && !info.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(info);

                firstNameInput.setText(jsonObject.getString("first_name"));
                lastNameInput.setText(jsonObject.getString("last_name"));
                emailInput.setText(jsonObject.getString("email"));
                cellPhoneNumberInput.setText(jsonObject.getString("telephone_number"));
                primaryAddressInput.setText(jsonObject.getString("primary_address"));
                secondaryAddressInput.setText(jsonObject.getString("secondary_address"));
                cityInput.setText(jsonObject.getString("city"));
                stateInput.setText(jsonObject.getString("state"));
                postalCodeInput.setText(jsonObject.getString("postal_code"));
                genderSpinner.setSelection(genderAdapter.getPosition(jsonObject.getString("gender")));

                if (!jsonObject.getString("birthday").isEmpty()) {
                    // Split the date string into objects
                    String[] birthday = jsonObject.getString("birthday").split("-");

                    // Convert each string object into integers
                    int year = Integer.parseInt(birthday[0]);
                    int month = Integer.parseInt(birthday[1]) - 1;
                    int day = Integer.parseInt(birthday[2].split(" ")[0]);

                    // Set the birthday value
                    birthdayDatePicker.updateDate(year, month, day);
                }


            } catch (JSONException ex) {
                Log.e(TAG, "Populating inputs error");
                Log.e(TAG, ex.getMessage());
            }
        }

        progressBar.setVisibility(GONE);
    }



    /**
     * Handle getting the user data.
     * The data will then be passed to the populateInputs method.
     */
    private void getUserData() {

        ExecutorService executor = Executors.newCachedThreadPool();

        Future<String> userInfo = executor.submit(() -> {

            UserProfileModel userProfile = new UserProfileModel();

            String userId = MainActivity.sharedPreferences.getString("USER_ID", "");

            String results = userProfile.doInBackground(userId);

            executor.shutdown();

            return results;

        });

        try {
            populateInputs(userInfo.get());
        } catch (ExecutionException | InterruptedException ex) {
            Log.e(TAG, "Exception");
            Log.e(TAG, ex.getMessage());
        }
    }
}
