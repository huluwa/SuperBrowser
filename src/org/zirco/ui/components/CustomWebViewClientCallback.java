package org.zirco.ui.components;


public interface CustomWebViewClientCallback {

	public void onPageFinished(String url);
	
	public void onPageStarted(String url);
	
	public void onReceivedError(String url); //加载失败
	
	public void onExternalApplicationUrl(String url); //外部URL
	
	public void onMailTo(String url); //邮件URL
	
	public void onUrlLoading(String url);
	
	public void setHttpAuthUsernamePassword(String host, String realm, String username, String password);
}
