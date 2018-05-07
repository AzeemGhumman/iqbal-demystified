package com.vanity.iqbal.tabs.tab_activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.vanity.iqbal.R;
import com.vanity.iqbal.factory.ListSherGenerator;
import com.vanity.iqbal.tabs.tab_adapters.TabsPagerAdapterFeeds;

import static com.vanity.iqbal.helper.Preferences.setCachedDiscussionFeedPreference;

@SuppressLint("NewApi")
public class TabsFeed extends FragmentActivity {

	private ViewPager viewPager;
	private TabsPagerAdapterFeeds mAdapter;

	// Tab titles
	private String[] tabs = { "Recent", "Popular", "My Poems" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs_feed);

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		mAdapter = new TabsPagerAdapterFeeds(getSupportFragmentManager());
		viewPager.setAdapter(mAdapter);

		TabLayout tableLayout = (TabLayout) findViewById(R.id.discussion_tab_layout);
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

    @Override
    public void onStop() {
        super.onStop();
        // Clear cached feeds
        setCachedDiscussionFeedPreference(this, ListSherGenerator.ListSherTypes.FEED_RECENT, "");
        setCachedDiscussionFeedPreference(this, ListSherGenerator.ListSherTypes.FEED_POPULAR, "");
        setCachedDiscussionFeedPreference(this,ListSherGenerator.ListSherTypes.FEED_BOOKMARKED_POEMS, "");
    }
}
