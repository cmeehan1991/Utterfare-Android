package com.cbmwebdevelopment.utterfare.notifications;

import android.app.Activity;
import android.app.AlertDialog;

/**
 * Created by Connor Meehan on 5/18/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class CustomAlerts {

    public void errorAlert(String title, String message, Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, id)->{});
       builder.show();
    }
}
