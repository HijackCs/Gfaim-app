package com.gfaim.activities.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;
import com.gfaim.activities.MenuData;

import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddIngredientsCalendar extends AppCompatActivity {

    private TextView participantCountText;
    private int participantCount = 1;
    private final int MIN_PARTICIPANTS = 1;
    private final int MAX_PARTICIPANTS = 10;
    private EditText menuNameEditText;
    private static final int REQUEST_CODE = 1;
    private MenuData menuData;
    private LinearLayout ingredientContainer;
    private List<String> ingredientList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredients_calendar);
        participantCountText = findViewById(R.id.participant_count);
        menuData = new MenuData();
        ingredientContainer = findViewById(R.id.ingredientContainer);
        ImageButton addIngredientButton = findViewById(R.id.add_step_button);
        addIngredientButton.setOnClickListener(v -> showAddIngredientDialog());
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

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(AddIngredientsCalendar.this, CalendarActivity.class);
            startActivity(intent);
        });


        updateParticipantCount();
        Button nextButton = findViewById(R.id.next);
        menuNameEditText = findViewById(R.id.menuName);

        nextButton.setOnClickListener(v -> {
            menuData.setMenuName(menuNameEditText.getText().toString().trim());
            menuData.setParticipantCount(participantCount);
            menuData.addIngredient("Escalope de poulet");

            Intent intent = new Intent(AddIngredientsCalendar.this, AddStepsCalendar.class);
            intent.putExtra("MENU_DATA", menuData);
            startActivity(intent);
        });
    }

    private void updateParticipantCount() {
        participantCountText.setText(String.valueOf(participantCount));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String menuName = data.getStringExtra("MENU_NAME");
            if (menuName != null) {
                menuNameEditText.setText(menuName); // Réinsère le texte dans l'EditText
            }
        }
    }

    private void showAddIngredientDialog() {
        // Affiche le pop-up
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_ingredient, null);
        builder.setView(dialogView);

        EditText ingredientName = dialogView.findViewById(R.id.ingredient_name);
        EditText calories = dialogView.findViewById(R.id.ingredient_calories);
        EditText grams = dialogView.findViewById(R.id.ingredient_grams);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = ingredientName.getText().toString().trim();
            String kcal = calories.getText().toString().trim();
            String g = grams.getText().toString().trim();

            if (!name.isEmpty() && !kcal.isEmpty() && !g.isEmpty()) {
                addIngredientToList(name, kcal, g);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void addIngredientToList(String name, String kcal, String grams) {
        String ingredientInfo = name + " - " + kcal + " kcal, " + grams + "g";
        ingredientList.add(ingredientInfo);

        TextView ingredientView = new TextView(this);
        ingredientView.setText(ingredientInfo);
        ingredientView.setTextSize(18);
        ingredientContainer.addView(ingredientView);
    }
}
