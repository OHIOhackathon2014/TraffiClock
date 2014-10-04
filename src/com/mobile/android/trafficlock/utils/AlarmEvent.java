package com.mobile.android.trafficlock.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
//        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        //long[] pattern = {0, 100, 100, 100, 100, 100, 100, 100};
//
//
//
////        Notification notify = new Notification();
//        Notification notify = new Notification.Builder(context)
//                .setContentTitle("Wake the fuck up!!1!")
//                .build();
//
//        notify.defaults |= Notification.DEFAULT_VIBRATE;
//
//
////        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
////        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
////                "MyWakelockTag");
////        wakeLock.acquire();
//
//        nm.notify(0, notify);


        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        final KeyguardManager.KeyguardLock kl=km.newKeyguardLock("My_App");
        kl.disableKeyguard();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl=pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "My_App");
        wl.acquire();

//        AlertDialog.Builder ad_b=new AlertDialog.Builder(context);
//        ad_b.setMessage("Hi there!");
//        ad_b.setCancelable(false);
//        ad_b.setNeutralButton("Close", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface d, int i) {
//                kl.reenableKeyguard();
//            }
//        });
//        AlertDialog ad=ad_b.create();
//        ad.show();

        AlertDialog.Builder customBuilder = new AlertDialog.Builder(context);
        customBuilder.setMessage("Fuck yo shit");
        customBuilder.setPositiveButton("lololol",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customBuilder.setCancelable(false);
        customBuilder.show();

        wl.release();
    }
}
