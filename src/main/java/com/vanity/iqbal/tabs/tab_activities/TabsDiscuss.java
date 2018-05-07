package com.vanity.iqbal.tabs.tab_activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.vanity.iqbal.R;
import com.vanity.iqbal.tabs.tab_adapters.TabsPagerAdapterDiscussion;

@SuppressLint("NewApi")
public class TabsDiscuss extends AppCompatActivity {

	//Fragment Control
	private ViewPager viewPager;
	private TabsPagerAdapterDiscussion mAdapter;

	// Tab titles
	private String[] tabs = { "Discussion", "Word Meanings" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs_discuss);

		// Initializations
		viewPager = (ViewPager) findViewById(R.id.discuss_view_pager);
		mAdapter = new TabsPagerAdapterDiscussion(getSupportFragmentManager()); //Send bundle to actual pages
		viewPager.setAdapter(mAdapter);

		TabLayout tableLayout = (TabLayout) findViewById(R.id.discuss_tab_layout);
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

		// This line hides the keyboard, which will otherwise pop up because of the EditText : TxtComment
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	} // onCreate

} // class
