package com.gfaim.activities.calendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.Ingredient;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private List<String> ingredients;
    private OnIngredientClickListener onIngredientClickListener;

    public interface OnIngredientClickListener {
        void onIngredientClick(String ingredient);
    }

    public IngredientAdapter(List<String> ingredients, OnIngredientClickListener listener) {
        this.ingredients = ingredients;
        this.onIngredientClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ingredient = ingredients.get(position);
        holder.ingredientName.setText(ingredient);

        // Ajoute l'événement de clic
        holder.itemView.setOnClickListener(v -> {
            if (onIngredientClickListener != null) {
                onIngredientClickListener.onIngredientClick(ingredient);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size() : 0;
    }

    public void updateData(List<String> newIngredients) {
        this.ingredients = newIngredients;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientName;

        public ViewHolder(View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.itemName);
        }
    }
}
