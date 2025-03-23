package com.gfaim.activities.calendar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.calendar.SharedStepsViewModel;
import com.gfaim.models.FoodItem;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private List<FoodItem> ingredients;
    private final OnIngredientClickListener listener;
    private final SharedStepsViewModel viewModel;

    public interface OnIngredientClickListener {
        void onIngredientClick(FoodItem ingredient);
    }

    public IngredientAdapter(List<FoodItem> ingredients, OnIngredientClickListener listener) {
        this.ingredients = ingredients;
        this.listener = listener;
        this.viewModel = new SharedStepsViewModel();
    }

    public void updateList(List<FoodItem> newIngredients) {
        this.ingredients = newIngredients;
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
        FoodItem ingredient = ingredients.get(position);
        holder.bind(ingredient, listener);

        // Vérifier si l'ingrédient est déjà sélectionné
       /* List<FoodItem> selectedIngredients = viewModel.getIngredients().getValue();
        if (selectedIngredients != null && selectedIngredients.contains(ingredient)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_green));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        }*/
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

        public void bind(final FoodItem ingredient, final OnIngredientClickListener listener) {
            nameTextView.setText(ingredient.getName());
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIngredientClick(ingredient);
                }
            });
        }
    }
}
