package com.gfaim.activities.calendar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.calendar.model.Ingredient;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private final List<Ingredient> ingredients;
    private final OnIngredientClickListener listener;

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
    }


    public IngredientAdapter(List<Ingredient> ingredients, OnIngredientClickListener listener) {
        this.ingredients = ingredients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient, listener);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }


    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView caloriesTextView;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.ingredient_name);
            caloriesTextView = itemView.findViewById(R.id.ingredient_calories);
        }

        public void bind(final Ingredient ingredient, final OnIngredientClickListener listener) {
            nameTextView.setText(ingredient.getName());
            caloriesTextView.setText(String.valueOf(ingredient.getCalories()) + " kcal");
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIngredientClick(ingredient);
                }
            });
        }
    }
}
