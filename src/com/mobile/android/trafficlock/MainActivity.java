package com.mobile.android.trafficlock;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import com.mobile.android.trafficlock.datagrabber.DataService;
import android.view.*;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent dataServiceIntent = new Intent(this, DataService.class);
        startService(dataServiceIntent);
        ActionBar actionBar = getActionBar();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("activated", false);

        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View actionBarView = inflater.inflate(
                R.layout.actionbar_main_page, null);

        actionBar.setCustomView(actionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        actionBarView.findViewById(R.id.actionbar_settings).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                        settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(settings);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });

        setContentView(R.layout.main);

        Button activationButton = (Button) findViewById(R.id.activationButton);

        activationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
