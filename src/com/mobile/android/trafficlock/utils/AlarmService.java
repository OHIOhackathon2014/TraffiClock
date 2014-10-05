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

        //long currentTime = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000 + cal.get(Calendar.MINUTE) * 60 * 1000 +
                cal.get(Calendar.SECOND) * 1000 + cal.get(Calendar.MILLISECOND);



//        long currentTime = System.currentTimeMillis() - (4l * 60l * 60l * 1000l);
//        if (currentTime < 0) {
//            currentTime += 24l * 60l * 60l * 1000l;
//        }



        if (currentTime < 3l * 60l * 1000l ) {
            SetAlarm.alertCanOccur = true;
        }


        //=================================================================================

        int hours = sharedPreferences.getInt("workStartTime.hours", 7);
        int minutes = sharedPreferences.getInt("workStartTime.minutes", 30);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        Set<String> values = (Set<String>) sharedPreferences.getStringSet("daysOfTheWeek", null);
        //String[] selected = values.toArray(new String[] {});


        boolean todayIsInSet = false;

        if (values.contains(Integer.toString(day - 1))) {
            //Log.e("12345", "TRUTHE");
            todayIsInSet = true;
        }
        //Log.e("12345", "It's NOT Saturday MotherFUcker");
        if (todayIsInSet) {
            //determine alarmTime from arrivalTime in following 2 if statements
            //compare or use a subtraction of alarmTime and currentTime to determine whether to "setAlarm" meaning noise

            //millisUntilWork == "arrivalTime"
            //timeUntil == NOT "alarmTime", is milliseconds until work
            //alarmTime == "timeUntil" - "trafficTime" - "readyTime" + "currentTime"

            //take timeUntil, subtract readyTime and trafficTime
            //if timeUntil is +, no alarm
            //if timeUntil is <= 0, setAlarm

//            long millisUntilWork = (hours * 60l + minutes) * 60l * 1000l;
//            long timeUntil = millisUntilWork - System.currentTimeMillis();


            long arrivalTime = (hours * 60l + minutes) * 60l * 1000l;


            //long timeUntil = arrivalTime - System.currentTimeMillis();
            long alarmTime = arrivalTime;

            // If weather is not available, but traffic is
            if (lastTrafficMinutes >= 0) {
                // Evaluate if the individual should be woken up
                alarmTime -= lastTrafficMinutes * 60l * 1000l;
            }
            // If traffic is not available, but weather is
            if (lastWeatherFactor >= 0) {
                // Evauluate if the individual should be woken up
                alarmTime -= 20l * lastWeatherFactor * 60l * 1000l;
            }



            if (SetAlarm.alertCanOccur) {
                if (currentTime > alarmTime) {
                    SetAlarm.setAlarm(getApplicationContext());

                    Log.e("12345", "currentTime > alarmTime");
                    Log.e("12345", "currentTime: " + currentTime);
                    Log.e("12345", "alarmTime: " + alarmTime);

                } else {
                    //wait some more
                }
            } else {
                Log.e("12345", "alert is disabled");
            }


//            if (timeUntil <= 0 && SetAlarm.alertCanOccur) {
//                SetAlarm.setAlarm(getApplicationContext());
//            }


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
