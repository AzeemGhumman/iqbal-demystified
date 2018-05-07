package com.vanity.iqbal.services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.vanity.iqbal.R;
import com.vanity.iqbal.activities.ActivityMain;

import static com.vanity.iqbal.helper.IntentExtras.ExtraNotificationID;

public class NotifyService extends Service {

    //Global Variables
    NotificationManager myNotificationManager;
    public static int POEM_OF_THE_DAY_NOTIFICATION_ID = 786; // tells the type of notification, we only have one for now

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        displayNotification();
        stopSelf();
    } // onCreate

    @SuppressLint("NewApi")
    public void displayNotification() {

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Invoking the default notification service
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Iqbal Demystified");
        mBuilder.setContentText("Read Poem of the Day!");
        mBuilder.setTicker("Iqbal Demystified: Poem of the Day!");
        mBuilder.setSmallIcon(R.drawable.iqbal_icon);
        mBuilder.setSound(sound);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ActivityMain.class);
        resultIntent.putExtra(ExtraNotificationID, POEM_OF_THE_DAY_NOTIFICATION_ID);

        //This ensures that navigating backward from the Activity leads out of the app to Home page
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent
        stackBuilder.addParentStack(ActivityMain.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT); //can only be used once
        // start the activity when the user clicks the notification text
        mBuilder.setContentIntent(resultPendingIntent);

        myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // pass the Notification object to the system
        myNotificationManager.notify(POEM_OF_THE_DAY_NOTIFICATION_ID, mBuilder.build());
    }
}
