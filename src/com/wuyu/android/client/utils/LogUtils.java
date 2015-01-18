package com.wuyu.android.client.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Log工具，类似android.util.Log。
 * <p/>
 * tag自动产生，格式: customTag[threadName:className.methodName(L:lineNumber)],
 * customTag为空时只输出：TAG[threadName:className.methodName(L:lineNumber)]。
 * <p/>
 * Author: chenyg Date: 20114-12-3
 */
public class LogUtils {

	public static String TAG = "chenyg";

	public static boolean isDebug = true;

	public static void d(String content) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, null);
		Log.d(gtag, content);
	}

	public static void d(String content, Throwable tr) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller, null);
		Log.d(tag, content, tr);
	}

	public static void d(String tag, String content) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, tag);
		Log.d(gtag, content);
	}

	public static void e(String content) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, null);
		Log.e(gtag, content);
	}

	public static void e(String content, Throwable tr) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, null);
		Log.e(gtag, content, tr);
	}

	public static void e(String tag, String content) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, tag);
		Log.e(gtag, content);
	}

	public static void i(String content) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, null);
		Log.i(gtag, content);
	}

	public static void i(String content, Throwable tr) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, null);
		Log.i(gtag, content, tr);
	}

	public static void i(String tag, String content) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, tag);
		Log.i(gtag, content);
	}

	public static void v(String content) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, null);
		Log.v(gtag, content);
	}

	public static void v(String content, Throwable tr) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, null);
		Log.v(gtag, content, tr);
	}

	public static void v(String tag, String content) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, tag);
		Log.v(gtag, content);
	}

	public static void w(String content) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, null);
		Log.w(gtag, content);
	}

	public static void w(String content, Throwable tr) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, null);
		Log.w(gtag, content, tr);
	}

	public static void w(Throwable tr) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, null);
		Log.w(gtag, tr);
	}

	public static void w(String tag, String content) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, tag);
		Log.w(gtag, content);
	}

	public static void wtf(String content) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, null);
		Log.wtf(gtag, content);
	}

	public static void wtf(String content, Throwable tr) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, null);
		Log.wtf(gtag, content, tr);
	}

	public static void wtf(Throwable tr) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, null);
		Log.wtf(gtag, tr);
	}

	public static void wtf(String tag, String content) {
		if (!isDebug)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String gtag = generateTag(caller, tag);
		Log.wtf(gtag, content);
	}

	public static StackTraceElement getCurrentStackTraceElement() {
		return Thread.currentThread().getStackTrace()[3];
	}

	public static StackTraceElement getCallerStackTraceElement() {
		return Thread.currentThread().getStackTrace()[4];
	}
	
	private static String generateTag(StackTraceElement caller, String customTag) {
		String tag = "[%s-%s.%s(L:%d)]";
		String callerClazzName = caller.getClassName();
		callerClazzName = callerClazzName.substring(callerClazzName
				.lastIndexOf(".") + 1);
		tag = String
				.format(tag, Thread.currentThread().getName().toUpperCase(),
						callerClazzName, caller.getMethodName(),
						caller.getLineNumber());
		tag = TextUtils.isEmpty(customTag) ? (TAG + tag) : (customTag + tag);
		return tag;
	}

	public static void printStackTrace() {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		Log.i(TAG, "call printStackTrace method");
		Log.i(TAG, "stacktrace len:" + stacktrace.length);
		for (int i = 0; i < stacktrace.length; i++) {
			Log.i(TAG, "----------------  the [" + i
					+ " element  ----------------");
			Log.i(TAG, "toString: " + stacktrace[i].toString());
			Log.i(TAG, "ClassName: " + stacktrace[i].getClassName());
			Log.i(TAG, "FileName: " + stacktrace[i].getFileName());
			Log.i(TAG, "LineNumber: " + stacktrace[i].getLineNumber());
			Log.i(TAG, "MethodName: " + stacktrace[i].getMethodName());
		}
	}

}
