package com.shivam.invisiblewidgetspro.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.shivam.invisiblewidgetspro.R;
import com.shivam.invisiblewidgetspro.extras.AppsWidgetsAdapter;
import com.shivam.invisiblewidgetspro.extras.NonScrollableRecyclerViewEmptyViewSupport;
import com.shivam.invisiblewidgetspro.utils.SharedPrefHelper;
import com.shivam.invisiblewidgetspro.utils.UpdateWidgetHelper;
import com.shivam.invisiblewidgetspro.widget.WidgetProvider;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private boolean isConfigModeOn;

    @BindView(R.id.config_desc)
    TextView configDesc;

    @BindView(R.id.config_title)
    TextView configTitle;

    @BindView(R.id.config_switch)
    Switch configSwitch;

    @BindView(R.id.adViewMain)
    AdView mAdView;

    @BindView(R.id.active_widget_info)
    TextView activeWidgetInfo;

    @BindView(R.id.active_widgets_recyclerview)
    NonScrollableRecyclerViewEmptyViewSupport recyclerView;

    @BindView(R.id.empty_view_appwidgets)
    LinearLayout emptyView;

    private int[] appWidgetIds;
    private Context mContext;
    private AppsWidgetsAdapter adapter;
    private boolean loadWidgetInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mContext = this;
        isConfigModeOn = SharedPrefHelper.getConfigModeValue(this);

        if(isConfigModeOn) {
            configSwitch.setChecked(true);
            configModeOn();
        }
        else {
            configSwitch.setChecked(false);
            configModeOff();
        }

        configSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                   configModeOn();
                } else {
                   configModeOff();
                }
            }
        });

        //appWidgetIds = new int[]{};

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setEmptyView(emptyView);
        recyclerView.setNestedScrollingEnabled(false);

        new LoadWidgetInfos().execute();

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        loadWidgetInfos = false;
    }

    private void updateWidgetsInfo() {
        recyclerView.setAdapter(adapter);

        if(appWidgetIds.length == 0) {
            activeWidgetInfo.setText(getString(R.string.widget_count_none));
            recyclerView.setVisibility(View.GONE);
        }
        else if(appWidgetIds.length == 1) {
            recyclerView.setVisibility(View.VISIBLE);
            activeWidgetInfo.setText(getString(R.string.widget_count_one));
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            activeWidgetInfo.setText(getString(R.string.widget_count_some, appWidgetIds.length));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadWidgetInfos = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean b = SharedPrefHelper.getConfigModeValue(this);
        if(isConfigModeOn != b) {
            isConfigModeOn = b;

            if(isConfigModeOn) {
                configSwitch.setChecked(true);
                configModeOn();
            }
            else {
                configSwitch.setChecked(false);
                configModeOff();
            }
        }

        if(loadWidgetInfos) {
            new LoadWidgetInfos().execute();
            loadWidgetInfos = false;
        }
    }

    private void configModeOn() {
        configDesc.setText(getString(R.string.config_mode_desc_on));
        configTitle.setText(getString(R.string.config_title_on));

        SharedPrefHelper.setConfigModeValue(this, true);

        UpdateWidgetHelper.showWidgets(this);
    }

    private void configModeOff() {
        configDesc.setText(getString(R.string.config_mode_desc_off));
        configTitle.setText(getString(R.string.config_title_off));

        SharedPrefHelper.setConfigModeValue(this, false);

        UpdateWidgetHelper.hideWidgets(this);
    }

    private class LoadWidgetInfos extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
            appWidgetIds =  appWidgetManager.getAppWidgetIds(new ComponentName(mContext,
                    WidgetProvider.class));

            ArrayList<Dataset> appWidgetData = new ArrayList<>();

            for(int i=0; i < appWidgetIds.length; i++) {
                try {
                    appWidgetData.add(new Dataset(
                            getPackageManager().getApplicationInfo(
                                    SharedPrefHelper.getPackageNameForWidgetId(
                                            mContext, appWidgetIds[i]), 0),
                            appWidgetIds[i]));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            adapter = new AppsWidgetsAdapter(mContext, appWidgetData);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateWidgetsInfo();
        }
    }

    public class Dataset {
        private ApplicationInfo applicationInfo;
        private int widgetId;

        public Dataset(ApplicationInfo applicationInfo, int widgetId) {
            this.applicationInfo = applicationInfo;
            this.widgetId = widgetId;
        }

        public int getWidgetId() {
            return widgetId;
        }

        public ApplicationInfo getApplicationInfo() {
            return applicationInfo;
        }
    }
}
