package com.gfaim.activities.recipe.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gfaim.R;
import com.gfaim.activities.NavigationBar;
import com.gfaim.activities.auth.LoginActivity;
import com.gfaim.activities.calendar.CalendarActivity;
import com.gfaim.activities.calendar.SharedStepsViewModel;
import com.gfaim.models.RecipeResponseBody;
import com.gfaim.models.RecipeStep;
import com.gfaim.models.RecipeStepResponse;

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
    private List<RecipeStepResponse> steps;
    private List<Integer> durations;
    private int currentStepIndex = 0;
    private LinearLayout stepIndicatorsLayout;

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
            List<RecipeStepResponse> steps = sharedStepsViewModel.getRawSteps();

            Log.d(TAG, "ViewModel - Menu name: " + menuName);
            Log.d(TAG, "ViewModel - Steps count: " + (steps != null ? steps.size() : 0));
            if (steps != null) {
                for (int i = 0; i < steps.size(); i++) {
                    Log.d(TAG, "Step " + (i+1) + ": " + steps.get(i).getDescription());
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
            createStepIndicators();
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
            stepIndicatorsLayout = view.findViewById(R.id.stepIndicatorsLayout);

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

            // Récupérer les étapes du ViewModel
            steps = sharedStepsViewModel.getRawSteps();
            durations = sharedStepsViewModel.getDurations().getValue();

            // Si pas d'étapes, créer des étapes de test
            if (steps == null || steps.isEmpty()) {
                Log.d(TAG, "Aucune étape dans le ViewModel, création d'étapes de test");
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
     * Crée dynamiquement les indicateurs d'étape en fonction du nombre d'étapes
     */
    private void createStepIndicators() {
        try {
            if (stepIndicatorsLayout == null || steps == null || steps.isEmpty()) {
                Log.e(TAG, "Impossible de créer les indicateurs d'étape");
                return;
            }

            // Supprimer tous les indicateurs existants
            stepIndicatorsLayout.removeAllViews();

            // Vérifier les étapes avant de créer les indicateurs
            for (int i = 0; i < steps.size(); i++) {
                RecipeStepResponse step = steps.get(i);
                Log.d(TAG, "Étape " + i + ": ID=" + step.getId() + ", Numéro=" + step.getStepNumber() + ", Description=" + step.getDescription());

                // S'assurer que chaque étape a un numéro et un ID valide
                if (step.getStepNumber() <= 0) {
                    step.setStepNumber(i + 1);
                    Log.d(TAG, "Correction du numéro d'étape: " + step.getStepNumber());
                }

                if (step.getId() == null) {
                    step.setId((long) (i + 1));
                    Log.d(TAG, "Correction de l'ID d'étape: " + step.getId());
                }
            }

            // Créer un nouvel indicateur pour chaque étape
            for (int i = 0; i < steps.size(); i++) {
                RecipeStepResponse step = steps.get(i);
                TextView indicator = new TextView(requireContext());

                // Définir l'ID de l'étape comme texte de l'indicateur
                indicator.setText(String.valueOf(step.getStepNumber()));
                Log.d(TAG, "Création de l'indicateur pour l'étape " + i + " avec numéro: " + step.getStepNumber());

                // Paramètres de mise en page
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.width = getResources().getDimensionPixelSize(R.dimen.step_indicator_size);
                params.height = getResources().getDimensionPixelSize(R.dimen.step_indicator_size);
                params.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.step_indicator_margin));
                indicator.setLayoutParams(params);

                // Définir l'apparence
                indicator.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                indicator.setTextSize(12);
                indicator.setGravity(android.view.Gravity.CENTER);
                indicator.setBackgroundResource(R.drawable.circle_bg);

                // Ajouter un tag pour identifier l'indicateur
                indicator.setTag(i);

                // Ajouter l'indicateur au layout
                stepIndicatorsLayout.addView(indicator);
            }

            Log.d(TAG, "Indicateurs d'étape créés: " + steps.size());
        } catch (Exception e) {
            Log.e(TAG, "Exception dans createStepIndicators", e);
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

            RecipeStepResponse currentStep = steps.get(currentStepIndex);

            // Mettre à jour le numéro et le texte de l'étape
            stepNumberTextView.setText("Step " + currentStep.getStepNumber());
            stepInstructionsTextView.setText(currentStep.getDescription());

            // Mise à jour des indicateurs d'étape
            updateStepIndicators();

            // Mise à jour des boutons
            updateNavigationButtons();
        } catch (Exception e) {
            Log.e(TAG, "Exception dans updateUIForCurrentStep", e);
        }
    }

    private void updateStepIndicators() {
        try {
            if (stepIndicatorsLayout == null || steps == null || steps.isEmpty()) {
                Log.e(TAG, "Indicateurs d'étape null dans updateStepIndicators");
                return;
            }

            // Mettre à jour les indicateurs en fonction de l'étape active
            for (int i = 0; i < stepIndicatorsLayout.getChildCount(); i++) {
                View child = stepIndicatorsLayout.getChildAt(i);
                if (child instanceof TextView) {
                    TextView indicator = (TextView) child;
                    if (i == currentStepIndex) {
                        // Indicateur actif
                        indicator.setBackgroundResource(R.drawable.circle_bg_active);
                    } else {
                        // Indicateur inactif
                        indicator.setBackgroundResource(R.drawable.circle_bg);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans updateStepIndicators", e);
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
                nextStepButton.setText("A Table !");
            } else {
                nextStepButton.setText("Suivant");
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
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans finishCooking", e);
        }
    }
}