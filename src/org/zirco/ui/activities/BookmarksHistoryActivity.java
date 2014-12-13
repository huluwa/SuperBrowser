/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.zirco.ui.activities;

import org.zirco.R;
import org.zirco.controllers.Controller;
import org.zirco.model.adapters.BookmarksHistoryPagerAdapter;
import org.zirco.ui.view.NestedViewPager;
import org.zirco.ui.view.PagerSlidingTabStrip;
import org.zirco.utils.Constants;
import org.zirco.utils.LogUtils;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * Combined bookmarks and history activity.
 */
public class BookmarksHistoryActivity extends BaseActivity {
	
	private NestedViewPager mViewPager;
	private BookmarksHistoryPagerAdapter mPagerAdapter;
	private PagerSlidingTabStrip mPagerSlidingTabStrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		if (Controller.getInstance().getPreferences().getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN, false)) {        	
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        
        if (Controller.getInstance().getPreferences().getBoolean(Constants.PREFERENCES_GENERAL_HIDE_TITLE_BARS, true)) {
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
		
		setContentView(R.layout.bookemarks_and_history_layout);
		
		initViewPagerAndTab();
	}
	
	/**
	 * 初始化ViewPager和tab
	 * */
	private void initViewPagerAndTab() {
		Log.i("chenyg", "CellFragment-->initViewPagerAndTab()");
		mViewPager = (NestedViewPager) this.findViewById(R.id.cell_viewpager);
		mViewPager.setPagingEnabled(true);
		mPagerAdapter = new BookmarksHistoryPagerAdapter(this.getSupportFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(0);
		
		mPagerSlidingTabStrip = (PagerSlidingTabStrip) this.findViewById(R.id.cell_tabs);
		mPagerSlidingTabStrip.setViewPager(mViewPager);
		mPagerSlidingTabStrip.setOnPageChangeListener(onPageChangeListener);
	}

	private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			LogUtils.i("postion=" + position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {

		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	};
	
}
