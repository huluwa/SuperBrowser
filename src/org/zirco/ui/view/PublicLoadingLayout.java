package org.zirco.ui.view;

import org.zirco.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PublicLoadingLayout extends FrameLayout {
	
	private Context mContext;
	
	private LinearLayout mContentLayout;
	private ProgressBar mLoading;
	private LinearLayout mErrorLayout;
	
	private OnRefreshDataListener onRefreshDataListener;
	
	public void setOnRefreshDataListener(OnRefreshDataListener l) {
		this.onRefreshDataListener = l;
	}
	
	public interface OnRefreshDataListener {
		public void onRefreshData();
	}
	
	public PublicLoadingLayout(Context context) {
		super(context);
		
		this.mContext = context;
		
		initView(context);
		
		findView();
	}
	
	public PublicLoadingLayout(Context context, int contentViewId) {
		this(context);
		
		addContent(contentViewId);
	}
	
	
	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.public_loading_layout, this);
	}

	private void findView() {
		mContentLayout = (LinearLayout) findViewById(R.id.content);
		mLoading = (ProgressBar) findViewById(R.id.loading);
		mErrorLayout = (LinearLayout) findViewById(R.id.error_layout);
		mErrorLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onRefreshDataListener != null) {
					onRefreshDataListener.onRefreshData();
				}
			}
		});
		
	}

	
	public void addContent(int contentViewId) {
		inflate(mContext, contentViewId, mContentLayout);
	}
	
	public void startLoading(boolean isShowContent) {
		mLoading.setVisibility(View.VISIBLE);
		mErrorLayout.setVisibility(View.GONE);
		if (isShowContent) {
			mContentLayout.setVisibility(View.VISIBLE);
		} else {
			mContentLayout.setVisibility(View.GONE);
		}
	}
	
	public void finishLoading() {
		mContentLayout.setVisibility(View.VISIBLE);
		mLoading.setVisibility(View.GONE);
		mErrorLayout.setVisibility(View.GONE);
	}
	
	public void finish() {
		mContentLayout.setVisibility(View.VISIBLE);
		mLoading.setVisibility(View.GONE);
		mErrorLayout.setVisibility(View.GONE);
	}

	/**
	 * 首页无网无cache提示
	 */
	public void errorRefresh() {
		mContentLayout.setVisibility(View.GONE);
		mLoading.setVisibility(View.GONE);
		mErrorLayout.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 首页无网有cache提示
	 */
	public void errorNoNetwork() {
		mContentLayout.setVisibility(View.VISIBLE);
		mLoading.setVisibility(View.GONE);
		mErrorLayout.setVisibility(View.GONE);
		
		Toast.makeText(mContext, R.string.error_toast_message_no_net, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 网络连接错误
	 */
	public void errorNetworkConnect() {
		mContentLayout.setVisibility(View.VISIBLE);
		mLoading.setVisibility(View.GONE);
		mErrorLayout.setVisibility(View.GONE);
		
		Toast.makeText(mContext, R.string.error_toast_message_net_connect, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 首页有网无cache无内容提示
	 */
	public void errorClosed() {
		mContentLayout.setVisibility(View.GONE);
		mLoading.setVisibility(View.GONE);
		mErrorLayout.setVisibility(View.VISIBLE);
	}
	
}
