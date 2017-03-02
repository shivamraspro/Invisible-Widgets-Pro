package com.shivam.invisiblewidgetspro.utils;

/**
 * Created by shivam on 18/02/17.
 */

public class AppConstants {
    public static final String CONFIG_MODE_KEY = "config_mode";

    public static final String PACKAGE_NAME_KEY = "package_name";
    public static final String WIDGET_ID_KEY = "widget_id";

    public static final String APP_SELECTOR_FRAGMENT_TAG = "app_selector";

    //Shared Prefs File Names
    public static final String CONFIG_MODE_FILE_KEY = "com.shivam.invisiblewidgetspro.CONFIG_MODE_FILE_KEY";
    public static final String WIDGETS_MAP_KEY = "com.shivam.invisiblewidgetspro.WIDGETS_FILE_KEY";

    public static final String PACKAGE_NAME_NOT_FOUND = "package_name_not_found";

    public static final String MANUAL_WIDGET_UPDATE =
            "com.shivam.invisiblewidgetspro.MANUAL_APPWIDGET_UPDATE";

    public static String getDummyUniqueAction(int widgetId) {
        return "DUMMY_UNIQUE_ACTION_" + widgetId;
    }
}
