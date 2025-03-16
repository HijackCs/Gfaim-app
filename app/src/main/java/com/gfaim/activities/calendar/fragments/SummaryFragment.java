package com.gfaim.activities.calendar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.gfaim.R;

public class SummaryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.summary_calendar, container, false);

        // Initialiser le NavController
        NavController navController = NavHostFragment.findNavController(this);

        // Configurer le bouton "finish" pour revenir à CalendarFragment
        Button finishButton = view.findViewById(R.id.finish);
        finishButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_summary_to_calendar);
        });

        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            navController.navigateUp(); // Revient au fragment précédent
        });

        return view;
    }
}