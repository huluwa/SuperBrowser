package org.zirco.ui.fragment;

import org.zirco.R;
import org.zirco.controllers.Controller;
import org.zirco.model.items.DownloadItem;
import org.zirco.ui.activities.MainActivity;
import org.zirco.ui.activities.MainActivity;
import org.zirco.ui.components.CustomWebView;
import org.zirco.ui.components.CustomWebViewClient;
import org.zirco.ui.components.CustomWebViewClientCallback;
import org.zirco.ui.runnables.FaviconUpdaterRunnable;
import org.zirco.ui.runnables.HistoryUpdater;
import org.zirco.ui.view.CustomWebContextMenu;
import org.zirco.utils.ApplicationUtils;
import org.zirco.utils.Constants;
import org.zirco.utils.LogUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WebviewFragment extends BaseFragment implements
		CustomWebViewClientCallback, OnTouchListener {

	private static final int CONTEXT_MENU_OPEN = Menu.FIRST + 10;
	private static final int CONTEXT_MENU_OPEN_IN_NEW_TAB = Menu.FIRST + 11;
	private static final int CONTEXT_MENU_DOWNLOAD = Menu.FIRST + 12;
	private static final int CONTEXT_MENU_COPY = Menu.FIRST + 13;
	private static final int CONTEXT_MENU_SEND_MAIL = Menu.FIRST + 14;
	private static final int CONTEXT_MENU_SHARE = Menu.FIRST + 15;

	private Activity mActivity;
	private ViewGroup mRootLayout;
	private ViewGroup mErrorLayout;
	private View mBackBtn;
	private View mRefreshBtn;
	
	private CustomWebView mCustomWebView;
	private ValueCallback<Uri> mUploadMessage;
	private Bitmap mDefaultVideoPoster = null;
	private View mVideoProgressView = null;
	private View mCustomView;
	private FrameLayout mFullscreenContainer;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
	private WebviewFragmentCallback mWebviewFragmentCallback;

	protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);

	public WebviewFragment() {
		setType(BaseFragment.TYPE_WEB_FRAGMENT);

		// layout = new RelativeLayout(context);
		// RelativeLayout.LayoutParams layoutParams = new
		// RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT);
		// layout.setLayoutParams(layoutParams);
		// mCustomWebView = new CustomWebView(context);
		// RelativeLayout.LayoutParams webParams = new
		// RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT);
		// mCustomWebView.setLayoutParams(webParams);
		// layout.addView(mCustomWebView);
		// initializeCurrentWebView();
	}

	public CustomWebView getCustomWebView() {
		return mCustomWebView;
	}

	public View getCustomView() {
		return mCustomView;
	}

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
		if (mRootLayout == null) {
			mRootLayout = (ViewGroup) inflater.inflate(R.layout.webview, null,
					false);
			findView();
		}
		return mRootLayout;
	}
	
	private void findView() {
		mCustomWebView = (CustomWebView) mRootLayout.findViewById(R.id.webview);
		mErrorLayout = (ViewGroup) mRootLayout.findViewById(R.id.errorLayout);
		
		mBackBtn = mRootLayout.findViewById(R.id.backBtn);
		mBackBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mRefreshBtn = mRootLayout.findViewById(R.id.refreshBtn);
		mRefreshBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mErrorLayout.setVisibility(View.GONE);
				mCustomWebView.setVisibility(View.VISIBLE);
				mCustomWebView.reload();
			}
		});
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		initializeCurrentWebView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initializeCurrentWebView() {

		mCustomWebView.setWebViewClient(new CustomWebViewClient(this));
		mCustomWebView.setOnTouchListener(this);
		
		mCustomWebView
				.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						
						/*长按webView上所选内容，可触发OnLongClickListener，以此可以来判断所选内容的类型，来做不同的处理。这类需要了解一下getHitTestResult()函数的使用。
						
						publicWebView.HitTestResult getHitTestResult ()

						Since: API Level 1 Return a HitTestResult based on the currentcursor node. If a HTML::a tag is found and the anchor has anon-JavaScript url, the HitTestResult type is set toSRC_ANCHOR_TYPE and the url is set in the "extra" field. If theanchor does not have a url or if it is a JavaScript url, the typewill be UNKNOWN_TYPE and the url has to be retrieved throughrequestFocusNodeHref(Message) asynchronously. If a HTML::img tag isfound, the HitTestResult type is set to IMAGE_TYPE and the url isset in the "extra" field. A type of SRC_IMAGE_ANCHOR_TYPE indicatesan anchor with a url that has an image as a child node. If a phonenumber is found, the HitTestResult type is set to PHONE_TYPE andthe phone number is set in the "extra" field of HitTestResult. If amap address is found, the HitTestResult type is set to GEO_TYPE andthe address is set in the "extra" field of HitTestResult. If anemail address is found, the HitTestResult type is set to EMAIL_TYPEand the email is set in the "extra" field of HitTestResult.Otherwise, HitTestResult type is set to UNKNOWN_TYPE.

						WebView.HitTestResult

						Constants

						int ANCHOR_TYPE HitTestResult for hitting a HTML::a tag 

						int EDIT_TEXT_TYPE HitTestResult for hitting an edit textarea 

						int EMAIL_TYPE HitTestResult for hitting an emailaddress 

						int GEO_TYPE HitTestResult for hitting a map address 

						int IMAGE_ANCHOR_TYPE HitTestResult for hitting a HTML::a tag whichcontains HTML::img 

						int IMAGE_TYPE HitTestResult for hitting an HTML::imgtag 

						int PHONE_TYPE HitTestResult for hitting a phone number 

						int SRC_ANCHOR_TYPE HitTestResult for hitting a HTML::a tag withsrc=http 

						int SRC_IMAGE_ANCHOR_TYPE HitTestResult for hitting a HTML::a tagwith src=http + HTML::img 

						int UNKNOWN_TYPE Default HitTestResult, where the target isunknown*/
						
						HitTestResult result = ((WebView) v).getHitTestResult();

						int resultType = result.getType();

						CustomWebContextMenu customWebContextMenu = new CustomWebContextMenu(mActivity, resultType);
						customWebContextMenu.show(v, (int)mRawX, (int)mRawY);
						
						if ((resultType == HitTestResult.ANCHOR_TYPE)
								|| (resultType == HitTestResult.IMAGE_ANCHOR_TYPE)
								|| (resultType == HitTestResult.SRC_ANCHOR_TYPE)
								|| (resultType == HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {

							Intent i = new Intent();
							i.putExtra(Constants.EXTRA_ID_URL,
									result.getExtra());

							MenuItem item = menu.add(0, CONTEXT_MENU_OPEN, 0,
									R.string.Main_MenuOpen);
							item.setIntent(i);

							item = menu.add(0, CONTEXT_MENU_OPEN_IN_NEW_TAB, 0,
									R.string.Main_MenuOpenNewTab);
							item.setIntent(i);

							item = menu.add(0, CONTEXT_MENU_COPY, 0,
									R.string.Main_MenuCopyLinkUrl);
							item.setIntent(i);

							item = menu.add(0, CONTEXT_MENU_DOWNLOAD, 0,
									R.string.Main_MenuDownload);
							item.setIntent(i);

							item = menu.add(0, CONTEXT_MENU_SHARE, 0,
									R.string.Main_MenuShareLinkUrl);
							item.setIntent(i);

							menu.setHeaderTitle(result.getExtra());
						} else if (resultType == HitTestResult.IMAGE_TYPE) {
							Intent i = new Intent();
							i.putExtra(Constants.EXTRA_ID_URL,
									result.getExtra());

							MenuItem item = menu.add(0, CONTEXT_MENU_OPEN, 0,
									R.string.Main_MenuViewImage);
							item.setIntent(i);

							item = menu.add(0, CONTEXT_MENU_COPY, 0,
									R.string.Main_MenuCopyImageUrl);
							item.setIntent(i);

							item = menu.add(0, CONTEXT_MENU_DOWNLOAD, 0,
									R.string.Main_MenuDownloadImage);
							item.setIntent(i);

							item = menu.add(0, CONTEXT_MENU_SHARE, 0,
									R.string.Main_MenuShareImageUrl);
							item.setIntent(i);

							menu.setHeaderTitle(result.getExtra());

						} else if (resultType == HitTestResult.EMAIL_TYPE) {

							Intent sendMail = new Intent(Intent.ACTION_VIEW,
									Uri.parse(WebView.SCHEME_MAILTO
											+ result.getExtra()));

							MenuItem item = menu.add(0, CONTEXT_MENU_SEND_MAIL,
									0, R.string.Main_MenuSendEmail);
							item.setIntent(sendMail);

							Intent i = new Intent();
							i.putExtra(Constants.EXTRA_ID_URL,
									result.getExtra());

							item = menu.add(0, CONTEXT_MENU_COPY, 0,
									R.string.Main_MenuCopyEmailUrl);
							item.setIntent(i);

							item = menu.add(0, CONTEXT_MENU_SHARE, 0,
									R.string.Main_MenuShareEmailUrl);
							item.setIntent(i);

							menu.setHeaderTitle(result.getExtra());
						} else {
							Intent i = new Intent();
							i.putExtra(Constants.EXTRA_ID_URL,
									result.getExtra());

							MenuItem item = menu.add(0, CONTEXT_MENU_OPEN, 0,
									R.string.Main_MenuOpen);
							item.setIntent(i);

							item = menu.add(0, CONTEXT_MENU_OPEN_IN_NEW_TAB, 0,
									R.string.Main_MenuOpenNewTab);
							item.setIntent(i);

							item = menu.add(0, CONTEXT_MENU_COPY, 0,
									R.string.Main_MenuCopyLinkUrl);
							item.setIntent(i);

							item = menu.add(0, CONTEXT_MENU_SHARE, 0,
									R.string.Main_MenuShareLinkUrl);
							item.setIntent(i);

							menu.setHeaderTitle(result.getExtra());
						}
					}

				});

		mCustomWebView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				doDownloadStart(url, userAgent, contentDisposition, mimetype,
						contentLength);
			}

		});

		mCustomWebView.setWebChromeClient(new WebChromeClient() {

			@SuppressWarnings("unused")
			// This is an undocumented method, it _is_ used, whatever Eclipse
			// may think :)
			// Used to show a file chooser dialog.
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				mActivity.startActivityForResult(Intent.createChooser(i,
						mActivity.getString(R.string.Main_FileChooserPrompt)),
						MainActivity.OPEN_FILE_CHOOSER_ACTIVITY);
			}

			@SuppressWarnings("unused")
			// This is an undocumented method, it _is_ used, whatever Eclipse
			// may think :)
			// Used to show a file chooser dialog.
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				mActivity.startActivityForResult(Intent.createChooser(i,
						mActivity.getString(R.string.Main_FileChooserPrompt)),
						MainActivity.OPEN_FILE_CHOOSER_ACTIVITY);
			}

			@Override
			public Bitmap getDefaultVideoPoster() {
				if (mDefaultVideoPoster == null) {
					mDefaultVideoPoster = BitmapFactory.decodeResource(
							mActivity.getResources(),
							R.drawable.default_video_poster);
				}

				return mDefaultVideoPoster;
			}

			@Override
			public View getVideoLoadingProgressView() {
				if (mVideoProgressView == null) {
					LayoutInflater inflater = LayoutInflater.from(mActivity);
					mVideoProgressView = inflater.inflate(
							R.layout.video_loading_progress, null);
				}

				return mVideoProgressView;
			}

			public void onShowCustomView(View view,
					WebChromeClient.CustomViewCallback callback) {
				showCustomView(view, callback);
			}

			@Override
			public void onHideCustomView() {
				hideCustomView();
			}

			// @Override
			// public void onShowCustomView(View view, CustomViewCallback
			// callback) {
			// super.onShowCustomView(view, callback);
			//
			// if (view instanceof FrameLayout) {
			// mCustomViewContainer = (FrameLayout) view;
			// mCustomViewCallback = callback;
			//
			// mContentView = (LinearLayout) findViewById(R.id.MainContainer);
			//
			// if (mCustomViewContainer.getFocusedChild() instanceof VideoView)
			// {
			// mCustomVideoView = (VideoView)
			// mCustomViewContainer.getFocusedChild();
			// // frame.removeView(video);
			// mContentView.setVisibility(View.GONE);
			// mCustomViewContainer.setVisibility(View.VISIBLE);
			//
			// setContentView(mCustomViewContainer);
			// //mCustomViewContainer.bringToFront();
			//
			// mCustomVideoView.setOnCompletionListener(new
			// OnCompletionListener() {
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// mp.stop();
			// onHideCustomView();
			// }
			// });
			//
			// mCustomVideoView.setOnErrorListener(new OnErrorListener() {
			// @Override
			// public boolean onError(MediaPlayer mp, int what, int extra) {
			// onHideCustomView();
			// return true;
			// }
			// });
			//
			// mCustomVideoView.start();
			// }
			//
			// }
			// }
			//
			// @Override
			// public void onHideCustomView() {
			// super.onHideCustomView();
			//
			// if (mCustomVideoView == null) {
			// return;
			// }
			//
			// mCustomVideoView.setVisibility(View.GONE);
			// mCustomViewContainer.removeView(mCustomVideoView);
			// mCustomVideoView = null;
			//
			// mCustomViewContainer.setVisibility(View.GONE);
			// mCustomViewCallback.onCustomViewHidden();
			//
			// mContentView.setVisibility(View.VISIBLE);
			// setContentView(mContentView);
			// }

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				((CustomWebView) view).setProgress(newProgress);
				// mProgressBar.setProgress(mCurrentWebView.getProgress());
				if (mWebviewFragmentCallback != null) {
					mWebviewFragmentCallback.onProgressChanged(newProgress);
				}
			}

			@Override
			public void onReceivedIcon(WebView view, Bitmap icon) {
				new Thread(new FaviconUpdaterRunnable(mActivity, view.getUrl(),
						view.getOriginalUrl(), icon)).start();
				// updateFavIcon();
				if (mWebviewFragmentCallback != null) {
					mWebviewFragmentCallback.onReceivedIcon(icon);
				}

				super.onReceivedIcon(view, icon);
			}

			@Override
			public boolean onCreateWindow(WebView view, final boolean dialog,
					final boolean userGesture, final Message resultMsg) {

				WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;

				// addTab(false, mViewFlipper.getDisplayedChild());
				if (mWebviewFragmentCallback != null) {
					mWebviewFragmentCallback.onCreateWindow();
				}

				transport.setWebView(mCustomWebView);
				resultMsg.sendToTarget();

				return true;
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				// setTitle(String.format(getResources().getString(R.string.ApplicationNameUrl),
				// title));

				startHistoryUpdaterRunnable(title, mCustomWebView.getUrl(),
						mCustomWebView.getOriginalUrl());

				super.onReceivedTitle(view, title);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final JsResult result) {
				new AlertDialog.Builder(mActivity)
						.setTitle(R.string.Commons_JavaScriptDialog)
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new AlertDialog.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								}).setCancelable(false).create().show();

				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {
				new AlertDialog.Builder(mActivity)
						.setTitle(R.string.Commons_JavaScriptDialog)
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.cancel();
									}
								}).create().show();

				return true;
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, final JsPromptResult result) {

				final LayoutInflater factory = LayoutInflater.from(mActivity);
				final View v = factory.inflate(
						R.layout.javascript_prompt_dialog, null);
				((TextView) v.findViewById(R.id.JavaScriptPromptMessage))
						.setText(message);
				((EditText) v.findViewById(R.id.JavaScriptPromptInput))
						.setText(defaultValue);

				new AlertDialog.Builder(mActivity)
						.setTitle(R.string.Commons_JavaScriptDialog)
						.setView(v)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										String value = ((EditText) v
												.findViewById(R.id.JavaScriptPromptInput))
												.getText().toString();
										result.confirm(value);
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										result.cancel();
									}
								})
						.setOnCancelListener(
								new DialogInterface.OnCancelListener() {
									public void onCancel(DialogInterface dialog) {
										result.cancel();
									}
								}).show();

				return true;

			}

		});

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();

		mCustomWebView.doOnResume();
	}

	@Override
	public void onPause() {
		super.onPause();

		mCustomWebView.doOnPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (mRootLayout != null) {
			ViewGroup parentViewGroup = (ViewGroup) mRootLayout.getParent();
			if (parentViewGroup != null) {
				parentViewGroup.removeView(mRootLayout);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	private void showCustomView(View view,
			WebChromeClient.CustomViewCallback callback) {
		// if a view already exists then immediately terminate the new one
		if (mCustomView != null) {
			callback.onCustomViewHidden();
			return;
		}
		// MainActivity.this.getWindow().getDecorView();

		FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
		mFullscreenContainer = new FullscreenHolder(mActivity);
		mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
		decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
		mCustomView = view;
		setStatusBarVisibility(false);
		mCustomViewCallback = callback;
	}

	private void hideCustomView() {
		if (mCustomView == null)
			return;

		setStatusBarVisibility(true);
		FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
		decor.removeView(mFullscreenContainer);
		mFullscreenContainer = null;
		mCustomView = null;
		mCustomViewCallback.onCustomViewHidden();
	}

	private void setStatusBarVisibility(boolean visible) {
		int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
		mActivity.getWindow().setFlags(flag,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * Initiate a download. Check the SD card and start the download runnable.
	 * 
	 * @param url
	 *            The url to download.
	 * @param userAgent
	 *            The user agent.
	 * @param contentDisposition
	 *            The content disposition.
	 * @param mimetype
	 *            The mime type.
	 * @param contentLength
	 *            The content length.
	 */
	private void doDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {

		if (ApplicationUtils.checkCardState(mActivity, true)) {
			DownloadItem item = new DownloadItem(mActivity, url);
			Controller.getInstance().addToDownload(item);
			item.startDownload();

			Toast.makeText(mActivity,
					getString(R.string.Main_DownloadStartedMsg),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Start a runnable to update history.
	 * 
	 * @param title
	 *            The page title.
	 * @param url
	 *            The page url.
	 */
	private void startHistoryUpdaterRunnable(String title, String url,
			String originalUrl) {
		if ((url != null) && (url.length() > 0)) {
			new Thread(new HistoryUpdater(mActivity, title, url, originalUrl))
					.start();
		}
	}

	static class FullscreenHolder extends FrameLayout {

		public FullscreenHolder(Context ctx) {
			super(ctx);
			setBackgroundColor(ctx.getResources().getColor(
					android.R.color.black));
		}

		@Override
		public boolean onTouchEvent(MotionEvent evt) {
			return true;
		}

	}
	
	public void setWebviewFragmentCallback(WebviewFragmentCallback callback) {
		mWebviewFragmentCallback = callback;
	}

	public interface WebviewFragmentCallback {
		public void onProgressChanged(int newProgress);

		public void onReceivedIcon(Bitmap icon);

		public void onCreateWindow();
	}

	// --------------CustomWebViewClientCallback------------------------------------------

	@Override
	public void onPageFinished(String url) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageStarted(String url) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onReceivedError(String url) {
		mErrorLayout.setVisibility(View.VISIBLE);
		mCustomWebView.setVisibility(View.GONE);
	}

	@Override
	public void onExternalApplicationUrl(String url) {
		try {

			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(i);

		} catch (Exception e) {

			// Notify user that the vnd url cannot be viewed.
			new AlertDialog.Builder(mActivity)
					.setTitle(R.string.Main_VndErrorTitle)
					.setMessage(
							String.format(
									getString(R.string.Main_VndErrorMessage),
									url))
					.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).setCancelable(true).create().show();
		}
	}

	@Override
	public void onMailTo(String url) {
		Intent sendMail = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(sendMail);
	}

	@Override
	public void onUrlLoading(String url) {
		// TODO Auto-generated method stub
		((MainActivity)mActivity).getController().addTab(false);
		((MainActivity)mActivity).getController().navigateToUrl(url);
	}

	@Override
	public void setHttpAuthUsernamePassword(String host, String realm,
			String username, String password) {
		mCustomWebView.setHttpAuthUsernamePassword(host, realm, username, password);

	}

	// ------------OnTouchListener--------------------------------------------

	private float mRawX;
	private float mRawY;
	
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		
		mRawX = arg1.getRawX();
		mRawY = arg1.getRawY();
		
		LogUtils.i("arg1.getRawX()=" + arg1.getRawX() + "ar1.getRawY()=" + arg1.getRawY() );
		return false;
	}

	/**
	 * Get a Drawable of the current favicon, with its size normalized relative
	 * to current screen density.
	 * 
	 * @return The normalized favicon.
	 */
	private BitmapDrawable getNormalizedFavicon() {

		BitmapDrawable favIcon = new BitmapDrawable(getResources(),
				mCustomWebView.getFavicon());

		if (mCustomWebView.getFavicon() != null) {
			int imageButtonSize = ApplicationUtils
					.getImageButtonSize(mActivity);
			int favIconSize = ApplicationUtils.getFaviconSize(mActivity);

			Bitmap bm = Bitmap.createBitmap(imageButtonSize, imageButtonSize,
					Bitmap.Config.ARGB_4444);
			Canvas canvas = new Canvas(bm);

			favIcon.setBounds((imageButtonSize / 2) - (favIconSize / 2),
					(imageButtonSize / 2) - (favIconSize / 2),
					(imageButtonSize / 2) + (favIconSize / 2),
					(imageButtonSize / 2) + (favIconSize / 2));
			favIcon.draw(canvas);

			favIcon = new BitmapDrawable(getResources(), bm);
		}

		return favIcon;
	}

}
