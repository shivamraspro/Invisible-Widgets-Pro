package com.shivam.invisiblewidgetspro.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.shivam.invisiblewidgetspro.R;

public class MainActivity extends AppCompatActivity {

    private TextView configDesc;
    private TextView configTitle;
    private Switch configSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configSwitch = (Switch) findViewById(R.id.config_switch);
        configDesc = (TextView) findViewById(R.id.config_desc);
        configTitle = (TextView) findViewById(R.id.config_title);

        //todo
        //check what happens when you randomly enter exit activity and don't want to change
        //the config mode automatically

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean isConfigModeOn = sharedPref.getBoolean("isConfigMode", false);

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

        setupStetho();
    }

    private void configModeOn() {
        configDesc.setText(getString(R.string.config_mode_desc_on));
        configTitle.setText(getString(R.string.config_title_on));

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isConfigMode", true);
        editor.apply();

        updateWidgets(true);
    }

    private void configModeOff() {
        configDesc.setText(getString(R.string.config_mode_desc_off));
        configTitle.setText(getString(R.string.config_title_off));

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isConfigMode", false);
        editor.apply();

        updateWidgets(false);
    }

    private void updateWidgets(Boolean isConfigMode) {
        Intent manualWidgetUpdateIntent =
                new Intent("com.shivam.invisiblewidgetspro.MANUAL_APPWIDGET_UPDATE")
                .setPackage(getPackageName());

        manualWidgetUpdateIntent.putExtra("is_config_mode", isConfigMode);

        sendBroadcast(manualWidgetUpdateIntent);
    }

    private void setupStetho() {
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build()
        );
    }
}
