package org.zirco.model.adapters;

import java.util.ArrayList;
import java.util.List;

import org.zirco.ui.fragment.BaseFragment;
import org.zirco.ui.fragment.HomeFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

public class TabPagerAdapter extends FragmentPagerAdapter {
	
	private ArrayList<BaseFragment> fragments;
	private int mCurrPosition = 0;
	
	public TabPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		fragments = new ArrayList<BaseFragment>();
//		fragments.add(new HomeFragment());
//		fragments.add(new WebviewFragment(context));
//		fragments.add(new WebviewFragment(context));
//		fragments.add(new WebviewFragment(context));
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		super.setPrimaryItem(container, position, object);
//		Log.i("chenyg", "TabPagerAdapter:setPrimaryItem(), position=" + position);
	}

	@Override
	public Fragment getItem(int position) {
//		Log.i("chenyg", "TabPagerAdapter:getItem(), position=" + position);
		if(fragments != null && !fragments.isEmpty()) {
			return fragments.get(position);
		}
		return null;
	}

    @Override
	public int getCount() {
//    	Log.i("chenyg", "TabPagerAdapter:getCount(), fragments.size()=" + fragments.size());
    	if(fragments != null && !fragments.isEmpty()) {
    		return fragments.size();
    	}
    	return 0;
	}
    
    public void setCurrentPosition(int position) {
		mCurrPosition = position;
    }
	
	public int getCurrentPosition() {
		return mCurrPosition;
	}
    
	public List<BaseFragment> getFragments() {
		return fragments;
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
