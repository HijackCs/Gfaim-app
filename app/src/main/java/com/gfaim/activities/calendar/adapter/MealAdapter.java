package com.gfaim.activities.calendar.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.recipe.fragments.RecipeActivity;

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
    private final Context context;

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
        this.context = null;
    }

    public MealAdapter(Context context, List<String> meals, OnMealClickListener listener) {
        this.context = context;
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
        final Context viewContext = holder.itemView.getContext();

        if (selectedDate != null && mealsByDate.containsKey(selectedDate)) {
            Map<String, MealInfo> dateMap = mealsByDate.get(selectedDate);
            final MealInfo mealInfo = dateMap.get(mealType);

            if (mealInfo != null) {
                // Afficher les informations du repas
                holder.menuNameText.setText(mealInfo.menuName);
                holder.caloriesText.setText(mealInfo.calories + " kcal");
                holder.timeText.setText(mealInfo.duration + " min");

                // Vérifier s'il y a un snack pour ce repas
                final MealInfo snackInfo = mealInfo.snacks.get("Snack");
                if (snackInfo != null) {
                    holder.snackText.setText(snackInfo.menuName);
                    holder.snackImage.setImageResource(R.drawable.ic_snack);

                    // Ajouter le clic sur le snack pour ouvrir RecipeActivity
                    holder.addSnackLayout.setOnClickListener(v -> {
                        if (!snackInfo.menuName.equals("Add a snack") && !snackInfo.menuName.contains("No")) {
                            openRecipeActivity(viewContext, snackInfo.menuName);
                        } else if (listener != null) {
                            listener.onMealClick("Snack", position);
                        }
                    });
                } else {
                    holder.snackText.setText("Add a snack");
                    holder.snackImage.setImageResource(R.drawable.ic_add_green);

                    // Configurer le clic pour ouvrir le popup
                    holder.addSnackLayout.setOnClickListener(v -> {
                        if (listener != null && selectedDate != null) {
                            listener.onMealClick("Snack", position);
                        }
                    });
                }

                // Ajouter le clic sur le repas principal pour ouvrir RecipeActivity
                if (!mealInfo.menuName.equals("No meal planned")) {
                    holder.itemView.setOnClickListener(v -> openRecipeActivity(viewContext, mealInfo.menuName));
                } else {
                    // Si pas de repas planifié, retour au comportement par défaut
                    holder.itemView.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onMealClick(mealType, position);
                        }
                    });
                }
            } else {
                // Afficher les valeurs par défaut pour le repas
                holder.menuNameText.setText(R.string.no_meal_planned);
                holder.caloriesText.setText("0 kcal");
                holder.timeText.setText("0 min");
                holder.snackText.setText("Add a snack");
                holder.snackImage.setImageResource(R.drawable.ic_add_green);

                // Configurer les clics par défaut
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
        } else {
            // Pas de date sélectionnée ou pas de données pour cette date
            holder.menuNameText.setText(R.string.no_meal_planned);
            holder.caloriesText.setText("0 kcal");
            holder.timeText.setText("0 min");
            holder.snackText.setText("Add a snack");
            holder.snackImage.setImageResource(R.drawable.ic_add_green);

            // Configurer les clics par défaut
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
    }

    /**
     * Ouvre l'activité RecipeActivity avec le nom du menu
     */
    private void openRecipeActivity(Context context, String menuName) {
        if (context != null && menuName != null) {
            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra("menuName", menuName);

            // Récupérer le mealType et la durée associés à ce menuName
            String mealType = null;
            int duration = 0;

            if (selectedDate != null) {
                Map<String, MealInfo> mealsForDate = mealsByDate.get(selectedDate);
                if (mealsForDate != null) {
                    // Chercher dans les repas principaux
                    for (Map.Entry<String, MealInfo> entry : mealsForDate.entrySet()) {
                        if (menuName.equals(entry.getValue().menuName)) {
                            mealType = entry.getKey();
                            duration = entry.getValue().duration;
                            break;
                        }

                        // Chercher également dans les snacks
                        Map<String, MealInfo> snacks = entry.getValue().snacks;
                        if (snacks != null) {
                            for (Map.Entry<String, MealInfo> snackEntry : snacks.entrySet()) {
                                if (menuName.equals(snackEntry.getValue().menuName)) {
                                    mealType = "Snack";
                                    duration = snackEntry.getValue().duration;
                                    break;
                                }
                            }
                        }
                        if (mealType != null) break; // Si trouvé dans les snacks
                    }
                }
            }

            // Ajouter les extras à l'intent
            if (mealType != null) {
                intent.putExtra("mealType", mealType);
            }
            intent.putExtra("duration", duration);

            context.startActivity(intent);
        }
    }

    /**
     * Trouve le type de repas associé à un menuName dans la map actuelle
     */
    private String findMealTypeForMenuName(String menuName) {
        if (selectedDate != null) {
            Map<String, MealInfo> mealsForDate = mealsByDate.get(selectedDate);
            if (mealsForDate != null) {
                for (Map.Entry<String, MealInfo> entry : mealsForDate.entrySet()) {
                    if (menuName.equals(entry.getValue().menuName)) {
                        return entry.getKey(); // Le mealType est la clé
                    }

                    // Chercher également dans les snacks
                    Map<String, MealInfo> snacks = entry.getValue().snacks;
                    if (snacks != null) {
                        for (Map.Entry<String, MealInfo> snackEntry : snacks.entrySet()) {
                            if (menuName.equals(snackEntry.getValue().menuName)) {
                                return "Snack";
                            }
                        }
                    }
                }
            }
        }
        return null;
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