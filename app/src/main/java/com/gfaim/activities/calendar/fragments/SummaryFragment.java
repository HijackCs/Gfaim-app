package com.gfaim.activities.calendar.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.gfaim.R;
import com.gfaim.activities.calendar.SharedStepsViewModel;
import com.gfaim.api.ApiClient;
import com.gfaim.api.MealService;
import com.gfaim.api.RecipeService;
import com.gfaim.models.CreateMealBody;
import com.gfaim.models.CreateRecipeBody;
import com.gfaim.models.CreateRecipeStepIngrBody;
import com.gfaim.models.FoodItem;
import com.gfaim.models.RecipeStep;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.callback.OnMemberReceivedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SummaryFragment extends Fragment {

    private SharedStepsViewModel sharedStepsViewModel;
    private LinearLayout ingredientsList;
    private LinearLayout stepsList;
    private UtileProfile utileProfile;
    private MemberSessionBody member;
    private Long mealId;

    private CreateRecipeBody recipe;
    private List<RecipeStep> recipeSteps = new ArrayList<>();
    private  List<CreateRecipeStepIngrBody> ingrSaved = new ArrayList<>();

    private TextView menuNameTextView;
    private TextView participantCountTextView;
    private TextView caloriesTextView;
    private TextView timeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.summary_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        utileProfile = new UtileProfile(getContext());

        utileProfile.getSessionMember(new OnMemberReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {}
            @Override
            public void onSuccess(MemberSessionBody session) {
                member = session;
            }
            @Override
            public void onFailure(Throwable error) {}
            @Override
            public void onSuccess(CreateMember body) {}
        });

        sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);
        ingredientsList = view.findViewById(R.id.ingredients_list);
        stepsList = view.findViewById(R.id.steps_list);

        // Initialiser les TextViews pour afficher les informations
        menuNameTextView = view.findViewById(R.id.menu_name);
        participantCountTextView = view.findViewById(R.id.participant_count);
        caloriesTextView = view.findViewById(R.id.caloriesText);
        timeTextView = view.findViewById(R.id.timeText);

        // Mettre à jour les informations depuis le ViewModel
        updateSummaryInfo();

        // Bouton de retour
        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        recipe = new CreateRecipeBody();

        // Observer pour l'affichage des ingrédients
        sharedStepsViewModel.getIngredients().observe(getViewLifecycleOwner(), ingredients -> {
            Log.d("SummaryFragment", "Observer - Ingrédients reçus : " + ingredients.size());
            ingredientsList.removeAllViews();
            ingrSaved.clear(); // Nettoyer la liste avant d'ajouter les nouveaux ingrédients

            if (ingredients.isEmpty()) {
                TextView emptyText = new TextView(requireContext());
                emptyText.setText("No ingredients added");
                emptyText.setTextSize(16);
                ingredientsList.addView(emptyText);
            } else {
                for (FoodItem ingredient : ingredients) {
                    Log.d("SummaryFragment", "Ajout de l'ingrédient : " + ingredient.getName());
                    ingrSaved.add(new CreateRecipeStepIngrBody(100, ingredient.getId(), 3L));
                    TextView ingredientTextView = new TextView(requireContext());
                    ingredientTextView.setText(ingredient.getName());
                    ingredientTextView.setTextSize(16);
                    ingredientTextView.setPadding(0, 8, 0, 8);
                    ingredientsList.addView(ingredientTextView);
                }
                // Une fois les ingrédients chargés, on peut créer les étapes
                createRecipeSteps();
            }
        });

        // Observer pour l'affichage des étapes
        sharedStepsViewModel.getSteps().observe(getViewLifecycleOwner(), steps -> {
            Log.d("SummaryFragment", "Observer - Étapes reçues : " + (steps != null ? steps.size() : 0));
            stepsList.removeAllViews();
            if (steps == null || steps.isEmpty()) {
                TextView emptyText = new TextView(requireContext());
                emptyText.setText("No steps added");
                emptyText.setTextSize(16);
                stepsList.addView(emptyText);
            } else {
                for (String step : steps) {
                    TextView stepView = new TextView(requireContext());
                    stepView.setText(step);
                    stepView.setTextSize(16);
                    stepView.setPadding(0, 8, 0, 8);
                    stepsList.addView(stepView);
                }
            }

            // Mettre à jour la durée totale
            updateSummaryInfo();
        });

        // Bouton de validation
        Button validateButton = view.findViewById(R.id.finish);
        validateButton.setOnClickListener(v -> {
            recipe.setSteps(recipeSteps);
            recipe.setName(sharedStepsViewModel.getMenuName());

            // Calculer la durée totale
            Integer totalDuration = 0;
            List<Integer> durations = sharedStepsViewModel.getDurations().getValue();
            if (durations != null) {
                for (Integer duration : durations) {
                    totalDuration += duration;
                }
            }
            recipe.setReadyInMinutes(totalDuration);
            recipe.setNbServings(sharedStepsViewModel.getParticipantCount());

            RecipeService service = ApiClient.getClient(requireContext()).create(RecipeService.class);
            Log.d(TAG, "Début du chargement des ingrédients");
            Call<CreateRecipeBody> call = service.createRecipe(recipe);

            call.enqueue(new Callback<CreateRecipeBody>() {
                @Override
                public void onResponse(Call<CreateRecipeBody> call, Response<CreateRecipeBody> response) {
                    if(response.isSuccessful()){
                        mealId = response.body().getId();
                        createMeal();
                    } else {
                        Toast.makeText(requireContext(), "Erreur lors de la création de la recette", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CreateRecipeBody> call, Throwable t) {
                    Toast.makeText(requireContext(), "Erreur lors de la création de la recette", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void createRecipeSteps() {
        List<String> steps = sharedStepsViewModel.getSteps().getValue();
        recipeSteps.clear(); // Nettoyer la liste avant d'ajouter les nouvelles étapes

        if (steps != null && !steps.isEmpty()) {
            for (int i = 0; i < steps.size(); i++) {
                RecipeStep recipeStep = new RecipeStep();
                recipeStep.setStepNumber(i + 1);
                recipeStep.setDescription(steps.get(i));

                // Ajouter les ingrédients uniquement à la première étape
                if (i == 0 && !ingrSaved.isEmpty()) {
                    recipeStep.setIngredients(ingrSaved);
                } else {
                    recipeStep.setIngredients(new ArrayList<>());
                }

                recipeSteps.add(recipeStep);
            }
        }
    }

    private void createMeal(){
        MealService service = ApiClient.getClient(requireContext()).create(MealService.class);
        Bundle currentArgs = getArguments();
        CreateMealBody mealBody = new CreateMealBody();

        // Définir la date
        String dateString = currentArgs.getString("selectedDate");
        mealBody.setDate(dateString);

        // Définir le type de repas
        String mealType = currentArgs.getString("mealType");

        switch (mealType) {
            case "Breakfast":
                mealBody.setMealType("BREAKFAST");
                break;
            case "Lunch":
                mealBody.setMealType("LUNCH");
                break;
            case "Dinner":
                mealBody.setMealType("DINNER");
                break;
            case "SNACK":
                mealBody.setMealType("BREAKFAST");
                break;
            default:
                Toast.makeText(requireContext(), "Type de repas invalide", Toast.LENGTH_SHORT).show();
                return;
        }
        mealBody.setRecipeId(mealId);

        Log.d(TAG, "Création du repas avec les données : " + mealBody);
        Call<CreateMealBody> call = service.createMeal(member.getFamilyId(), mealBody);

        call.enqueue(new Callback<CreateMealBody>() {
            @Override
            public void onResponse(Call<CreateMealBody> call, Response<CreateMealBody> response) {
                if(response.isSuccessful()){
                    NavHostFragment.findNavController(SummaryFragment.this)
                            .navigate(R.id.action_summary_to_calendar);
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de la création du repas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateMealBody> call, Throwable t) {
                Toast.makeText(requireContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Met à jour toutes les informations de résumé (nom du menu, participants, calories, temps)
     */
    private void updateSummaryInfo() {
        if (sharedStepsViewModel != null) {
            // Mettre à jour le nom du menu
            String menuName = sharedStepsViewModel.getMenuName();
            if (menuName != null && !menuName.isEmpty()) {
                menuNameTextView.setText(menuName);
            } else {
                menuNameTextView.setText("Menu sans nom");
            }

            // Mettre à jour le nombre de participants
            int participantCount = sharedStepsViewModel.getParticipantCount();
            participantCountTextView.setText(String.valueOf(participantCount));

            // Mettre à jour les calories totales
            /*int totalCalories = sharedStepsViewModel.getTotalCalories();
            caloriesTextView.setText(totalCalories + " kcal");*/

            // Mettre à jour le temps total
            int totalDuration = sharedStepsViewModel.getTotalDuration();
            timeTextView.setText(totalDuration + " min");

            Log.d("SummaryFragment", "Informations mises à jour - Menu: " + menuName +
                    ", Participants: " + participantCount +
                   /* ", Calories: " + totalCalories +*/
                    ", Durée: " + totalDuration);
        }
    }
}
