package com.shivam.invisiblewidgetspro.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.shivam.invisiblewidgetspro.R;

/**
 * Created by shivam on 10/02/17.
 */

public class WidgetProvider extends AppWidgetProvider {

    private boolean is_config_mode = false;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getString(R.string.widgets_id_to_package_file_key),
                    Context.MODE_PRIVATE);
          ///  int defaultValue = context.getResources().getInteger(R.string
             //       .saved_high_score_default);
         //   long highScore = sharedPref.getInt(getString(R.string.saved_high_score), 0);

            Log.d("xxx", appWidgetId+ " from widget provider");
            Log.d("xxx",sharedPref.toString());
            String packageName = sharedPref.getString(appWidgetId+"", null);
            Log.d("xxx", packageName+ "  pkg name from widget provider");
            // Create an Intent to launch ExampleActivity
            if(packageName != null) {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                // Get the layout for the App Widget and attach an on-click listener
                // to the button
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_visible);
                views.setOnClickPendingIntent(R.id.visible_widget_layout, pendingIntent);

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
            else
                Log.d("xxx", "else blovl");
        }
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        super.onReceive(context, intent);
//    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        onUpdate(context, appWidgetManager, new int[]{appWidgetId});
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
