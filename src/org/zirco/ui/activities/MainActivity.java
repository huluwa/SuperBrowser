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
import java.util.List;

import org.greendroid.QuickAction;
import org.greendroid.QuickActionGrid;
import org.greendroid.QuickActionWidget;
import org.greendroid.QuickActionWidget.OnQuickActionClickListener;
import org.zirco.R;
import org.zirco.controllers.Controller;
import org.zirco.events.EventConstants;
import org.zirco.events.EventController;
import org.zirco.events.IDownloadEventsListener;
import org.zirco.model.adapters.TabPagerAdapter;
import org.zirco.model.adapters.UrlSuggestionCursorAdapter;
import org.zirco.model.items.DownloadItem;
import org.zirco.providers.BookmarksProviderWrapper;
import org.zirco.providers.BookmarksProviderWrapper.BookmarksSource;
import org.zirco.ui.activities.preferences.PreferencesActivity;
import org.zirco.ui.components.CustomWebView;
import org.zirco.ui.components.CustomWebViewClientCallback;
import org.zirco.ui.fragment.BaseFragment;
import org.zirco.ui.fragment.HomeFragment;
import org.zirco.ui.fragment.WebviewFragment;
import org.zirco.ui.runnables.HideToolbarsRunnable;
import org.zirco.ui.view.NestedViewPager;
import org.zirco.utils.AnimationManager;
import org.zirco.utils.ApplicationUtils;
import org.zirco.utils.Constants;
import org.zirco.utils.UrlUtils;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebIconDatabase;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.Toast;

/**
 * The application main activity.
 */
public class MainActivity extends FragmentActivity implements IToolbarsContainer, OnTouchListener, IDownloadEventsListener {
	
	public static MainActivity INSTANCE = null;
	
	private static final int MENU_ADD_BOOKMARK = Menu.FIRST;
	private static final int MENU_SHOW_BOOKMARKS = Menu.FIRST + 1;
	private static final int MENU_SHOW_DOWNLOADS = Menu.FIRST + 2;	
	private static final int MENU_PREFERENCES = Menu.FIRST + 3;
	private static final int MENU_EXIT = Menu.FIRST + 4;	
	
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
	
	protected LayoutInflater mInflater = null;
	
	private LinearLayout mTopBar;
	private LinearLayout mBottomBar;
	private LinearLayout mFindBar;
	//查找框
	private ImageButton mFindPreviousButton;
	private ImageButton mFindNextButton;
	private ImageButton mFindCloseButton;
	private EditText mFindText;
	//导航框
	private ImageButton mToolsButton;
	private AutoCompleteTextView mUrlEditText;
	private ImageButton mGoButton;	
	private ProgressBar mProgressBar;
	private Drawable mCircularProgress;
	private TextWatcher mUrlTextWatcher;
	private QuickActionGrid mToolsActionGrid;
	// 下方工具栏
	private ImageButton mPreviousButton;
	private ImageButton mNextButton;
	private ImageButton mRemoveButton;
	private ImageButton mNewTabButton;
	private ImageButton mMenuButton;
	private ImageButton mQuickButton;
	
	private CustomWebView mCurrentWebView;
	private View mCustomView;
	private BaseFragment mCurrentFragment;
	private NestedViewPager mCurrentTabViewPager;
	private TabPagerAdapter mTabPagerAdapter;
	private List<NestedViewPager> mTabViewPagers;
	
	private boolean mUrlBarVisible;
	private boolean mToolsActionGridVisible = false;
	private boolean mFindDialogVisible = false;
	

	private HideToolbarsRunnable mHideToolbarsRunnable;
//	private GestureDetector mGestureDetector;
	
	private ValueCallback<Uri> mUploadMessage;
	
