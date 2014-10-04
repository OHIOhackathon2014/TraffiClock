package com.mobile.android.trafficlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.mobile.android.trafficlock.datagrabber.DataService;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent dataServiceIntent = new Intent(this, DataService.class);
        startService(dataServiceIntent);

    }


}
