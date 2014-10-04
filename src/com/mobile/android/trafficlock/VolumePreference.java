package com.mobile.android.trafficlock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

public class VolumePreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {

    private static final int startVolume = 50;

    private SeekBar seekBar;

    public VolumePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        seekBar = (SeekBar) view.findViewById(R.id.alarmVolume);
        seekBar.setMax(100);
        seekBar.setProgress(getSharedPreferences().getInt(getKey() + ".progress", startVolume));
    }

    @Override
    public void onClick(DialogInterface dialog, int button) {
        if (button == dialog.BUTTON_POSITIVE) {
            SharedPreferences.Editor editor = getEditor();
            editor.putInt(getKey() + ".progress", seekBar.getProgress());
            editor.commit();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
