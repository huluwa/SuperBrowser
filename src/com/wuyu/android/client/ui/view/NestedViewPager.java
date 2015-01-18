package com.wuyu.android.client.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class NestedViewPager extends ViewPager {
	
	private boolean enabled;
	
	public NestedViewPager(Context context) {
		super(context);
	}

	public NestedViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.enabled = false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	@Override
	protected boolean canScroll(View arg0, boolean arg1, int arg2, int arg3,
			int arg4) {
		if(arg0 != this && arg0 instanceof ViewPager) {
	        int currentItem = ((ViewPager) arg0).getCurrentItem();
	        int countItem = ((ViewPager) arg0).getAdapter().getCount();
	        if((currentItem==(countItem-1) && arg2<0) || (currentItem==0 && arg2>0)){
	            return false;
	        }
	        return true;
	    }
		return super.canScroll(arg0, arg1, arg2, arg3, arg4);
	}

	public void setPagingEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
