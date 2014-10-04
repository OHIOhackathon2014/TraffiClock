package com.mobile.android.trafficlock;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
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

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("activated", false).apply();

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

        actionBarView.findViewById(R.id.actionbar_map).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Intent map = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=20.5666,45.345"));
                    }
                });

        setContentView(R.layout.main);

        final Button activateButton = (Button) findViewById(R.id.activationButton);
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(activateButton, "scaleX", 1.0f, 1.05f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(activateButton, "scaleY", 1.0f, 1.05f);
        scaleXAnim.setRepeatMode(ValueAnimator.REVERSE);
        scaleYAnim.setRepeatMode(ValueAnimator.REVERSE);
        scaleXAnim.setRepeatCount(ValueAnimator.INFINITE);
        scaleYAnim.setRepeatCount(ValueAnimator.INFINITE);
        scaleXAnim.setDuration(900);
        scaleYAnim.setDuration(900);
        scaleXAnim.start();
        scaleYAnim.start();

        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The service is not activated
                if (!sharedPreferences.getBoolean("activated", false)) {
                    if (Build.VERSION.SDK_INT >= 16) {
                        activateButton.setBackground(getResources().getDrawable(R.drawable.red_button));
                    }
                    else {
                        activateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_button));
                    }
                    activateButton.setText(getResources().getString(R.string.deactivated));
                    editor.putBoolean("activated", true).apply();
                }
                // The service is activated
                else {
                    if (Build.VERSION.SDK_INT >= 16) {
                        activateButton.setBackground(getResources().getDrawable(R.drawable.green_button));
                    }
                    else {
                        activateButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
                    }
                    activateButton.setText(getResources().getString(R.string.activated));
                    editor.putBoolean("activated", false).apply();
                }
            }
        });
    }
}
