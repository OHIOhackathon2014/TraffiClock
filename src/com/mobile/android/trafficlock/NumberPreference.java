package com.mobile.android.trafficlock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPreference extends DialogPreference {

    private static final int startMinutes = 30;

    private NumberPicker numberPicker;

    public NumberPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        numberPicker = (NumberPicker) view.findViewById(R.id.numberPickerLayout);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(60);
        numberPicker.setValue(getSharedPreferences().getInt(getKey() + ".minutes", startMinutes));
    }

    @Override
    public void onClick(DialogInterface dialog, int button) {
        if (button == dialog.BUTTON_POSITIVE) {
            SharedPreferences.Editor editor = getEditor();
            editor.putInt(getKey() + ".hours", numberPicker.getValue());
            editor.commit();
        }
    }
}
