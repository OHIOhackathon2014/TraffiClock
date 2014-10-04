package com.mobile.android.trafficlock.datagrabber;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationService implements LocationListener{

    private static final int ONE_MINUTE = 1000 * 60;
    private static final int MIN_TIME = ONE_MINUTE;
    private static final int MIN_DISTANCE = 0;
    private List<ILocationListener> listeners = new ArrayList<ILocationListener>();
    private Location currentLocation;

    public LocationService(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        List<String> providers = locationManager.getProviders(criteria, true);
        for (String provider : providers) {
            locationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, this);
        }
    }

    public Location getCurrentLocation(){
        return currentLocation;
    }

    public void addListener(ILocationListener listener){
        listeners.add(listener);
    }

    public void removeListener(ILocationListener listener){
        listeners.remove(listener);
    }

    public void notifyLocationListeners(Location location){
        for(ILocationListener listener : listeners){
            listener.onLocationChanged(location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(isBetterLocation(location)){
            notifyLocationListeners(location);
            currentLocation = location;
        }
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     */
    protected boolean isBetterLocation(Location location) {
        if (currentLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > ONE_MINUTE;
        boolean isSignificantlyOlder = timeDelta < -ONE_MINUTE;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    interface ILocationListener {

        public void onLocationChanged(Location location);

    }


}
