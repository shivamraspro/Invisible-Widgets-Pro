package in.meegotech.invisiblewidgetspro.cache;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import in.meegotech.invisiblewidgetspro.R;
import in.meegotech.invisiblewidgetspro.ui.ConfigurationActivity;
import in.meegotech.invisiblewidgetspro.utils.AppConstants;
import in.meegotech.invisiblewidgetspro.utils.SharedPrefHelper;
import in.meegotech.invisiblewidgetspro.widget.WidgetProvider;

/**
 * Created by shivam on 21/03/17.
 */

public class AppListenerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent cacheAppsIntent = new Intent(context, AppsService.class);
        context.startService(cacheAppsIntent);

        //When an app is deleted, set the widget corresponding to it as a blank widget todo
        if(intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                    WidgetProvider.class));

            String removedPackage = intent.getDataString();

            for(int i=0; i < appWidgetIds.length; i++) {
                if(removedPackage.contains(SharedPrefHelper.getPackageNameForWidgetId
                        (context, appWidgetIds[i]))) {
                    SharedPrefHelper.setPackageNameForWidgetId(context, appWidgetIds[i],
                            AppConstants.PLACEHOLDER_WIDGET);

                    //Updating the widget associated with the deleted package and assigning it a
                    // new intent while in active config mode as it'll call the onNewIntent()
                    // method of the Configuration Activity as it is using singleTop as its
                    // launchMode
                    if(SharedPrefHelper.getConfigModeValue(context)) {
                        intent = new Intent(context, ConfigurationActivity.class);
                        intent.putExtra(AppConstants.PACKAGE_NAME_KEY, AppConstants.PLACEHOLDER_WIDGET);
                        intent.putExtra(AppConstants.WIDGET_ID_KEY, appWidgetIds[i]);
                        intent.setAction(AppConstants.getDummyUniqueAction(appWidgetIds[i]));
                        PendingIntent pendingIntent;
                        pendingIntent = PendingIntent.getActivity(context, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        RemoteViews views;
                        views = new RemoteViews(context.getPackageName(), R.layout.widget_visible);
                        views.setCharSequence(R.id.widget_id_visible, "setText", "#" +
                                appWidgetIds[i]);
                        views.setOnClickPendingIntent(R.id.visible_widget_layout, pendingIntent);

                        appWidgetManager.updateAppWidget(appWidgetIds[i], views);
                    } else {
                        RemoteViews views;
                        views = new RemoteViews(context.getPackageName(), R.layout.widget_invisible);
                        appWidgetManager.updateAppWidget(appWidgetIds[i], views);
                    }
                }
            }
        }

    }
}
