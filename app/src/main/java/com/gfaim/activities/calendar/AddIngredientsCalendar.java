package com.gfaim.activities.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;
import com.gfaim.activities.Ingredient;
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
    private Ingredient ingredient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredients_calendar);
        participantCountText = findViewById(R.id.participant_count);
        menuData = new MenuData();
        ingredientContainer = findViewById(R.id.ingredientContainer);
        ImageButton addIngredientButton = findViewById(R.id.add_ingredient_button);
        addIngredientButton.setOnClickListener(v -> openIngredientDialog());
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
        back.setOnClickListener(v -> {
            Intent intent = new Intent(AddIngredientsCalendar.this, CalendarActivity.class);
            intent.putExtra("MENU_DATA", menuData);
            startActivity(intent);
            finish();
        });

        if (menuData != null && menuData.getIngredients() != null) {
            for (String ingredient : menuData.getIngredients()) {
                EditText ingredientEditText = new EditText(this);
                ingredientEditText.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                ingredientEditText.setText(ingredient);
                ingredientEditText.setHint("Ingredient");
                ingredientEditText.setPadding(16, 16, 16, 16);
                ingredientEditText.setTextSize(16);
                ingredientEditText.setTextColor(getResources().getColor(R.color.black));

                ingredientContainer.addView(ingredientEditText);
            }
        }

        updateParticipantCount();
        Button nextButton = findViewById(R.id.next);
        menuNameEditText = findViewById(R.id.menuName);

        nextButton.setOnClickListener(v -> {
            menuData.setMenuName(menuNameEditText.getText().toString().trim());
            menuData.setParticipantCount(participantCount);
            //menuData.addIngredient(ingredient.getName());

            Intent intent = new Intent(AddIngredientsCalendar.this, AddStepsCalendar.class);
            intent.putExtra("MENU_DATA", menuData);
            startActivity(intent);
        });
    }

    private void updateParticipantCount() {
        participantCountText.setText(String.valueOf(participantCount));
    }

    private void openIngredientDialog() {
        DialogAddIngredient dialog = new DialogAddIngredient(this, this::addIngredientToList);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String menuName = data.getStringExtra("MENU_NAME");
            if (menuName != null) {
                menuNameEditText.setText(menuName);
            }
        }
    }

    private void addIngredientToList(Ingredient ingredient) {
        ingredientList.add(String.valueOf(ingredient));

        TextView ingredientView = new TextView(this);
        ingredientView.setText(ingredient.getName() + " - " + ingredient.getCalories() + " kcal");
        ingredientView.setTextSize(18);
        ingredientContainer.addView(ingredientView);
    }

    private void addIngredient(String ingredient) {
        ingredientList.add(ingredient);
        Log.d("Ingredients", "Ajouté: " + ingredient);
    }
}
