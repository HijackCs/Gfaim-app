package com.gfaim.activities.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.Meal;
import com.gfaim.activities.MealAdapter;
import com.gfaim.activities.NavigationBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private CardView cardView;
    private TextView cardText;
    private TextView textTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.calendar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        NavigationBar navigationBar = new NavigationBar(this);

        int activeButtonId = getIntent().getIntExtra("activeButtonId", -1);
        if (activeButtonId != -1) {
            navigationBar.setActiveButton(activeButtonId);
        }

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            Log.d("CalendarActivity", "Date sélectionnée: " + date);

            RecyclerView recyclerView = findViewById(R.id.mealRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            List<String> mealList = Arrays.asList("Breakfast", "Lunch", "Dinner");
            Log.d("CalendarActivity", "Nombre de repas: " + mealList.size());

            MealAdapter adapter = new MealAdapter(mealList, CalendarActivity.this);
            recyclerView.setAdapter(adapter);
        });
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);


    }


}
