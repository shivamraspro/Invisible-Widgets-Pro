package com.shivam.invisiblewidgetspro.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.shivam.invisiblewidgetspro.R;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch configSwitch = (Switch) findViewById(R.id.config_switch);
        final TextView configDesc = (TextView) findViewById(R.id.config_desc);
        final TextView configTitle = (TextView) findViewById(R.id.config_title);

        configSwitch.setChecked(false);
        configTitle.setText(getString(R.string.config_title_off));
        configDesc.setText(getString(R.string.config_mode_desc_off));

        configSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    configDesc.setText(getString(R.string.config_mode_desc_on));
                    configTitle.setText(getString(R.string.config_title_on));
                } else {
                    configDesc.setText(getString(R.string.config_mode_desc_off));
                    configTitle.setText(getString(R.string.config_title_off));
                }
            }
        });

        setupStetho();
    }
    private void setupStetho() {
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build()
        );
    }
}