	private OnSharedPreferenceChangeListener mPreferenceChangeListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);              

        Log.i("chenyg", "onCreate()");
        INSTANCE = this;
        
        Constants.initializeConstantsFromResources(this);
        
        Controller.getInstance().setPreferences(PreferenceManager.getDefaultSharedPreferences(this));       
        
        if (Controller.getInstance().getPreferences().getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN, false)) {        	
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        
        if (Controller.getInstance().getPreferences().getBoolean(Constants.PREFERENCES_GENERAL_HIDE_TITLE_BARS, true)) {
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        setProgressBarVisibility(true);
        
        setContentView(R.layout.main);                        
        
        mCircularProgress = getResources().getDrawable(R.drawable.spinner);
        
        EventController.getInstance().addDownloadListener(this);                
                
        mHideToolbarsRunnable = null;
        
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
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
        
        startToolbarsHideRunnable();
        
    }
    
    
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return super.onCreateView(name, context, attrs);
	}

    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
    	Log.i("chenyg", "onWindowFocusChanged()");
    	addTab(true);
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
			mCurrentWebView.clearCache(true);
		}
		
		EventController.getInstance().removeDownloadListener(this);
		
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);

		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(Constants.EXTRA_SAVED_URL, mCurrentWebView.getUrl());
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
    		addTab(false);
    		navigateToUrl(intent.getDataString());
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
		
		mToolsActionGrid = new QuickActionGrid(this);
		mToolsActionGrid.addQuickAction(new QuickAction(this, R.drawable.ic_btn_share, R.string.QuickAction_Share));
		mToolsActionGrid.addQuickAction(new QuickAction(this, R.drawable.ic_btn_find, R.string.QuickAction_Find));
		mToolsActionGrid.addQuickAction(new QuickAction(this, R.drawable.ic_btn_select, R.string.QuickAction_SelectText));
		mToolsActionGrid.addQuickAction(new QuickAction(this, R.drawable.ic_btn_mobile_view, R.string.QuickAction_MobileView));
				
		mToolsActionGrid.setOnQuickActionClickListener(new OnQuickActionClickListener() {			
			@Override
			public void onQuickActionClicked(QuickActionWidget widget, int position) {
				switch (position) {
				case 0:
					navigateToHome();
					break;
				case 1: //分享
					ApplicationUtils.sharePage(MainActivity.this, mCurrentWebView.getTitle(), mCurrentWebView.getUrl());
					break;
				case 2:					
					// Somewhat dirty hack: when the find dialog was shown from a QuickAction,
					// the soft keyboard did not show... Hack is to wait a little before showing
					// the file dialog through a thread.
					startShowFindDialogRunnable();
					break;
				case 3:
					swithToSelectAndCopyTextMode();
					break;
				case 4:
					String currentUrl = mUrlEditText.getText().toString();
		    		
		    		// Do not reload mobile view if already on it.
		    		if (!currentUrl.startsWith(Constants.URL_GOOGLE_MOBILE_VIEW_NO_FORMAT)) {
		    			String url = String.format(Constants.URL_GOOGLE_MOBILE_VIEW, mUrlEditText.getText().toString());
		    			navigateToUrl(url);
		    		}
		    		break;				
				}
			}
		});
				
		mToolsActionGrid.setOnDismissListener(new PopupWindow.OnDismissListener() {			
			@Override
			public void onDismiss() {
				mToolsActionGridVisible = false;
				startToolbarsHideRunnable();
			}
		});
		
//		mGestureDetector = new GestureDetector(this, new GestureListener());
    	
    	mUrlBarVisible = true;
    	
