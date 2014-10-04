package com.mobile.android.trafficlock.datagrabber;

import android.content.Context;
import android.os.AsyncTask;
import com.mobile.android.trafficlock.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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


    public final static double MAX_PRECIP = 5;
    public final static int TIME_INTERVAL = 1000 * 60; // in ms

    private Timer timer;
    private Context context;
    private double weatherData = -1;
    private WeatherListener listener;

    public WeatherGrabber(Context context){
        this.context = context;
        timer = new Timer();
        timer.scheduleAtFixedRate(new DataTimerTask(), 0, TIME_INTERVAL);
    }

    public void destroy(){
        timer.cancel();
        timer.purge();
    }

    @Override
    public int getTimeInterval() {
        return TIME_INTERVAL;
    }

    @Override
    public double getFactor() {
        return weatherData;
    }

    private void processWeatherData(JSONObject weather){
        if(weather == null)
            return;
        try {
            JSONObject now = weather.getJSONObject("data").getJSONObject("current_condition");
            double precip = now.getDouble("precipMM");
            weatherData = precip / MAX_PRECIP;
            if(weatherData > 1){
                weatherData = 1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(listener != null){
            listener.weatherUpdated(weatherData);
        }

    }

    public void setWeatherListener(WeatherListener listener){
        this.listener = listener;
    }

    public void removeWeatherListener(){
        this.listener = null;
    }

    interface WeatherListener {

        public void weatherUpdated(double factor);

    }

    private class DataTimerTask extends TimerTask {

        @Override
        public void run() {
            if(Utils.isConnected(context)){
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
                        "/weather.ashx?q=columbus&format=json&key=3d241f00c0201d81de9d7be9a82a6c69b0b76d68");
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
