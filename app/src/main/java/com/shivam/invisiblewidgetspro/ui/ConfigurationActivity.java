package com.shivam.invisiblewidgetspro.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.shivam.invisiblewidgetspro.R;
import com.shivam.invisiblewidgetspro.utils.AppConstants;
import com.shivam.invisiblewidgetspro.utils.SharedPrefHelper;

public class ConfigurationActivity extends AppCompatActivity implements AppSelectorDialogFragment.AppSelectedListener {

    private int mAppWidgetId;
    private String launchPackageName;
    private TextView packageNameTextView;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private boolean isConfigModeOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        packageNameTextView = (TextView) findViewById(R.id.selected_package);

//        final PackageManager pm = getPackageManager();
//        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        Intent launchIntent = getIntent();
        Bundle extras = launchIntent.getExtras();
        if (extras != null) {
            //either this activity is started by the system on creating a new widget or by
            //clicking on a widget in active configuration mode
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, extras.getInt(AppConstants.WIDGET_ID_KEY));

            launchPackageName = extras.getString(AppConstants.PACKAGE_NAME_KEY);
//            isConfigModeOn = extras.getBoolean("is_config_mode", sharedPref.getBoolean
//                    ("isConfigMode", false));
        }
        if (launchPackageName == null)
            launchPackageName = getPackageName();

        packageNameTextView.setText(launchPackageName);

        SharedPrefHelper.setPackageNameForWidgetId(this, mAppWidgetId, launchPackageName);

        updateWidget();

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
    }

    @Override
    public void getSelectedAppPackage(String packageName) {
        launchPackageName = packageName;
        packageNameTextView.setText(launchPackageName);

        SharedPrefHelper.setPackageNameForWidgetId(this, mAppWidgetId, launchPackageName);

        updateWidget();
    }

    public void chooseApplication(View view) {
        AppSelectorDialogFragment fragment = new AppSelectorDialogFragment();
        fragment.show(getFragmentManager(), "app_selector");
    }

    private void updateWidget() {
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        isConfigModeOn = SharedPrefHelper.getConfigModeValue(this);

        Intent intent = getPackageManager().getLaunchIntentForPackage(launchPackageName);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        RemoteViews views;
        if (isConfigModeOn) {
            views = new RemoteViews(getPackageName(), R.layout.widget_visible);
            views.setOnClickPendingIntent(R.id.visible_widget_layout, pendingIntent);
        } else {
            views = new RemoteViews(getPackageName(), R.layout.widget_invisible);
            views.setOnClickPendingIntent(R.id.invisible_widget_layout, pendingIntent);
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(mAppWidgetId, views);
    }
}
