package org.zirco.controllers;

import java.util.List;

import org.zirco.R;
import org.zirco.model.adapters.TabPagerAdapter;
import org.zirco.ui.activities.BookmarksHistoryActivity02;
import org.zirco.ui.activities.DownloadsListActivity;
import org.zirco.ui.activities.EditBookmarkActivity;
import org.zirco.ui.activities.MainActivity02;
import org.zirco.ui.activities.preferences.PreferencesActivity;
import org.zirco.ui.activities.preferences.SettingsActivity;
import org.zirco.ui.components.CustomWebView;
import org.zirco.ui.fragment.BaseFragment;
import org.zirco.ui.fragment.HomeFragment;
import org.zirco.ui.fragment.WebviewFragment;
import org.zirco.ui.menu.NavigatBar;
import org.zirco.ui.menu.NavigatBarCallback;
import org.zirco.ui.menu.ToolBar;
import org.zirco.ui.menu.ToolBarCallback;
import org.zirco.ui.view.NestedViewPager;
import org.zirco.utils.ApplicationUtils;
import org.zirco.utils.Constants;
import org.zirco.utils.LogUtils;
import org.zirco.utils.UrlUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ValueCallback;

public class MainController implements ToolBarCallback, NavigatBarCallback {

	private MainActivity02 mActivity;
	
	private CustomWebView mCurrentWebView;
	private View mCustomView;
	private BaseFragment mCurrentFragment;
	private NestedViewPager mCurrentTabViewPager;
	private TabPagerAdapter mTabPagerAdapter;
	private List<NestedViewPager> mTabViewPagers;
	
	private NavigatBar mNavigatBar;
	private ToolBar mToolBar;
	
	private ValueCallback<Uri> mUploadMessage;
	private OnSharedPreferenceChangeListener mPreferenceChangeListener;
	
	public MainController(MainActivity02 activity) {
		
		this.mActivity = activity;
		
		buildComponents();
		
	}
	
	private void buildComponents() {
		
		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.content_layout, mActivity.getContentLayout(), true);
		inflater.inflate(R.layout.navigatbar_layout, mActivity.getTopLayout(), true);
		inflater.inflate(R.layout.toolbar_layout, mActivity.getBottomLayout(), true);
		
//		mNavigatBar = new NavigatBar(this, mActivity.getWindow().getDecorView().getRootView());
//		mToolBar = new ToolBar(this, mActivity.getWindow().getDecorView().getRootView());
		
		mNavigatBar = new NavigatBar(this, mActivity.getTopLayout());
		mToolBar = new ToolBar(this, mActivity.getBottomLayout());
		mToolBar.setToolBarCallback(this);
		mNavigatBar.setNavigatBarCallback(this);
		
