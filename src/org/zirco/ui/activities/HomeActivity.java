package org.zirco.ui.activities;

import org.zirco.R;
import org.zirco.model.adapters.TabPagerAdapter;
import org.zirco.ui.view.NestedViewPager;
import org.zirco.ui.view.PagerSlidingTabStrip;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

/**
 * for test
 * @author chenyueguo
 *
 */
public class HomeActivity extends FragmentActivity {
	
	private NestedViewPager mTabViewPager;
	private TabPagerAdapter mTabPagerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.home_layout);
		
		initViewPagerAndTab();
		
	}
	
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
	
	/**
	 * 初始化ViewPager和tab
	 * */
	private void initViewPagerAndTab() {
		mTabViewPager = (NestedViewPager) this.findViewById(R.id.detailplay_half_detail_viewpager);
		mTabViewPager.setPagingEnabled(true);
		mTabPagerAdapter = new TabPagerAdapter(this.getSupportFragmentManager());
		mTabViewPager.setAdapter(mTabPagerAdapter);
		mTabViewPager.setCurrentItem(0);
	}
	
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
