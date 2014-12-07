package org.zirco.ui.menu;

import org.greendroid.QuickAction;
import org.greendroid.QuickActionGrid;
import org.greendroid.QuickActionWidget;
import org.greendroid.QuickActionWidget.OnQuickActionClickListener;
import org.zirco.BrowserApplication;
import org.zirco.R;
import org.zirco.controllers.Controller;
import org.zirco.controllers.MainController;
import org.zirco.model.adapters.UrlSuggestionCursorAdapter;
import org.zirco.providers.BookmarksProviderWrapper;
import org.zirco.ui.activities.IToolbarsContainer;
import org.zirco.ui.runnables.HideToolbarsRunnable;
import org.zirco.utils.AnimationManager;
import org.zirco.utils.Constants;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;

public class NavigatBar implements IToolbarsContainer{

	// 布局
	private LinearLayout mTopBar;
	private LinearLayout mFindBar;
	// 查找框
	private ImageButton mFindPreviousButton;
	private ImageButton mFindNextButton;
	private ImageButton mFindCloseButton;
	private EditText mFindText;
	// 导航框
	private ImageButton mToolsButton;
	private AutoCompleteTextView mUrlEditText;
	private ImageButton mGoButton;
	private ProgressBar mProgressBar;
	private Drawable mCircularProgress;
	private TextWatcher mUrlTextWatcher;
	private QuickActionGrid mToolsActionGrid;
	
	private boolean mUrlBarVisible;
	private boolean mToolsActionGridVisible = false;
	private boolean mFindDialogVisible = false;
	
	private MainController mMainController;
	private HideToolbarsRunnable mHideToolbarsRunnable;
	private NavigatBarCallback callback;
	
	public void setNavigatBarCallback(NavigatBarCallback callback) {
		this.callback = callback;
	}
	
	public NavigatBar(MainController controller, View root) {
		mMainController = controller;
		initView(root);
	}

