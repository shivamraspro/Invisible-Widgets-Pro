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
        particualar widget instance but different for diiferent widget instances.
        */

        if(showWidgets) {
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
                views.setCharSequence(R.id.widget_id_visible, "setText", "#"+appWidgetId);
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

    @Override
    public void onReceive(Context context, Intent intent) {
       if(intent.getAction().equals(AppConstants.MANUAL_WIDGET_UPDATE)) {
           AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
           int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                   getClass()));

           showWidgets =  intent.getBooleanExtra(AppConstants.CONFIG_MODE_KEY, false);

           onUpdate(context, appWidgetManager, appWidgetIds);
       }
       /*
       Workaround to delete a widget since onDeleted is not called while deleting a widget
       */
       else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_DELETED)) {
           int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                   AppWidgetManager.INVALID_APPWIDGET_ID);

           if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
               this.onDeleted(context, new int[] { appWidgetId });
           }
       }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds) {
            SharedPrefHelper.deletePackageNameForId(context, appWidgetId);
        }
    }
}
