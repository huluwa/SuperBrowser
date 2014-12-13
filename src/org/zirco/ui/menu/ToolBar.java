package org.zirco.ui.menu;

import org.greendroid.QuickAction;
import org.greendroid.QuickActionGrid;
import org.greendroid.QuickActionWidget;
import org.greendroid.QuickActionWidget.OnQuickActionClickListener;
import org.zirco.R;
import org.zirco.controllers.MainController;
import org.zirco.ui.activities.BookmarksHistoryActivity;
import org.zirco.ui.activities.DownloadsListActivity;
import org.zirco.ui.activities.EditBookmarkActivity;
import org.zirco.ui.activities.MainActivity;
import org.zirco.ui.activities.preferences.PreferencesActivity;
import org.zirco.utils.Constants;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class ToolBar {
	
	private static final int MENU_ADD_BOOKMARK = 0;
	private static final int MENU_SHOW_BOOKMARKS = 1;
	private static final int MENU_SHOW_DOWNLOADS = 2;	
	private static final int MENU_PREFERENCES = 3;
	private static final int MENU_EXIT = 4;
	
	// 布局
	private LinearLayout mBottomBar;
	// 下方工具栏
	private ImageButton mPreviousButton;
	private ImageButton mNextButton;
	private ImageButton mRemoveButton;
	private ImageButton mTabCenterButton;
	private ImageButton mMenuButton;
	private ImageButton mHomeButton;
	//menu
	private RelativeLayout mMenuLayout;
	
	private MainController mMainController;
	
	private QuickActionGrid mToolsActionGrid;
	
	private ToolBarCallback callback;
	
	public void setToolBarCallback(ToolBarCallback callback) {
		this.callback = callback;
	}
	
	public ToolBar(MainController controller, View root) {
		
		mMainController = controller;
		
		initView(root);
	}
	
	
	private void initView(View root) {
		
		mBottomBar = (LinearLayout) root.findViewById(R.id.BottomLayout);
    	mBottomBar.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// Dummy event to steel it from the WebView, in case of clicking between the buttons.				
			}
		});
    	
    	
    	mPreviousButton = (ImageButton) root.findViewById(R.id.PreviousBtn);
    	mNextButton = (ImageButton) root.findViewById(R.id.NextBtn);
    	
    	mPreviousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if(callback != null) {
            		callback.navigatePrevious();
            	}
            }          
        });
		
		mNextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if(callback != null) {
            		callback.navigateNext();
            	}
            }          
        });
		
//		mNewTabButton.setOnClickListener(new View.OnClickListener() {
//      public void onClick(View view) {
//      	if(callback != null) {
//      		callback.onRemoveTabPage();
//      	}
//      }          
//  });
    	
		mTabCenterButton = (ImageButton) root.findViewById(R.id.TabCenterBtn);
		mTabCenterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if(callback != null) {
            		callback.onCreateTabPage();
            	}
            }          
        });
		
		mMenuButton = (ImageButton) root.findViewById(R.id.MenuBtn);
		mMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//            	mMenuLayout.setVisibility(View.VISIBLE);
            	
				mToolsActionGrid.show(view);
            }
        });
		
		mHomeButton = (ImageButton) root.findViewById(R.id.HomeBtn);
		mHomeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {            	
            	if(callback != null) {
            		callback.onQuickButton();
            	}
            }          
        });
		
		
		mToolsActionGrid = new QuickActionGrid(mMainController.getActivity());
		mToolsActionGrid.addQuickAction(new QuickAction(mMainController.getActivity(), R.drawable.ic_menu_add_bookmark, R.string.Main_MenuAddBookmark));
		mToolsActionGrid.addQuickAction(new QuickAction(mMainController.getActivity(), R.drawable.ic_menu_bookmarks, R.string.Main_MenuShowBookmarks));
		mToolsActionGrid.addQuickAction(new QuickAction(mMainController.getActivity(), R.drawable.ic_menu_downloads, R.string.Main_MenuShowDownloads));
		mToolsActionGrid.addQuickAction(new QuickAction(mMainController.getActivity(), R.drawable.ic_menu_preferences, R.string.Main_MenuPreferences));
		mToolsActionGrid.addQuickAction(new QuickAction(mMainController.getActivity(), R.drawable.ic_menu_exit, R.string.Main_MenuExit));
				
		mToolsActionGrid.setOnQuickActionClickListener(new OnQuickActionClickListener() {			
			@Override
			public void onQuickActionClicked(QuickActionWidget widget, int position) {
				
				if(callback == null) {
					return;
				}
				
				switch (position) {
				case MENU_ADD_BOOKMARK:    		
					callback.onOpenAddBookmarkDialog();
		            break;
		            
		    	case MENU_SHOW_BOOKMARKS:    		
		    		callback.onOpenBookmarksHistoryActivity();
		    		break;
		    		
		    	case MENU_SHOW_DOWNLOADS:    		
		    		callback.onOpenDownloadsList();
		    		break;
		    		
		    	case MENU_PREFERENCES:    		
		    		callback.onOpenPreferences();
		    		break;  
		    		
		    	case MENU_EXIT:
		    		callback.onQuit();
		    		break;				
				}
			}
		});
		
		mToolsActionGrid.setOnDismissListener(new PopupWindow.OnDismissListener() {			
			@Override
			public void onDismiss() {
				
			}
		});
		
		mMenuLayout = (RelativeLayout) root.findViewById(R.id.menu_layout);
	}
	
	/**
	 * Update the UI: Url edit text, previous/next button state,...
	 */
	public void updateUI() {
//		if(mCurrentWebView != null) {
//			mPreviousButton.setEnabled(mCurrentTabViewPager.getCurrentItem() > 0);
//			mNextButton.setEnabled(mCurrentTabViewPager.getCurrentItem() < mCurrentTabViewPager.getChildCount());
//			
//			if (mCurrentWebView.getUrl() != null) {
//				mMenuButton.setEnabled((mCurrentTabViewPager.getChildCount() > 1 || !mCurrentWebView.getUrl().equals(Constants.URL_ABOUT_START)));
//			} else {
//				mMenuButton.setEnabled(mCurrentTabViewPager.getChildCount() > 1);
//			}
//			
//		}
	}
	
}
