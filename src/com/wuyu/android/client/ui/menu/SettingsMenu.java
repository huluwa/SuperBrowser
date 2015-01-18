package com.wuyu.android.client.ui.menu;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.wuyu.android.client.R;
import com.wuyu.android.client.controllers.MainController;

public class SettingsMenu {
	
	private LinearLayout mMenuLayout;
	private View mAddBookmarkBtn;
	private View mBookmarksBtn;
	private View mDownloadBtn;
	private View mSettingsBtn;
	private View mQuitBtn;

	private MainController mMainController;
	
	private SettingsMenuCallBack callback;
	
	public void setSettingsMenuCallBack(SettingsMenuCallBack callback) {
		this.callback = callback;
	}
	
	public SettingsMenu(MainController controller, View root) {
		mMainController = controller;
		
		findView(root);
	}

	private void findView(View root) {
		mMenuLayout = (LinearLayout) root.findViewById(R.id.SettingMenu);
		
		mAddBookmarkBtn = (LinearLayout) root.findViewById(R.id.menu_btn_add_bookmark);
		mAddBookmarkBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(callback != null) {
					callback.onOpenAddBookmarkDialog();
				}
			}
		});
		
		mBookmarksBtn = (LinearLayout) root.findViewById(R.id.menu_btn_bookmarks);
		mBookmarksBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(callback != null) {
					callback.onOpenBookmarksHistoryActivity();
				}
			}
		});
		
		mDownloadBtn = (LinearLayout) root.findViewById(R.id.menu_btn_downloads);
		mDownloadBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(callback != null) {
					callback.onOpenDownloadsList();
				}
			}
		});
		
		mSettingsBtn = (LinearLayout) root.findViewById(R.id.menu_btn_preferences);
		mSettingsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(callback != null) {
					callback.onOpenPreferences();
				}
			}
		});
		
		mQuitBtn = (LinearLayout) root.findViewById(R.id.menu_btn_exit);
		mQuitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(callback != null) {
					callback.onQuit();
				}
			}
		});
		
	}
	
	public void showClosed() {
		if(mMenuLayout.getVisibility() == View.VISIBLE) {
			mMenuLayout.setVisibility(View.GONE);
		} else {
			mMenuLayout.setVisibility(View.VISIBLE);
		}
	}
	
	public void show() {
		mMenuLayout.setVisibility(View.VISIBLE);
	}
	
	public void closed() {
		mMenuLayout.setVisibility(View.GONE);
	}
	
	public interface SettingsMenuCallBack {

		public void onOpenAddBookmarkDialog();

		public void onOpenBookmarksHistoryActivity();

		public void onOpenDownloadsList();

		public void onOpenPreferences();

		public void onQuit();
	}
}
