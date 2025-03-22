package com.gfaim.activities.calendar.adapter;

import android.content.Context;
import android.util.Log;
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
    private static final String TAG = "IngredientAdapter";
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
        Log.d(TAG, "setIngredientList: Mise à jour de la liste avec " + this.ingredients.size() + " ingrédients");

        // Log détaillé pour chaque ingrédient
        for (int i = 0; i < this.ingredients.size(); i++) {
            Ingredient ingredient = this.ingredients.get(i);
            Log.d(TAG, "Ingrédient " + (i+1) + ": " +
                    "Nom=" + ingredient.getName() + ", " +
                    "Quantité=" + ingredient.getQuantity() + ", " +
                    "Unité=" + ingredient.getUnit());
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Création d'un nouveau ViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        if (ingredients != null && position < ingredients.size()) {
            Ingredient ingredient = ingredients.get(position);
            Log.d(TAG, "onBindViewHolder: Liaison de l'ingrédient " + ingredient.getName() + " à la position " + position);
            holder.bind(ingredient, listener);
        } else {
            Log.e(TAG, "onBindViewHolder: Impossible de lier l'ingrédient à la position " + position);
        }
    }

    @Override
    public int getItemCount() {
        int count = ingredients != null ? ingredients.size() : 0;
        Log.d(TAG, "getItemCount: " + count + " ingrédients");
        return count;
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.ingredient_name);

            // Vérifier que la vue est correctement initialisée
            if (nameTextView == null) {
                Log.e(TAG, "IngredientViewHolder: TextView ingredient_name est null!");
            }
        }

        public void bind(final Ingredient ingredient, final OnIngredientClickListener listener) {
            if (ingredient != null) {
                // Formatage du texte pour inclure quantité et unité si disponibles
                String displayText = ingredient.getName();
                if (ingredient.getQuantity() > 0) {
                    displayText = String.format("%s (%.1f %s)",
                            ingredient.getName(),
                            ingredient.getQuantity(),
                            ingredient.getUnit() != null && !ingredient.getUnit().isEmpty() ? ingredient.getUnit() : "");
                }

                Log.d(TAG, "bind: Affichage du texte: " + displayText);
                nameTextView.setText(displayText);
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onIngredientClick(ingredient);
                    }
                });
            } else {
                Log.e(TAG, "bind: L'ingrédient est null");
            }
        }
    }
}
