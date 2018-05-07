package com.vanity.iqbal.tabs.tab_adapters;

import com.vanity.iqbal.fragments.FragmentDiscussSherGeneral;
import com.vanity.iqbal.fragments.FragmentDiscussSherWords;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapterDiscussion  extends FragmentPagerAdapter{

	public TabsPagerAdapterDiscussion(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		switch (index) {
		case 0:
			return new FragmentDiscussSherGeneral();
		case 1:
			return new FragmentDiscussSherWords();
		}
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}
}
