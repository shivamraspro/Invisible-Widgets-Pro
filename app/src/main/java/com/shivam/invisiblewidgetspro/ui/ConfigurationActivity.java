package com.shivam.invisiblewidgetspro.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.shivam.invisiblewidgetspro.R;
import com.shivam.invisiblewidgetspro.utils.AppConstants;
import com.shivam.invisiblewidgetspro.utils.SharedPrefHelper;
import com.shivam.invisiblewidgetspro.utils.UpdateWidgetHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfigurationActivity extends AppCompatActivity
        implements AppSelectorDialogFragment.AppSelectedListener {

    private boolean isConfigModeOn;

    @BindView(R.id.config_desc)
    TextView configDesc;

    @BindView(R.id.config_title)
    TextView configTitle;

    @BindView(R.id.config_switch)
    Switch configSwitch;

    @BindView(R.id.list_item_container)
    LinearLayout listItemContainer;

    @BindView(R.id.widget_id_info)
    TextView widgetIdInfoTextView;

    @BindView(R.id.app_icon)
    ImageView appIconImageView;

    @BindView(R.id.app_name)
    TextView appNameTextView;

    @BindView(R.id.app_pkg_name)
    TextView appPackageNameTextView;

    @BindView(R.id.adViewConfig)
    AdView mAdView;

    private int widgetId;
    private Drawable appIcon;
    private CharSequence appName;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        ButterKnife.bind(this);

        isConfigModeOn = SharedPrefHelper.getConfigModeValue(this);

        if (isConfigModeOn) {
            configSwitch.setChecked(true);
            configModeOn(false);
        } else {
            configSwitch.setChecked(false);
            configModeOff(false);
        }

        configSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isConfigModeOn = isChecked;
                if (isChecked) {
                    configModeOn(true);
                } else {
                    configModeOff(true);
                }
            }
        });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        if(widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            //This Activity is launched by tapping on a widget from the home screen
            widgetId = extras.getInt(AppConstants.WIDGET_ID_KEY);
            packageName = extras.getString(AppConstants.PACKAGE_NAME_KEY);

            //Show Widget Information in Configuration Activity
            showWidgetInformation();
        }
        else {
            //This Activity is launched by default while adding a new widget
            packageName = getPackageName();

            SharedPrefHelper.setConfigModeValue(this, true);

            configModeOn(true);

            //Save Widget Information in SharedPreferences
            SharedPrefHelper.setPackageNameForWidgetId(this, widgetId, packageName);

            //Show Widget Information in Configuration Activity
            showWidgetInformation();

            //Update the corresponding widget on home screen
            updateWidget();

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            setResult(RESULT_OK, resultValue);

            finish();
        }

//        Intent resultValue = new Intent();
//        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
//        setResult(RESULT_OK, resultValue);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void showWidgetInformation() {

        widgetIdInfoTextView.setText(getString(R.string.widget_id_info_title, widgetId));

        try {
            appIcon = getPackageManager().getApplicationIcon(packageName);
            appName = getPackageManager().getApplicationInfo(packageName, 0).loadLabel
                    (getPackageManager());
        } catch (Exception e) {}

        appIconImageView.setImageDrawable(appIcon);
        appNameTextView.setText(appName);
        appPackageNameTextView.setText(packageName);
    }

    private void updateWidget() {
        Intent intent;
        PendingIntent pendingIntent;
        RemoteViews views;

        //Not checking the value of isConfigModeOn as it is updated correctly every time
        //the switch is toggled, activity is created or entered (see OnCreate/OnStart)

        if (isConfigModeOn) {
            //show widget
            intent = new Intent(getApplicationContext(), ConfigurationActivity.class);
            intent.putExtra(AppConstants.PACKAGE_NAME_KEY, packageName);
            intent.putExtra(AppConstants.WIDGET_ID_KEY, widgetId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setAction(AppConstants.getDummyUniqueAction(widgetId));
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            views = new RemoteViews(getPackageName(), R.layout.widget_visible);
            views.setCharSequence(R.id.widget_id_visible, "setText", "#" + widgetId);
            views.setOnClickPendingIntent(R.id.visible_widget_layout, pendingIntent);
        } else {
            //hide widget
            intent = getPackageManager().getLaunchIntentForPackage(packageName);
            intent.setAction(AppConstants.getDummyUniqueAction(widgetId));
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            views = new RemoteViews(getPackageName(), R.layout.widget_invisible);
            views.setOnClickPendingIntent(R.id.invisible_widget_layout, pendingIntent);
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    private void configModeOn(boolean b) {
        configDesc.setText(getString(R.string.config_mode_desc_on));
        configTitle.setText(getString(R.string.config_title_on));

        if(b) {
            SharedPrefHelper.setConfigModeValue(this, true);
            UpdateWidgetHelper.showWidgets(this);
        }
    }

    private void configModeOff(boolean b) {
        configDesc.setText(getString(R.string.config_mode_desc_off));
        configTitle.setText(getString(R.string.config_title_off));

        if(b) {
            SharedPrefHelper.setConfigModeValue(this, false);
            UpdateWidgetHelper.hideWidgets(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean b = SharedPrefHelper.getConfigModeValue(this);
        if (isConfigModeOn != b) {
            isConfigModeOn = b;

            if (isConfigModeOn) {
                configSwitch.setChecked(true);
                configModeOn(true);
            } else {
                configSwitch.setChecked(false);
                configModeOff(true);
            }
        }
    }

    @Override
    public void getSelectedAppPackage(String packageName) {
        this.packageName = packageName;
        //Save Widget Information in SharedPreferences
        SharedPrefHelper.setPackageNameForWidgetId(this, widgetId, packageName);

        //Show Widget Information in Configuration Activity
        showWidgetInformation();

        //Update the corresponding widget on home screen
        updateWidget();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultValue);

        updateWidget();
    }

    @OnClick(R.id.list_item_container)
    public void chooseApplication(View view) {
        AppSelectorDialogFragment fragment = new AppSelectorDialogFragment();
        fragment.show(getFragmentManager(), AppConstants.APP_SELECTOR_FRAGMENT_TAG);

        /*
        Use the following code in future releases to use the dialog as a fragment.

        To implement that, create a fragment for configuration activity's current role
        and replace that fragment with dialog fragment and vice versa using fragment
        transactions.
         */

//        // The device is smaller, so show the fragment fullscreen
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        // For a little polish, specify a transition animation
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        // To make it fullscreen, use the 'content' root view as the container
//        // for the fragment, which is always the root view for the activity
//        transaction.add(android.R.id.content, newFragment)
//                .addToBackStack(null).commit();
    }
}
