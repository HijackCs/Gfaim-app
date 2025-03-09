package com.gfaim.activities.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddIngredientsCalendar extends AppCompatActivity {

    private TextView participantCountText;
    private int participantCount = 1;
    private final int MIN_PARTICIPANTS = 1;
    private final int MAX_PARTICIPANTS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredient_calendar);
        participantCountText = findViewById(R.id.participant_count);
        
        ImageButton addButton = findViewById(R.id.add_btn);
        ImageButton removeButton = findViewById(R.id.remove_btn);
        addButton.setOnClickListener(v -> {
            if (participantCount < MAX_PARTICIPANTS) {
                participantCount++;
                updateParticipantCount();
            } else {
                Toast.makeText(this, "Max 10 participants", Toast.LENGTH_SHORT).show();
            }
        });
        removeButton.setOnClickListener(v -> {
            if (participantCount > MIN_PARTICIPANTS) {
                participantCount--;
                updateParticipantCount();
            } else {
                Toast.makeText(this, "Min 1 participant", Toast.LENGTH_SHORT).show();
            }
        });

        updateParticipantCount();
        Button nextButton = findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddIngredientsCalendar.this, AddStepsCalendar.class);
                startActivity(intent);
            }
        });
    }

    private void updateParticipantCount() {
        participantCountText.setText(String.valueOf(participantCount));
    }
}
