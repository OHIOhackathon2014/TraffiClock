package com.mobile.android.trafficlock.utils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;

/**
 * Created by Michael Abbott on 10/4/14.
 */
public class AlarmEventDispatcher extends IntentService {


    public static final String ACTION = "ACTION";

    public AlarmEventDispatcher() {
        super("something");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //
    }

    private void execute() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //
            }
        };

        Intent noiseEvent = new Intent(this, br.getClass());

        long time = 0;

        //it's a singleton
        //PendingIntent pi = new PendingIntent();


        //"IntentSender" is now/redirects to "PendingIntent" in the API documentation


        //one of these to retrieve an instance:
        PendingIntent noiseEventPending = PendingIntent.getBroadcast(this, 0, noiseEvent, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pi = PendingIntent.getService();
        //get Broadcast is mentioned to be what is typically used.

        //am.set(AlarmManager.RTC_WAKEUP, time, pi);
    }
}
