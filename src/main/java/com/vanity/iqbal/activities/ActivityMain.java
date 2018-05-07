package com.vanity.iqbal.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.vanity.iqbal.R;
import com.vanity.iqbal.tabs.tab_activities.TabsBookmark;
import com.vanity.iqbal.tabs.tab_activities.TabsFeed;
import com.vanity.iqbal.tabs.tab_adapters.TabsPagerAdapterBooks;

import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.LIST_BOOKMARKED_POEMS;
import static com.vanity.iqbal.factory.ListPoemGenerator.ListPoemTypes.LIST_EDITORS_PICK;
import static com.vanity.iqbal.helper.IntentExtras.ExtraListSelected;
import static com.vanity.iqbal.helper.IntentExtras.ExtraNotificationID;
import static com.vanity.iqbal.helper.IntentExtras.ExtraPoemSelected;
import static com.vanity.iqbal.helper.PoemOfTheDay.createAlarmForPoemOfTheDayNotification;
import static com.vanity.iqbal.helper.PoemOfTheDay.selectPoemOfTheDay;
import static com.vanity.iqbal.helper.Preferences.getTimesAppOpenedPreference;
import static com.vanity.iqbal.helper.Preferences.getUsernamePreference;
import static com.vanity.iqbal.helper.Preferences.setTimesAppOpenedPreference;
import static com.vanity.iqbal.helper.RateApp.AskUserToRateApp;
import static com.vanity.iqbal.services.NotifyService.POEM_OF_THE_DAY_NOTIFICATION_ID;

@SuppressLint("NewApi")
public class ActivityMain extends FragmentActivity implements ActionBar.TabListener {

    ImageView BtnEditorPick, BtnBookmarks, BtnSearch, BtnInfo;
    ImageView BtnProfile, BtnMessageBoard;

    private ViewPager viewPager;
    // Tab titles
    private String[] tabs = {"Urdu Books", "Persian (1)", "Persian (2)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if notification id is set: meaning that we need to select the poem of the day
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int notificationID = extras.getInt(ExtraNotificationID, 0);
            if (notificationID == POEM_OF_THE_DAY_NOTIFICATION_ID) {
                //Remove Notification from tray
                NotificationManager myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // remove the notification with the specific id
                myNotificationManager.cancel(POEM_OF_THE_DAY_NOTIFICATION_ID);

                // Randomly select a poem from Editor's Pick and go to that poem directly
                String poemId = selectPoemOfTheDay(getApplicationContext());
                Intent intent = new Intent(this, ActivityPoem.class);
                intent.putExtra(ExtraPoemSelected, poemId);
                startActivity(intent);
            }
        }

        // Create Alarm for Poem of the Day Notification
        createAlarmForPoemOfTheDayNotification(this);

        BtnEditorPick = (ImageView) findViewById(R.id.BtnEditorChoice);
        BtnBookmarks = (ImageView) findViewById(R.id.BtnBookmarks);
        BtnSearch = (ImageView) findViewById(R.id.BtnSearch);
        BtnInfo = (ImageView) findViewById(R.id.BtnInfo);

        BtnProfile = (ImageView) findViewById(R.id.BtnProfile);
        BtnMessageBoard = (ImageView) findViewById(R.id.BtnMessageBoard);

        //Button Click Events

        BtnEditorPick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), ActivityListPoem.class);
                intent.putExtra(ExtraListSelected, LIST_EDITORS_PICK.ordinal());
                startActivity(intent);
            }
        });

        BtnBookmarks.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), TabsBookmark.class);
                intent.putExtra(ExtraListSelected, LIST_BOOKMARKED_POEMS.ordinal());
                startActivity(intent);
            }
        });

        BtnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), ActivitySearch.class);
                startActivity(intent);
            }
        });

        BtnInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), ActivitySettings.class);
                startActivity(intent);
            }
        });

        BtnProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String username = getUsernamePreference(getApplicationContext());
                if (username.equals("")) {
                    //Go to sign in page
                    Intent intent = new Intent(getApplicationContext(), ActivitySignIn.class);
                    startActivity(intent);
                } else {
                    //Go to profile page
                    Intent intent = new Intent(getApplicationContext(), ActivityProfile.class);
                    startActivity(intent);
                }
            }
        });

        BtnMessageBoard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), TabsFeed.class);
                startActivity(intent);
            }
        });

        // Initialization
        viewPager = (ViewPager) findViewById(R.id.books_pager);
        TabsPagerAdapterBooks mAdapter = new TabsPagerAdapterBooks(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        TabLayout tableLayout = (TabLayout) findViewById(R.id.books_tab_layout);
        tableLayout.setTabGravity(tableLayout.GRAVITY_FILL);
        for (String tab_name : tabs) {
            tableLayout.addTab(tableLayout.newTab().setText(tab_name));
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tableLayout));
        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        int timesAppOpened = getTimesAppOpenedPreference(getApplicationContext());
        // Ask user to rate app every 5th fifth time, except if the user has declined to rate
        if (timesAppOpened % 5 == 0 && timesAppOpened > 0) {
            AskUserToRateApp(this);
        }
        //if times_opened = -1 , it means user pressed the Yes or the No button sometime ago, and we disable this Rate feature.
        if (timesAppOpened != -1) {
            setTimesAppOpenedPreference(timesAppOpened + 1, getApplicationContext());
        }
    } // onCreate

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    // Menu Button - Manage Audio Downloads
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), ActivityDownloads.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Select Profile Image - based on if user is signed in or signed out
        //This image change is done here so, when we get back from signed in or signed out page,
        // it's a BACK operation, and onCreate is not called on BACK
        String username = getUsernamePreference(getApplicationContext());
        if (username.equals("")) //User is signed out
        {
            int resID = getResources().getIdentifier("icon_signed_out", "drawable", getPackageName());
            BtnProfile.setImageResource(resID);

        } else // User is signed in
        {
            int resID = getResources().getIdentifier("icon_signed_in", "drawable", getPackageName());
            BtnProfile.setImageResource(resID);
        }
    } // onResume
}
