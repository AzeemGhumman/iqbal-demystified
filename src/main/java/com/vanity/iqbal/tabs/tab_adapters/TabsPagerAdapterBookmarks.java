package com.vanity.iqbal.tabs.tab_adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vanity.iqbal.fragments.FragmentBookmarkPoem;
import com.vanity.iqbal.fragments.FragmentListSher;
import com.vanity.iqbal.factory.ListSherGenerator;

public class TabsPagerAdapterBookmarks extends FragmentPagerAdapter {

	public TabsPagerAdapterBookmarks(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		switch (index) {
		case 0:
			// Bookmark Poem activity
			// TODO: Replace this? have a common fragment for all listpoems?
			return new FragmentBookmarkPoem();
		case 1:
			// Bookmark Sher activity
			return FragmentListSher.newInstance(ListSherGenerator.ListSherTypes.BOOKMARKED_SHERS);
		}
		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of com.vanity.iqbal.tabswipe.tabs
		return 2;
	}

}
