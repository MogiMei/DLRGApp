package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class CustomCalendarView extends LinearLayout {
    final String TAG = "CustomCalendarView";

    LinearLayout header;
    ImageView btnPrev;
    ImageView btnNext;
    TextView txtMonthYear;
    GridView gridView;
    private String[] months = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};

    public CustomCalendarView(Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar_layout, this);
        assignUiElements();
        updateCalendar(null);
    }

    private void assignUiElements() {
        header = findViewById(R.id.calendar_header);
        Log.d(TAG, String.valueOf(header.getChildAt(0).getVisibility() == View.VISIBLE));
        btnPrev = findViewById(R.id.calendar_prev_button);
        btnNext = findViewById(R.id.calendar_next_button);
        txtMonthYear = findViewById(R.id.date_display);
        gridView = findViewById(R.id.calendar_grid);
    }

    public void updateCalendar(HashSet<Date> events) {
        ArrayList<Date> cells = new ArrayList<>();
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int DAYS_COUNT = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.DAY_OF_WEEK) - 2;
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        while (cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        gridView.setAdapter(new CalendarAdapter(getContext(), cells, events));

        txtMonthYear.setText((months[calendar.get(Calendar.MONTH) - 1] + " " +calendar.get(Calendar.YEAR)));
    }
}
