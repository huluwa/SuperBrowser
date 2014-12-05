package org.zirco.ui.activities.sharePreference;


import android.content.Context;

/**
 * SharedPreference管理类
 * 
 */
public class SettingManager {

    public final static String LOGIN_STATE = "login_state";
    
    public static void setLoginState(Context context, String state) {
        SettingHelper.setEditor(context, LOGIN_STATE, state);
    }
    
    public static String getLoginState(Context context) {
        return SettingHelper.getSharedPreferences(context, LOGIN_STATE, "");
    }

}
