package com.gfaim.activities.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;
import com.gfaim.activities.Ingredient;
import com.gfaim.activities.MenuData;

import java.util.HashMap;

public class SummaryCalendar extends AppCompatActivity {

    private TextView menuNameText;
    private TextView participantCountText;
    private LinearLayout ingredientsList;
    private LinearLayout stepsList;
    private MenuData menuData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_calendar);

        // Initialisation des vues
        menuNameText = findViewById(R.id.menu_name);
        participantCountText = findViewById(R.id.participant_count);
        ingredientsList = findViewById(R.id.ingredients_list);
        stepsList = findViewById(R.id.steps_list);

        // Récupération de l'objet MenuData passé par l'Intent
        menuData = (MenuData) getIntent().getSerializableExtra("MENU_DATA");

        if (menuData != null) {
            // Affiche le nom du menu et le nombre de participants
            menuNameText.setText(menuData.getMenuName());
            participantCountText.setText("Number of participants: " + menuData.getParticipantCount());

            // Affiche les ingrédients
            ingredientsList.removeAllViews(); // Clear pour éviter des doublons si on revient sur l'activité
            for (String ingredient : menuData.getIngredients()) {
                TextView ingredientView = new TextView(this);
                ingredientView.setText(ingredient);
                ingredientsList.addView(ingredientView);
            }

            // Affiche les étapes
            stepsList.removeAllViews(); // Pareil pour éviter les doublons
            int stepNumber = 1;
            for (String step : menuData.getSteps()) {
                TextView stepView = new TextView(this);
                stepView.setText(stepNumber + " - " + step);
                stepsList.addView(stepView);
                stepNumber++;
            }
            Button finishButton = findViewById(R.id.finish);
            finishButton.setOnClickListener(v -> {
                int totalCalories = calculateTotalCalories();
                Intent intent = new Intent(SummaryCalendar.this, CalendarActivity.class);
                intent.putExtra("MENU_NAME", menuData.getMenuName());
                intent.putExtra("TOTAL_CALORIES", totalCalories);
                setResult(RESULT_OK, intent);
                startActivity(intent);
            });
        }

        if (menuData == null) {
            Log.e("SummaryCalendar", "menuData is null!");
        } else {
            Log.d("SummaryCalendar", "Menu Name: " + menuData.getMenuName());
            Log.d("SummaryCalendar", "Ingredients: " + menuData.getIngredients());
            Log.d("SummaryCalendar", "Steps: " + menuData.getSteps());
        }


        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(SummaryCalendar.this, CalendarActivity.class);
            intent.putExtra("MENU_DATA", menuData);
            startActivity(intent);
            finish();
        });

    }

    private int calculateTotalCalories() {
        int totalCalories = 0;
        for (String ingredientName : menuData.getIngredients()) {
            int calories = getCaloriesFromDatabase(ingredientName);
            totalCalories += calories;
        }

        return totalCalories;
    }
    private int getCaloriesFromDatabase(String ingredientName) {
        HashMap<String, Integer> fakeDB = new HashMap<>();
        fakeDB.put("Tomato", 20);
        fakeDB.put("Cheese", 100);
        fakeDB.put("Bread", 80);

        return fakeDB.getOrDefault(ingredientName, 0);
    }


}
