package com.mobile.android.trafficlock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by jkahn on 10/4/14.
 */
public class DialogActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(getString(R.string.alarmNotificationTitle));

        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.wakeUpMessage))
                .setCancelable(false)
                .setNeutralButton(getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        stopService(getIntent());
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}
