package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import android.app.DatePickerDialog;
import android.content.Context;

public class WachstundenDatePicker extends DatePickerDialog {
    private CharSequence title;

    public WachstundenDatePicker(Context context, DatePickerDialog.OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
    }

    public void setPermanentTitle(CharSequence title) {
        this.title = title;
        setTitle(title);
    }

    public void setTitle(CharSequence title) {
        super.setTitle(this.title);
    }
}
