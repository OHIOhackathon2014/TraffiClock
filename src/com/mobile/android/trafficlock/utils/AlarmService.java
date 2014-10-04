package com.mobile.android.trafficlock.utils;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by michael on 10/4/14.
 */
public class AlarmService extends IntentService {


    public AlarmService() {
        super("something");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //
    }
}
