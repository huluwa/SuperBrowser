package org.zirco.ui.fragment;

import org.zirco.R;
import org.zirco.model.adapters.HomePagerAdapter;
import org.zirco.ui.view.PagerSlidingTabStrip;
import org.zirco.ui.view.NestedViewPager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class HomeFragment extends BaseFragment {

	private Activity mActivity;
	private NestedViewPager mCellViewPager;
	private HomePagerAdapter mCellPagerAdapter;
	private PagerSlidingTabStrip mPagerSlidingTabStrip;
	
	private LinearLayout mRootLinearLayout;
	
	public HomeFragment() {
		setType(BaseFragment.TYPE_CELL_FRAGMENT);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		return super.onCreateView(inflater, container, savedInstanceState);
		mRootLinearLayout = (LinearLayout) inflater.inflate(R.layout.cell_fragment_layout, null);
		return mRootLinearLayout;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initViewPagerAndTab();
	}
	
	/**
	 * 初始化ViewPager和tab
	 * */
	private void initViewPagerAndTab() {
		Log.i("chenyg", "CellFragment-->initViewPagerAndTab()");
		mCellViewPager = (NestedViewPager) mRootLinearLayout.findViewById(R.id.cell_viewpager);
		mCellViewPager.setPagingEnabled(true);
		mCellPagerAdapter = new HomePagerAdapter(getChildFragmentManager());
		mCellViewPager.setAdapter(mCellPagerAdapter);
		mCellViewPager.setCurrentItem(0);
		
		mPagerSlidingTabStrip = (PagerSlidingTabStrip) mRootLinearLayout.findViewById(R.id.cell_tabs);
		mPagerSlidingTabStrip.setViewPager(mCellViewPager);
		
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

}
