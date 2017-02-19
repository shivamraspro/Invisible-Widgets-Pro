package com.shivam.invisiblewidgetspro.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.shivam.invisiblewidgetspro.R;
import com.shivam.invisiblewidgetspro.utils.SharedPrefHelper;
import com.shivam.invisiblewidgetspro.utils.UpdateWidgetHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

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

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setupStetho();
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

    private void setupStetho() {
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build()
        );
    }
}
