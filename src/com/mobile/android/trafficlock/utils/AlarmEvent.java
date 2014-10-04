package com.mobile.android.trafficlock.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by michael on 10/4/14.
 */
public class AlarmEvent extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        long[] pattern = new long[5];

        Notification notify = new Notification.Builder(context)
                .setVibrate(pattern)
                .build();

        nm.notify(0, notify);
    }
}
