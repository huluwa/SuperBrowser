package com.wuyu.android.client.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

/**
 * Utility for Check Storage.
 * 
 * <p/>Convenience for check and get data from Storage ExternalMemory (SD card).
 * 
 * @since 1.0, 04.23.2011
 * @version 1.0, 04.23.2011
 */
public class StorageCheckor {
	
	
	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		return status.equals(Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * Check sdcard.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkSdcard(Context context){
		if(TextUtils.isEmpty(getExternalMemoryPath())) {
			return false;
		}
		if(getAvailableExternalMemorySize() <= 0) {
			return false;
		}
		return true;
	}
	
	public static boolean checkExceedSdcardSize(long fileSize) {
		long s = StorageCheckor.getAvailableExternalMemorySize();
		if(s <= 0)
			return false;
		return fileSize >= s;
	}
	
	
	/**
	 * Get ExternalMemory file Path.
	 * 
	 * @return
	 */
	public static String getExternalMemoryPath() {
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)) {  
	        File file = Environment.getExternalStorageDirectory();
	        if(null == file)
	        	return "";
	        if(file.exists()) 
	        	return file.getAbsolutePath();
		}
		return "";
	}
	
	/**
	 * GetTotalInternalMemorySize.
	 * 
	 * @return
	 */
	public static long getTotalInternalMemorySize() {  
        File path = Environment.getDataDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long totalBlocks = stat.getBlockCount();  
        return totalBlocks * blockSize;
    }  

	/**
	 * GetAvailableInternalMemorySize.
	 * 
	 * @return
	 */
    public static long getAvailableInternalMemorySize() {  
        File path = Environment.getDataDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long availableBlocks = stat.getAvailableBlocks();  
        return availableBlocks * blockSize;  
    }  
    
    /**
     * GetAvailableExternalMemorySize.
     * 
     * @return
     */
    public static long getAvailableExternalMemorySize() {  
        long availableExternalMemorySize = 0;  
        try
		{
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			{
				String path = Environment.getExternalStorageDirectory().getPath();
				StatFs stat = new StatFs(path);
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				availableExternalMemorySize = availableBlocks * blockSize;
			}
			else if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED))
			{
				availableExternalMemorySize = 0;
			}
		}
		catch (Exception e)
		{
		}
        
        return availableExternalMemorySize;  
    }  
  
    /**
     * GetTotalExternalMemorySize.
     * 
     * @return
     */
    public static long getTotalExternalMemorySize() {  
        long totalExternalMemorySize = 0;  
        if (Environment.getExternalStorageState().equals(  
                Environment.MEDIA_MOUNTED)) {  
            File path = Environment.getExternalStorageDirectory();  
            StatFs stat = new StatFs(path.getPath());  
            long blockSize = stat.getBlockSize();  
            long totalBlocks = stat.getBlockCount();  
            totalExternalMemorySize = totalBlocks * blockSize;  
        } else if (Environment.getExternalStorageState().equals(  
                Environment.MEDIA_REMOVED)) {  
            totalExternalMemorySize = 0;  
        }
        return totalExternalMemorySize;  
    }  
}