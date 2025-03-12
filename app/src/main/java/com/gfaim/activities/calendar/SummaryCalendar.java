package com.gfaim.activities.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;
import com.gfaim.activities.Ingredient;
import com.gfaim.activities.MenuData;

public class SummaryCalendar extends AppCompatActivity {

    private TextView menuNameText;
    private TextView participantCountText;
    private TextView ingredientsText;
    private TextView stepsText;
    private MenuData menuData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_calendar);
        MenuData menuData = (MenuData) getIntent().getSerializableExtra("MENU_DATA");

        TextView menuNameTextView = findViewById(R.id.menu_name);
        TextView participantCount = findViewById(R.id.participant_count);
        LinearLayout ingredientsList = findViewById(R.id.ingredients_list);
        LinearLayout stepsList = findViewById(R.id.steps_list);

        if (menuData != null) {
            menuNameTextView.setText(menuData.getMenuName());
            participantCount.setText("Number of participants: " + menuData.getParticipantCount());

            for (String ingredientName : menuData.getIngredients()) {
                TextView ingredientView = new TextView(this);
                ingredientView.setText(ingredientName); // Affiche juste le nom
                ingredientsList.addView(ingredientView);
            }

            // Ajouter les étapes
            int stepNumber = 1;
            for (String step : menuData.getSteps()) {
                TextView stepView = new TextView(this);
                stepView.setText(stepNumber + " - " + step);
                stepsList.addView(stepView);
                stepNumber++;
            }
        }

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(SummaryCalendar.this, AddStepsCalendar.class);
            intent.putExtra("MENU_NAME", R.id.menu_name);
            startActivity(intent);
        });



        // Récupérer le menuName de AddStepActivity
        String menuName = getIntent().getStringExtra("MENU_NAME");
        if (menuName != null && !menuName.isEmpty()) {
            menuNameTextView.setText(menuName);
        }

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
