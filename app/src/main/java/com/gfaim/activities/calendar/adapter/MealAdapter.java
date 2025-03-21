package com.gfaim.activities.calendar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
        public Map<String, MealInfo> snacks;  // Map pour stocker les snacks associés à ce repas

        public MealInfo(String menuName, int calories, int duration) {
            this.menuName = menuName;
            this.calories = calories;
            this.duration = duration;
            this.snacks = new HashMap<>();
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

    public void updateMealInfo(String date, String mealType, String menuName, int calories, int duration, String parentMeal) {
        Map<String, MealInfo> dateMap = mealsByDate.computeIfAbsent(date, k -> new HashMap<>());

        if ("Snack".equals(mealType) && parentMeal != null) {
            // Mettre à jour le snack pour le repas parent
            MealInfo parentMealInfo = dateMap.get(parentMeal);
            if (parentMealInfo == null) {
                parentMealInfo = new MealInfo("No meal planned", 0, 0);
                dateMap.put(parentMeal, parentMealInfo);
            }
            parentMealInfo.snacks.put("Snack", new MealInfo(menuName, calories, duration));
        } else {
            // Mettre à jour le repas normal
            MealInfo mealInfo = new MealInfo(menuName, calories, duration);
            dateMap.put(mealType, mealInfo);
        }

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

        if (selectedDate != null && mealsByDate.containsKey(selectedDate)) {
            Map<String, MealInfo> dateMap = mealsByDate.get(selectedDate);
            MealInfo mealInfo = dateMap.get(mealType);

            if (mealInfo != null) {
                // Afficher les informations du repas
                holder.menuNameText.setText(mealInfo.menuName);
                holder.caloriesText.setText(mealInfo.calories + " kcal");
                holder.timeText.setText(mealInfo.duration + " min");

                // Vérifier s'il y a un snack pour ce repas
                MealInfo snackInfo = mealInfo.snacks.get("Snack");
                if (snackInfo != null) {
                    holder.snackText.setText(snackInfo.menuName);
                    holder.snackImage.setImageResource(R.drawable.ic_snack);
                } else {
                    holder.snackText.setText("Add a snack");
                    holder.snackImage.setImageResource(R.drawable.ic_add_green);
                }
            } else {
                // Afficher les valeurs par défaut pour le repas
                holder.menuNameText.setText(R.string.no_meal_planned);
                holder.caloriesText.setText("0 kcal");
                holder.timeText.setText("0 min");
                holder.snackText.setText("Add a snack");
                holder.snackImage.setImageResource(R.drawable.ic_add_green);
            }
        } else {
            // Pas de date sélectionnée ou pas de données pour cette date
            holder.menuNameText.setText(R.string.no_meal_planned);
            holder.caloriesText.setText("0 kcal");
            holder.timeText.setText("0 min");
            holder.snackText.setText("Add a snack");
            holder.snackImage.setImageResource(R.drawable.ic_add_green);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMealClick(mealType, position);
            }
        });

        holder.addSnackLayout.setOnClickListener(v -> {
            if (listener != null && selectedDate != null) {
                listener.onMealClick("Snack", position);
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
        LinearLayout addSnackLayout;
        ImageView snackImage;
        TextView snackText;

        MealViewHolder(View itemView) {
            super(itemView);
            mealTypeText = itemView.findViewById(R.id.textTitle);
            menuNameText = itemView.findViewById(R.id.cardText);
            caloriesText = itemView.findViewById(R.id.caloriesText);
            timeText = itemView.findViewById(R.id.timeText);
            addSnackLayout = itemView.findViewById(R.id.add_snack);
            snackImage = itemView.findViewById(R.id.snack_image);
            snackText = itemView.findViewById(R.id.snack_text);
        }
    }
}