package org.zirco.model.adapters;

import java.util.ArrayList;

import org.zirco.ui.fragment.BaseFragment;
import org.zirco.ui.fragment.CellFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class TabPagerAdapter extends FragmentPagerAdapter {
	
	private ArrayList<BaseFragment> fragments;
	private FragmentManager mFragmentManager;
	
	public TabPagerAdapter(FragmentManager fm) {
		super(fm);
		mFragmentManager = fm;
		fragments = new ArrayList<BaseFragment>();
		fragments.add(new CellFragment());
	}

	@Override
	public Fragment getItem(int position) {
		if(fragments != null) {
			return fragments.get(position);
		}
		return null;
	}

    @Override
	public int getCount() {
    	if(fragments != null) {
    		return fragments.size();
    	}
    	return 0;
	}
    
    /**
     * Add "Fragment" to right end of "Fragments".
     * 
     * @param f
     * @return  the position of the new view.
     */
	public int addView(BaseFragment f) {
		return addView(f, fragments.size());
	}
    
	/**
	 * Add "Fragment" at "position" to "Fragments".
	 * 
	 * @param f
	 * @param position of new view.
	 * @return
	 */
	public int addView(BaseFragment f, int position) {
		fragments.add(position, f);
		return position;
	}

	/**
	 * Removes "Fragment" from "Fragments".
	 * 
	 * @param pager
	 * @param f
	 * @return position of removed view.
	 */
	public int removeView(ViewPager pager, BaseFragment f) {
		return removeView(pager, fragments.indexOf(f));
	}

	/**
	 * Removes the "view" at "position" from "views".
	 * 
	 * @param pager
	 * @param position
	 * @return position of removed view.
	 */
    public int removeView (ViewPager pager, int position) {
      // ViewPager doesn't have a delete method; the closest is to set the adapter
      // again.  When doing so, it deletes all its views.  Then we can delete the view
      // from from the adapter and finally set the adapter to the pager again.  Note
      // that we set the adapter to null before removing the view from "views" - that's
      // because while ViewPager deletes all its views, it will call destroyItem which
      // will in turn cause a null pointer ref.
      pager.setAdapter (null);
      fragments.remove (position);
      pager.setAdapter (this);

      return position;
    }

}
