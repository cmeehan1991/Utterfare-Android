package com.cbmwebdevelopment.utterfare.saved;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbmwebdevelopment.utterfare.profile.UserProfileActivity;
import com.cbmwebdevelopment.utterfare.user.SignInModel;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import cbmwebdevelopment.utterfare.R;

import static android.view.View.VISIBLE;
import static com.cbmwebdevelopment.utterfare.main.MainActivity.UF_SHARED_PREFERENCES;

/**
 * Created by Connor Meehan on 5/5/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class SavedItemsActivity extends Fragment {
    private View v;
    public SharedPreferences sharedPreferences;
    private String TAG = this.getClass().getName();
    private Activity mActivity;
    private Context mContext;
    private RecyclerView savedItemsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ProgressBar progressBar, signInProgressBar;
    private List<SavedItems> itemsList;
    private boolean isLoggedIn;
    private AlertDialog signInDialog;
    private EditText usernameInput, passwordInput;
    private Button signInButton, cancelSignInButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(UF_SHARED_PREFERENCES, getContext().MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("LOGGED_IN", false);

        setHasOptionsMenu(isLoggedIn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_saved, container, false);

        mContext = getContext();

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && !isLoggedIn) {
            setHasOptionsMenu(false);

            // Instantiate the alert dialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // Get the layout inflater

            View signInView = LayoutInflater.from(this.getContext()).inflate(R.layout.view_login_dialog, null);

            usernameInput = (EditText) signInView.findViewById(R.id.username_input);
            passwordInput = (EditText) signInView.findViewById(R.id.password_input);
            signInButton = (Button) signInView.findViewById(R.id.sign_in_button);
            cancelSignInButton = (Button) signInView.findViewById(R.id.cancel_sign_in_button);
            signInProgressBar = (ProgressBar) signInView.findViewById(R.id.sign_in_progress);

            // Add the content to the dialog
            builder.setTitle("Sign In")
                    .setView(signInView);


            signInButton.setOnClickListener((listener) -> {
                signInProgressBar.setVisibility(VISIBLE);
                boolean signedIn = false;
                if(!usernameInput.getText().toString().isEmpty() && !passwordInput.getText().toString().isEmpty()){
                   signedIn = signUserIn(usernameInput.getText().toString(), passwordInput.getText().toString());
                   sharedPreferences.edit().putBoolean("LOGGED_IN", signedIn).commit();
                }

                if(signedIn){

                    signInDialog.cancel();
                }

            });

            cancelSignInButton.setOnClickListener((listener) -> {
                signInDialog.cancel();
            });

            // Create the alert dialog
            signInDialog = builder.create();
            signInDialog.show();
        }
    }

    public boolean signUserIn(String username, String password) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Boolean> signIn = executor.submit(() -> {

            // Instantiate the sign in model
            SignInModel signInModel = new SignInModel();

            // Execute the sign in
            signInModel.execute(username, password);

            // Get the response
            String response = signInModel.get();

            Log.d(TAG, response);

            // Convert the response from a string to a JSON Object for parsing
            JSONObject jsonObj = new JSONObject(response);

            boolean success = false;

            if(jsonObj.getString("response").equals("SUCCESS")){
                success = true;
                sharedPreferences.edit().putString("USER_ID", jsonObj.getString("user_id")).commit();
            }

            executor.shutdown();
            return success;
        });

        // Try to get the response from the task
        try {
            return signIn.get();
        } catch (ExecutionException | InterruptedException ex) {
            Log.e(TAG, "Sign In Exception");
            Log.e(TAG, ex.getMessage());
            return false;
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        initializeView();
    }

    private void initializeView() {
        savedItemsRecyclerView = (RecyclerView) v.findViewById(R.id.savedItemsRecyclerView);
        savedItemsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        savedItemsRecyclerView.setLayoutManager(layoutManager);
        progressBar = (ProgressBar) v.findViewById(R.id.loadMoreProgressBar);

        itemsList = new ArrayList<>();

        setRecycler();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_menu, menu);
        menu.getItem(0).setOnMenuItemClickListener((l) -> {
            UserProfileActivity userProfileActivity = new UserProfileActivity();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(android.R.id.tabcontent, userProfileActivity)
                    .addToBackStack("SavedItems");
            fragmentTransaction.commit();
            return true;
        });
    }

    private void setRecycler() {
        adapter = new SavedItemsAdapter(itemsList, mContext);

        savedItemsRecyclerView.setAdapter(adapter);
        getItems();
    }

    private void getItems() {
        progressBar.setVisibility(VISIBLE);
        progressBar.bringToFront();
        try {
            GetSavedItemsModel getSavedItemsModel = new GetSavedItemsModel();
            String userId = sharedPreferences.getString("USER_ID", null);
            if (userId != null) {
                String results = getSavedItemsModel.execute(userId).get();

                if (results != null) {
                    JSONArray jsonArray = new JSONArray(results);
                    showItems(jsonArray);
                } else {
                    Log.i(TAG, "Activity: " + mActivity);
                    Snackbar.make(mActivity.findViewById(android.R.id.tabcontent), "You don't have any saved items yet.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("SEARCH", (l) -> {
                                FragmentTabHost host = (FragmentTabHost) mActivity.findViewById(android.R.id.tabhost);
                                host.setCurrentTab(0);
                            }).show();
                }
            }
        } catch (ExecutionException | InterruptedException | JSONException ex) {
            Log.e(TAG, "Results: " + ex.getMessage());
        }
    }

    private void showItems(JSONArray jsonArrResponse) {

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            for (int i = 0; i < jsonArrResponse.length(); i++) {
                SavedItems items = new SavedItems();
                try {
                    JSONObject jsonObject = jsonArrResponse.getJSONObject(i);
                    items.setItemId(jsonObject.getString("ITEM_ID"));
                    items.setItemName(jsonObject.getString("ITEM_NAME"));
                    items.setDataTable(jsonObject.getString("ITEM_DATA_TABLE"));
                    items.setItemImage(jsonObject.getString("ITEM_IMAGE_URL"));
                    items.setItemName(jsonObject.getString("ITEM_NAME"));
                } catch (JSONException ex) {
                    Log.e(TAG, "JSON Exception: " + ex.getMessage());
                }
                itemsList.add(items);
            }
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setProgress(0);
            executor.shutdown();
        });
    }


}