		initViewPagerAndTab();
    }
	
	private void initViewPagerAndTab() {
		mCurrentTabViewPager = (NestedViewPager) mActivity.findViewById(R.id.detailplay_half_detail_viewpager);
		mCurrentTabViewPager.setPagingEnabled(true);
		mTabPagerAdapter = new TabPagerAdapter(mActivity, mActivity.getSupportFragmentManager());
		mCurrentTabViewPager.setAdapter(mTabPagerAdapter);
		mCurrentTabViewPager.setOnPageChangeListener(onPageChangeListener);
		
		addTab(true);
	}
	
	private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			LogUtils.i("onPageChangeListener.onPageSelected(), Position index=" + arg0);
			mTabPagerAdapter.setCurrentPosition(arg0);
			mCurrentFragment = (BaseFragment) mTabPagerAdapter.getItem(mTabPagerAdapter.getCurrentPosition());
			if(mCurrentFragment.getType() == BaseFragment.TYPE_WEB_FRAGMENT) {
				mCurrentWebView = ((WebviewFragment) mCurrentFragment).getCustomWebView();
			}
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	};
	
	public FragmentActivity getActivity() {
		return mActivity;
	}
	
	public CustomWebView getCurrentWebView() {
		return mCurrentWebView;
	}
	
	/**
     * Add a new tab at the end
     * @param navigateToHome If True, will load the user home page.
     */
    public void addTab(boolean navigateToHome) {
    	addTab(navigateToHome, -1);
    }
    
    /**
     * Add a new tab behind the current tab
     * @param navigateToHome If True, will load the user home page.
     */
    public void addTabInsert(boolean navigateToHome) {
    	addTab(navigateToHome, mTabPagerAdapter.getCurrentPosition());
    }
    
    /**
     * Add a new tab.
     * @param navigateToHome If True, will load the user home page.
     * @param parentIndex The index of the new tab.
     */
    public void addTab(boolean navigateToHome, int parentIndex) {
    	Log.i("chenyg", "MainActivity->addTab()");

    	if(navigateToHome) {
    		mCurrentFragment = new HomeFragment();
//    		mCurrentFragment = new WebviewFragment(mActivity);
    	} else {
    		mCurrentFragment = new WebviewFragment();
    	}
//    	mCurrentWebView = Fragment.getCustomWebView();
		
    	synchronized (mCurrentTabViewPager) {
    		int index = mTabPagerAdapter.addView(mCurrentFragment);
    		mTabPagerAdapter.notifyDataSetChanged();
    		mCurrentTabViewPager.setCurrentItem(index);
    		Log.i("chenyg", "setCurrentItem(" + index + ")");
    	}
    	
    	updateUI();
    	
//    	if (navigateToHome) {
//    		navigateToHome();
//    	}
    }
    
    /**
     * Remove the current tab.
     */
    private void removeCurrentTab() {
    	
    	synchronized (mCurrentTabViewPager) {
    		int index = mTabPagerAdapter.removeView(mCurrentTabViewPager, mTabPagerAdapter.getCurrentPosition());
			mTabPagerAdapter.notifyDataSetChanged();
			mCurrentTabViewPager.setCurrentItem(index - 1);
			Log.i("chenyg", "setCurrentItem(" + (index - 1) + ")");
    	}
    	
    	updateUI();
    	
    }
    
    
    
    /**
     * Change the tool bars visibility.
     * @param visible If True, the tool bars will be shown.
     */
    private void setToolbarsVisibility(boolean visible) {
    	    	
//    	if (visible) {
//    		
//    		if (!mUrlBarVisible) {    			
//    			mTopBar.startAnimation(AnimationManager.getInstance().getTopBarShowAnimation());
//    			mBottomBar.startAnimation(AnimationManager.getInstance().getBottomBarShowAnimation());
//    			
//    			mTopBar.setVisibility(View.VISIBLE);
//    			mBottomBar.setVisibility(View.VISIBLE);
//
//    		}
//    		
//    		startToolbarsHideRunnable();
//    		
//    		mUrlBarVisible = true;    		    		
//    		
//    	} else {  	
//    		
//    		if (mUrlBarVisible) {
//    			mTopBar.startAnimation(AnimationManager.getInstance().getTopBarHideAnimation());
//    			mBottomBar.startAnimation(AnimationManager.getInstance().getBottomBarHideAnimation());    			    			
//    			
//    			mTopBar.setVisibility(View.GONE);
//    			mBottomBar.setVisibility(View.GONE);
//    			
//    		}
//			
//			mUrlBarVisible = false;
//    	}
    }
    
    /**
     * Navigate to the given url.
     * @param url The url.
     */
    public void navigateToUrl(String url) {
    	// Needed to hide toolbars properly.
    	mNavigatBar.clearFocus();
    	
    	if ((url != null) &&
    			(url.length() > 0)) {
    	
    		if (UrlUtils.isUrl(url)) {
    			url = UrlUtils.checkUrl(url);
    		} else {
    			url = UrlUtils.getSearchUrl(mActivity, url);
    		}    		    	
    		
    		if (url.equals(Constants.URL_ABOUT_START)) {
    			
    			mCurrentWebView.loadDataWithBaseURL("file:///android_asset/startpage/",
    					ApplicationUtils.getStartPage(mActivity), "text/html", "UTF-8", Constants.URL_ABOUT_START);
    			
    		} else {
    			
    			// If the url is not from GWT mobile view, and is in the mobile view url list, then load it with GWT.
    			if ((!url.startsWith(Constants.URL_GOOGLE_MOBILE_VIEW_NO_FORMAT)) &&
    					(UrlUtils.checkInMobileViewUrlList(mActivity, url))) {
    				
    				url = String.format(Constants.URL_GOOGLE_MOBILE_VIEW, url);    				
    			}
    			
    			mCurrentWebView.loadUrl(url);
    		}
    	}
    }        
    
    /**
     * Navigate to the user home page.
     */
    private void navigateToHome() {
    	navigateToUrl(Controller.getInstance().getPreferences().getString(Constants.PREFERENCES_GENERAL_HOME_PAGE,
    			Constants.URL_ABOUT_START));
    }
    
    /**
	 * Show a toast alert on tab switch.
	 */
	private void showToastOnTabSwitch() {
		if (Controller.getInstance().getPreferences().getBoolean(Constants.PREFERENCES_SHOW_TOAST_ON_TAB_SWITCH, true)) {
//			String text;
//			if (mCurrentWebView.getTitle() != null) {
//				text = String.format(getString(R.string.Main_ToastTabSwitchFullMessage), mViewFlipper.getDisplayedChild() + 1, mCurrentWebView.getTitle());
//			} else {
//				text = String.format(getString(R.string.Main_ToastTabSwitchMessage), mViewFlipper.getDisplayedChild() + 1);
//			}
//			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		}		
	}
	
	/**
	 * Show the previous tab, if any.
	 */
	private void showPreviousTab() {
		
		if (mCurrentTabViewPager.getChildCount() > 1) {
			
			mCurrentWebView.doOnPause();
			
			mCurrentTabViewPager.setCurrentItem(mTabPagerAdapter.getCurrentPosition() -1);

//			mCurrentWebView = mWebViews.get(mViewFlipper.getDisplayedChild());
			mCurrentWebView = ((WebviewFragment)mTabPagerAdapter.getItem(mTabPagerAdapter.getCurrentPosition())).getCustomWebView();
			mCurrentWebView.doOnResume();
			
			showToastOnTabSwitch();
			

			updateUI();
		}
	}
	
	/**
	 * Show the next tab, if any.
	 */
	private void showNextTab() {
		
		if (mCurrentTabViewPager.getChildCount() > 1) {
			
			mCurrentWebView.doOnPause();
			
			mCurrentTabViewPager.setCurrentItem(mTabPagerAdapter.getCurrentPosition() + 1);

//			mCurrentWebView = mWebViews.get(mViewFlipper.getDisplayedChild());
			mCurrentWebView = ((WebviewFragment)mTabPagerAdapter.getItem(mTabPagerAdapter.getCurrentPosition())).getCustomWebView();
			mCurrentWebView.doOnResume();

			showToastOnTabSwitch();
			
			updateUI();
		}
	}
	
	/**
	 * Update the UI: Url edit text, previous/next button state,...
	 */
	private void updateUI() {
			
//			updateTitle();
//			
//			mToolBar.updateUI();
//			mNavigatBar.updateUI();
	}
	
	/**
	 * Set the application title to default.
	 */
	private void clearTitle() {
		mActivity.setTitle(mActivity.getResources().getString(R.string.ApplicationName));
    }
	
	/**
	 * Update the application title.
	 */
	private void updateTitle() {
		String value = mCurrentWebView.getTitle();
    	
    	if ((value != null) &&
    			(value.length() > 0)) {    	
    		mActivity.setTitle(String.format(mActivity.getResources().getString(R.string.ApplicationNameUrl), value));    		
    	} else {
    		clearTitle();
    	}
	}
	
	/**
	 * Open the "Add bookmark" dialog.
	 */
	private void openAddBookmarkDialog() {
		Intent i = new Intent(mActivity, EditBookmarkActivity.class);
		
		i.putExtra(Constants.EXTRA_ID_BOOKMARK_ID, (long) -1);
		i.putExtra(Constants.EXTRA_ID_BOOKMARK_TITLE, getCurrentWebView().getTitle());
		i.putExtra(Constants.EXTRA_ID_BOOKMARK_URL, getCurrentWebView().getUrl());
		
		mActivity.startActivity(i);
	}
	
	/**
	 * Open the bookmark list.
	 */
	private void openBookmarksHistoryActivity() {
//    	Intent i = new Intent(mActivity, BookmarksHistoryActivity.class);
		Intent i = new Intent(mActivity, BookmarksHistoryActivity02.class);
    	mActivity.startActivityForResult(i, MainActivity02.OPEN_BOOKMARKS_HISTORY_ACTIVITY);
    }
	
	/**
	 * Open the download list.
	 */
	private void openDownloadsList() {
		Intent i = new Intent(mActivity, DownloadsListActivity.class);
		mActivity.startActivityForResult(i, MainActivity02.OPEN_DOWNLOADS_ACTIVITY);
	}
	
	/**
	 * Open preferences.
	 */
	private void openPreferences() {
		Intent preferencesActivity = new Intent(mActivity, PreferencesActivity.class);
		mActivity.startActivity(preferencesActivity);
	}

	//--------------------------ToolBarCallback  bengin-----------------------------
	
	@Override
	public void navigatePrevious() {
    	// Needed to hide toolbars properly.
    	mNavigatBar.clearFocus();
    	
    	int index = mCurrentTabViewPager.getCurrentItem();
    	mCurrentTabViewPager.setCurrentItem(--index);
	}

	@Override
	public void navigateNext() {
    	// Needed to hide toolbars properly.
    	mNavigatBar.clearFocus();
    	
    	int index = mCurrentTabViewPager.getCurrentItem();
    	mCurrentTabViewPager.setCurrentItem(++index);
	}

	@Override
	public void onQuickButton() {
		addTab(true);
	}

	@Override
	public void onCreateTabPage() {
		addTab(true);
	}

	@Override
	public void onRemoveTabPage() {
		removeCurrentTab();
	}

	@Override
	public void onOpenAddBookmarkDialog() {
		openAddBookmarkDialog();
	}

	@Override
	public void onOpenBookmarksHistoryActivity() {
		openBookmarksHistoryActivity();
	}

	@Override
	public void onOpenDownloadsList() {
		openDownloadsList();
	}

	@Override
	public void onOpenPreferences() {
		openPreferences();
	}

	@Override
	public void onQuit() {
		Intent i = new Intent(mActivity, SettingsActivity.class);
		mActivity.startActivity(i);
//		mActivity.finish();
//		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	//--------------------------ToolBarCallback  end-----------------------------
	
	
	//--------------------------NavigatBarCallback  begin-----------------------------

	@Override
	public void onFindPrevious(String target) {
		mCurrentWebView.findNext(false);
	}

	@Override
	public void onFindNext(String target) {
		mCurrentWebView.findNext(true);
	}

	@Override
	public void onNavigateToHome() {
		navigateToHome();
	}

	@Override
	public void onNavigateToUrl(String url) {
		addTab(false);
		navigateToUrl(url);
	}

	@Override
	public void onSharePage() {
		ApplicationUtils.sharePage(mActivity, mCurrentWebView.getTitle(), mCurrentWebView.getUrl());
	}
	
	//--------------------------NavigatBarCallback  end-----------------------------
	
	
//	public void onPageFinished(String url) {
//	updateUI();			
//	
//	if ((Controller.getInstance().getPreferences().getBoolean(Constants.PREFERENCES_ADBLOCKER_ENABLE, true)) &&
//			(!checkInAdBlockWhiteList(mCurrentWebView.getUrl()))) {
//		mCurrentWebView.loadAdSweep();
//	}
//	WebIconDatabase.getInstance().retainIconForPageUrl(mCurrentWebView.getUrl());
//	
//	if (mUrlBarVisible) {
//		startToolbarsHideRunnable();
//	}
//}
//
//public void onPageStarted(String url) {
//	if (mFindDialogVisible) {
//		closeFindDialog();
//	}
//	
//	mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
//	mUrlEditText.setText(url);
//	mUrlEditText.addTextChangedListener(mUrlTextWatcher);
//	
//	mPreviousButton.setEnabled(false);
//	mNextButton.setEnabled(false);
//	
//	updateGoButton();
//	
//	setToolbarsVisibility(true);
//}
//
//public void onUrlLoading(String url) {
//	setToolbarsVisibility(true);
//}
}
