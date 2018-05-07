package com.vanity.iqbal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vanity.iqbal.R;

import static com.vanity.iqbal.helper.Preferences.getUsernamePreference;
import static com.vanity.iqbal.helper.Preferences.setUsernamePreference;

public class ActivityProfile extends AppCompatActivity {

    TextView TxtSignOut, TxtMsg;
    Button BtnChangePassword, BtnNavigateToAudioDownloads;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("My Profile");

        TxtSignOut = (TextView)findViewById(R.id.TxtSignOut);
        TxtMsg = (TextView)findViewById(R.id.TxtMsg);
        BtnChangePassword = (Button)findViewById(R.id.BtnChangePassword);
        BtnNavigateToAudioDownloads = (Button)findViewById(R.id.BtnNavigateToAudioDownloads);

        // Set username
        username = getUsernamePreference(getApplicationContext());
        TxtMsg.setText(username);

        // Sign Out - Clicked
        TxtSignOut.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(), "Logged Out!", Toast.LENGTH_LONG).show();
                // Reset Username in SharedPreferences
                setUsernamePreference("", getApplicationContext());
                // Go back to Main Page
                finish();
            }
        });

        // Change Password Button clicked
        BtnChangePassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), ActivityChangePassword.class);
                startActivity(i);
            }
        });

        // Audio Manager Button clicked
        BtnNavigateToAudioDownloads.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), ActivityDownloads.class);
                startActivity(i);
            }
        });
    } // onCreate
}//class