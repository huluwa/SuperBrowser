package org.zirco.ui.menu;

public interface ToolBarCallback {

	public void navigatePrevious();
	
	public void navigateNext();
	
	public void onQuickButton();
	
	public void onCreateTabPage();
	
	public void onRemoveTabPage();
	
	public void onOpenAddBookmarkDialog();
	
	public void onOpenBookmarksHistoryActivity();
	
	public void onOpenDownloadsList();
	
	public void onOpenPreferences();
	
	public void onQuit();
	
}
