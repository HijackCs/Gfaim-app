package com.gfaim.activities.calendar.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        TextView textTitle = holder.itemView.findViewById(R.id.textTitle);

        // Définir le titre en fonction du type de repas
        switch (meal) {
            case "Breakfast":
                textTitle.setText(R.string.breakfast);
                break;
            case "Lunch":
                textTitle.setText(R.string.lunch);
                break;
            case "Dinner":
                textTitle.setText(R.string.dinner);
                break;
            default:
                textTitle.setText(meal);
        }

        // Ajouter un OnClickListener au CardView
        holder.cardView.setOnClickListener(v -> showRecipeOptionsDialog(v.getContext()));

        // Gérer le clic sur "Add a snack"
        View addSnackLayout = holder.itemView.findViewById(R.id.add_snack);
        addSnackLayout.setOnClickListener(v -> showRecipeOptionsDialog(v.getContext()));
    }

    private void showRecipeOptionsDialog(android.content.Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose an option");

        // Créer les boutons
        builder.setPositiveButton("Create a recipe", (dialog, which) -> {
            if (navController != null) {
                // Naviguer vers AddIngredientsFragment
                navController.navigate(R.id.action_calendar_to_addIngredients);
            }
        });

        builder.setNegativeButton("Choose a recipe", (dialog, which) -> {
            if (navController != null) {
                navController.navigate(R.id.action_calendar_to_chooseRecipe);
            }
        });

        // Créer et afficher le dialog
        builder.create().show();
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