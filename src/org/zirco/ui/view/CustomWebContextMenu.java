package org.zirco.ui.view;

import org.zirco.R;
import org.zirco.utils.DensityUtils;
import org.zirco.utils.Tools;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView.HitTestResult;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class CustomWebContextMenu extends PopupWindow implements
		OnItemClickListener {

	private Context mContext;
	private ListView mListView;
	private int[] mItems;

	public CustomWebContextMenu(Context context, int resultType) {
		super(context);

		mContext = context;

		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

		setContentView(R.layout.custom_content_menu);

		findview();

		init(resultType);
	}

	private void findview() {
		mListView = (ListView) getContentView().findViewById(R.id.list);
		mListView.setAdapter(new BaseAdapter() {

			public View getView(int position, View view, ViewGroup parent) {

				TextView textView = (TextView) view;

				if (view == null) {
					final LayoutInflater inflater = LayoutInflater
							.from(mContext);
					textView = (TextView) inflater
							.inflate(R.layout.custom_content_menu_item,
									mListView, false);
				}

				int item = mItems[position];
				textView.setText(mContext.getResources().getString(item));

				return textView;

			}

			public long getItemId(int position) {
				return position;
			}

			public Object getItem(int position) {
				return null;
			}

			public int getCount() {
				return (mItems == null) ? 0 : mItems.length;
			}
		});

		mListView.setOnItemClickListener(this);
	}

	private void init(int resultType) {
		if ((resultType == HitTestResult.ANCHOR_TYPE)
				|| (resultType == HitTestResult.IMAGE_ANCHOR_TYPE)
				|| (resultType == HitTestResult.SRC_ANCHOR_TYPE)
				|| (resultType == HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {

			mItems = new int[] { R.string.Main_MenuOpen,
					R.string.Main_MenuOpenNewTab,
					R.string.Main_MenuCopyLinkUrl, R.string.Main_MenuDownload,
					R.string.Main_MenuShareLinkUrl };
		} else if (resultType == HitTestResult.IMAGE_TYPE) {

			mItems = new int[] { R.string.Main_MenuViewImage,
					R.string.Main_MenuCopyImageUrl,
					R.string.Main_MenuDownloadImage,
					R.string.Main_MenuShareImageUrl };
		} else if (resultType == HitTestResult.EMAIL_TYPE) {

			mItems = new int[] { R.string.Main_MenuSendEmail,
					R.string.Main_MenuCopyEmailUrl,
					R.string.Main_MenuShareEmailUrl, };
		} else {

			// mItems = new int[] {
			// R.string.Main_MenuOpen,
			// R.string.Main_MenuOpenNewTab,
			// R.string.Main_MenuCopyLinkUrl,
			// R.string.Main_MenuShareLinkUrl
			// };
		}
	}

	public void setContentView(int layoutId) {
		setContentView(LayoutInflater.from(mContext).inflate(layoutId, null));
	}

	public void show(View anchor, int x, int y) {

		int width = DensityUtils.dip2px(mContext, 100);

		setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		if ((x - width) < 0) {
			showAtLocation(anchor, Gravity.NO_GRAVITY, x, y);
		} else {
			showAtLocation(anchor, Gravity.NO_GRAVITY,
					x - DensityUtils.dip2px(mContext, 100), y);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}
}
