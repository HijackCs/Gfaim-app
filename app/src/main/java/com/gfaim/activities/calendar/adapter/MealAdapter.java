package com.gfaim.activities.calendar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<String> mealList;
    private NavController navController;

    public MealAdapter(List<String> mealList, NavController navController) {
        this.mealList = mealList;
        this.navController = navController;
    }

    public MealAdapter(List<String> mealList) {
        this.mealList = mealList;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_card, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        String meal = mealList.get(position);

        // Configurer les données du CardView ici (si nécessaire)

        // Ajouter un OnClickListener au CardView
        holder.cardView.setOnClickListener(v -> {
            if (navController != null) {
                // Naviguer vers AddIngredientsFragment
                navController.navigate(R.id.action_calendar_to_addIngredients);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}