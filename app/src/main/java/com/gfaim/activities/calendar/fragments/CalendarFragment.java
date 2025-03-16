package com.gfaim.activities.calendar.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.calendar.adapter.MealAdapter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Initialiser le NavController en utilisant la vue du fragment
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        // Gestion du CalendarView
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            Log.d("CalendarFragment", "Date sélectionnée: " + date);

            // Initialiser le RecyclerView
            RecyclerView recyclerView = view.findViewById(R.id.mealRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Liste des repas (exemple)
            List<String> mealList = Arrays.asList("Breakfast", "Lunch", "Dinner");
            Log.d("CalendarFragment", "Nombre de repas: " + mealList.size());

            // Initialiser l'adapteur avec le NavController
            MealAdapter adapter = new MealAdapter(mealList, navController);
            recyclerView.setAdapter(adapter);
        });

        view.post(() -> {
            try {
                navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            } catch (Exception e) {
                Log.e("CalendarFragment", "NavController non trouvé", e);
            }
        });


        return view;
    }
}