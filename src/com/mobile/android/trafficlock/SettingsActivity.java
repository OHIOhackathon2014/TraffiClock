package com.mobile.android.trafficlock;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.mobile.android.trafficlock.utils.Utils;

import java.util.prefs.Preferences;

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
