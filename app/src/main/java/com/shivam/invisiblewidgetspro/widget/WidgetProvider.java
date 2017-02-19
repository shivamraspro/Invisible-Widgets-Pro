package com.shivam.invisiblewidgetspro.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.shivam.invisiblewidgetspro.R;
import com.shivam.invisiblewidgetspro.ui.ConfigurationActivity;
import com.shivam.invisiblewidgetspro.utils.AppConstants;
import com.shivam.invisiblewidgetspro.utils.SharedPrefHelper;

/**
 * Created by shivam on 10/02/17.
 */

public class WidgetProvider extends AppWidgetProvider {

 //   private boolean isConfigMode = false;

    private boolean showWidgets = false;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Intent intent;
        PendingIntent pendingIntent;
        RemoteViews views;
        String packageName;

        if(showWidgets) {
            //show all widgets
            for (int appWidgetId : appWidgetIds) {
                packageName = SharedPrefHelper.getPackageNameForWidgetId(context, appWidgetId);

                intent = new Intent(context, ConfigurationActivity.class);
                intent.putExtra(AppConstants.PACKAGE_NAME_KEY, packageName);
                intent.putExtra(AppConstants.WIDGET_ID_KEY, appWidgetId);
                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                views = new RemoteViews(context.getPackageName(), R.layout.widget_visible);
                views.setCharSequence(R.id.widget_id_visible, "setText", "#"+appWidgetId);
                views.setOnClickPendingIntent(R.id.visible_widget_layout, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        } else {
            //hide all widgets
            for (int appWidgetId : appWidgetIds) {
                packageName = SharedPrefHelper.getPackageNameForWidgetId(context, appWidgetId);

                intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                views = new RemoteViews(context.getPackageName(), R.layout.widget_invisible);
                views.setOnClickPendingIntent(R.id.invisible_widget_layout, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }

        showWidgets = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       if(intent.getAction().equals(AppConstants.MANUAL_WIDGET_UPDATE)) {
           AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
           int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                   getClass()));

           showWidgets =  intent.getBooleanExtra(AppConstants.CONFIG_MODE_KEY, false);

           onUpdate(context, appWidgetManager, appWidgetIds);

       }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds) {
            SharedPrefHelper.deletePackageNameForId(context, appWidgetId);
        }
    }
}
