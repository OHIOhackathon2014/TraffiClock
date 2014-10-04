package com.mobile.android.trafficlock.utils;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by michael on 10/4/14.
 */
public class AlarmEvent extends BroadcastReceiver {


    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //long[] pattern = {0, 100, 100, 100, 100, 100, 100, 100};



//        Notification notify = new Notification();
        Notification notify = new Notification.Builder(context)
                .setContentTitle("Wake the fuck up!!1!")
                .build();

        notify.defaults |= Notification.DEFAULT_VIBRATE;


//        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                "MyWakelockTag");
//        wakeLock.acquire();

        nm.notify(0, notify);


        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();

        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock =  keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();

//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wakeLock.release();
    }
}
