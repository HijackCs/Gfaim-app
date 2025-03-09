package com.gfaim.activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.calendar.AddIngredientsCalendar;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private final List<String> mealList;
    private final Context context;

    public MealAdapter(List<String> mealList, Context context) {
        this.mealList = mealList;
        this.context = context;
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
        holder.textTitle.setText(meal);

        // Click listener pour chaque card
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddIngredientsCalendar.class);
            intent.putExtra("mealType", meal); // passer le type de repas
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
        }
    }
}