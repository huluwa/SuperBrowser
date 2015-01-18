package com.wuyu.android.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

public class HomeNavigatFragment extends WebviewFragment {

	private String NAVIGAT_URL = "file:///android_asset/navigate.html";
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if(!NAVIGAT_URL.equals(getCustomWebView().getLoadedUrl())) {
			getCustomWebView().loadUrl(NAVIGAT_URL);
		}
	}

}
