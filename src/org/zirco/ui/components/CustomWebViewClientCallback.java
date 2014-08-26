package org.zirco.ui.components;


public interface CustomWebViewClientCallback {

	public void onPageFinished(String url);
	
	public void onPageStarted(String url);
	
	public void onExternalApplicationUrl(String url);
	
	public void onMailTo(String url);
	
	public void onUrlLoading(String url);
	
	public void setHttpAuthUsernamePassword(String host, String realm, String username, String password);
}
