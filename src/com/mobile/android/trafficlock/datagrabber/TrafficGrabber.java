package com.mobile.android.trafficlock.datagrabber;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.tasks.na.*;
import com.esri.core.geometry.Point;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;
import com.esri.core.tasks.geocode.LocatorReverseGeocodeResult;

import com.mobile.android.trafficlock.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Rniemo
 * Date: 10/4/14
 * Time: 2:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class TrafficGrabber implements DataGrabber{


    // esri updates traffic every 5 minutes
    public final static int TIME_INTERVAL = 1000 * 60 * 5; // in ms
    private Timer timer;
    private Context context;
    private double trafficData = -1;

    private static List<TrafficListener> listeners = new ArrayList<TrafficListener>();

    private String destinationAddress;
    private Location destinationLocation;
    private Location location;
;
    public TrafficGrabber(Context context){
        this.context = context;

        if(location == null){
            location = Utils.getLastLocation();
        }
        timer = new Timer();
        timer.schedule(new DataTimerTask(), 0);
        timer.scheduleAtFixedRate(new DataTimerTask(), 0, TIME_INTERVAL);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if(s.equals("prefDestination")){
                    timer.schedule(new DataTimerTask(), 0);
                    for(TrafficListener listener : listeners) {
                        listener.trafficUpdated("Calculating...");
                    }
                }
            }
        });
    }

    public void query(){
        timer.schedule(new DataTimerTask(), 0);
    }

    public void destroy(){
        timer.cancel();
        timer.purge();
    }

    @Override
    public int getTimeInterval() {
        return TIME_INTERVAL;
    }

    /**
     *
     * @return number of minutes from given location to destination
     *  -1 if no data available yet
     */
    @Override
    public double getData() {
        return trafficData;
    }

    public void updateLocation(Location loc){
        if(location == null){
            timer.schedule(new DataTimerTask(), 0);
        }
        this.location = loc;
    }

    private void processRoutes(JSONObject routes){
        Log.e("k", "in process routes");
        try{
            JSONObject attrs = routes.getJSONObject("routes").getJSONArray("features").
                            getJSONObject(0).getJSONObject("attributes");
            double time = attrs.getDouble("Total_TravelTime");
            trafficData = time;
            Log.e("k", "traffic  in process routes: " + trafficData);
        }catch(Exception e){
            e.printStackTrace();
        }
        for(TrafficListener listener : listeners) {
            if (listener != null) {
                listener.trafficUpdated(trafficData);
            }
        }

    }

    public static void addTrafficLIstener(TrafficListener l){
        listeners.add(l);
    }

    public static void removeTrafficListener(TrafficListener listener) {
        listeners.remove(listener);
    }

    public interface TrafficListener {

        public void trafficUpdated(double factor);

        public void trafficUpdated(String message);

    }

    private class DataTimerTask extends TimerTask {

        @Override
        public void run() {
            if(Utils.isConnected(context) && location != null){
                Log.e("k", "running\n\n\n\n\ninside loc");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String newDestination = prefs.getString("prefDestination", destinationAddress);
                if(newDestination != null && !newDestination.equals(destinationAddress)){
                    destinationAddress = newDestination;
                    destinationLocation = Utils.geocode(destinationAddress);
                }
                if(destinationLocation != null){
                    RouteFindTask routeFindTask = new RouteFindTask();
                    routeFindTask.execute();
                }

            }
        }
    }



    private class RouteFindTask extends AsyncTask<Void, Void, Void> {

        private Locator locator;

        protected void onPreExecute() {
            locator = Locator.createOnlineLocator();
        }

        // The result of geocode task is passed as a parameter to map the
        // results
        protected void onPostExecute(List<LocatorGeocodeResult> result) {

            if (result == null || result.size() == 0) {
                // no results found
            } else {
                Point destinationLocation = result.get(0).getLocation();
            }
        }

        // invoke background thread to perform geocode task
        @Override
        protected Void doInBackground(Void ... params) {
            try {
                // access token for arcgis
                //MlgYiMxHuf2KYzz8zHLGvoljudlgtObFDSjOIr9IXNguH83SgEfnst3aVCkXQtXxnJ5mr8OQjfPOIzwWOt8gRvYfR9eRJeazY4yFUsY1kalMV0v0dm8SkvznStS19ztQsgYFlF0m0jw_gJb4bt7sxw..
                String BASE_URL = "http://route.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World/solve";

                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(BASE_URL +
                        "?token=MlgYiMxHuf2KYzz8zHLGvoljudlgtObFDSjOIr9IXNguH83SgEfnst3aVCkXQtXxnJ5mr8OQjfPOIzwWOt8gRvYfR9eRJeazY4yFUsY1kalMV0v0dm8SkvznStS19ztQsgYFlF0m0jw_gJb4bt7sxw.." +
                        "&stops=" + location.getLongitude() + "," + location.getLatitude() +
                        ";" + destinationLocation.getLongitude() + "," + destinationLocation.getLatitude() +
                        "&f=json");
                HttpResponse response = client.execute(get);
                BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while((line = br.readLine()) != null){
                    sb.append(line);
                }
                JSONObject obj = new JSONObject(sb.toString());
                processRoutes(obj);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return null;
        }
    }

}
