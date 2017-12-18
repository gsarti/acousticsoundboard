package com.cegepsth.asb.acousticsoundboard;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {
    Activity activity;
    RadioButton radioDarkTheme;
    RadioButton radioLightTheme;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ThemeManager.onActivityCreateSetTheme(this);
        activity = this;

        radioDarkTheme = (RadioButton) findViewById(R.id.radioDarkTheme);
        radioLightTheme = (RadioButton) findViewById(R.id.radioLightTheme);
        switch (ThemeManager.CurrentTheme) {
            case R.style.LightTheme:
                radioLightTheme.setChecked(true);
                break;
            case R.style.DarkTheme:
                radioDarkTheme.setChecked(true);
                break;
        }

        radioDarkTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context ctx = radioDarkTheme.getContext();
                ThemeManager.changeToTheme(activity, R.style.DarkTheme);
            }
        });

        radioLightTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context ctx = radioLightTheme.getContext();
                ThemeManager.changeToTheme(activity, R.style.LightTheme);
            }
        });
    }
}
