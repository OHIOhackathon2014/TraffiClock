package com.mobile.android.trafficlock.datagrabber;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import com.mobile.android.trafficlock.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit.RestAdapter;
import retrofit.android.AndroidApacheClient;
import retrofit.client.Client;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Rniemo
 * Date: 10/3/14
 * Time: 9:43 PM
 * To change this template use File | Settings | File Templates.
 */


public class DataService extends Service{

    public final static double MAX_PRECIP = 5;

    private Timer timer;
    private double weatherData = -1;

    @Override
    public void onCreate(){

        timer = new Timer();
        timer.scheduleAtFixedRate(new DataTimerTask(), 0, 1000 * 60);


    }

    @Override
    public void onDestroy(){
        timer.cancel();
        timer.purge();
    }

    /**
     * Returns how much of a factor the weather is based on the current level of precipitation
     * (0-1.0)
     * -1 means weather data was not available
     * @return 0-1.0, 0 being no precipitation and 1.0 being >= DataService.MAX_PRECIP;
     */
    public double getWeatherFactor(){
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

    }

    @Override

    public IBinder onBind(Intent intent) {

        return null;
    }

    private class DataTimerTask extends TimerTask {

        @Override
        public void run() {
            if(Utils.isConnected(getApplicationContext())){
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