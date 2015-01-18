package com.wuyu.android.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

public class HomeCommonFragment extends WebviewFragment {

	private String COMMON_URL = "file:///android_asset/common.html";
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (!COMMON_URL.equals(getCustomWebView().getLoadedUrl())) {
			getCustomWebView().loadUrl(COMMON_URL);
		}
	}

}
