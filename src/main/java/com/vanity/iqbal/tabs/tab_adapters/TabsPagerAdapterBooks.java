package com.vanity.iqbal.tabs.tab_adapters;


import com.vanity.iqbal.fragments.FragmentPersianPartOne;
import com.vanity.iqbal.fragments.FragmentPersianPartTwo;
import com.vanity.iqbal.fragments.FragmentUrdu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapterBooks extends FragmentPagerAdapter {

	public TabsPagerAdapterBooks(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Urdu Books
			return new FragmentUrdu();
		case 1:
			// Persian Books 1
			return new FragmentPersianPartOne();
		case 2:
			// Persian Books 2
			return new FragmentPersianPartTwo();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of com.vanity.iqbal.tabswipe.tabs
		return 3;
	}

}
