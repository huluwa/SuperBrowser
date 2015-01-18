/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 - 2012 J. Devauchelle and contributors.
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

import java.util.Iterator;

import org.zirco.R;
import org.zirco.controllers.Controller;
import org.zirco.controllers.MainController;
import org.zirco.events.EventConstants;
import org.zirco.events.EventController;
import org.zirco.events.IDownloadEventsListener;
import org.zirco.model.items.DownloadItem;
import org.zirco.providers.BookmarksProviderWrapper;
import org.zirco.providers.BookmarksProviderWrapper.BookmarksSource;
import org.zirco.utils.ApplicationUtils;
import org.zirco.utils.Constants;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebIconDatabase;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * The application main activity.
 */
public class MainActivity extends BaseActivity implements IDownloadEventsListener, OnTouchListener {
	
	public static MainActivity INSTANCE = null;
	
	private static final int CONTEXT_MENU_OPEN = Menu.FIRST + 10;
	private static final int CONTEXT_MENU_OPEN_IN_NEW_TAB = Menu.FIRST + 11;
	private static final int CONTEXT_MENU_DOWNLOAD = Menu.FIRST + 12;
	private static final int CONTEXT_MENU_COPY = Menu.FIRST + 13;
	private static final int CONTEXT_MENU_SEND_MAIL = Menu.FIRST + 14;
	private static final int CONTEXT_MENU_SHARE = Menu.FIRST + 15;
	
	public static final int OPEN_BOOKMARKS_HISTORY_ACTIVITY = 0;
	public static final int OPEN_DOWNLOADS_ACTIVITY = 1;
	public static final int OPEN_FILE_CHOOSER_ACTIVITY = 2;
	
	protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS =
	        new FrameLayout.LayoutParams(
	        ViewGroup.LayoutParams.MATCH_PARENT,
	        ViewGroup.LayoutParams.MATCH_PARENT);
	
	private ViewGroup mTopLayout;
	private ViewGroup mBottomLayout;
	private ViewGroup mContentLayout;
	private ViewGroup mContentUpperLayout;
	
	private MainController mManController;
	
	private ValueCallback<Uri> mUploadMessage;
	private OnSharedPreferenceChangeListener mPreferenceChangeListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);              

        Log.i("chenyg", "onCreate()");
        INSTANCE = this;
        
        Constants.initializeConstantsFromResources(this);
        
        Controller.getInstance().setPreferences(PreferenceManager.getDefaultSharedPreferences(this));       
        
        setContentView(R.layout.main02);                        
        
        EventController.getInstance().addDownloadListener(this); 
        
        buildComponents();
        
        updateBookmarksDatabaseSource();
        
        registerPreferenceChangeListener();
        

//		Intent i = getIntent();
//        if (i.getData() != null) {
//        	// App first launch from another app.
//        	addTab(false);
//        	navigateToUrl(i.getDataString());
//        } else {
//        	// Normal start.
//        	int currentVersionCode = ApplicationUtils.getApplicationVersionCode(this);
//        	int savedVersionCode = PreferenceManager.getDefaultSharedPreferences(this).getInt(Constants.PREFERENCES_LAST_VERSION_CODE, -1);
//        	
//        	// If currentVersionCode and savedVersionCode are different, the application has been updated.
////        	if (currentVersionCode != savedVersionCode) {
////        		// Save current version code.
////        		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
////            	editor.putInt(Constants.PREFERENCES_LAST_VERSION_CODE, currentVersionCode);
////            	editor.commit();
////            	
////            	// Display changelog dialog.
////            	Intent changelogIntent = new Intent(this, ChangelogActivity.class);
////        		startActivity(changelogIntent);
////        	}
//        	
//        	boolean lastPageRestored = false;
//        	if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.PREFERENCES_BROWSER_RESTORE_LAST_PAGE, false)) {
//        		if (savedInstanceState != null) {        		
//        			String savedUrl = savedInstanceState.getString(Constants.EXTRA_SAVED_URL);
//        			if (savedUrl != null) {
//        				addTab(false);
//        				navigateToUrl(savedUrl);
//        				lastPageRestored = true;
//        			}
//        		}
//        	}
//        	
//        	if (!lastPageRestored) {
//        		addTab(true);
//        	}
//        }
        
        initializeWebIconDatabase();
        
    }
    
    
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return super.onCreateView(name, context, attrs);
	}

    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
    	Log.i("chenyg", "onWindowFocusChanged()");
		super.onWindowFocusChanged(hasFocus);
	}

	/**
     * Initialize the Web icons database.
     */
    private void initializeWebIconDatabase() {
        
    	final WebIconDatabase db = WebIconDatabase.getInstance();
    	db.open(getDir("icons", 0).getPath());   
    }

    @Override
	protected void onDestroy() {
		WebIconDatabase.getInstance().close();
		
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.PREFERENCES_PRIVACY_CLEAR_CACHE_ON_EXIT, false)) {
			mManController.getCurrentWebView().clearCache(true);
		}
		
		EventController.getInstance().removeDownloadListener(this);
		
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);

		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
