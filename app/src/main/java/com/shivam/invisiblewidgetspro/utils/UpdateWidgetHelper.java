package com.shivam.invisiblewidgetspro.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by shivam on 18/02/17.
 */

public class UpdateWidgetHelper {
    public static void hideWidgets(Context context) {
        Intent hideWidgetsIntent =
                new Intent(AppConstants.MANUAL_WIDGET_UPDATE)
                        .setPackage(context.getPackageName());

        boolean showWidgets = false;

        hideWidgetsIntent.putExtra(AppConstants.CONFIG_MODE_KEY, showWidgets);

        context.sendBroadcast(hideWidgetsIntent);
    }

    public static void showWidgets(Context context) {
        Intent showWidgetsIntent =
                new Intent(AppConstants.MANUAL_WIDGET_UPDATE)
                        .setPackage(context.getPackageName());

        boolean showWidgets = true;

        showWidgetsIntent.putExtra(AppConstants.CONFIG_MODE_KEY, showWidgets);

        context.sendBroadcast(showWidgetsIntent);
    }
}