//    	mWebViews = new ArrayList<CustomWebView>();
//    	Controller.getInstance().setWebViewList(mWebViews);
//    	mWebviewFragments = new ArrayList<WebviewFragment>();
    	
    	mTopBar = (LinearLayout) findViewById(R.id.BarLayout);    	
    	mTopBar.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// Dummy event to steel it from the WebView, in case of clicking between the buttons.				
			}
		});
    	
    	mBottomBar = (LinearLayout) findViewById(R.id.BottomBarLayout);    	
    	mBottomBar.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// Dummy event to steel it from the WebView, in case of clicking between the buttons.				
			}
		});
    	
    	mFindBar = (LinearLayout) findViewById(R.id.findControls);
    	mFindBar.setVisibility(View.GONE);
    	
    	String[] from = new String[] {UrlSuggestionCursorAdapter.URL_SUGGESTION_TITLE, UrlSuggestionCursorAdapter.URL_SUGGESTION_URL};
    	int[] to = new int[] {R.id.AutocompleteTitle, R.id.AutocompleteUrl};
    	
    	UrlSuggestionCursorAdapter adapter = new UrlSuggestionCursorAdapter(this, R.layout.url_autocomplete_line, null, from, to);
    	      	
    	adapter.setCursorToStringConverter(new CursorToStringConverter() {			
			@Override
			public CharSequence convertToString(Cursor cursor) {
				String aColumnString = cursor.getString(cursor.getColumnIndex(UrlSuggestionCursorAdapter.URL_SUGGESTION_URL));
                return aColumnString;
			}
		});
    	
    	adapter.setFilterQueryProvider(new FilterQueryProvider() {		
			@Override
			public Cursor runQuery(CharSequence constraint) {
				if ((constraint != null) &&
						(constraint.length() > 0)) {
					return BookmarksProviderWrapper.getUrlSuggestions(getContentResolver(),
							constraint.toString(),
							PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean(Constants.PREFERENCE_USE_WEAVE, false));
				} else {
					return BookmarksProviderWrapper.getUrlSuggestions(getContentResolver(),
							null,
							PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean(Constants.PREFERENCE_USE_WEAVE, false));
				}
			}
		});
    	
    	
    	mUrlEditText = (AutoCompleteTextView) findViewById(R.id.UrlText);
    	mUrlEditText.setThreshold(1);
    	mUrlEditText.setAdapter(adapter);    
    	
    	mUrlEditText.setOnKeyListener(new View.OnKeyListener() {
    		
    		@Override
    		public boolean onKey(View v, int keyCode, KeyEvent event) {												
    			if (keyCode == KeyEvent.KEYCODE_ENTER) {
    				navigateToUrl();
    				return true;
    			}
    			
    			return false;
    		}
    		
    	});

    	mUrlTextWatcher = new TextWatcher() {			
    		@Override
    		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

    		@Override
    		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

    		@Override
    		public void afterTextChanged(Editable arg0) {
    			updateGoButton();
    		}
    	};
    	
    	mUrlEditText.addTextChangedListener(mUrlTextWatcher);
    	
    	mUrlEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

    		@Override
    		public void onFocusChange(View v, boolean hasFocus) {
    			// Select all when focus gained.
    			if (hasFocus) {
    				mUrlEditText.setSelection(0, mUrlEditText.getText().length());
    			}
    		}
    	});    	
    	
    	mUrlEditText.setCompoundDrawablePadding(5);
    	    	
    	mGoButton = (ImageButton) findViewById(R.id.GoBtn);    	
    	mGoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	
            	if (mCurrentWebView.isLoading()) {
            		mCurrentWebView.stopLoading();
            	} else if (!mCurrentWebView.isSameUrl(mUrlEditText.getText().toString())) {
            		navigateToUrl();
            	} else {
            		mCurrentWebView.reload();
            	}
            }          
        });
    	
    	mToolsButton = (ImageButton) findViewById(R.id.ToolsBtn);
    	mToolsButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				mToolsActionGridVisible = true;
				mToolsActionGrid.show(v);				
			}
		});
    	
    	mProgressBar = (ProgressBar) findViewById(R.id.WebViewProgress);
    	mProgressBar.setMax(100);
    	
    	mPreviousButton = (ImageButton) findViewById(R.id.PreviousBtn);
    	mNextButton = (ImageButton) findViewById(R.id.NextBtn);
    	
    	mPreviousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	navigatePrevious();
            }          
        });
		
		mNextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	navigateNext();
            }          
        });
    	
		mNewTabButton = (ImageButton) findViewById(R.id.NewTabBtn);
		mNewTabButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//            	addTab(true);
            	Log.i("chenyg", "mTabPagerAdapter.getCurrentPosition()=" + mTabPagerAdapter.getCurrentPosition());
            	addTab(false, mTabPagerAdapter.getCurrentPosition());
            }          
        });
		
		mMenuButton = (ImageButton) findViewById(R.id.MenuBtn);
		mMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	MainActivity.this.findViewById(R.id.menu_layout).setVisibility(View.VISIBLE);
            }
        });
		
//		mRemoveButton = (ImageButton) findViewById(R.id.RemoveBtn);
//		mRemoveButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//            	if (mCurrentTabViewPager.getChildCount() == 1 && !mCurrentWebView.getUrl().equals(Constants.URL_ABOUT_START)) {
//            		navigateToHome();
//                	updateUI();
//            	} else {
//            		removeCurrentTab();
//            	}
//            }          
//        });
		
		mQuickButton = (ImageButton) findViewById(R.id.QuickBtn);
		mQuickButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {            	
            	onQuickButton();
            }          
        });
		
		mFindPreviousButton = (ImageButton) findViewById(R.id.find_previous);
		mFindPreviousButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCurrentWebView.findNext(false);
				hideKeyboardFromFindDialog();
			}
		});
		
		mFindNextButton = (ImageButton) findViewById(R.id.find_next);
		mFindNextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCurrentWebView.findNext(true);
				hideKeyboardFromFindDialog();
			}
		});
		
		mFindCloseButton = (ImageButton) findViewById(R.id.find_close);
		mFindCloseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeFindDialog();
			}
		});
		
		mFindText = (EditText) findViewById(R.id.find_value);
		mFindText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				doFind();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) { }
		});
		
		initViewPagerAndTab();

    }
	
	private void initViewPagerAndTab() {
		mCurrentTabViewPager = (NestedViewPager) this.findViewById(R.id.detailplay_half_detail_viewpager);
		mCurrentTabViewPager.setPagingEnabled(true);
		mTabPagerAdapter = new TabPagerAdapter(this, this.getSupportFragmentManager());
		mCurrentTabViewPager.setAdapter(mTabPagerAdapter);
		mCurrentTabViewPager.setOnPageChangeListener(onPageChangeListener);
	}
	
	private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			Log.i("chenyg", "onPageChangeListener.onPageSelected(), Position index=" + arg0);
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
    	// To update to Bubble position.
    	setToolbarsVisibility(false);
    	
