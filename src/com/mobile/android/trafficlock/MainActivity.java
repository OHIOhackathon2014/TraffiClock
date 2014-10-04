package com.mobile.android.trafficlock;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.mobile.android.trafficlock.datagrabber.DataService;
import android.view.*;
import com.mobile.android.trafficlock.datagrabber.TrafficGrabber;
import com.mobile.android.trafficlock.datagrabber.WeatherGrabber;
import com.mobile.android.trafficlock.utils.SetAlarm;
import com.mobile.android.trafficlock.utils.Utils;

public class MainActivity extends Activity implements WeatherGrabber.WeatherListener, TrafficGrabber.TrafficListener {


    private Location destinationLocation;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent dataServiceIntent = new Intent(this, DataService.class);
        startService(dataServiceIntent);
        TrafficGrabber.setTrafficListener(this);
        WeatherGrabber.setWeatherListener(this);

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
                        if (destinationLocation != null && destinationLocation.getLatitude() != 0 &&
                                        destinationLocation.getLongitude() != 0) {
                            Intent map = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/" +
                                    "maps?daddr=" + destinationLocation.getLatitude() + "," + destinationLocation.getLongitude()));
                            startActivity(map);
                        } else {
                            Toast.makeText(getBaseContext(), "Please enter an address in settings.", Toast.LENGTH_SHORT).show();
                        }
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

                SetAlarm.setAlarm(getApplicationContext());





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

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        p.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if(s.equals("prefDestination")){
                    String dest = sharedPreferences.getString(s, "");
                    GeocodeTask geocodeTask = new GeocodeTask();
                    geocodeTask.execute(dest);
                }
            }
        });
        if(!p.getString("prefDestination", "").equals("")){
            String dest = sharedPreferences.getString("prefDestination", "");
            GeocodeTask geocodeTask = new GeocodeTask();
            geocodeTask.execute(dest);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(DataService.getInstance() != null){
            DataService.getInstance().queryTrafficData();
        }
    }

    @Override
    public void trafficUpdated(double time) {
        final TextView trafficValueText = (TextView) findViewById(R.id.currTrafficValue);
        int trafficTime = (int) Math.round(time);
        if(trafficTime != -1){
            int hours = trafficTime / 60;
            final int minutes = trafficTime % 60;
            final String hoursText = hours > 0 ? hours + " hr" + (hours != 1 ? "s" : "") + " and " : "";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    trafficValueText.setText(hoursText + minutes + " min(s) to destination!");
                }
            });
        }
    }

    @Override
    public void trafficUpdated(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView trafficValueText = (TextView) findViewById(R.id.currTrafficValue);
                trafficValueText.setText(message);
            }
        });
    }

    @Override
    public void weatherFactorUpdated(double factor) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void weatherDescriptionUpdated(final String description) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView weatherValueText = (TextView) findViewById(R.id.currWeatherValue);
                weatherValueText.setText(description);
            }
        });
    }

    private class GeocodeTask extends AsyncTask<String, Void, Location> {


        @Override
        protected Location doInBackground(String ... dests) {
            String dest = dests[0];
            Location loc = Utils.geocode(dest);
            return loc;
        }

        @Override
        protected void onPostExecute(Location loc){
            destinationLocation = loc;
        }
    }
}
