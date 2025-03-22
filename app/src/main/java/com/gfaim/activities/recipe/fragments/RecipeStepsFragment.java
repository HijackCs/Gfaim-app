package com.gfaim.activities.recipe.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gfaim.R;
import com.gfaim.activities.NavigationBar;
import com.gfaim.activities.calendar.SharedStepsViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeStepsFragment extends Fragment {

    private static final String TAG = "RecipeStepsFragment";

    private SharedStepsViewModel sharedStepsViewModel;
    private TextView stepNumberTextView;
    private TextView stepInstructionsTextView;
    private Button previousStepButton;
    private Button nextStepButton;
    private List<String> steps;
    private List<Integer> durations;
    private int currentStepIndex = 0;

    // Step indicators
    private TextView step1Indicator, step2Indicator, step3Indicator, step4Indicator;

    // Step ingredients
    private TextView ingredient1NameTextView, ingredient1QuantityTextView;
    private TextView ingredient2NameTextView, ingredient2QuantityTextView;

    public static RecipeStepsFragment newInstance() {
        return new RecipeStepsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);
            debugViewModel();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'initialisation du ViewModel", e);
        }
    }

    /**
     * Méthode de débogage pour vérifier les données du ViewModel
     */
    private void debugViewModel() {
        if (sharedStepsViewModel != null) {
            String menuName = sharedStepsViewModel.getMenuName();
            List<String> steps = sharedStepsViewModel.getSteps().getValue();

            Log.d(TAG, "ViewModel - Menu name: " + menuName);
            Log.d(TAG, "ViewModel - Steps count: " + (steps != null ? steps.size() : 0));
            if (steps != null) {
                for (int i = 0; i < steps.size(); i++) {
                    Log.d(TAG, "Step " + (i+1) + ": " + steps.get(i));
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_recipe_steps, container, false);
            initViews(view);
            fetchStepsData();
            updateUIForCurrentStep();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la création de la vue", e);
        }
        return view != null ? view : new View(requireContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            setupListeners();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la configuration des écouteurs", e);
        }
    }

    private void initViews(View view) {
        try {
            if (view == null) {
                Log.e(TAG, "View est null dans initViews");
                return;
            }

            stepNumberTextView = view.findViewById(R.id.stepNumberTextView);
            stepInstructionsTextView = view.findViewById(R.id.stepInstructionsTextView);

            // Indicators
            step1Indicator = view.findViewById(R.id.step1Indicator);
            step2Indicator = view.findViewById(R.id.step2Indicator);
            step3Indicator = view.findViewById(R.id.step3Indicator);
            step4Indicator = view.findViewById(R.id.step4Indicator);

            // Ingredients in this step
            /*ingredient1NameTextView = view.findViewById(R.id.ingredient1NameTextView);
            ingredient1QuantityTextView = view.findViewById(R.id.ingredient1QuantityTextView);
            ingredient2NameTextView = view.findViewById(R.id.ingredient2NameTextView);
            ingredient2QuantityTextView = view.findViewById(R.id.ingredient2QuantityTextView);*/

            // Navigation buttons
            previousStepButton = view.findViewById(R.id.previousStepButton);
            nextStepButton = view.findViewById(R.id.nextStepButton);
        } catch (Exception e) {
            Log.e(TAG, "Exception dans initViews", e);
        }
    }

    private void fetchStepsData() {
        try {
            if (sharedStepsViewModel == null) {
                Log.e(TAG, "SharedStepsViewModel est null dans fetchStepsData");
                steps = new ArrayList<>();
                durations = new ArrayList<>();
                return;
            }

            // Récupérer les étapes et les durées du ViewModel
            steps = sharedStepsViewModel.getSteps().getValue();
            durations = sharedStepsViewModel.getDurations().getValue();

            // Si pas d'étapes, créer des étapes de test
            if (steps == null || steps.isEmpty()) {
                Log.d(TAG, "Aucune étape dans le ViewModel, création d'étapes de test");
                addTestSteps();
            } else {
                Log.d(TAG, "Étapes récupérées: " + steps.size());
            }

            // S'assurer que durations n'est pas null
            if (durations == null) {
                durations = new ArrayList<>();
                // Ajouter des durées par défaut si nécessaire
                for (int i = 0; i < steps.size(); i++) {
                    durations.add(10); // 10 minutes par défaut
                }

                // Mettre à jour le ViewModel avec ces durées
                sharedStepsViewModel.setDurations(durations);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans fetchStepsData", e);
            steps = new ArrayList<>();
            durations = new ArrayList<>();
        }
    }

    /**
     * Ajoute des étapes de test et met à jour le ViewModel
     */
    private void addTestSteps() {
        try {
            // Créer des étapes de test
            List<String> testSteps = Arrays.asList(
                    "Prepare all ingredients: chop vegetables, measure spices, and slice meat.",
                    "Heat a large pot over medium heat. Add sesame oil and sauté ginger until fragrant.",
                    "Add chicken and cook until browned on all sides, about 5-7 minutes.",
                    "We tie the bacon with twine so that the skin is on the outside and one end and the other practically meet. Heat a little oil in a pressure cooker and mark the bacon all over until golden brown. We remove and discard the oil."
            );

            // Créer des durées correspondantes
            List<Integer> testDurations = Arrays.asList(5, 3, 7, 10);

            // Mettre à jour le ViewModel
            steps = new ArrayList<>(testSteps);
            durations = new ArrayList<>(testDurations);

            sharedStepsViewModel.setSteps(steps);
            sharedStepsViewModel.setDurations(durations);

            Log.d(TAG, "Étapes de test ajoutées: " + steps.size());
        } catch (Exception e) {
            Log.e(TAG, "Exception dans addTestSteps", e);
        }
    }

    private void updateUIForCurrentStep() {
        try {
            if (steps == null || steps.isEmpty() || stepNumberTextView == null || stepInstructionsTextView == null) {
                Log.e(TAG, "Données manquantes dans updateUIForCurrentStep");
                return;
            }

            // Vérifier l'index actuel
            if (currentStepIndex >= steps.size()) {
                currentStepIndex = steps.size() - 1;
            }

            // Mettre à jour le numéro et le texte de l'étape
            stepNumberTextView.setText("Step " + (currentStepIndex + 1));
            stepInstructionsTextView.setText(steps.get(currentStepIndex));

            // Mise à jour des indicateurs d'étape
            updateStepIndicators();

            // Mise à jour des boutons
            updateNavigationButtons();

            // Mise à jour des ingrédients spécifiques à cette étape
            updateStepIngredients();
        } catch (Exception e) {
            Log.e(TAG, "Exception dans updateUIForCurrentStep", e);
        }
    }

    /**
     * Met à jour les ingrédients affichés pour l'étape actuelle
     * Dans un cas réel, chaque étape aurait ses propres ingrédients
     */
    private void updateStepIngredients() {
        try {
            // Pour cet exemple, nous utilisons des ingrédients statiques différents selon l'étape
            switch (currentStepIndex) {
                case 0:
                    setStepIngredients("All ingredients", "Various", "Cutting board", "1");
                    break;
                case 1:
                    setStepIngredients("Sesame oil", "30 ml", "Fresh ginger", "20 g");
                    break;
                case 2:
                    setStepIngredients("Chicken breasts", "350 g", "Salt & pepper", "to taste");
                    break;
                case 3:
                    setStepIngredients("Bacon", "50 gr", "Soy Sauce", "200 ml");
                    break;
                default:
                    setStepIngredients("Ingredients", "as needed", "", "");
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans updateStepIngredients", e);
        }
    }

    /**
     * Définit les ingrédients à afficher pour l'étape courante
     */
    private void setStepIngredients(String name1, String quantity1, String name2, String quantity2) {
        if (ingredient1NameTextView != null && ingredient1QuantityTextView != null) {
            ingredient1NameTextView.setText(name1);
            ingredient1QuantityTextView.setText(quantity1);
        }

        if (ingredient2NameTextView != null && ingredient2QuantityTextView != null) {
            if (name2.isEmpty()) {
                // Cacher le deuxième ingrédient si non utilisé
                ingredient2NameTextView.setVisibility(View.GONE);
                ingredient2QuantityTextView.setVisibility(View.GONE);
            } else {
                ingredient2NameTextView.setVisibility(View.VISIBLE);
                ingredient2QuantityTextView.setVisibility(View.VISIBLE);
                ingredient2NameTextView.setText(name2);
                ingredient2QuantityTextView.setText(quantity2);
            }
        }
    }

    private void updateStepIndicators() {
        try {
            if (step1Indicator == null || step2Indicator == null ||
                    step3Indicator == null || step4Indicator == null || steps == null) {
                Log.e(TAG, "Indicateurs d'étape null dans updateStepIndicators");
                return;
            }

            // Réinitialiser tous les indicateurs
            resetIndicators();

            // Mettre en surbrillance l'indicateur actuel
            /*switch (currentStepIndex) {
                case 0:
                    step1Indicator.setBackgroundResource(R.drawable.circle_bg_active);
                    break;
                case 1:
                    step2Indicator.setBackgroundResource(R.drawable.circle_bg_active);
                    break;
                case 2:
                    step3Indicator.setBackgroundResource(R.drawable.circle_bg_active);
                    break;
                case 3:
                    step4Indicator.setBackgroundResource(R.drawable.circle_bg_active);
                    break;
            }*/

            // Cacher les indicateurs qui ne sont pas nécessaires
            int stepCount = steps.size();
            if (stepCount < 4) step4Indicator.setVisibility(View.GONE);
            if (stepCount < 3) step3Indicator.setVisibility(View.GONE);
            if (stepCount < 2) step2Indicator.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, "Exception dans updateStepIndicators", e);
        }
    }

    private void resetIndicators() {
        try {
            /*if (step1Indicator != null) step1Indicator.setBackgroundResource(R.drawable.circle_bg);
            if (step2Indicator != null) step2Indicator.setBackgroundResource(R.drawable.circle_bg);
            if (step3Indicator != null) step3Indicator.setBackgroundResource(R.drawable.circle_bg);
            if (step4Indicator != null) step4Indicator.setBackgroundResource(R.drawable.circle_bg);*/
        } catch (Exception e) {
            Log.e(TAG, "Exception dans resetIndicators", e);
        }
    }

    private void updateNavigationButtons() {
        try {
            if (previousStepButton == null || nextStepButton == null || steps == null) {
                Log.e(TAG, "Boutons de navigation null dans updateNavigationButtons");
                return;
            }

            // Activer/désactiver le bouton précédent selon la position
            previousStepButton.setEnabled(currentStepIndex > 0);

            // Changer le texte du bouton suivant à la dernière étape
            if (currentStepIndex == steps.size() - 1) {
                nextStepButton.setText("Finish cook");
            } else {
                nextStepButton.setText("Next");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans updateNavigationButtons", e);
        }
    }

    private void setupListeners() {
        try {
            if (previousStepButton == null || nextStepButton == null || steps == null) {
                Log.e(TAG, "Boutons null dans setupListeners");
                return;
            }

            previousStepButton.setOnClickListener(v -> {
                if (currentStepIndex > 0) {
                    currentStepIndex--;
                    updateUIForCurrentStep();
                }
            });

            nextStepButton.setOnClickListener(v -> {
                if (steps.isEmpty() || currentStepIndex >= steps.size() - 1) {
                    // C'est la dernière étape, terminer la recette
                    finishCooking();
                } else {
                    // Passer à l'étape suivante
                    currentStepIndex++;
                    updateUIForCurrentStep();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception dans setupListeners", e);
        }
    }

    private void finishCooking() {
        try {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), NavigationBar.class);
                intent.putExtra("fragment", "home");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans finishCooking", e);
        }
    }
}