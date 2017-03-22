package in.meegotech.invisiblewidgetspro.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import in.meegotech.invisiblewidgetspro.R;
import in.meegotech.invisiblewidgetspro.ui.ConfigurationActivity;
import in.meegotech.invisiblewidgetspro.utils.AppConstants;
import in.meegotech.invisiblewidgetspro.utils.NotificationHelper;
import in.meegotech.invisiblewidgetspro.utils.SharedPrefHelper;

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

        //For the time a new widget is added
        if (appWidgetIds.length > 0 &&
                SharedPrefHelper.getPackageNameForWidgetId(context, appWidgetIds[0]).equals
                (AppConstants.PACKAGE_NAME_NOT_FOUND)) {
            showWidgets = true;
            SharedPrefHelper.setConfigModeValue(context, true);
            //By default each new widget will be a placeholder/blank widget.
            packageName = AppConstants.PLACEHOLDER_WIDGET;
            SharedPrefHelper.setPackageNameForWidgetId(context, appWidgetIds[0], packageName);
            //We make all the widgets visible on adding a new widget
            appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));

            NotificationHelper.showNotification(context);
        }

        if (showWidgets) {
            //show all widgets
            for (int appWidgetId : appWidgetIds) {
                packageName = SharedPrefHelper.getPackageNameForWidgetId(context, appWidgetId);

                intent = new Intent(context, ConfigurationActivity.class);
                intent.putExtra(AppConstants.PACKAGE_NAME_KEY, packageName);
                intent.putExtra(AppConstants.WIDGET_ID_KEY, appWidgetId);
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


                if(packageName.equals(AppConstants.PLACEHOLDER_WIDGET)) {
                    views = new RemoteViews(context.getPackageName(), R.layout.widget_invisible);
                } else {
                    intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                    intent.setAction(AppConstants.getDummyUniqueAction(appWidgetId));
                    pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    views = new RemoteViews(context.getPackageName(), R.layout.widget_invisible);
                    views.setOnClickPendingIntent(R.id.invisible_widget_layout, pendingIntent);
                }

                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    /*
       IMPORTANT : onReceive() is meant to receive the broadcasts and then dispatch the calls
       to other methods like onUpdate(), onDelete() etc. So If you are overriding onReceive() and
       not writing code to call onDelete(), onDisabled etc, they would never be called.

       Hence it's very important to call super.onReceive() here, haha
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
        } else
            showWidgets = SharedPrefHelper.getConfigModeValue(context);

        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            SharedPrefHelper.deletePackageNameForId(context, appWidgetId);
        }
    }
}
