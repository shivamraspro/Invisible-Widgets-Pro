package com.shivam.invisiblewidgetspro.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.shivam.invisiblewidgetspro.R;
import com.shivam.invisiblewidgetspro.ui.ConfigurationActivity;

/**
 * Created by shivam on 10/02/17.
 */

public class WidgetProvider extends AppWidgetProvider {

    private boolean isConfigMode = false;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Intent intent;
        PendingIntent pendingIntent;
        RemoteViews views;
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.widgets_id_to_package_file_key),
                Context.MODE_PRIVATE);
        String packageName;

        if(isConfigMode) {
            //todo show app widget id
            for (int appWidgetId : appWidgetIds) {
                packageName = sharedPref.getString(appWidgetId + "", context.getPackageName());

                intent = new Intent(context, ConfigurationActivity.class);
                intent.putExtra("packageName", packageName);
                intent.putExtra("widgetId", appWidgetId);
//                intent.putExtra("is_config_mode", isConfigMode);
                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                views = new RemoteViews(context.getPackageName(), R.layout.widget_visible);
                views.setOnClickPendingIntent(R.id.visible_widget_layout, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        } else {
            for (int appWidgetId : appWidgetIds) {
                packageName = sharedPref.getString(appWidgetId + "", context.getPackageName());

                intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                if(packageName.equals(context.getPackageCodePath())) {
                    intent.putExtra("packageName", packageName);
                    intent.putExtra("widgetId", appWidgetId);
//                    intent.putExtra("is_config_mode", isConfigMode);
                }

                views = new RemoteViews(context.getPackageName(), R.layout.widget_invisible);
                views.setOnClickPendingIntent(R.id.invisible_widget_layout, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       if(intent.getAction().equals("com.shivam.invisiblewidgetspro.MANUAL_APPWIDGET_UPDATE")) {
           AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
           int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                   getClass()));

           isConfigMode = intent.getBooleanExtra("is_config_mode", false);

           onUpdate(context, appWidgetManager, appWidgetIds);

       }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.widgets_id_to_package_file_key),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        for(int appWidgetId : appWidgetIds) {
            editor.remove(appWidgetId+"");
        }
        editor.apply();
    }
}
