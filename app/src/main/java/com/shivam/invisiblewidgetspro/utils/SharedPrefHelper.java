package com.shivam.invisiblewidgetspro.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shivam on 18/02/17.
 */

public class SharedPrefHelper {

    public static boolean getConfigModeValue(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(AppConstants.CONFIG_MODE_FILE_KEY,
                Context.MODE_PRIVATE);

        //Default value of Config Mode is FALSE for first install of the app
        return sharedPrefs.getBoolean(AppConstants.CONFIG_MODE_KEY, false);
    }

    public static void setConfigModeValue(Context context, boolean b) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(AppConstants.CONFIG_MODE_FILE_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(AppConstants.CONFIG_MODE_KEY, b);
        editor.apply();
    }

    public static String getPackageNameForWidgetId(Context context, int widgetId) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(AppConstants.WIDGETS_MAP_KEY,
                Context.MODE_PRIVATE);

        //Default value for package name is this.app for all newly installed widgets
        //This default value will be set by checking the returned value from this method
        return sharedPrefs.getString(widgetId + "", AppConstants.PACKAGE_NAME_NOT_FOUND);
    }

    public static void setPackageNameForWidgetId(Context context, int widgetId, String
            packageName) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(AppConstants.WIDGETS_MAP_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(widgetId + "", packageName);
        editor.apply();
    }

    public static void deletePackageNameForId(Context context, int widgetId) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                AppConstants.WIDGETS_MAP_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(widgetId + "");
        editor.apply();
    }
}
