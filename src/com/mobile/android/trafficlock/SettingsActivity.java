package com.mobile.android.trafficlock;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();

        actionBar.setTitle(getResources().getString(R.string.app_name) + " Settings");

        addPreferencesFromResource(R.xml.preferences);


    }
}
