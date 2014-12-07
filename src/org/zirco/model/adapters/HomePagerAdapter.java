package org.zirco.model.adapters;

import org.zirco.BrowserApplication;
import org.zirco.R;
import org.zirco.ui.fragment.HomeFragmentManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class HomePagerAdapter extends FragmentPagerAdapter {
	
	private HomeFragmentManager mHomeFragmentManager;
	private FragmentManager mFragmentManager;
	
	protected static String[] TEXTS ;

	public HomePagerAdapter(FragmentManager fm) {
		super(fm);
		mFragmentManager = fm;
		mHomeFragmentManager = new HomeFragmentManager();
		TEXTS = BrowserApplication.getContext().getResources().getStringArray(R.array.home_tabs);
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
	    return TEXTS[position];
	}

	@Override
	public Fragment getItem(int position) {
		return mHomeFragmentManager.newInstance(position);
	}

    @Override
	public int getCount() {
		return TEXTS.length;
	}
	
	public void format(){
		mHomeFragmentManager.destroy(mFragmentManager);
		mHomeFragmentManager = null ;
	}

}
