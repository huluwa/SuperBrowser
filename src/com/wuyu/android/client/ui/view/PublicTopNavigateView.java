package com.wuyu.android.client.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wuyu.android.client.R;

public class PublicTopNavigateView extends RelativeLayout {

	private Context mContext;
	private ViewGroup mContentLayout;
	private View mBackBtn;
	private View mTitleTextView;

	private OnTopNavigateClickListener onRefreshDataListener;

	public PublicTopNavigateView(Context context) {
		super(context);
		this.mContext = context;
		initView(context);
		findView();
	}

	public PublicTopNavigateView(Context context, int contentViewId) {
		this(context);
		addContent(contentViewId);
	}

	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.public_top_navigate_layout, this);
	}

	private void findView() {
		mContentLayout = (ViewGroup) this.findViewById(R.id.content);
		mTitleTextView = this.findViewById(R.id.title);
		mBackBtn = this.findViewById(R.id.leftBtn);
		mBackBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onRefreshDataListener != null) {
					onRefreshDataListener.onBack();
				}
			}
		});
	}

	public void addContent(int contentViewId) {
		inflate(mContext, contentViewId, mContentLayout);
	}

	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title) {
		if (!TextUtils.isEmpty(title)) {
			mTitleTextView.setVisibility(View.VISIBLE);
			((TextView) mTitleTextView).setText(title);
		}
	}

	public void setOnRefreshDataListener(OnTopNavigateClickListener l) {
		this.onRefreshDataListener = l;
	}

	public interface OnTopNavigateClickListener {
		public void onBack();
	}
}
