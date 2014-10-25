package org.zirco.ui.menu;

public interface NavigatBarCallback {

	public void onFindPrevious(String target);
	
	public void onFindNext(String target);
	
	public void onNavigateToHome();
	
	public void onNavigateToUrl(String url);
	
	public void onSharePage();
}
