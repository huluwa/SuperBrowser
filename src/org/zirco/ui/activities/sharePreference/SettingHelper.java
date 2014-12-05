package org.zirco.ui.activities.sharePreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * sharedpreferences封装类
 * @author chenyueguo
 *
 */
class SettingHelper {
	
    private static SharedPreferences.Editor editor = null;
    private static SharedPreferences sharedPreferences = null;
    
    //---------------------------默认sharepreference---------------------------
    
    private static SharedPreferences getSharedPreferencesObject(Context context) {
        if (sharedPreferences == null) {
        	sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sharedPreferences;
    }

    private static SharedPreferences.Editor getEditorObject(Context context) {
        if (editor == null) {
        	editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        }
        return editor;
    }

	public static int getSharedPreferences(Context context, String key, int defValue) {
        return getSharedPreferencesObject(context).getInt(key, defValue);
    }

    public static long getSharedPreferences(Context context, String key, long defValue) {
        return getSharedPreferencesObject(context).getLong(key, defValue);
    }
    
    public static float getSharedPreferences(Context context, String key, float defValue) {
        return getSharedPreferencesObject(context).getFloat(key, defValue);
    }

    public static Boolean getSharedPreferences(Context context, String key, Boolean defValue) {
        return getSharedPreferencesObject(context).getBoolean(key, defValue);
    }

    public static String getSharedPreferences(Context context, String key, String defValue) {
        return getSharedPreferencesObject(context).getString(key, defValue);
    }

    public static void setEditor(Context context, String key, int value) {
        getEditorObject(context).putInt(key, value).commit();
    }

    public static void setEditor(Context context, String key, long value) {
        getEditorObject(context).putLong(key, value).commit();
    }
    
    public static void setEditor(Context context, String key, float value) {
        getEditorObject(context).putFloat(key, value).commit();
    }

    public static void setEditor(Context context, String key, Boolean value) {
        getEditorObject(context).putBoolean(key, value).commit();
    }

    public static void setEditor(Context context, String key, String value) {
        getEditorObject(context).putString(key, value).commit();
    }
    
    public static void removeKey(Context context, String key) {
        getEditorObject(context).remove(key).commit();
    }
    
    public static void clearSharedPreferences(Context context) {
    	getEditorObject(context).clear().commit();
    }
    
	public void registerOnSharedPreferenceChangeListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
		getSharedPreferencesObject(context).registerOnSharedPreferenceChangeListener(listener);
	}
	
	public void unregisterOnSharedPreferenceChangeListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
		getSharedPreferencesObject(context).unregisterOnSharedPreferenceChangeListener(listener);
	}
    
    //---------------------------自定义sharePreference---------------------------
    
    private static SharedPreferences getCustomSharedPreferencesObject(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
    
    private static SharedPreferences.Editor getCustomEditorObject(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE).edit();
    }
    
    public static int getCustomSharedPreferences(Context context, String name, String key, int defValue) {
        return getCustomSharedPreferencesObject(context, name).getInt(key, defValue);
    }

    public static long getCustomSharedPreferences(Context context, String name, String key, long defValue) {
        return getCustomSharedPreferencesObject(context, name).getLong(key, defValue);
    }
    
    public static float getCustomSharedPreferences(Context context, String name, String key, float defValue) {
        return getCustomSharedPreferencesObject(context, name).getFloat(key, defValue);
    }

    public static Boolean getCustomSharedPreferences(Context context, String name, String key, Boolean defValue) {
        return getCustomSharedPreferencesObject(context, name).getBoolean(key, defValue);
    }

    public static String getCustomSharedPreferences(Context context, String name, String key, String defValue) {
        return getCustomSharedPreferencesObject(context, name).getString(key, defValue);
    }

    public static void setCustomEditor(Context context, String name, String key, int value) {
    	getCustomEditorObject(context, name).putInt(key, value).commit();
    }

    public static void setCustomEditor(Context context, String name, String key, long value) {
    	getCustomEditorObject(context, name).putLong(key, value).commit();
    }
    
    public static void setCustomEditor(Context context, String name, String key, float value) {
    	getCustomEditorObject(context, name).putFloat(key, value).commit();
    }

    public static void setCustomEditor(Context context, String name, String key, Boolean value) {
    	getCustomEditorObject(context, name).putBoolean(key, value).commit();
    }

    public static void setCustomEditor(Context context, String name, String key, String value) {
    	getCustomEditorObject(context, name).putString(key, value).commit();
    }
    
    public static void removeCustomKey(Context context, String name, String key) {
    	getCustomEditorObject(context, name).remove(key).commit();
    }
    
    public static void clearCustomSharedPreferences(Context context, String name) {
    	getCustomEditorObject(context, name).clear().commit();
    }
    
}
