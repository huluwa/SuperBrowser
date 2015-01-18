package com.wuyu.android.client.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.wuyu.android.client.R;
import com.wuyu.android.client.controllers.Controller;
import com.wuyu.android.client.model.adapters.HistoryExpandableListAdapter;
import com.wuyu.android.client.model.items.HistoryItem;
import com.wuyu.android.client.providers.BookmarksProviderWrapper;
import com.wuyu.android.client.ui.components.CustomWebView;
import com.wuyu.android.client.utils.ApplicationUtils;
import com.wuyu.android.client.utils.Constants;

public class HistoryFragment extends BaseFragment implements OnChildClickListener {

	private static final int MENU_CLEAR_HISTORY = Menu.FIRST;

	private static final int MENU_OPEN_IN_TAB = Menu.FIRST + 10;
	private static final int MENU_COPY_URL = Menu.FIRST + 11;
	private static final int MENU_SHARE = Menu.FIRST + 12;
	private static final int MENU_DELETE_FROM_HISTORY = Menu.FIRST + 13;

	private Activity mActivity;
	private ViewGroup mRootViewGroup;
	private ExpandableListView mExpandableListView;
	private ExpandableListAdapter mAdapter;
	private ProgressDialog mProgressDialog;
	private OnCheckedChangeListener mBookmarkStarChangeListener;

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
		mRootViewGroup = (ViewGroup) inflater.inflate(
				R.layout.history_fragment_layout, null);
		return mRootViewGroup;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		findView();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private void findView() {
		mExpandableListView = (ExpandableListView) mRootViewGroup.findViewById(R.id.expandablelist);
		mExpandableListView.setOnChildClickListener(this);
		
		mBookmarkStarChangeListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				long id = (Long) buttonView.getTag();
				BookmarksProviderWrapper.toggleBookmark(mActivity.getContentResolver(),
						id, isChecked);

				if (isChecked) {
					Toast.makeText(mActivity,
							R.string.HistoryListActivity_BookmarkAdded,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mActivity,
							R.string.HistoryListActivity_BookmarkRemoved,
							Toast.LENGTH_SHORT).show();
				}
			}
		};

		fillData();
	}

	/**
	 * Fill the history list.
	 */
	private void fillData() {
		Cursor c = BookmarksProviderWrapper
				.getStockHistory(mActivity.getContentResolver());

		mAdapter = new HistoryExpandableListAdapter(mActivity,
				mBookmarkStarChangeListener, c,
				c.getColumnIndex(Browser.BookmarkColumns.DATE),
				ApplicationUtils.getFaviconSizeForBookmarks(mActivity));

		mExpandableListView.setAdapter(mAdapter);

		if (mAdapter.getGroupCount() > 0) {
			mExpandableListView.expandGroup(0);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);
		int group = ExpandableListView
				.getPackedPositionGroup(info.packedPosition);
		int child = ExpandableListView
				.getPackedPositionChild(info.packedPosition);

		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

			HistoryItem item = (HistoryItem) mAdapter
					.getChild(group, child);
			menu.setHeaderTitle(item.getTitle());

			menu.add(0, MENU_OPEN_IN_TAB, 0,
					R.string.HistoryListActivity_MenuOpenInTab);
			menu.add(0, MENU_COPY_URL, 0,
					R.string.BookmarksHistoryActivity_MenuCopyLinkUrl);
			menu.add(0, MENU_SHARE, 0, R.string.Main_MenuShareLinkUrl);
			menu.add(0, MENU_DELETE_FROM_HISTORY, 0,
					R.string.HistoryListActivity_MenuDelete);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuItem
				.getMenuInfo();

		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);

		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			int group = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
			int child = ExpandableListView
					.getPackedPositionChild(info.packedPosition);

			HistoryItem item = (HistoryItem) mAdapter
					.getChild(group, child);

			switch (menuItem.getItemId()) {
			case MENU_OPEN_IN_TAB:
				doNavigateToUrl(item.getUrl(), true);
				break;
			case MENU_COPY_URL:
				ApplicationUtils.copyTextToClipboard(mActivity, item.getUrl(),
						getString(R.string.Commons_UrlCopyToastMessage));
				break;
			case MENU_SHARE:
				ApplicationUtils
						.sharePage(mActivity, item.getTitle(), item.getUrl());
				break;
			case MENU_DELETE_FROM_HISTORY:
				BookmarksProviderWrapper.deleteHistoryRecord(
						mActivity.getContentResolver(), item.getId());
				fillData();
				break;
			default:
				break;
			}
		}

		return super.onContextItemSelected(menuItem);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//
//		MenuItem item;
//		item = menu
//				.add(0, MENU_CLEAR_HISTORY, 0, R.string.Commons_ClearHistory);
//		item.setIcon(R.drawable.ic_menu_delete);
//
//		return true;
//	}
//
//	@Override
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//
//		switch (item.getItemId()) {
//		case MENU_CLEAR_HISTORY:
//			clearHistory();
//			return true;
//		default:
//			return super.onMenuItemSelected(featureId, item);
//		}
//	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		HistoryItem item = (HistoryItem) mAdapter.getChild(groupPosition,
				childPosition);
		doNavigateToUrl(item.getUrl(), false);
		return false;
	}

	/**
	 * Load the given url.
	 * 
	 * @param url
	 *            The url.
	 * @param newTab
	 *            If True, will open a new tab. If False, the current tab is
	 *            used.
	 */
	private void doNavigateToUrl(String url, boolean newTab) {
		Intent result = new Intent();
		result.putExtra(Constants.EXTRA_ID_NEW_TAB, newTab);
		result.putExtra(Constants.EXTRA_ID_URL, url);

//		if (getParent() != null) {
//			getParent().setResult(RESULT_OK, result);
//		} else {
//			setResult(RESULT_OK, result);
//		}
//		finish();
	}

	/**
	 * Clear history.
	 */
	private void doClearHistory() {
		mProgressDialog = ProgressDialog.show(mActivity, this.getResources()
				.getString(R.string.Commons_PleaseWait), this.getResources()
				.getString(R.string.Commons_ClearingHistory));

		new HistoryClearer();
	}

	/**
	 * Display confirmation and clear history.
	 */
	private void clearHistory() {
		ApplicationUtils.showYesNoDialog(mActivity,
				android.R.drawable.ic_dialog_alert,
				R.string.Commons_ClearHistory, R.string.Commons_NoUndoMessage,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						doClearHistory();
					}
				});
	}

	/**
	 * Runnable to clear history.
	 */
	private class HistoryClearer implements Runnable {

		/**
		 * Constructor.
		 */
		public HistoryClearer() {
			new Thread(this).start();
		}

		@Override
		public void run() {
			BookmarksProviderWrapper.clearHistoryAndOrBookmarks(
					mActivity.getContentResolver(), true, false);

			for (CustomWebView webView : Controller.getInstance()
					.getWebViewList()) {
				webView.clearHistory();
			}

			handler.sendEmptyMessage(0);
		}

		private Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				mProgressDialog.dismiss();
				fillData();
			}
		};
	}
}
