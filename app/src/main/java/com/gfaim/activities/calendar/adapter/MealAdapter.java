package com.gfaim.activities.calendar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    public interface OnMealClickListener {
        void onMealClick(String mealType, int position);
    }

    private final List<String> meals;
    private final OnMealClickListener listener;
    private String selectedDate;
    private final Map<String, Map<String, MealInfo>> mealsByDate = new HashMap<>();

    public static class MealInfo {
        public String menuName;
        public int calories;
        public int duration;

        public MealInfo(String menuName, int calories, int duration) {
            this.menuName = menuName;
            this.calories = calories;
            this.duration = duration;
        }
    }

    public MealAdapter(List<String> meals, OnMealClickListener listener) {
        this.meals = meals;
        this.listener = listener;
    }

    public void setSelectedDate(String date) {
        this.selectedDate = date;
        notifyDataSetChanged();
    }

    public void updateMealInfo(String date, String mealType, String menuName, int calories, int duration) {
        mealsByDate.computeIfAbsent(date, k -> new HashMap<>())
                .put(mealType, new MealInfo(menuName, calories, duration));
        if (date.equals(selectedDate)) {
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meal_card, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        String mealType = meals.get(position);
        holder.mealTypeText.setText(mealType);

        // Récupérer les informations du repas pour la date sélectionnée
        MealInfo mealInfo = null;
        if (selectedDate != null && mealsByDate.containsKey(selectedDate)) {
            mealInfo = mealsByDate.get(selectedDate).get(mealType);
        }

        // Toujours afficher les TextView
        holder.menuNameText.setVisibility(View.VISIBLE);
        holder.caloriesText.setVisibility(View.VISIBLE);
        holder.timeText.setVisibility(View.VISIBLE);

        if (mealInfo != null) {
            // Afficher les informations du repas
            holder.menuNameText.setText(mealInfo.menuName);
            holder.caloriesText.setText(mealInfo.calories + " kcal");
            holder.timeText.setText(mealInfo.duration + " min");
        } else {
            // Afficher les valeurs par défaut
            holder.menuNameText.setText(R.string.no_meal_planned);
            holder.caloriesText.setText("0 kcal");
            holder.timeText.setText("0 min");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMealClick(mealType, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView mealTypeText;
        TextView menuNameText;
        TextView caloriesText;
        TextView timeText;

        MealViewHolder(View itemView) {
            super(itemView);
            mealTypeText = itemView.findViewById(R.id.textTitle);
            menuNameText = itemView.findViewById(R.id.cardText);
            caloriesText = itemView.findViewById(R.id.caloriesText);
            timeText = itemView.findViewById(R.id.timeText);
        }
    }
}