package com.gfaim.activities.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gfaim.R;
import com.gfaim.activities.MenuData;

public class AddStepsCalendar extends AppCompatActivity {

    private LinearLayout stepsContainer;
    private int stepCount = 2;
    private MenuData menuData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_steps_calendar);

        stepsContainer = findViewById(R.id.stepsContainer);
        ImageButton addStepButton = findViewById(R.id.add_step_button);

        addStepButton.setOnClickListener(v -> addNewStep());

        menuData = (MenuData) getIntent().getSerializableExtra("MENU_DATA");
        if (menuData == null) {
            menuData = new MenuData();
        }

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(AddStepsCalendar.this, AddIngredientsCalendar.class);
            intent.putExtra("MENU_DATA", menuData);
            startActivity(intent);
            finish();
        });

        if (menuData != null && menuData.getSteps() != null) {
            for (String step : menuData.getSteps()) {
                EditText stepEditText = new EditText(this);
                stepEditText.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                stepEditText.setText(step); // Mettre la valeur dans l'EditText
                stepEditText.setHint("Step " + stepCount);
                stepEditText.setPadding(16, 16, 16, 16);
                stepEditText.setTextSize(16); // Applique le même style
                stepEditText.setTextColor(getResources().getColor(R.color.black));

                stepsContainer.addView(stepEditText);
                stepCount++;
            }
        }

        Button nextButton = findViewById(R.id.next);
        nextButton.setOnClickListener(v -> {
            saveAllSteps();
            Intent intent = new Intent(AddStepsCalendar.this, SummaryCalendar.class);
            intent.putExtra("MENU_DATA", menuData);
            startActivity(intent);
        });
    }

    private void addNewStep() {
        EditText firstStep = findViewById(R.id.step);
        EditText newStep = new EditText(this);
        newStep.setLayoutParams(firstStep.getLayoutParams());
        newStep.setHint("Step " + stepCount);
        newStep.setPadding(
                firstStep.getPaddingLeft(),
                firstStep.getPaddingTop(),
                firstStep.getPaddingRight(),
                firstStep.getPaddingBottom()
        );
        newStep.setTextSize(16);
        newStep.setBackground(firstStep.getBackground());
        newStep.setTextColor(firstStep.getCurrentTextColor());
        stepsContainer.addView(newStep);
        stepCount++;
    }

    private void saveAllSteps() {
        menuData.getSteps().clear();

        for (int i = 0; i < stepsContainer.getChildCount(); i++) {
            View view = stepsContainer.getChildAt(i);
            if (view instanceof EditText) {
                String step = ((EditText) view).getText().toString().trim();
                if (!step.isEmpty()) {
                    menuData.addStep(step);
                }
            }
        }
    }
}
