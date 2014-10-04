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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
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
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(activateButton, "scaleX", .99f, 1.04f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(activateButton, "scaleY", .99f, 1.04f);
        scaleXAnim.setRepeatMode(ValueAnimator.REVERSE);
        scaleYAnim.setRepeatMode(ValueAnimator.REVERSE);
        scaleXAnim.setRepeatCount(ValueAnimator.INFINITE);
        scaleYAnim.setRepeatCount(ValueAnimator.INFINITE);
        scaleXAnim.setDuration(900);
        scaleYAnim.setDuration(900);
        scaleXAnim.start();
        scaleYAnim.start();

        String[] colors = {"#ffffffff", "#ffdddddd", "#ffcccccc", "#ffbbbbbb", "#ffaaaaaa",
                "#ff999999", "#ff888888", "#ff777777", "#ff666666", "#ff555555",};
        int[] resids = {R.id.orbiting_circle, R.id.orbiting_circle2, R.id.orbiting_circle3, R.id.orbiting_circle4,
                R.id.orbiting_circle5, R.id.orbiting_circle6, R.id.orbiting_circle7, R.id.orbiting_circle8,
                R.id.orbiting_circle9, R.id.orbiting_circle10};
        float ANGLE_SPREAD = (float) (Math.PI * 2 / 5);
        float NUM_CIRCLES = 10;
        for(int i = 0; i < NUM_CIRCLES; i++){
            ImageView img = (ImageView) findViewById(resids[i]);
            img.setBackgroundColor(Color.parseColor(colors[i]));
            final float MAX_SIZE = 1.0f, MIN_SIZE = .1f;
            float scaleFactor = ((MAX_SIZE - MIN_SIZE) / (-NUM_CIRCLES + 1)) * i + 1;
            img.setScaleX(scaleFactor);
            img.setScaleY(scaleFactor);
            OrbitingCircle orbiting = new OrbitingCircle(img, activateButton);

            float startAngle = i * ANGLE_SPREAD / NUM_CIRCLES;
                    ObjectAnimator angleAnimator = ObjectAnimator.ofFloat(orbiting, "angle",
                            (float) (Math.PI * 2 + startAngle), startAngle);
            angleAnimator.setRepeatMode(ValueAnimator.RESTART);
            angleAnimator.setRepeatCount(ValueAnimator.INFINITE);
            angleAnimator.setInterpolator(new LinearInterpolator());
            angleAnimator.setDuration(3000);
            angleAnimator.start();
        }


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
