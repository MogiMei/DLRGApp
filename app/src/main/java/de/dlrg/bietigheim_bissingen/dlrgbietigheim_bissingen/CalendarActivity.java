package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {
    private static final String TAG = "Calendar";
    private CustomCalendarView calendarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = (CustomCalendarView) findViewById(R.id.calendarView);
    }
}
