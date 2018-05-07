package com.vanity.iqbal.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import static com.vanity.iqbal.helper.Preferences.setTimesAppOpenedPreference;

/**
 * Created by aghumman on 5/2/2018.
 */

public class RateApp {

    public static void AskUserToRateApp(final Activity activity) {

        //Are you sure Dialog? only when in Bookmark Lists
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        setTimesAppOpenedPreference(-1, activity.getApplicationContext());

                        //Open App Store, if market place app not installed, open a web browser
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //Try Google play
                        intent.setData(Uri.parse("market://details?id=com.vanity.iqbal"));
                        if (!isStartActivitySuccessful(intent, activity)) {
                            // Market (Google play) app seems not installed, let's try to open a webbrowser
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.vanity.iqbal"));
                            if (!isStartActivitySuccessful(intent, activity)) {
                                //Well if this also fails, we have run out of options, inform the user.
                                Toast.makeText(activity.getApplicationContext(), "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        setTimesAppOpenedPreference(-1, activity.getApplicationContext());
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        //this pop-up will come after some time
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Please Rate This App!")
                .setPositiveButton("Never", dialogClickListener)
                .setNeutralButton("Later", dialogClickListener)
                .setNegativeButton("Sure", dialogClickListener).show();

    } //ask user to rate app

    private static boolean isStartActivitySuccessful(Intent aIntent, Activity activity) {
        try {
            activity.startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }
}