	public void initView(View root) {

		mTopBar = (LinearLayout) root.findViewById(R.id.BarLayout);
		mTopBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Dummy event to steel it from the WebView, in case of clicking
				// between the buttons.
			}
		});
		
    	mFindBar = (LinearLayout) root.findViewById(R.id.findControls);
    	mFindBar.setVisibility(View.GONE);

		mFindPreviousButton = (ImageButton) root.findViewById(R.id.find_previous);
		mFindPreviousButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(callback != null) {
					callback.onFindPrevious(mFindText.getText().toString());
				}
				hideKeyboardFromFindDialog();
			}
		});

		mFindNextButton = (ImageButton) root.findViewById(R.id.find_next);
		mFindNextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(callback != null) {
					callback.onFindNext(mFindText.getText().toString());
				}
				hideKeyboardFromFindDialog();
			}
		});

		mFindCloseButton = (ImageButton) root.findViewById(R.id.find_close);
		mFindCloseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeFindDialog();
			}
		});

		mFindText = (EditText) root.findViewById(R.id.find_value);
		mFindText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				doFind();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
        mCircularProgress = mMainController.getActivity().getResources().getDrawable(R.drawable.spinner);
        
        mToolsActionGrid = new QuickActionGrid(mMainController.getActivity());
		mToolsActionGrid.addQuickAction(new QuickAction(mMainController.getActivity(), R.drawable.ic_btn_home, R.string.QuickAction_Home));
		mToolsActionGrid.addQuickAction(new QuickAction(mMainController.getActivity(), R.drawable.ic_btn_share, R.string.QuickAction_Share));
		mToolsActionGrid.addQuickAction(new QuickAction(mMainController.getActivity(), R.drawable.ic_btn_find, R.string.QuickAction_Find));
		mToolsActionGrid.addQuickAction(new QuickAction(mMainController.getActivity(), R.drawable.ic_btn_select, R.string.QuickAction_SelectText));
		mToolsActionGrid.addQuickAction(new QuickAction(mMainController.getActivity(), R.drawable.ic_btn_mobile_view, R.string.QuickAction_MobileView));
				
		mToolsActionGrid.setOnQuickActionClickListener(new OnQuickActionClickListener() {			
			@Override
			public void onQuickActionClicked(QuickActionWidget widget, int position) {
				switch (position) {
				case 0:
					if(callback != null) {
						callback.onNavigateToHome();
					}
					break;
				case 1: //分享
					if(callback != null) {
						callback.onSharePage();
					}
					break;
				case 2:					
					// Somewhat dirty hack: when the find dialog was shown from a QuickAction,
					// the soft keyboard did not show... Hack is to wait a little before showing
					// the file dialog through a thread.
					startShowFindDialogRunnable();
					break;
				case 3:
//					swithToSelectAndCopyTextMode();
					break;
				case 4:
					String currentUrl = mUrlEditText.getText().toString();
		    		
		    		// Do not reload mobile view if already on it.
		    		if (!currentUrl.startsWith(Constants.URL_GOOGLE_MOBILE_VIEW_NO_FORMAT)) {
		    			String url = String.format(Constants.URL_GOOGLE_MOBILE_VIEW, mUrlEditText.getText().toString());
		    			if(callback != null) {
							callback.onNavigateToUrl(url);
						}
		    		}
		    		break;				
				}
			}
		});
				
		mToolsActionGrid.setOnDismissListener(new PopupWindow.OnDismissListener() {			
			@Override
			public void onDismiss() {
				mToolsActionGridVisible = false;
//				startToolbarsHideRunnable();
			}
		});
		
		
		String[] from = new String[] {UrlSuggestionCursorAdapter.URL_SUGGESTION_TITLE, UrlSuggestionCursorAdapter.URL_SUGGESTION_URL};
    	int[] to = new int[] {R.id.AutocompleteTitle, R.id.AutocompleteUrl};
    	
    	UrlSuggestionCursorAdapter adapter = new UrlSuggestionCursorAdapter(mMainController.getActivity(), R.layout.url_autocomplete_line, null, from, to);
    	      	
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
					return BookmarksProviderWrapper.getUrlSuggestions(mMainController.getActivity().getContentResolver(),
							constraint.toString(),
							PreferenceManager.getDefaultSharedPreferences(BrowserApplication.getContext()).getBoolean(Constants.PREFERENCE_USE_WEAVE, false));
				} else {
					return BookmarksProviderWrapper.getUrlSuggestions(mMainController.getActivity().getContentResolver(),
							null,
							PreferenceManager.getDefaultSharedPreferences(BrowserApplication.getContext()).getBoolean(Constants.PREFERENCE_USE_WEAVE, false));
				}
			}
		});
		
		mUrlEditText = (AutoCompleteTextView) root.findViewById(R.id.UrlText);
    	mUrlEditText.setThreshold(1);
    	mUrlEditText.setAdapter(adapter);    
    	
    	mUrlEditText.setOnKeyListener(new View.OnKeyListener() {
    		
    		@Override
    		public boolean onKey(View v, int keyCode, KeyEvent event) {												
    			if (keyCode == KeyEvent.KEYCODE_ENTER) {
    				if(callback != null) {
    					callback.onNavigateToUrl(null);
    				}
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
    	    	
    	mGoButton = (ImageButton) root.findViewById(R.id.GoBtn);    	
    	mGoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if(callback != null) {
					callback.onNavigateToUrl(mUrlEditText.getText().toString());
				}
            }          
        });
    	
    	mToolsButton = (ImageButton) root.findViewById(R.id.ToolsBtn);
    	mToolsButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				mToolsActionGridVisible = true;
				mToolsActionGrid.show(v);				
			}
		});
    	
    	mProgressBar = (ProgressBar) root.findViewById(R.id.WebViewProgress);
    	mProgressBar.setMax(100);
	}
	
	/**
	 * Update the "Go" button image.
	 */
	private void updateGoButton() {
//		if (mCurrentWebView.isLoading()) {
//			mGoButton.setImageResource(R.drawable.ic_btn_stop);			
//			mUrlEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mCircularProgress, null);
//			((AnimationDrawable) mCircularProgress).start();
//		} else {
//			if (!mCurrentWebView.isSameUrl(mUrlEditText.getText().toString())) {
//				mGoButton.setImageResource(R.drawable.ic_btn_go);
//			} else {
//				mGoButton.setImageResource(R.drawable.ic_btn_reload);
//			}			
//			
//			mUrlEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);			
//			((AnimationDrawable) mCircularProgress).stop();
//		}
	}
	
	/**
	 * Update the fav icon display.
	 */
	private void updateFavIcon() {
//		BitmapDrawable favicon = getNormalizedFavicon();
//		
//		if (mCurrentWebView.getFavicon() != null) {
//			mToolsButton.setImageDrawable(favicon);
//		} else {
//			mToolsButton.setImageResource(R.drawable.fav_icn_default_toolbar);
//		}
	}

	private void doFind() {
		CharSequence find = mFindText.getText();
		if (find.length() == 0) {
			mFindPreviousButton.setEnabled(false);
			mFindNextButton.setEnabled(false);
			mMainController.getCurrentWebView().clearMatches();
		} else {
			int found = mMainController.getCurrentWebView().findAll(find.toString());
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
		mMainController.getCurrentWebView().doSetFindIsUp(true);
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
		mMainController.getCurrentWebView().doNotifyFindDialogDismissed();
		mMainController.getCurrentWebView().clearMatches();
		setFindBarVisibility(false);
	}

	private void setFindBarVisibility(boolean visible) {
		if (visible) {
			mFindBar.startAnimation(AnimationManager.getInstance()
					.getTopBarShowAnimation());
			mFindBar.setVisibility(View.VISIBLE);
			mFindDialogVisible = true;
		} else {
			mFindBar.startAnimation(AnimationManager.getInstance()
					.getTopBarHideAnimation());
			mFindBar.setVisibility(View.GONE);
			mFindDialogVisible = false;
		}
	}

	private void showKeyboardForFindDialog() {
		InputMethodManager imm = (InputMethodManager) mMainController.getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mFindText, InputMethodManager.SHOW_IMPLICIT);
	}

	private void hideKeyboardFromFindDialog() {
		InputMethodManager imm = (InputMethodManager) mMainController.getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mFindText.getWindowToken(), 0);
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
				
				setToolbarsVisibility(false);
			}
		}
		mHideToolbarsRunnable = null;
	}
	
	 /**
     * Change the tool bars visibility.
     * @param visible If True, the tool bars will be shown.
     */
    private void setToolbarsVisibility(boolean visible) {
    	    	
    	if (visible) {
    		
    		if (!mUrlBarVisible) {    			
    			mTopBar.startAnimation(AnimationManager.getInstance().getTopBarShowAnimation());
    			
    			mTopBar.setVisibility(View.VISIBLE);
    		}
    		
    		startToolbarsHideRunnable();
    		
    		mUrlBarVisible = true;    		    		
    		
    	} else {  	
    		
    		if (mUrlBarVisible) {
    			mTopBar.startAnimation(AnimationManager.getInstance().getTopBarHideAnimation());
    			
    			mTopBar.setVisibility(View.GONE);
    			
    		}
			
			mUrlBarVisible = false;
    	}
    }
    
    /**
     * Hide the keyboard.
     * @param delayedHideToolbars If True, will start a runnable to delay tool bars hiding. If False, tool bars are hidden immediatly.
     */
    private void hideKeyboard(boolean delayedHideToolbars) {
    	InputMethodManager imm = (InputMethodManager) mMainController.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(mUrlEditText.getWindowToken(), 0);
    }
    
    public void clearFocus() {
    	mUrlEditText.clearFocus();
    }
    
    public void updateUI() {
//		if(mCurrentWebView != null) {
//			
//			mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
//			mUrlEditText.setText(mCurrentWebView.getUrl());
//			mUrlEditText.addTextChangedListener(mUrlTextWatcher);
//			
//			mProgressBar.setProgress(mCurrentWebView.getProgress());
//		
//			updateGoButton();
//			
//			updateFavIcon();
//		}
	}
}
