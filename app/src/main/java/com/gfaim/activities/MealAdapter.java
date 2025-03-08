package com.gfaim.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<Meal> mealList;

    public MealAdapter(List<Meal> mealList) {
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
        Meal meal = mealList.get(position);
        holder.textTitle.setText(meal.getTitle());
        holder.cardText.setText(meal.getText());
        holder.cardImage.setImageResource(meal.getImageResId());
        holder.timeText.setText(meal.getTime() + " min");
        holder.caloriesText.setText(meal.getCalories() + " kcal");
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, cardText, timeText, caloriesText;
        ImageView cardImage;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            cardText = itemView.findViewById(R.id.cardText);
            cardImage = itemView.findViewById(R.id.cardImage);
            timeText = itemView.findViewById(R.id.timeText);
            caloriesText = itemView.findViewById(R.id.caloriesText);
        }
    }
}
