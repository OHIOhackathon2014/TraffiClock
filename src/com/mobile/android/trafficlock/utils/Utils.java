package com.mobile.android.trafficlock.utils;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: Rniemo
 * Date: 10/4/14
 * Time: 1:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class Utils {


    private static final String GEOCODE_REQUEST = "http://maps.google.com/maps/api/geocode/json";

    public static boolean isConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static Location geocode(String address){
        Location ret = new Location("");
        HttpURLConnection conn = null;
        try{
            URL url = new URL(GEOCODE_REQUEST + "?address=" + URLEncoder.encode(address, "UTF-8") + "&sensor=false");

            // prepare an HTTP connection to the geocoder
            conn = (HttpURLConnection) url.openConnection();

            conn.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String input = "";
            String jsonString = "";
            while((input = in.readLine()) != null){
                jsonString += input;
            }
            in.close();
            JSONObject json = new JSONObject(jsonString);
            JSONArray results = json.getJSONArray("results");
            if(results.length() > 0){
                JSONObject result = results.getJSONObject(0);
                JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
                ret.setLatitude(location.getDouble("lat"));
                ret.setLongitude(location.getDouble("lng"));
            }else{
                //	System.out.println(jsonString);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            if(conn != null){
                conn.disconnect();
            }
        }
        return ret;
    }
}
