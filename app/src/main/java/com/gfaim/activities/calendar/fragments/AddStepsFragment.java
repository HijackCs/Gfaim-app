package com.gfaim.activities.calendar.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.gfaim.R;
import com.gfaim.activities.calendar.SharedStepsViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddStepsFragment extends Fragment {

    private LinearLayout stepsContainer;
    private SharedStepsViewModel sharedStepsViewModel;
    private EditText firstStepEditText; // Pour sauvegarder le premier EditText

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_steps_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser le ViewModel
        sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);

        // Restaurer les étapes sauvegardées
        restoreSteps();

        // Gérer le bouton "Add a step"
        view.findViewById(R.id.add_step_layout).setOnClickListener(v -> addNewStep());

        // Gérer la navigation
        view.findViewById(R.id.back).setOnClickListener(v -> {
            saveSteps();
            NavHostFragment.findNavController(this).navigateUp();
        });

        // Bouton Next
        Button nextButton = view.findViewById(R.id.next);
        nextButton.setOnClickListener(v -> {
            // Sauvegarder les étapes avant de naviguer
            saveSteps();

            // Récupérer les arguments actuels
            Bundle currentArgs = getArguments();
            if (currentArgs != null) {
                // Créer un nouveau bundle avec les mêmes informations
                Bundle args = new Bundle();
                args.putString("selectedDate", currentArgs.getString("selectedDate"));
                args.putString("mealType", currentArgs.getString("mealType"));
                args.putString("parentMeal", currentArgs.getString("parentMeal"));
                args.putInt("cardPosition", currentArgs.getInt("cardPosition"));
                args.putString("menuName", currentArgs.getString("menuName"));

                // Log pour déboguer
                android.util.Log.d("AddStepsFragment", "Navigating to Summary: Date=" +
                        currentArgs.getString("selectedDate") + ", MealType=" +
                        currentArgs.getString("mealType") + ", Parent=" +
                        currentArgs.getString("parentMeal") + ", MenuName=" +
                        currentArgs.getString("menuName"));

                // Naviguer vers SummaryFragment
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_addSteps_to_summary, args);
            } else {
                // Si pas d'arguments, naviguer sans arguments
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_addSteps_to_summary);
            }
        });
    }

    private void saveSteps() {
        LinearLayout stepsContainer = requireView().findViewById(R.id.stepsContainer);
        List<String> steps = new ArrayList<>();
        List<Integer> durations = new ArrayList<>();

        for (int i = 0; i < stepsContainer.getChildCount(); i++) {
            LinearLayout stepLayout = (LinearLayout) stepsContainer.getChildAt(i);
            EditText stepEditText = (EditText) stepLayout.getChildAt(0);
            EditText durationEditText = (EditText) stepLayout.getChildAt(1);

            String stepText = stepEditText.getText().toString().trim();
            String durationText = durationEditText.getText().toString().trim();

            steps.add(stepText);
            try {
                durations.add(Integer.parseInt(durationText));
            } catch (NumberFormatException e) {
                durations.add(0);
            }
        }

        sharedStepsViewModel.setSteps(steps);
        sharedStepsViewModel.setDurations(durations);
    }

    private void addNewStep() {
        LinearLayout stepsContainer = requireView().findViewById(R.id.stepsContainer);
        int stepNumber = stepsContainer.getChildCount() + 1;

        if (stepNumber > 5) {
            return;
        }

        LinearLayout stepLayout = new LinearLayout(requireContext());
        stepLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        stepLayout.setOrientation(LinearLayout.HORIZONTAL);
        if (stepNumber > 1) {
            ((LinearLayout.LayoutParams) stepLayout.getLayoutParams()).topMargin =
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            20,
                            getResources().getDisplayMetrics()
                    );
        }

        EditText stepEditText = new EditText(requireContext());
        LinearLayout.LayoutParams stepParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        stepEditText.setLayoutParams(stepParams);
        stepEditText.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_border));
        stepEditText.setHint("Etape " + stepNumber);
        stepEditText.setPadding(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics())
        );
        stepEditText.setMinHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                100,
                getResources().getDisplayMetrics()
        ));
        stepEditText.setGravity(Gravity.TOP);
        stepEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey));
        stepEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        EditText durationEditText = new EditText(requireContext());
        LinearLayout.LayoutParams durationParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        durationParams.setMarginStart((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8,
                getResources().getDisplayMetrics()
        ));
        durationEditText.setLayoutParams(durationParams);
        durationEditText.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_border));
        durationEditText.setHint("min");
        durationEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        durationEditText.setPadding(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics())
        );
        durationEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey));
        durationEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        stepLayout.addView(stepEditText);
        stepLayout.addView(durationEditText);
        stepsContainer.addView(stepLayout);
    }

    private void restoreSteps() {
        LinearLayout stepsContainer = requireView().findViewById(R.id.stepsContainer);
        stepsContainer.removeAllViews();
        List<String> steps = sharedStepsViewModel.getSteps().getValue();
        List<Integer> durations = sharedStepsViewModel.getDurations().getValue();

        if (steps != null && !steps.isEmpty()) {

            for (int i = 0; i < steps.size(); i++) {
                LinearLayout stepLayout = new LinearLayout(requireContext());
                stepLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                stepLayout.setOrientation(LinearLayout.HORIZONTAL);
                if (i > 0) {
                    ((LinearLayout.LayoutParams) stepLayout.getLayoutParams()).topMargin =
                            (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    20,
                                    getResources().getDisplayMetrics()
                            );
                }

                EditText stepEditText = new EditText(requireContext());
                LinearLayout.LayoutParams stepParams = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );
                stepEditText.setLayoutParams(stepParams);
                stepEditText.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_border));
                stepEditText.setHint("Etape " + (i + 1));
                stepEditText.setText(steps.get(i));
                stepEditText.setPadding(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics())
                );
                stepEditText.setMinHeight((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100,
                        getResources().getDisplayMetrics()
                ));
                stepEditText.setGravity(Gravity.TOP);
                stepEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey));
                stepEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

                EditText durationEditText = new EditText(requireContext());
                LinearLayout.LayoutParams durationParams = new LinearLayout.LayoutParams(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                durationParams.setMarginStart((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8,
                        getResources().getDisplayMetrics()
                ));
                durationEditText.setLayoutParams(durationParams);
                durationEditText.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_border));
                durationEditText.setHint("min");
                durationEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                if (durations != null && i < durations.size()) {
                    durationEditText.setText(String.valueOf(durations.get(i)));
                }
                durationEditText.setPadding(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics())
                );
                durationEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey));
                durationEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

                stepLayout.addView(stepEditText);
                stepLayout.addView(durationEditText);
                stepsContainer.addView(stepLayout);
            }
        }
    }
}
