package com.mobile.android.trafficlock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.SeekBar;

/**
 * Created by jkahn on 10/4/14.
 */
public class VolumePreference extends DialogPreference {

    private static final int startVolume = 50;

    private SeekBar seekBar;

    public VolumePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        seekBar = (SeekBar) view.findViewById(R.id.numberPickerLayout);
        seekBar.setMax(100);
        seekBar.setProgress(getSharedPreferences().getInt(getKey() + ".minutes", startVolume));
    }

    @Override
    public void onClick(DialogInterface dialog, int button) {
        if (button == dialog.BUTTON_POSITIVE) {
            SharedPreferences.Editor editor = getEditor();
            editor.putInt(getKey() + ".progress", seekBar.getProgress());
            editor.commit();
        }
    }
}
