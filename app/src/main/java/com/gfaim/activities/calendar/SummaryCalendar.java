package com.gfaim.activities.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;

public class SummaryCalendar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_calendar);

        Button finishButton = findViewById(R.id.finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryCalendar.this, CalendarActivity.class);
                startActivity(intent);
            }
        });
    }
}
