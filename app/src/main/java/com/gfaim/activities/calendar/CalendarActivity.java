package com.gfaim.activities.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.MealAdapter;
import com.gfaim.activities.NavigationBar;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {


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


        Intent intent = getIntent();
        String menuName = intent.getStringExtra("MENU_NAME");
        int totalCalories = intent.getIntExtra("TOTAL_CALORIES", 0);
        String selectedDate = intent.getStringExtra("SELECTED_DATE"); // Ex: "26 Mai"

        if (menuName != null && selectedDate != null) {
            updateMealCard(selectedDate, menuName, totalCalories);
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);


    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("MENU_NAME") && intent.hasExtra("TOTAL_CALORIES")) {
            String menuName = intent.getStringExtra("MENU_NAME");
            int totalCalories = intent.getIntExtra("TOTAL_CALORIES", 0);

            TextView cardText = findViewById(R.id.cardText);
            TextView caloriesText = findViewById(R.id.caloriesText);

            cardText.setText(menuName);
            caloriesText.setText(totalCalories + " kcal");
        }
    }

    private void updateMealCard(String date, String menuName, int totalCalories) {
        TextView cardText = findViewById(R.id.cardText);
        TextView caloriesText = findViewById(R.id.caloriesText);
        cardText.setText(menuName);
        caloriesText.setText(totalCalories + " kcal");
    }
}
