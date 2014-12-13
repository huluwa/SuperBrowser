package org.zirco.utils;

import java.util.Formatter;
import java.util.Locale;

import org.zirco.BrowserApplication;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Images;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class Tools {

    /**
     * 获取顶部状态栏高度
     * 
     * @param act
     * @return
     */
    public static int getStatusBarHeight(Activity act) {
        Rect frame = new Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 获取标题栏高度
     * 
     * @param act
     * @return
     */
    public static int getTitleBarHeight(Activity act) {
        int contentTop = act.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentTop - getStatusBarHeight(act);
        return titleBarHeight;
    }

    /**
     * 获取内容区域高度
     * 
     * @param act
     * @return
     */
    public static int getContentHeight(Activity act) {
        int contentTop = act.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return (getScreenHeight() - contentTop);
    }

    /**
     * 得到屏幕宽度
     * */
    public static int getScreenWidth() {
        return ((WindowManager) BrowserApplication.getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getWidth();
    }

    /**
     * 得到屏幕高度
     * */
    public static int getScreenHeight() {
        return ((WindowManager)  BrowserApplication.getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();
    }

    /**
     * 判断是否是横屏
     * */
    public static boolean isLandscape(Activity activity) {

        int t = activity.getResources().getConfiguration().orientation;
        if (t == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        }

        return false;
    }

    /**
     * 全屏
     * */
    public static void fullScreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 隐藏Title
     * */
    public static void notFullScreen(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 设置为横屏
     * */
    public static void screenLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 设置为竖屏
     * */
    public static void screenPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
	

	public static String getStringTwo(String strIn) {
		String strResult;
		if (strIn.length() >= 2) {
			strResult = strIn;
		} else {
			strResult = "0".concat(String.valueOf(String.valueOf(strIn)));
		}
		return strResult;
	}
	
	/**
	 * 格式化时间字符串
	 * 
	 * @param timeMs
	 *            毫秒
	 * @return 返回格式00:00:00
	 */
	public static String stringForTime(int timeMs) {

		StringBuilder formatBuilder = new StringBuilder();
		Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

		try {
			int totalSeconds = timeMs / 1000;

			int seconds = totalSeconds % 60;
			int minutes = (totalSeconds / 60) % 60;
			int hours = totalSeconds / 3600;

			formatBuilder.setLength(0);

			if (hours > 0) {
				return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
			} else {
				return formatter.format("%02d:%02d", minutes, seconds).toString();
			}
		} finally {
			formatter.close();
		}
	}
	
	/**
	 *  获得视频缩略图
	 * @param path
	 * @return
	 */
	public static Bitmap createVideoThumbnail(String path) {
		Bitmap bitmap = null;
		try {
			bitmap = ThumbnailUtils.createVideoThumbnail(path,
					Images.Thumbnails.MINI_KIND);
		} catch (IllegalArgumentException ex) {
			return null;
		} catch (RuntimeException ex) {
			return null;
		} catch (OutOfMemoryError ex) {
			System.gc();
			return null;
		}
		return bitmap;
	}
	
	/**
	 * 获取当前程序的版本号
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	
    /**
     * 隐藏键盘输入法
     * 
     * @param mActivity
     */
    public static void hideSoftkeyboard(Activity mActivity) {
        if (null != mActivity && null != mActivity.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) mActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (null != mInputMethodManager) {
                mInputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
	
}
