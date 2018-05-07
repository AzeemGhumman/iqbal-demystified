package com.vanity.iqbal.tabs.tab_activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.vanity.iqbal.R;
import com.vanity.iqbal.tabs.tab_adapters.TabsPagerAdapterBookmarks;

/**
 * Created by aghumman on 4/15/2018.
 */

@SuppressLint("NewApi")
public class TabsBookmark extends FragmentActivity {

    private ViewPager viewPager;
    public TabsPagerAdapterBookmarks mAdapter;
    // Tab titles
    private String[] tabs = { "Poems", "Shers"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_bookmark);

        setTitle("Bookmarks");

        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapterBookmarks(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        TabLayout tableLayout = (TabLayout) findViewById(R.id.bookmark_tab_layout);
        for (String tab_name : tabs) {
            tableLayout.addTab(tableLayout.newTab().setText(tab_name));
        }

        tableLayout.setTabGravity(tableLayout.GRAVITY_FILL);

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
    }
}
