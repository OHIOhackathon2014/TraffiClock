package com.mobile.android.trafficlock.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import com.mobile.android.trafficlock.R;

/**
 * Created by michael on 10/4/14.
 */
public class SetAlarm {

    public static void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);



//        BroadcastReceiver br = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                nm.notify();
//            }
//        };

        //Intent noiseEvent = new Intent(this, br.getClass());

        Intent noiseEvent = new Intent(context, AlarmEvent.class);



        long time = 3000 + System.currentTimeMillis();

        //it's a singleton
        //PendingIntent pi = new PendingIntent();


        //"IntentSender" is now/redirects to "PendingIntent" in the API documentation


        //one of these to retrieve an instance:
        PendingIntent noiseEventPending = PendingIntent.getBroadcast(context, 0, noiseEvent, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pi = PendingIntent.getService();
        //get Broadcast is mentioned to be what is typically used.

        //am.set(AlarmManager.RTC_WAKEUP, time, noiseEventPending);

        if (Build.VERSION.SDK_INT >= 19) {
            am.setExact(AlarmManager.RTC_WAKEUP, time, noiseEventPending);
        }
        else {
            am.set(AlarmManager.RTC_WAKEUP, time, noiseEventPending);
        }
    }


}
