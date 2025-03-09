package com.gfaim.activities.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gfaim.R;

public class AddStepsCalendar extends AppCompatActivity {

    private LinearLayout stepsContainer;
    private int stepCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_steps_calendar);

        stepsContainer = findViewById(R.id.stepsContainer);
        ImageButton addStepButton = findViewById(R.id.add_step_button);

        addStepButton.setOnClickListener(v -> addNewStep());

        Button nextButton = findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddStepsCalendar.this, SummaryCalendar.class);
                startActivity(intent);
            }
        });
    }

    private void addNewStep() {
        // Récupère le premier EditText pour copier le style
        EditText firstStep = findViewById(R.id.step);
        EditText newStep = new EditText(this);

        // Copie les paramètres du premier EditText
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

        // Ajoute le nouveau champ dans le container
        stepsContainer.addView(newStep);
        stepCount++;
    }

}
