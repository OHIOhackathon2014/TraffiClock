package com.mobile.android.trafficlock.utils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Michael Abbott on 10/4/14.
 */
public class AlarmEventDispatcher extends IntentService {


    public AlarmEventDispatcher() {
        super("something");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //
    }

    private void execute(String action, String notificationId) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent noiseEvent = new Intent();

        long time = 0;

        //it's a singleton
        //PendingIntent pi = new PendingIntent();

        //one of these to retrieve an instance:
        PendingIntent noiseEventPending = PendingIntent.getBroadcast(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pi = PendingIntent.getService();
        //get Broadcast is mentioned to be what is typically used.

        //am.set(AlarmManager.RTC_WAKEUP, time, pi);
    }
}
