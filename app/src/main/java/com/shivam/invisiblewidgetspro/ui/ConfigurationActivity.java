package com.shivam.invisiblewidgetspro.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.shivam.invisiblewidgetspro.R;

import java.util.List;

public class ConfigurationActivity extends AppCompatActivity {

    private int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        final PackageManager pm = getPackageManager();
//get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

//        for (ApplicationInfo packageInfo : packages) {
//            Log.d(TAG, "Installed package :" + packageInfo.packageName);
//            Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
//            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
//        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.widgets_id_to_package_file_key),Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(mAppWidgetId+"", packages.get(0).packageName);
        editor.apply();

 //       savewidget(null);

        Intent nintent = getPackageManager().getLaunchIntentForPackage(packages.get(0).packageName);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nintent, 0);

        // Get the layout for the App Widget and attach an on-click listener
        // to the button
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_visible);
        views.setOnClickPendingIntent(R.id.visible_widget_layout, pendingIntent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(mAppWidgetId, views);
        Log.d("xxx", mAppWidgetId+ " from  config activity");

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
     //   finish();
    }

    public void savewidget(View view) {
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
//        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget_visible);
//        appWidgetManager.updateAppWidget(mAppWidgetId, views);
//        Toast.makeText(this, "SAVED", Toast.LENGTH_SHORT).show();



    }
}
