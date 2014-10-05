package com.mobile.android.trafficlock.utils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.IBinder;

import android.preference.PreferenceManager;
import com.mobile.android.trafficlock.datagrabber.TrafficGrabber;
import com.mobile.android.trafficlock.datagrabber.WeatherGrabber;

import java.util.Timer;

/**
 * Created by Michael Abbott on 10/4/14.
 */
public class AlarmService extends Service implements WeatherGrabber.WeatherListener, TrafficGrabber.TrafficListener{


    private final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    private final SharedPreferences.Editor editor = sharedPreferences.edit();

    private static double lastTrafficMinutes = -1d;
    private static double lastWeatherFactor = -1d;

    private static double trafficMinutes = -1d;
    private static double weatherFactor = -1d;

    public static final String ACTION = "ACTION";

    private IntentFilter matcher;
    private Timer timer;


    @Override
    public void onCreate(){
        WeatherGrabber.addWeatherListener(this);
        TrafficGrabber.addTrafficLIstener(this);
    }

    private void timeUntilWakeup() {
        int hours = sharedPreferences.getInt("workStartTime.hours", 7);
        int minutes = sharedPreferences.getInt("workStartTime.minutes", 30);
        long millisUntilWork = (hours * 60l + minutes) * 60l * 1000l;
        long timeUntil = millisUntilWork - System.currentTimeMillis();
        // If weather is not available, but traffic is
        if (lastTrafficMinutes >= 0) {
            // Evaluate if the individual should be woken up
        }
        // If traffic is not available, but weather is
        if (lastWeatherFactor >= 0) {
            // Evauluate if the individual should be woken up
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void trafficUpdated(double factor) {
        trafficMinutes = factor;
    }

    @Override
    public void trafficUpdated(String message) {

    }

    @Override
    public void weatherFactorUpdated(double factor) {
        weatherFactor = factor;
    }

    @Override
    public void weatherDescriptionUpdated(String description) {

    }
}
