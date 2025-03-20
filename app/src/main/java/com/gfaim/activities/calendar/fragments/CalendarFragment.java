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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private NavController navController;
    private MealAdapter mealAdapter;
    private String selectedDate;
    private RecyclerView recyclerView;
    private CalendarView calendarView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Initialiser le NavController
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        // Initialiser le RecyclerView
        recyclerView = view.findViewById(R.id.mealRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Masquer le RecyclerView au démarrage
        recyclerView.setVisibility(View.GONE);

        // Liste des repas
        List<String> mealList = Arrays.asList("Breakfast", "Lunch", "Dinner");

        // Initialiser l'adapteur avec un OnMealClickListener
        mealAdapter = new MealAdapter(mealList, (mealType, position) -> {
            // Créer un bundle avec les informations nécessaires
            Bundle args = new Bundle();
            args.putString("selectedDate", selectedDate);
            args.putString("mealType", mealType);
            args.putInt("cardPosition", position);

            // Naviguer vers AddIngredientsFragment avec les arguments
            navController.navigate(R.id.action_calendar_to_addIngredients, args);
        });
        recyclerView.setAdapter(mealAdapter);

        // Gestion du CalendarView
        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, month + 1, year);
            Log.d("CalendarFragment", "Date sélectionnée: " + selectedDate);
            mealAdapter.setSelectedDate(selectedDate);
            // Afficher le RecyclerView lorsqu'une date est sélectionnée
            recyclerView.setVisibility(View.VISIBLE);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Vérifier si nous avons des arguments de navigation
        Bundle args = getArguments();
        if (args != null) {
            String menuName = args.getString("menuName");
            int calories = args.getInt("calories");
            int duration = args.getInt("duration");
            String date = args.getString("selectedDate");
            String mealType = args.getString("mealType");

            Log.d("CalendarFragment", "Arguments reçus - Date: " + date +
                    ", Type: " + mealType + ", Menu: " + menuName +
                    ", Calories: " + calories + ", Durée: " + duration);

            if (date != null && !date.isEmpty() && mealType != null && !mealType.isEmpty() && menuName != null) {
                // Mettre à jour la date sélectionnée
                selectedDate = date;
                mealAdapter.setSelectedDate(date);

                // Mettre à jour les informations du repas
                mealAdapter.updateMealInfo(date, mealType, menuName, calories, duration);

                // Mettre à jour la date du CalendarView
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
                    Date selectedDateObj = sdf.parse(date);
                    if (selectedDateObj != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(selectedDateObj);
                        calendarView.setDate(calendar.getTimeInMillis());
                    }
                } catch (ParseException e) {
                    Log.e("CalendarFragment", "Erreur lors du parsing de la date", e);
                }
            }
        }
    }

    private void showPopup() {
        // Créer un EditText pour saisir du texte
        EditText editText = new EditText(getContext());
        editText.setHint("Enter snack name");

        // Créer un AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add a Snack");
        builder.setView(editText);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String snack = editText.getText().toString();
            Log.d("Popup", "Snack added: " + snack);
        });

        builder.setNegativeButton("Decline", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

}