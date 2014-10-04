package com.mobile.android.trafficlock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {

    private static final int startHour = 7;
    private static final int startMinutes = 30;

    private TimePicker timePicker;

    public TimePreference(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    @Override
    public void onBindDialogView(View view) {
        timePicker = (TimePicker) view.findViewById(R.id.wakeUpNoLaterThan);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(getContext()));
                timePicker.setCurrentHour(getSharedPreferences().getInt(getKey() + ".hours", startHour));
        timePicker.setCurrentMinute(getSharedPreferences().getInt(getKey() + ".minutes", startMinutes));
    }

    @Override
    public void onClick(DialogInterface dialog, int button) {
        if (button == dialog.BUTTON_POSITIVE) {
            SharedPreferences.Editor editor = getEditor();
            editor.putInt(getKey() + ".hours", timePicker.getCurrentHour());
            editor.putInt(getKey() + ".minutes", timePicker.getCurrentMinute());
            editor.commit();
        }
    }
}
