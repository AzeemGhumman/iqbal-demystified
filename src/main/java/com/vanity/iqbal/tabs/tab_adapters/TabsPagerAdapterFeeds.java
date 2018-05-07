package com.vanity.iqbal.tabs.tab_adapters;


import com.vanity.iqbal.fragments.FragmentListSher;
import com.vanity.iqbal.factory.ListSherGenerator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapterFeeds extends FragmentPagerAdapter {

	public TabsPagerAdapterFeeds(FragmentManager fm) {
		super(fm);
		
	}

	@Override
	public Fragment getItem(int index) {
		

		switch (index) {
		case 0:
			// Recent fragment activity
			return FragmentListSher.newInstance(ListSherGenerator.ListSherTypes.FEED_RECENT);
		case 1:
			// Popufragment activity
			return FragmentListSher.newInstance(ListSherGenerator.ListSherTypes.FEED_POPULAR);
		case 2:
			// MyPoems fragment activity
			return FragmentListSher.newInstance(ListSherGenerator.ListSherTypes.FEED_BOOKMARKED_POEMS);
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of com.vanity.iqbal.tabswipe.tabs
		return 3;
	}

}
