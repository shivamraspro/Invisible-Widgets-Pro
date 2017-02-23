package com.shivam.invisiblewidgetspro.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.shivam.invisiblewidgetspro.R;
import com.shivam.invisiblewidgetspro.ui.ConfigurationActivity;
import com.shivam.invisiblewidgetspro.utils.AppConstants;
import com.shivam.invisiblewidgetspro.utils.SharedPrefHelper;

/**
 * Created by shivam on 10/02/17.
 */

public class WidgetProvider extends AppWidgetProvider {

    private boolean showWidgets = false;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Intent intent;
        PendingIntent pendingIntent;
        RemoteViews views;
        String packageName;

        /*
        In ACTIVE Configuration Mode, the system considers all the pending intents to be identical
        since they all have the same target class i.e. ConfigurationActivity. Thus, the pending
        intents are updated for all the widget instances while updating the pending intent of any
        one of them, irrespective of the fact that they have different intent extras.

        To solve this, a dummy action is added for each widget. This action is identical for a
        particular widget instance but different for different widget instances.
        */

        if (showWidgets) {
            //show all widgets
            for (int appWidgetId : appWidgetIds) {
                packageName = SharedPrefHelper.getPackageNameForWidgetId(context, appWidgetId);

                intent = new Intent(context, ConfigurationActivity.class);
                intent.putExtra(AppConstants.PACKAGE_NAME_KEY, packageName);
                intent.putExtra(AppConstants.WIDGET_ID_KEY, appWidgetId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setAction(AppConstants.getDummyUniqueAction(appWidgetId));
                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                views = new RemoteViews(context.getPackageName(), R.layout.widget_visible);
                views.setCharSequence(R.id.widget_id_visible, "setText", "#" + appWidgetId);
                views.setOnClickPendingIntent(R.id.visible_widget_layout, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        } else {
            //hide all widgets
            for (int appWidgetId : appWidgetIds) {
                packageName = SharedPrefHelper.getPackageNameForWidgetId(context, appWidgetId);

                intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                intent.setAction(AppConstants.getDummyUniqueAction(appWidgetId));
                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                views = new RemoteViews(context.getPackageName(), R.layout.widget_invisible);
                views.setOnClickPendingIntent(R.id.invisible_widget_layout, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
        showWidgets = false;
    }

    /*
       IMPORTANT : onReceive() is meant to receive the broadcasts and then dispatch the calls
       to other methods like onUpdate(), onDelete() etc. So If you are overriding onReceive() and
       not writing code to call onDelete(), onDisabled etc, they would never be called.
    */
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(AppConstants.MANUAL_WIDGET_UPDATE) ||
                action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                    getClass()));

            showWidgets = intent.getBooleanExtra(AppConstants.CONFIG_MODE_KEY,
                    SharedPrefHelper.getConfigModeValue(context));

            this.onUpdate(context, appWidgetManager, appWidgetIds);
        } else if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                showWidgets = SharedPrefHelper.getConfigModeValue(context);
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                }
            }
        } else if (action.equals(AppWidgetManager.ACTION_APPWIDGET_DELETED)) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                final int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                this.onDeleted(context, new int[] { appWidgetId });
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                final int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                this.onDeleted(context, new int[]{appWidgetId});
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)
                    && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS)) {
                int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                Bundle widgetExtras = extras.getBundle(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS);
                this.onAppWidgetOptionsChanged(context, AppWidgetManager.getInstance(context),
                        appWidgetId, widgetExtras);
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(action)) {
            this.onEnabled(context);
        } else if (AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)) {
            this.onDisabled(context);
        } else if (AppWidgetManager.ACTION_APPWIDGET_RESTORED.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int[] oldIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_OLD_IDS);
                int[] newIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (oldIds != null && oldIds.length > 0) {
                    this.onRestored(context, oldIds, newIds);
                    this.onUpdate(context, AppWidgetManager.getInstance(context), newIds);
                }
            }
        }
    }

}