//    	for (CustomWebView view : mWebViews) {
//    		view.initializeOptions();
//    	}
    	
    	for (BaseFragment f : mTabPagerAdapter.getFragments()) {
    		if(f instanceof WebviewFragment) {
    			((WebviewFragment) f).getCustomWebView().initializeOptions();
    		}
    	}
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
         shiftPressEvent.dispatch(mCurrentWebView);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
    
    
    /**
     * Add a new tab.
     * @param navigateToHome If True, will load the user home page.
     */
    private void addTab(boolean navigateToHome) {
    	addTab(navigateToHome, -1);
    }
    
    /**
     * Add a new tab.
     * @param navigateToHome If True, will load the user home page.
     * @param parentIndex The index of the new tab.
     */
    private void addTab(boolean navigateToHome, int parentIndex) {
    	Log.i("chenyg", "MainActivity->addTab()");
    	if (mFindDialogVisible) {
    		closeFindDialog();
    	}
    	
//    	RelativeLayout view = (RelativeLayout) mInflater.inflate(R.layout.webview, mViewFlipper, false);
//    	mCurrentWebView = (CustomWebView) view.findViewById(R.id.webview);
//    	initializeCurrentWebView();
    	if(navigateToHome) {
    		mCurrentFragment = new HomeFragment();
//    		mCurrentFragment = new WebviewFragment(this);
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
    	
    	mUrlEditText.clearFocus();
    	
//    	if (navigateToHome) {
//    		navigateToHome();
//    	}
    }
    
    /**
     * Remove the current tab.
     */
    private void removeCurrentTab() {
    	
    	if (mFindDialogVisible) {
    		closeFindDialog();
    	}
    	
    	synchronized (mCurrentTabViewPager) {
    		int index = mTabPagerAdapter.removeView(mCurrentTabViewPager, mTabPagerAdapter.getCurrentPosition());
			mTabPagerAdapter.notifyDataSetChanged();
			mCurrentTabViewPager.setCurrentItem(index - 1);
			Log.i("chenyg", "setCurrentItem(" + (index - 1) + ")");
    	}
    	
    	updateUI();
    	
    	mUrlEditText.clearFocus();
    }
    
    private void doFind() {
		CharSequence find = mFindText.getText();
		if (find.length() == 0) {
			mFindPreviousButton.setEnabled(false);
			mFindNextButton.setEnabled(false);
			mCurrentWebView.clearMatches();
		} else {
			int found = mCurrentWebView.findAll(find.toString());
			if (found < 2) {
				mFindPreviousButton.setEnabled(false);
				mFindNextButton.setEnabled(false);
			} else {
				mFindPreviousButton.setEnabled(true);
				mFindNextButton.setEnabled(true);
			}
		}
	}
	
	private void showFindDialog() {
		setFindBarVisibility(true);
		mCurrentWebView.doSetFindIsUp(true);
		CharSequence text = mFindText.getText();
		if (text.length() > 0) {
			mFindText.setSelection(0, text.length());
			doFind();
		} else {
			mFindPreviousButton.setEnabled(false);
			mFindNextButton.setEnabled(false);
		}
				
		mFindText.requestFocus();
		showKeyboardForFindDialog();		
	}
	
	private void closeFindDialog() {
		hideKeyboardFromFindDialog();
		mCurrentWebView.doNotifyFindDialogDismissed();
		setFindBarVisibility(false);
	}
    
    private void setFindBarVisibility(boolean visible) {
    	if (visible) {
    		mFindBar.startAnimation(AnimationManager.getInstance().getTopBarShowAnimation());
    		mFindBar.setVisibility(View.VISIBLE);    		
    		mFindDialogVisible = true;
    	} else {
    		mFindBar.startAnimation(AnimationManager.getInstance().getTopBarHideAnimation());
    		mFindBar.setVisibility(View.GONE);
    		mFindDialogVisible = false;
    	}
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
    
    private void showKeyboardForFindDialog() {
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.showSoftInput(mFindText, InputMethodManager.SHOW_IMPLICIT);
    }
    
    private void hideKeyboardFromFindDialog() {
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(mFindText.getWindowToken(), 0);
    }
    
    /**
     * Hide the keyboard.
     * @param delayedHideToolbars If True, will start a runnable to delay tool bars hiding. If False, tool bars are hidden immediatly.
     */
    private void hideKeyboard(boolean delayedHideToolbars) {
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(mUrlEditText.getWindowToken(), 0);
    	
    	if (mUrlBarVisible) {
    		if (delayedHideToolbars) {
    			startToolbarsHideRunnable();
    		} else {
    			setToolbarsVisibility(false);
    		}
    	}
    }
    
    /**
     * Thread to delay the show of the find dialog. This seems to be necessary when shown from
     * a QuickAction. If not, the keyboard does not show. 50ms seems to be enough on
     * a Nexus One and on the (rather) slow emulator. Dirty hack :(
     */
    private void startShowFindDialogRunnable() {
    	new Thread(new Runnable() {
			
    		private Handler mHandler = new Handler() {
    			public void handleMessage(Message msg) {
    				showFindDialog();
    			}
    		};
    		
			@Override
			public void run() {
				try {
					Thread.sleep(50);
					mHandler.sendEmptyMessage(0);					
				} catch (InterruptedException e) {
					mHandler.sendEmptyMessage(0);
				}
				
			}
		}).start();
    }
    
    /**
     * Start a runnable to hide the tool bars after a user-defined delay.
     */
    private void startToolbarsHideRunnable() {
    	    	    	
    	if (mHideToolbarsRunnable != null) {
    		mHideToolbarsRunnable.setDisabled();
    	}
    	
    	int delay = Integer.parseInt(Controller.getInstance().getPreferences().getString(Constants.PREFERENCES_GENERAL_BARS_DURATION, "3000"));
    	if (delay <= 0) {
    		delay = 3000;
    	}
    	
    	mHideToolbarsRunnable = new HideToolbarsRunnable(this, delay);    	
    	new Thread(mHideToolbarsRunnable).start();
    }
    
    /**
	 * Hide the tool bars.
	 */
	public void hideToolbars() {
		if (mUrlBarVisible) {			
			if ((!mUrlEditText.hasFocus()) &&
					(!mToolsActionGridVisible)) {
				
				if (!mCurrentWebView.isLoading()) {
					setToolbarsVisibility(false);
				}
			}
		}
		mHideToolbarsRunnable = null;
	}
    
    /**
     * Navigate to the given url.
     * @param url The url.
     */
    private void navigateToUrl(String url) {
    	// Needed to hide toolbars properly.
    	mUrlEditText.clearFocus();
    	
    	if ((url != null) &&
    			(url.length() > 0)) {
    	
    		if (UrlUtils.isUrl(url)) {
    			url = UrlUtils.checkUrl(url);
    		} else {
    			url = UrlUtils.getSearchUrl(this, url);
    		}    		    	
    		
    		hideKeyboard(true);
    		
    		if (url.equals(Constants.URL_ABOUT_START)) {
    			
    			mCurrentWebView.loadDataWithBaseURL("file:///android_asset/startpage/",
    					ApplicationUtils.getStartPage(this), "text/html", "UTF-8", Constants.URL_ABOUT_START);
    			
    		} else {
    			
    			// If the url is not from GWT mobile view, and is in the mobile view url list, then load it with GWT.
    			if ((!url.startsWith(Constants.URL_GOOGLE_MOBILE_VIEW_NO_FORMAT)) &&
    					(UrlUtils.checkInMobileViewUrlList(this, url))) {
    				
    				url = String.format(Constants.URL_GOOGLE_MOBILE_VIEW, url);    				
    			}
    			
    			mCurrentWebView.loadUrl(url);
    		}
    	}
    }        
    
    /**
     * Navigate to the url given in the url edit text.
     */
    private void navigateToUrl() {
    	navigateToUrl(mUrlEditText.getText().toString());    	
    }
    
    /**
     * Navigate to the user home page.
     */
    private void navigateToHome() {
    	navigateToUrl(Controller.getInstance().getPreferences().getString(Constants.PREFERENCES_GENERAL_HOME_PAGE,
    			Constants.URL_ABOUT_START));
    }
    
    /**
     * Navigate to the previous page in history.
     */
    private void navigatePrevious() {
    	// Needed to hide toolbars properly.
    	mUrlEditText.clearFocus();
    	
    	hideKeyboard(true);
    	int index = mCurrentTabViewPager.getCurrentItem();
    	mCurrentTabViewPager.setCurrentItem(--index);
    }
    /**
     * Navigate to the next page in history. 
     */
    private void navigateNext() {
    	// Needed to hide toolbars properly.
    	mUrlEditText.clearFocus();
    	
    	hideKeyboard(true);
    	int index = mCurrentTabViewPager.getCurrentItem();
    	mCurrentTabViewPager.setCurrentItem(++index);
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
			if (mCustomView != null) {
//				hideCustomView();
			} else if (mFindDialogVisible) {
				closeFindDialog();
			} else {
				if (mCurrentWebView.canGoBack()) {
					mCurrentWebView.goBack();				
				} else {
					this.moveTaskToBack(true);
				}
			}
			return true;
		case KeyEvent.KEYCODE_SEARCH:
			if (!mFindDialogVisible) {
				showFindDialog();
			}
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
				
				if (volumeKeysBehaviour.equals("SWITCH_TABS")) {
					showPreviousTab(false);
				} else if (volumeKeysBehaviour.equals("SCROLL")) {
					mCurrentWebView.pageDown(false);
				} else if (volumeKeysBehaviour.equals("HISTORY")) {
					mCurrentWebView.goForward();
				} else {
					mCurrentWebView.zoomIn();
				}
				
				return true;
			case KeyEvent.KEYCODE_VOLUME_UP:
				
				if (volumeKeysBehaviour.equals("SWITCH_TABS")) {
					showNextTab(false);
				} else if (volumeKeysBehaviour.equals("SCROLL")) {
					mCurrentWebView.pageUp(false);
				} else if (volumeKeysBehaviour.equals("HISTORY")) {
					mCurrentWebView.goBack();
				} else {
					mCurrentWebView.zoomOut();
				}
				
				return true;
			default: return super.onKeyDown(keyCode, event);
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * Set the application title to default.
	 */
	private void clearTitle() {
		this.setTitle(getResources().getString(R.string.ApplicationName));
    }
	
	/**
	 * Update the application title.
	 */
	private void updateTitle() {
		String value = mCurrentWebView.getTitle();
    	
    	if ((value != null) &&
    			(value.length() > 0)) {    	
    		this.setTitle(String.format(getResources().getString(R.string.ApplicationNameUrl), value));    		
    	} else {
    		clearTitle();
    	}
	}
	
	/**
	 * Get a Drawable of the current favicon, with its size normalized relative to current screen density.
	 * @return The normalized favicon.
	 */
	private BitmapDrawable getNormalizedFavicon() {
		
		BitmapDrawable favIcon = new BitmapDrawable(getResources(), mCurrentWebView.getFavicon());
		
		if (mCurrentWebView.getFavicon() != null) {
			int imageButtonSize = ApplicationUtils.getImageButtonSize(this);
			int favIconSize = ApplicationUtils.getFaviconSize(this);
			
			Bitmap bm = Bitmap.createBitmap(imageButtonSize, imageButtonSize, Bitmap.Config.ARGB_4444);
			Canvas canvas = new Canvas(bm);
			
			favIcon.setBounds((imageButtonSize / 2) - (favIconSize / 2), (imageButtonSize / 2) - (favIconSize / 2), (imageButtonSize / 2) + (favIconSize / 2), (imageButtonSize / 2) + (favIconSize / 2));
			favIcon.draw(canvas);
			
			favIcon = new BitmapDrawable(getResources(), bm);
		}
		
		return favIcon;
	}
	
	/**
	 * Update the "Go" button image.
	 */
	private void updateGoButton() {
		if (mCurrentWebView.isLoading()) {
			mGoButton.setImageResource(R.drawable.ic_btn_stop);			
			mUrlEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mCircularProgress, null);
			((AnimationDrawable) mCircularProgress).start();
		} else {
			if (!mCurrentWebView.isSameUrl(mUrlEditText.getText().toString())) {
				mGoButton.setImageResource(R.drawable.ic_btn_go);
			} else {
				mGoButton.setImageResource(R.drawable.ic_btn_reload);
			}			
			
			mUrlEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);			
			((AnimationDrawable) mCircularProgress).stop();
		}
	}
	
	/**
	 * Update the fav icon display.
	 */
	private void updateFavIcon() {
		BitmapDrawable favicon = getNormalizedFavicon();
		
		if (mCurrentWebView.getFavicon() != null) {
			mToolsButton.setImageDrawable(favicon);
		} else {
			mToolsButton.setImageResource(R.drawable.fav_icn_default_toolbar);
		}
	}
	
	/**
	 * Update the UI: Url edit text, previous/next button state,...
	 */
	private void updateUI() {
		if(mCurrentWebView != null) {
			mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
				mUrlEditText.setText(mCurrentWebView.getUrl());
			mUrlEditText.addTextChangedListener(mUrlTextWatcher);
			
			mPreviousButton.setEnabled(mCurrentTabViewPager.getCurrentItem() > 0);
			mNextButton.setEnabled(mCurrentTabViewPager.getCurrentItem() < mCurrentTabViewPager.getChildCount());
			
//			if (mCurrentWebView.getUrl() != null) {
//				mMenuButton.setEnabled((mCurrentTabViewPager.getChildCount() > 1 || !mCurrentWebView.getUrl().equals(Constants.URL_ABOUT_START)));
//			} else {
//				mMenuButton.setEnabled(mCurrentTabViewPager.getChildCount() > 1);
//			}
			
			mProgressBar.setProgress(mCurrentWebView.getProgress());
		
			updateGoButton();
			
			updateTitle();
			
			updateFavIcon();
		}
	}
	
	/**
	 * Open the "Add bookmark" dialog.
	 */
	private void openAddBookmarkDialog() {
		Intent i = new Intent(this, EditBookmarkActivity.class);
		
		i.putExtra(Constants.EXTRA_ID_BOOKMARK_ID, (long) -1);
		i.putExtra(Constants.EXTRA_ID_BOOKMARK_TITLE, mCurrentWebView.getTitle());
		i.putExtra(Constants.EXTRA_ID_BOOKMARK_URL, mCurrentWebView.getUrl());
		
		startActivity(i);
	}
	
	/**
	 * Open the bookmark list.
	 */
	private void openBookmarksHistoryActivity() {
    	Intent i = new Intent(this, BookmarksHistoryActivity.class);
    	startActivityForResult(i, OPEN_BOOKMARKS_HISTORY_ACTIVITY);
    }
	
	/**
	 * Open the download list.
	 */
	private void openDownloadsList() {
		Intent i = new Intent(this, DownloadsListActivity.class);
    	startActivityForResult(i, OPEN_DOWNLOADS_ACTIVITY);
	}
	
	/**
	 * Perform the user-defined action when clicking on the quick button.
	 */
	private void onQuickButton() {
//		openBookmarksHistoryActivity();
		addTab(true);
	}
	
	/**
	 * Open preferences.
	 */
	private void openPreferences() {
		Intent preferencesActivity = new Intent(this, PreferencesActivity.class);
  		startActivity(preferencesActivity);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	MenuItem item;
    	
    	item = menu.add(0, MENU_ADD_BOOKMARK, 0, R.string.Main_MenuAddBookmark);
        item.setIcon(R.drawable.ic_menu_add_bookmark);
        
        item = menu.add(0, MENU_SHOW_BOOKMARKS, 0, R.string.Main_MenuShowBookmarks);
        item.setIcon(R.drawable.ic_menu_bookmarks);
        
        item = menu.add(0, MENU_SHOW_DOWNLOADS, 0, R.string.Main_MenuShowDownloads);
        item.setIcon(R.drawable.ic_menu_downloads);                
        
        item = menu.add(0, MENU_PREFERENCES, 0, R.string.Main_MenuPreferences);
        item.setIcon(R.drawable.ic_menu_preferences);
        
        item = menu.add(0, MENU_EXIT, 0, R.string.Main_MenuExit);
        item.setIcon(R.drawable.ic_menu_exit);
    	
    	return true;
	}
	
	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()) {
    	case MENU_ADD_BOOKMARK:    		
    		openAddBookmarkDialog();
            return true;
    	case MENU_SHOW_BOOKMARKS:    		
    		openBookmarksHistoryActivity();
            return true;
    	case MENU_SHOW_DOWNLOADS:    		
    		openDownloadsList();
            return true;
    	case MENU_PREFERENCES:    		
    		openPreferences();
            return true;    	
    	case MENU_EXIT:
    		this.finish();
    		return true;
        default: return super.onMenuItemSelected(featureId, item);
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
        				addTab(false);
        			}
        			navigateToUrl(b.getString(Constants.EXTRA_ID_URL));
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
	private void showPreviousTab(boolean resetToolbarsRunnable) {
		
		if (mCurrentTabViewPager.getChildCount() > 1) {
			
			if (mFindDialogVisible) {
				closeFindDialog();
			}
			
			mCurrentWebView.doOnPause();
			
			mCurrentTabViewPager.setCurrentItem(mTabPagerAdapter.getCurrentPosition() -1);

//			mCurrentWebView = mWebViews.get(mViewFlipper.getDisplayedChild());
			mCurrentWebView = ((WebviewFragment)mTabPagerAdapter.getItem(mTabPagerAdapter.getCurrentPosition())).getCustomWebView();
			mCurrentWebView.doOnResume();
			
			if (resetToolbarsRunnable) {
				startToolbarsHideRunnable();
			}
			
			showToastOnTabSwitch();
			

			updateUI();
		}
	}
	
	/**
	 * Show the next tab, if any.
	 */
	private void showNextTab(boolean resetToolbarsRunnable) {
		
		if (mCurrentTabViewPager.getChildCount() > 1) {
			
			if (mFindDialogVisible) {
				closeFindDialog();
			}
			
			mCurrentWebView.doOnPause();
			
			mCurrentTabViewPager.setCurrentItem(mTabPagerAdapter.getCurrentPosition() + 1);

//			mCurrentWebView = mWebViews.get(mViewFlipper.getDisplayedChild());
			mCurrentWebView = ((WebviewFragment)mTabPagerAdapter.getItem(mTabPagerAdapter.getCurrentPosition())).getCustomWebView();
			mCurrentWebView.doOnResume();
			
			if (resetToolbarsRunnable) {
				startToolbarsHideRunnable();
			}
			
			showToastOnTabSwitch();
			
			updateUI();
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		hideKeyboard(false);
		
//		return mGestureDetector.onTouchEvent(event);
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
	
//	public void onPageFinished(String url) {
//		updateUI();			
//		
//		if ((Controller.getInstance().getPreferences().getBoolean(Constants.PREFERENCES_ADBLOCKER_ENABLE, true)) &&
//				(!checkInAdBlockWhiteList(mCurrentWebView.getUrl()))) {
//			mCurrentWebView.loadAdSweep();
//		}
//		WebIconDatabase.getInstance().retainIconForPageUrl(mCurrentWebView.getUrl());
//		
//		if (mUrlBarVisible) {
//			startToolbarsHideRunnable();
//		}
//	}
//	
//	public void onPageStarted(String url) {
//		if (mFindDialogVisible) {
//			closeFindDialog();
//		}
//		
//		mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
//		mUrlEditText.setText(url);
//		mUrlEditText.addTextChangedListener(mUrlTextWatcher);
//		
//		mPreviousButton.setEnabled(false);
//		mNextButton.setEnabled(false);
//		
//		updateGoButton();
//		
//		setToolbarsVisibility(true);
//	}
//	
//	public void onUrlLoading(String url) {
//		setToolbarsVisibility(true);
//	}
//	
//	public void onMailTo(String url) {
//		Intent sendMail = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//		startActivity(sendMail);
//	}
//	
//	public void onExternalApplicationUrl(String url) {
//		try {
//			
//			Intent i  = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//			startActivity(i);
//			
//		} catch (Exception e) {
//			
//			// Notify user that the vnd url cannot be viewed.
//			new AlertDialog.Builder(this)
//			.setTitle(R.string.Main_VndErrorTitle)
//			.setMessage(String.format(getString(R.string.Main_VndErrorMessage), url))
//			.setPositiveButton(android.R.string.ok,
//					new AlertDialog.OnClickListener()
//			{
//				public void onClick(DialogInterface dialog, int which) { }
//			})
//			.setCancelable(true)
//			.create()
//			.show();
//		}
//	}
//	
//	public void setHttpAuthUsernamePassword(String host, String realm, String username, String password) {
//		mCurrentWebView.setHttpAuthUsernamePassword(host, realm, username, password);
//	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		if ((item != null) &&
				(item.getIntent() != null)) {
			Bundle b = item.getIntent().getExtras();

			switch(item.getItemId()) {
			case CONTEXT_MENU_OPEN:
				if (b != null) {
					navigateToUrl(b.getString(Constants.EXTRA_ID_URL));
				}			
				return true;

			case CONTEXT_MENU_OPEN_IN_NEW_TAB:
				if (b != null) {
//					addTab(false, mViewFlipper.getDisplayedChild());
					addTab(false, mTabPagerAdapter.getCurrentPosition());
					navigateToUrl(b.getString(Constants.EXTRA_ID_URL));
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
	
}
