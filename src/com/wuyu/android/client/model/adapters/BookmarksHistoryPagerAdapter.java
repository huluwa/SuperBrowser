package com.wuyu.android.client.model.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wuyu.android.client.BrowserApplication;
import com.wuyu.android.client.R;
import com.wuyu.android.client.ui.fragment.BookmarksHistoryFragmentManager;

public class BookmarksHistoryPagerAdapter extends FragmentPagerAdapter {
	
	private BookmarksHistoryFragmentManager mBookmarksHistoryFragmentManager;
	private FragmentManager mFragmentManager;
	
	protected static String[] TEXTS ;

	public BookmarksHistoryPagerAdapter(FragmentManager fm) {
		super(fm);
		mFragmentManager = fm;
		mBookmarksHistoryFragmentManager = new BookmarksHistoryFragmentManager();
		TEXTS = BrowserApplication.getContext().getResources().getStringArray(R.array.bookmarks_history_tabs);
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
	    return TEXTS[position];
	}

	@Override
	public Fragment getItem(int position) {
		return mBookmarksHistoryFragmentManager.newInstance(position);
	}

    @Override
	public int getCount() {
		return TEXTS.length;
	}
	
	public void format(){
		mBookmarksHistoryFragmentManager.destroy(mFragmentManager);
		mBookmarksHistoryFragmentManager = null ;
	}

}
