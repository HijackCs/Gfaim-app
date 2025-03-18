package com.gfaim.activities.calendar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gfaim.R;
import com.gfaim.activities.calendar.SharedStepsViewModel;

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

        // Récupérer le conteneur des étapes
        stepsContainer = view.findViewById(R.id.stepsContainer);
        firstStepEditText = view.findViewById(R.id.step);

        // Charger les étapes sauvegardées
        List<String> savedSteps = sharedStepsViewModel.getSteps();
        if (!savedSteps.isEmpty()) {
            // Restaurer le premier EditText
            firstStepEditText.setText(savedSteps.get(0));
            // Restaurer les autres étapes
            for (int i = 1; i < savedSteps.size(); i++) {
                addStepEditText(savedSteps.get(i));
            }
        }

        // Sauvegarde du premier EditText
        firstStepEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                sharedStepsViewModel.updateStep(0, firstStepEditText.getText().toString());
            }
        });

        // Gérer le bouton "Add a step"
        LinearLayout addStepLayout = view.findViewById(R.id.add_step_layout);
        addStepLayout.setOnClickListener(v -> addStepEditText(""));

        // Gérer la navigation
        NavController navController = Navigation.findNavController(view);

        view.findViewById(R.id.next).setOnClickListener(v -> navController.navigate(R.id.action_addSteps_to_summary));
        view.findViewById(R.id.back).setOnClickListener(v -> navController.navigateUp());
    }

    private void addStepEditText(String text) {
        // Créer un nouvel EditText basé sur le premier existant
        EditText editText = new EditText(getContext());
        editText.setLayoutParams(firstStepEditText.getLayoutParams());
        editText.setBackground(firstStepEditText.getBackground());
        editText.setPadding(firstStepEditText.getPaddingLeft(),
                firstStepEditText.getPaddingTop(),
                firstStepEditText.getPaddingRight(),
                firstStepEditText.getPaddingBottom());
        editText.setTextSize(16);
        editText.setHint("Step " + (stepsContainer.getChildCount() + 1));
        editText.setText(text);

        // Ajouter l'EditText au conteneur
        stepsContainer.addView(editText);

        // Ajouter la valeur au ViewModel si elle change
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                sharedStepsViewModel.updateStep(stepsContainer.indexOfChild(editText), editText.getText().toString());
            }
        });
    }
}
