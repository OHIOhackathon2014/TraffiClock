package com.mobile.android.trafficlock.datagrabber;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import com.mobile.android.trafficlock.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Rniemo
 * Date: 10/4/14
 * Time: 1:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class WeatherGrabber implements DataGrabber {


    public final static double MAX_PRECIP = 10;
    public final static int TIME_INTERVAL = 1000 * 60; // in ms

    private Timer timer;
    private Context context;
    private double weatherData = -1;
    private String description = "No weather data available";
    private ArrayList<double[]> precips = new ArrayList<double[]>();

    private Location location;

    private static WeatherListener listener;

    public WeatherGrabber(Context context){
        this.context = context;
        if(location == null){
            location = Utils.getLastLocation();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new DataTimerTask(), 0, TIME_INTERVAL);
        timer.schedule(new DataTimerTask(), 0);
        try{
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("precipitation.txt")));
            String line;
            while((line = reader.readLine()) != null && !line.trim().isEmpty()){
                String[] strs = line.split(",");
                double[] city = new double[strs.length];
                for(int i = 0; i < strs.length; i++){
                    city[i] = Double.parseDouble(strs[i]);
                }
                precips.add(city);
            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void destroy(){
        timer.cancel();
        timer.purge();
    }

    public void updateLocation(Location loc){
        if(location == null){
            timer.schedule(new DataTimerTask(), 0);
        }
        this.location = loc;
    }

    @Override
    public int getTimeInterval() {
        return TIME_INTERVAL;
    }

    @Override
    public double getData() {
        return weatherData;
    }

    public String getDescription(){
        return description;
    }

    private double getClosestPrecip(){
        final int CLOSEST_SIZE = 5;
        double[] closest = precips.get(0);
        double closestDist = Integer.MAX_VALUE;
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        for(double[] db : precips){
            double distSq = (db[0] - lat) * (db[0] - lat) + (db[1] - lon) * (db[1] - lon);
            if(distSq < closestDist){
                closest = db;
                closestDist = distSq;
            }
        }
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        return closest[month + 2];
    }

    private void processWeatherData(JSONObject weather){
        if(weather == null)
            return;
        try {
            JSONObject now = weather.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0);
            double precip = now.getDouble("precipMM");
            double closestAvgPrecip = getClosestPrecip();
            description = now.getInt("temp_F") + "F, " + now.getJSONArray("weatherDesc")
                        .getJSONObject(0).getString("value");
            double diff = precip - closestAvgPrecip;
            double errFromMean = diff / closestAvgPrecip;
            if(errFromMean > 1){
                errFromMean = 1;
            }
            errFromMean /= 2;
            errFromMean += .5;
            weatherData = errFromMean;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(listener != null){
            listener.weatherFactorUpdated(weatherData);
            listener.weatherDescriptionUpdated(description);
        }

    }

    public static void setWeatherListener(WeatherListener l){
        listener = l;
    }

    public static void removeWeatherListener(){
        listener = null;
    }


    public interface WeatherListener {

        public void weatherFactorUpdated(double factor);

        public void weatherDescriptionUpdated(String description);

    }

    private class DataTimerTask extends TimerTask {

        @Override
        public void run() {
            if(Utils.isConnected(context) && location != null){
                WeatherTask task = new WeatherTask();
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
            }
        }
    }

    private class WeatherTask extends AsyncTask<Void, Void, Void> {

        public final static String BASE_URL = "http://api.worldweatheronline.com/free/v1";

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(BASE_URL +
                        "/weather.ashx?q=" + location.getLatitude() + "," +
                        location.getLongitude() + "&format=json&key=3d241f00c0201d81de9d7be9a82a6c69b0b76d68");
                HttpResponse response = client.execute(get);
                BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while((line = br.readLine()) != null){
                    sb.append(line);
                }
                JSONObject obj = new JSONObject(sb.toString());
                processWeatherData(obj);
            }catch(IOException e){
                e.printStackTrace();
            }catch(JSONException ex){
                ex.printStackTrace();
            }
            return null;
        }
    }
}
