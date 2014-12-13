package org.zirco.model.adapters;

import org.zirco.BrowserApplication;
import org.zirco.R;
import org.zirco.ui.fragment.BookmarksHistoryFragmentManager;
import org.zirco.ui.fragment.HomeFragmentManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

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
