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
import com.mobile.android.trafficlock.DialogActivity;
import com.mobile.android.trafficlock.R;

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




        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl=pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "DialogActivity");
        wl.acquire();




        //The alert way of doing shit


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





        //The notification way of doing shit

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//        Notification notify = new Notification();
        Notification notify = new Notification.Builder(context)
                .setTicker("Ticker Message")
                .setContentTitle("Wake the fuck up!!1!")
                .setContentText("Content Text")
                .setSmallIcon(R.drawable.ic_launcher)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .build();

        //notify.defaults |= Notification.DEFAULT_VIBRATE;
        //notify.defaults |= Notification.DEFAULT_ALL;

//        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                "MyWakelockTag");
//        wakeLock.acquire();

        nm.notify(0, notify);







//        Intent dialog = new Intent(context, DialogActivity.class);
//        dialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(dialog);

        wl.release();


        SetAlarm.alertCanOccur = false;
    }
}
