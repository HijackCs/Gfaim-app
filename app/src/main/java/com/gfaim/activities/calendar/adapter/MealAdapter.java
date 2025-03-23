package com.gfaim.activities.calendar.adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.gfaim.api.ApiClient;
import com.gfaim.api.IngredientCatalogService;
import com.gfaim.api.MealService;
import com.gfaim.models.FoodItem;
import com.gfaim.models.IngredientCatalogItem;
import com.gfaim.models.MealResponseBody;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.callback.OnMemberReceivedListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    public interface OnMealClickListener {
        void onMealClick(String mealType, int position);
    }

    private final List<String> meals;
    private final OnMealClickListener listener;
    private String selectedDate;

    private UtileProfile utileProfile;
    private MemberSessionBody member;
    private Context context;
    private final Map<String, Map<String, MealInfo>> mealsByDate = new HashMap<>();

    private MealViewHolder holderM;

    public static class MealInfo {
        public String menuName;
        public int duration;
        public Map<String, MealInfo> snacks;  // Map pour stocker les snacks associés à ce repas

        public MealInfo(String menuName, int duration) {
            this.menuName = menuName;
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

    public void updateMealInfo(String date, String mealType, String menuName, int duration, String parentMeal) {
        Map<String, MealInfo> dateMap = mealsByDate.computeIfAbsent(date, k -> new HashMap<>());

        if ("Snack".equals(mealType) && parentMeal != null) {
            // Mettre à jour le snack pour le repas parent
            MealInfo parentMealInfo = dateMap.get(parentMeal);
            if (parentMealInfo == null) {
                parentMealInfo = new MealInfo("No meal planned", 0);
                dateMap.put(parentMeal, parentMealInfo);
            }
            parentMealInfo.snacks.put("Snack", new MealInfo(menuName, duration));
        } else {
            // Mettre à jour le repas normal
            MealInfo mealInfo = new MealInfo(menuName, duration);
            dateMap.put(mealType, mealInfo);
        }

        if (date.equals(selectedDate)) {
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.meal_card, parent, false);
        return new MealViewHolder(view);
    }

    private void openRecipeActivity(Long id) {
            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra("id", id);
            context.startActivity(intent);
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


    public void getMeal(String mealType, MealViewHolder holder, int position) {
        MealService service = ApiClient.getClient(context).create(MealService.class);

        Log.d(TAG, "Début du chargement des repas pour le type: " + mealType);
        Call<List<MealResponseBody>> call = service.getMeal(member.getFamilyId(), selectedDate, mealType);

        call.enqueue(new Callback<List<MealResponseBody>>() {
            @Override
            public void onResponse(Call<List<MealResponseBody>> call, Response<List<MealResponseBody>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    MealResponseBody meal = response.body().get(0);

                    System.out.println("meal "+ meal.getRecipe().getName());
                    // Afficher les informations du repas
                    holder.menuNameText.setText(meal.getRecipe().getName());
                    holder.timeText.setText(meal.getRecipe().getReadyInMinutes() + " min");

                    holder.itemView.setOnClickListener(v -> openRecipeActivity(meal.getRecipe().getId()));

                    holder.addSnackLayout.setOnClickListener(v -> {
                        if (listener != null && selectedDate != null) {
                            listener.onMealClick("Snack", position);
                        }
                    });

                    Log.d(TAG, "Repas chargé avec succès: ");
                } else {
                    String errorCode = String.valueOf(response.code());
                    Log.e(TAG, "Erreur lors du chargement des repas: " + errorCode);
                    holder.menuNameText.setText(R.string.no_meal_planned);
                    holder.caloriesText.setText("0 kcal");
                    holder.timeText.setText("0 min");
                    holder.snackText.setText("Add a snack");
                    holder.snackImage.setImageResource(R.drawable.ic_add_green);
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

            @Override
            public void onFailure(Call<List<MealResponseBody>> call, Throwable t) {
                Log.e(TAG, "Erreur de connexion: " + t.getMessage());
                holder.menuNameText.setText(R.string.no_meal_planned);
                holder.timeText.setText("0 min");
                holder.snackText.setText("Add a snack");
                holder.snackImage.setImageResource(R.drawable.ic_add_green);
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
        });
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, @SuppressLint("RecyclerView") int p) {
        String mealType = meals.get(p);
        holder.mealTypeText.setText(mealType);

        utileProfile = new UtileProfile(context);

        utileProfile.getSessionMember(new OnMemberReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {}
            @Override
            public void onSuccess(MemberSessionBody session) {
                member = session;
                if(Objects.equals(mealType, "Breakfast")){
                    getMeal("BREAKFAST", holder, p);
                }
                if(Objects.equals(mealType, "Lunch")){
                    getMeal("LUNCH", holder, p);
                }
                if(Objects.equals(mealType, "Dinner")){
                    getMeal("DINNER", holder, p);
                }
            }
            @Override
            public void onFailure(Throwable error) {}
            @Override
            public void onSuccess(CreateMember body) {}
        });

        System.out.println("passe la");

       /* if (selectedDate != null && mealsByDate.containsKey(selectedDate)) {
            Map<String, MealInfo> dateMap = mealsByDate.get(selectedDate);
            MealInfo mealInfo = dateMap.get(mealType);

            if (mealInfo != null) {
                // Afficher les informations du repas
                holder.menuNameText.setText(mealInfo.menuName);
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

       */

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