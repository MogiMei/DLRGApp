package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import android.app.TimePickerDialog;
import android.content.Context;

public class WachstundenTimePicker extends TimePickerDialog {
    private CharSequence title;

    public WachstundenTimePicker(Context context, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, listener, hourOfDay, minute, is24HourView);

    }

    public void setPermanentTitle(CharSequence title) {
        this.title = title;
        setTitle(title);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(this.title);
    }
}