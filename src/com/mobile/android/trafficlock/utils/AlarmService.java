package com.mobile.android.trafficlock.utils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.IBinder;

import android.preference.MultiSelectListPreference;
import android.preference.PreferenceManager;
import android.util.Log;
import com.mobile.android.trafficlock.datagrabber.TrafficGrabber;
import com.mobile.android.trafficlock.datagrabber.WeatherGrabber;

import java.util.Calendar;
import java.util.Set;
import java.util.Timer;

/**
 * Created by Michael Abbott on 10/4/14.
 */
public class AlarmService extends Service implements WeatherGrabber.WeatherListener, TrafficGrabber.TrafficListener{


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
    }

    private void timeUntilWakeup() {
        int hours = sharedPreferences.getInt("workStartTime.hours", 7);
        int minutes = sharedPreferences.getInt("workStartTime.minutes", 30);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        Set<String> values = (Set<String>) sharedPreferences.getStringSet("daysOfTheWeek", null);
        //String[] selected = values.toArray(new String[] {});


        boolean todayIsInSet = false;

//        if (values == null) {
//            Log.e("12345", "VALUES IS NULL");
//        }

//        for (String string : values) {
//            Log.e("12345", string);
//        }

        if (values.contains(Integer.toString(day - 1))) {
            Log.e("12345", "TRUTHE");
            todayIsInSet = true;
        }

//        if (day == Calendar.SUNDAY && (values.contains("Sunday") || values.contains("sunday"))) {
//            todayIsInSet = true;
//        } else if (day == Calendar.MONDAY && (values.contains("Monday") || values.contains("monday"))) {
//            todayIsInSet = true;
//        } else if (day == Calendar.TUESDAY && (values.contains("Tuesday") || values.contains("tuesday"))) {
//            todayIsInSet = true;
//        } else if (day == Calendar.WEDNESDAY && (values.contains("Wednesday") || values.contains("wednesday"))) {
//            todayIsInSet = true;
//        } else if (day == Calendar.THURSDAY && (values.contains("Thursday") || values.contains("thursday"))) {
//            todayIsInSet = true;
//        } else if (day == Calendar.FRIDAY && (values.contains("Friday") || values.contains("friday"))) {
//            todayIsInSet = true;
//        } else if (day == Calendar.SATURDAY && (values.contains("Saturday") || values.contains("saturday"))) {
//            Log.e("12345", "It's Saturday MotherFUcker");
//            todayIsInSet = true;
//        }
        Log.e("12345", "It's NOT Saturday MotherFUcker");
        if (todayIsInSet) {
            //determine alarmTime from arrivalTime in following 2 if statements
            //compare or use a subtraction of alarmTime and currentTime to determine whether to "setAlarm" meaning noise

            //millisUntilWork == "arrivalTime"
            //timeUntil == NOT "alarmTime", is milliseconds until work
            //alarmTime == "timeUntil" - "trafficTime" - "readyTime" + "currentTime"

            //take timeUntil, subtract readyTime and trafficTime
            //if timeUntil is +, no alarm
            //if timeUntil is <= 0, setAlarm

            long millisUntilWork = (hours * 60l + minutes) * 60l * 1000l;
            long timeUntil = millisUntilWork - System.currentTimeMillis();
            // If weather is not available, but traffic is
            if (lastTrafficMinutes >= 0) {
                // Evaluate if the individual should be woken up
                timeUntil -= lastTrafficMinutes * 60l * 1000l;
            }
            // If traffic is not available, but weather is
            if (lastWeatherFactor >= 0) {
                // Evauluate if the individual should be woken up
                timeUntil -= 20l * lastWeatherFactor * 60l * 1000l;
            }


            if (timeUntil <= 0) {
                SetAlarm.setAlarm(getApplicationContext());
            }


            //SetAlarm.setAlarm(getApplicationContext());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void trafficUpdated(double factor) {
        trafficMinutes = factor;

        if (trafficMinutes >= 0) {
            lastTrafficMinutes = trafficMinutes;
        }

        timeUntilWakeup();
    }

    @Override
    public void trafficUpdated(String message) {

    }

    @Override
    public void weatherFactorUpdated(double factor) {
        weatherFactor = factor;

        if (weatherFactor >= 0) {
            lastWeatherFactor = weatherFactor;
        }

        timeUntilWakeup();
    }

    @Override
    public void weatherDescriptionUpdated(String description) {

    }
}
