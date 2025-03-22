package com.gfaim.activities.calendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.calendar.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private List<Ingredient> ingredients;
    private final OnIngredientClickListener listener;
    private final Context context;

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
    }

    public IngredientAdapter(Context context, OnIngredientClickListener listener) {
        this.context = context;
        this.ingredients = new ArrayList<>();
        this.listener = listener;
    }

    public IngredientAdapter(List<Ingredient> ingredients, OnIngredientClickListener listener) {
        this.context = null;
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
        this.listener = listener;
    }

    /**
     * Met à jour la liste des ingrédients
     * @param ingredients Nouvelle liste d'ingrédients
     */
    public void setIngredientList(List<Ingredient> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
        notifyDataSetChanged();
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
        if (ingredients != null && position < ingredients.size()) {
            Ingredient ingredient = ingredients.get(position);
            holder.bind(ingredient, listener);
        }
    }

    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size() : 0;
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
            if (ingredient != null) {
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
}
