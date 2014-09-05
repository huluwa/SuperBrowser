/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.zirco.ui.activities;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.zirco.R;
import org.zirco.controllers.Controller;
import org.zirco.events.EventConstants;
import org.zirco.events.EventController;
import org.zirco.events.IDownloadEventsListener;
import org.zirco.model.adapters.DownloadListAdapter;
import org.zirco.model.items.DownloadItem;
import org.zirco.utils.StorageCheckor;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Download list activity.
 */
public class DownloadsListActivity extends ListActivity implements IDownloadEventsListener {
	
	private static final int MENU_CLEAR_DOWNLOADS = Menu.FIRST;
	
	private TextView capacityTextV;
	private ProgressBar capacityPBar;
	private DownloadListAdapter mAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloads_list_activity);
        
        setTitle(R.string.DownloadListActivity_Title);
        
        EventController.getInstance().addDownloadListener(this);
        capacityTextV = (TextView) findViewById(R.id.textv_capacity);
		capacityPBar = (ProgressBar) findViewById(R.id.progressbar_capacity);
        fillData();
        
        updateSdcardSpace();
	}
	
	@Override
	protected void onDestroy() {
		EventController.getInstance().removeDownloadListener(this);
		super.onDestroy();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	MenuItem item = menu.add(0, MENU_CLEAR_DOWNLOADS, 0, R.string.DownloadListActivity_RemoveCompletedDownloads);
        item.setIcon(R.drawable.ic_menu_delete);
        
        return true;
	}
	
	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch(item.getItemId()) {
		case MENU_CLEAR_DOWNLOADS:
			Controller.getInstance().clearCompletedDownloads();
			fillData();
			return true;
		default: return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
				
		DownloadItem item = (DownloadItem) mAdapter.getItem(position);
		if (item != null) {
			
			if (item.isFinished() &&
					!item.isAborted() &&
					item.getErrorMessage() == null) {
				
				File file = new File(item.getFilePath());
				if (file.exists()) {
				
					MimeTypeMap mime = MimeTypeMap.getSingleton();
					String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();
					String type = mime.getMimeTypeFromExtension(ext);
					
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setDataAndType(Uri.fromFile(file), type);

					try {

						startActivity(i);

					} catch (ActivityNotFoundException e) {
						new AlertDialog.Builder(this)
						.setTitle(R.string.Main_VndErrorTitle)
						.setMessage(String.format(getString(R.string.Main_VndErrorMessage), item.getFilePath()))
						.setPositiveButton(android.R.string.ok,
								new AlertDialog.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int which) { }
						})
						.setCancelable(true)
						.create()
						.show();
					}
				}
			}
		}
	}

	/**
	 * Fill the download list.
	 */
	private void fillData() {
		mAdapter = new DownloadListAdapter(this, Controller.getInstance().getDownloadList());
		setListAdapter(mAdapter);
	}

	@Override
	public void onDownloadEvent(String event, Object data) {
		if (event.equals(EventConstants.EVT_DOWNLOAD_ON_START)) {
			fillData();
		} else if (event.equals(EventConstants.EVT_DOWNLOAD_ON_PROGRESS)) {				
			if (data != null) {
				DownloadItem item = (DownloadItem) data;
				ProgressBar bar = mAdapter.getBarMap().get(item);
				if (bar != null) {
					bar.setMax(100);
					bar.setProgress(item.getProgress());
				}				
			}
		} else if (event.equals(EventConstants.EVT_DOWNLOAD_ON_FINISHED)) {
			if (data != null) {
				DownloadItem item = (DownloadItem) data;
				
				TextView title = mAdapter.getTitleMap().get(item);
				if (title != null) {
					if (item.isAborted()) {
						title.setText(String.format(getResources().getString(R.string.DownloadListActivity_Aborted), item.getFileName()));
					} else {
						title.setText(String.format(getResources().getString(R.string.DownloadListActivity_Finished), item.getFileName()));
					}
				}
				
				ProgressBar bar = mAdapter.getBarMap().get(item);
				if (bar != null) {					
					bar.setProgress(bar.getMax());
				}
				
				ImageButton button = mAdapter.getButtonMap().get(item);
				if (button != null) {
					button.setEnabled(false);
				}
			}
		}
		
	}
	
	/**
	 * 每次进来刷新下剩余空间
	 */
	public void updateSdcardSpace() {
		final long availableSize = StorageCheckor.getAvailableExternalMemorySize();
		final long totalSize = StorageCheckor.getTotalExternalMemorySize();
		capacityTextV.setText(getString(R.string.download_videos_manage_space,
				getGB_Number(availableSize, 1), getGB_Number(availableSize, 1)));
		
		int progress = 0;
		if (totalSize != 0) {
			progress = (int) (100 - (((float)availableSize / (float)totalSize) * 100));
		}
		capacityPBar.setProgress(progress);
	}
	
	public static String getGB_Number(long size, int bit) {
		if (size > 1024 * 1024 * 1024) {
			double div = (double) size / (1024 * 1024 * 1024.0);
			return getDecimalsVal(div, bit) + "G";
		} else if (size > 1024 * 1024) {
			double div = (double) size / (1024 * 1024.0);
			return getDecimalsVal(div, bit) + "M";
		} else if (size == 0) {
			return "0M";
		} else {
			return getMB_Decimal(size);
		}
	}

	public static String getDecimalsVal(double val, int bit) {
		BigDecimal bd = new BigDecimal(val);
		bd = bd.setScale(bit, BigDecimal.ROUND_HALF_UP);

		return bd.toString() + "";
	}

	public static String getMB_Number(long size) {

		int m = (int) (size / (1024 * 1024));
		if (m > 0) {
			return new DecimalFormat("0.0").format(size / (1024 * 1024.0)) + "M";
		}
		return getMB_Decimal(size);

	}

	public static String getMB_Decimal(long size) {

		double md = size / (1024 * 1024.0);

		String mS = new Double(new DecimalFormat(".0").format(md)).toString();

		int start = mS.lastIndexOf(".");

		if (start > 0) {

			int len = mS.substring(start + 1).length();

			if (len == 1) {

				return mS + "0MB";
			}

			return mS + "MB";
		}

		return mS + ".0MB";
	}
}
