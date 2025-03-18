package com.gfaim.activities.calendar.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

        /*LinearLayout addStepLayout = view.findViewById(R.id.add_snack);

        addStepLayout.setOnClickListener(v -> {
            // Créer le popup avec un champ de texte et les boutons
            showPopup();
        });*/


        view.post(() -> {
            try {
                navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            } catch (Exception e) {
                Log.e("CalendarFragment", "NavController non trouvé", e);
            }
        });


        return view;
    }

    private void showPopup() {
        // Créer un EditText pour saisir du texte
        EditText editText = new EditText(getContext());
        editText.setHint("Enter snack name"); // Indication dans le champ de texte

        // Créer un AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add a Snack");
        builder.setView(editText); // Ajouter le champ de texte au dialog

        // Ajouter le bouton "Add"
        builder.setPositiveButton("Add", (dialog, which) -> {
            String snack = editText.getText().toString(); // Récupérer le texte saisi
            // Faites quelque chose avec le snack (par exemple, l'ajouter à une liste)
            Log.d("Popup", "Snack added: " + snack);
        });

        // Ajouter le bouton "Decline"
        builder.setNegativeButton("Decline", (dialog, which) -> {
            dialog.dismiss(); // Fermer le popup sans faire d'action
        });

        // Créer et afficher le dialog
        builder.create().show();
    }

}