package com.mobile.android.trafficlock.utils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.IBinder;

import com.mobile.android.trafficlock.datagrabber.TrafficGrabber;
import com.mobile.android.trafficlock.datagrabber.WeatherGrabber;

import java.util.Timer;

/**
 * Created by Michael Abbott on 10/4/14.
 */
public class AlarmService extends Service implements WeatherGrabber.WeatherListener, TrafficGrabber.TrafficListener{


    public static final String ACTION = "ACTION";

    private IntentFilter matcher;
    private Timer timer;


    @Override
    public void onCreate(){
        WeatherGrabber.addWeatherListener(this);
        TrafficGrabber.addTrafficLIstener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





    @Override
    public void trafficUpdated(double factor) {

    }

    @Override
    public void trafficUpdated(String message) {

    }

    @Override
    public void weatherFactorUpdated(double factor) {

    }

    @Override
    public void weatherDescriptionUpdated(String description) {

    }
}
