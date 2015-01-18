package com.wuyu.android.client;

import android.app.Application;
import android.content.res.Configuration;

public class BrowserApplication extends Application {

	private static BrowserApplication letvApplication;
	
	public static BrowserApplication getContext() {
		return BrowserApplication.letvApplication;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		letvApplication = this;
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
}
