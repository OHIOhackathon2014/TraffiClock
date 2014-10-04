package com.mobile.android.trafficlock.datagrabber;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

/**
 * Created with IntelliJ IDEA.
 * User: Rniemo
 * Date: 10/3/14
 * Time: 9:43 PM
 * To change this template use File | Settings | File Templates.
 */


public class DataService extends Service implements LocationService.ILocationListener{


    private WeatherGrabber wGrabber;
    private TrafficGrabber tGrabber;


    @Override
    public void onCreate(){

        LocationService ls = new LocationService(getApplicationContext());
        ls.addListener(this);
        wGrabber = new WeatherGrabber(getApplicationContext());
        tGrabber = new TrafficGrabber(getApplicationContext());

    }

    @Override
    public void onDestroy(){
        wGrabber.destroy();
        tGrabber.destroy();
    }

    /**
     * Returns how much of a factor the weather is based on the current level of precipitation
     * (0-1.0)
     * -1 means weather data was not available
     * @return 0-1.0, 0 being no precipitation and 1.0 being >= DataService.MAX_PRECIP;
     */
    public double getWeatherFactor(){
        return wGrabber.getData();
    }

    public double getTrafficTime(){
        return tGrabber.getData();
    }


    @Override

    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override
    public void onLocationChanged(Location location) {
        tGrabber.updateLocation(location);
    }
}