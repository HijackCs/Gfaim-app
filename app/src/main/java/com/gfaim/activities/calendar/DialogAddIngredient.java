package com.gfaim.activities.calendar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gfaim.R;
import com.gfaim.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class DialogAddIngredient extends Dialog {
    private final OnIngredientSelectedListener listener;

    public interface OnIngredientSelectedListener {
        void onIngredientSelected(FoodItem ingredient);
    }

    public DialogAddIngredient(@NonNull Context context, OnIngredientSelectedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_ingredient);

        RecyclerView recyclerView = findViewById(R.id.mealRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //List<FoodItem> ingredientList = getSampleIngredients();
        //IngredientAdapter adapter = new IngredientAdapter(ingredientList, listener::onIngredientSelected);
       // recyclerView.setAdapter(adapter);

        ImageView closeButton = findViewById(R.id.back);
        closeButton.setOnClickListener(v -> dismiss());
    }


   /* private List<Ingredient> getSampleIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Tomato", 18));
        ingredients.add(new Ingredient("Cheese", 402));
        ingredients.add(new Ingredient("Chicken", 165));
        ingredients.add(new Ingredient("Rice", 130));
        ingredients.add(new Ingredient("Egg", 155));
        ingredients.add(new Ingredient("Milk", 42));
        ingredients.add(new Ingredient("Avocado", 160));
        ingredients.add(new Ingredient("Potato", 77));
        ingredients.add(new Ingredient("Salmon", 208));
        return ingredients;
    }*/
}