//		outState.putString(Constants.EXTRA_SAVED_URL, mManController.getCurrentWebView().getUrl());
		super.onSaveInstanceState(outState);
	}

	/**
     * Handle url request from external apps.
     * @param intent The intent.
     */
    @Override
	protected void onNewIntent(Intent intent) {
    	Log.i("chenyg", "onNewIntent()");
    	if (intent.getData() != null) {
    		mManController.addTab(false);
    		mManController.navigateToUrl(intent.getDataString());
    	}
		
		setIntent(intent);
		
		super.onNewIntent(intent);
	}        
    
    /**
     * Restart the application.
     */
    public void restartApplication() {
    	PendingIntent intent = PendingIntent.getActivity(this.getBaseContext(), 0, new Intent(getIntent()), getIntent().getFlags());
		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, intent);
		System.exit(2);
    }
    
    /**
     * Create main UI.
     */
	private void buildComponents() {
		
		mTopLayout = (ViewGroup) findViewById(R.id.TopBarLayout);
		mBottomLayout = (ViewGroup) findViewById(R.id.BottomBarLayout);
		mContentLayout = (ViewGroup) findViewById(R.id.ContentLayout);
		mContentUpperLayout = (ViewGroup) findViewById(R.id.ContentUpperLayout);
		
		mManController = new MainController(this);
    }
	
	private void registerPreferenceChangeListener() {
    	mPreferenceChangeListener = new OnSharedPreferenceChangeListener() {			
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if (key.equals(Constants.PREFERENCE_BOOKMARKS_DATABASE)) {
					updateBookmarksDatabaseSource();
				}
			}
		};
		
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
    }
	
    /**
     * Apply preferences to the current UI objects.
     */
    public void applyPreferences() {    	
    	
//    	for (BaseFragment f : mTabPagerAdapter.getFragments()) {
//    		if(f instanceof WebviewFragment) {
//    			((WebviewFragment) f).getCustomWebView().initializeOptions();
//    		}
//    	}
    }
    
    private void updateBookmarksDatabaseSource() {
    	String source = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREFERENCE_BOOKMARKS_DATABASE, "STOCK");
    	
    	if (source.equals("STOCK")) {
    		BookmarksProviderWrapper.setBookmarksSource(BookmarksSource.STOCK);
    	} else if (source.equals("INTERNAL")) {
    		BookmarksProviderWrapper.setBookmarksSource(BookmarksSource.INTERNAL);
    	}
    }
    
    /**
     * Select Text in the webview and automatically sends the selected text to the clipboard.
     */
    public void swithToSelectAndCopyTextMode() {
        try {
         KeyEvent shiftPressEvent = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
         shiftPressEvent.dispatch(mManController.getCurrentWebView());
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
    
    
    

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			this.moveTaskToBack(true);
			return true;
		default: return super.onKeyLongPress(keyCode, event);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
//			if (mCustomView != null) {
//				hideCustomView();
//			} else {
//				if (mCurrentWebView.canGoBack()) {
//					mCurrentWebView.goBack();				
//				} else {
//					this.moveTaskToBack(true);
//				}
//			}
			return true;
		case KeyEvent.KEYCODE_SEARCH:
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			String volumeKeysBehaviour = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREFERENCES_UI_VOLUME_KEYS_BEHAVIOUR, "DEFAULT");
			
			if (volumeKeysBehaviour.equals("DEFAULT")) {
				return super.onKeyUp(keyCode, event);
			} else {
				return true;
			}
		default: return super.onKeyUp(keyCode, event);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		String volumeKeysBehaviour = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREFERENCES_UI_VOLUME_KEYS_BEHAVIOUR, "DEFAULT");
		
		if (!volumeKeysBehaviour.equals("DEFAULT")) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				
//				if (volumeKeysBehaviour.equals("SWITCH_TABS")) {
//					showPreviousTab(false);
//				} else if (volumeKeysBehaviour.equals("SCROLL")) {
//					mCurrentWebView.pageDown(false);
//				} else if (volumeKeysBehaviour.equals("HISTORY")) {
//					mCurrentWebView.goForward();
//				} else {
//					mCurrentWebView.zoomIn();
//				}
				
				return true;
			case KeyEvent.KEYCODE_VOLUME_UP:
				
//				if (volumeKeysBehaviour.equals("SWITCH_TABS")) {
//					showNextTab(false);
//				} else if (volumeKeysBehaviour.equals("SCROLL")) {
//					mCurrentWebView.pageUp(false);
//				} else if (volumeKeysBehaviour.equals("HISTORY")) {
//					mCurrentWebView.goBack();
//				} else {
//					mCurrentWebView.zoomOut();
//				}
				
				return true;
			default: return super.onKeyDown(keyCode, event);
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        if (requestCode == OPEN_BOOKMARKS_HISTORY_ACTIVITY) {
        	if (intent != null) {
        		Bundle b = intent.getExtras();
        		if (b != null) {
        			if (b.getBoolean(Constants.EXTRA_ID_NEW_TAB)) {
        				mManController.addTab(false);
        			}
        			mManController.navigateToUrl(b.getString(Constants.EXTRA_ID_URL));
        		}
        	}
        } else if (requestCode == OPEN_FILE_CHOOSER_ACTIVITY) {
        	if (mUploadMessage == null) {
        		return;
        	}
        	
        	Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		return super.onTouchEvent(event);
	}
	
	/**
	 * Check if the url is in the AdBlock white list.
	 * @param url The url to check
	 * @return true if the url is in the white list
	 */
	private boolean checkInAdBlockWhiteList(String url) {
		
		if (url != null) {
			boolean inList = false;
			Iterator<String> iter = Controller.getInstance().getAdBlockWhiteList(this).iterator();			
			while ((iter.hasNext()) &&
					(!inList)) {
				if (url.contains(iter.next())) {
					inList = true;
				}
			}
			return inList;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		if ((item != null) &&
				(item.getIntent() != null)) {
			Bundle b = item.getIntent().getExtras();

			switch(item.getItemId()) {
			case CONTEXT_MENU_OPEN:
				if (b != null) {
					mManController.navigateToUrl(b.getString(Constants.EXTRA_ID_URL));
				}			
				return true;

			case CONTEXT_MENU_OPEN_IN_NEW_TAB:
				if (b != null) {
					mManController.addTabInsert(false);
					mManController.navigateToUrl(b.getString(Constants.EXTRA_ID_URL));
				}			
				return true;

			case CONTEXT_MENU_DOWNLOAD:
				if (b != null) {
					doDownloadStart(b.getString(Constants.EXTRA_ID_URL), null, null, null, 0);
				}
				return true;
			case CONTEXT_MENU_COPY:
				if (b != null) {
					ApplicationUtils.copyTextToClipboard(this, b.getString(Constants.EXTRA_ID_URL), getString(R.string.Commons_UrlCopyToastMessage));
				}
				return true;
			case CONTEXT_MENU_SHARE:
				if (b != null) {
					ApplicationUtils.sharePage(this, "", b.getString(Constants.EXTRA_ID_URL));
				}
				return true;
			default: return super.onContextItemSelected(item);
			}	
		}
		
		return super.onContextItemSelected(item);
	}

    /**
     * Initiate a download. Check the SD card and start the download runnable.
     * @param url The url to download.
     * @param userAgent The user agent.
     * @param contentDisposition The content disposition.
     * @param mimetype The mime type.
     * @param contentLength The content length.
     */
    private void doDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
    	    
        if (ApplicationUtils.checkCardState(this, true)) {
        	DownloadItem item = new DownloadItem(this, url);
        	Controller.getInstance().addToDownload(item);
        	item.startDownload();

        	Toast.makeText(this, getString(R.string.Main_DownloadStartedMsg), Toast.LENGTH_SHORT).show();
        }
    }
    
	
	@Override
	public void onDownloadEvent(String event, Object data) {
		if (event.equals(EventConstants.EVT_DOWNLOAD_ON_FINISHED)) {
			
			DownloadItem item = (DownloadItem) data;
			
			if (item.getErrorMessage() == null) {
				Toast.makeText(this, getString(R.string.Main_DownloadFinishedMsg), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, getString(R.string.Main_DownloadErrorMsg, item.getErrorMessage()), Toast.LENGTH_SHORT).show();
			}
		}			
	}
	
	public ViewGroup getTopLayout() {
		return mTopLayout;
	}
	
	public ViewGroup getBottomLayout() {
		return mBottomLayout;
	}
	
	public ViewGroup getContentLayout() {
		return mContentLayout;
	}
	
	public ViewGroup getContentUpperLayout() {
		return mContentUpperLayout;
	}
	
	public MainController getController() {
		return mManController;
	}
	
}
