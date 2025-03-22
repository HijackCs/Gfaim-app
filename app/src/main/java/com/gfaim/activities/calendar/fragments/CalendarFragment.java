package com.gfaim.activities.calendar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.view.Gravity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.CalendarView;
import com.gfaim.R;
import com.gfaim.activities.calendar.adapter.MealAdapter;
import com.gfaim.activities.calendar.adapter.MealAdapter.OnMealClickListener;
import com.gfaim.activities.calendar.SharedStepsViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment implements OnMealClickListener {
    private MealAdapter mealAdapter;
    private String selectedDate;
    private final List<String> meals = Arrays.asList("Breakfast", "Lunch", "Dinner");
    private SharedStepsViewModel sharedStepsViewModel;
    private String lastMealType;
    private String lastParentMeal;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Initialiser le ViewModel
        sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);

        // Initialiser le RecyclerView avec l'adaptateur
        RecyclerView recyclerView = view.findViewById(R.id.mealRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mealAdapter = new MealAdapter(meals, this);
        recyclerView.setAdapter(mealAdapter);

        // Initialiser le calendrier
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            mealAdapter.setSelectedDate(selectedDate);
        });

        // Restaurer les données si on revient d'un autre fragment
        if (getArguments() != null) {
            String date = getArguments().getString("selectedDate");
            String mealType = getArguments().getString("mealType");
            String menuName = getArguments().getString("menuName");
            int calories = getArguments().getInt("calories", 0);
            int duration = getArguments().getInt("duration", 0);
            String parentMeal = getArguments().getString("parentMeal");

            if (date != null && mealType != null) {
                // Si on a un menuName dans les arguments, on l'utilise
                if (menuName != null) {
                    mealAdapter.updateMealInfo(date, mealType, menuName, calories, duration, parentMeal);
                }
                // Sinon, on vérifie si on a un menuName dans le ViewModel
                else if ("Snack".equals(mealType) && sharedStepsViewModel.getMenuName() != null) {
                    String viewModelMenuName = sharedStepsViewModel.getMenuName();
                    if (viewModelMenuName != null && !viewModelMenuName.trim().isEmpty()) {
                        mealAdapter.updateMealInfo(
                                date,
                                mealType,
                                viewModelMenuName,
                                0,
                                sharedStepsViewModel.getTotalDuration(),
                                parentMeal
                        );
                    }
                }

                // Mettre à jour la date sélectionnée dans le calendrier
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date parsedDate = sdf.parse(date);
                    if (parsedDate != null) {
                        calendarView.setDate(parsedDate.getTime());
                        selectedDate = date;
                        mealAdapter.setSelectedDate(date);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Les arguments ont priorité sur le ViewModel
        Bundle args = getArguments();
        if (args != null) {
            String date = args.getString("selectedDate");
            String mealType = args.getString("mealType");
            String menuName = args.getString("menuName");
            int calories = args.getInt("calories", 0);
            int duration = args.getInt("duration", 0);
            String parentMeal = args.getString("parentMeal");

            // Logs pour déboguer
            android.util.Log.d("CalendarFragment", "onResume avec args: Date=" + date +
                    ", MealType=" + mealType + ", MenuName=" + menuName +
                    ", Parent=" + parentMeal);

            if (date != null && mealType != null && menuName != null) {
                selectedDate = date;
                mealAdapter.setSelectedDate(date);
                mealAdapter.updateMealInfo(date, mealType, menuName, calories, duration, parentMeal);

                // Réinitialiser les arguments après utilisation
                setArguments(null);
            }
        }
        // Si pas d'arguments mais que nous avons un lastMealType et un lastParentMeal
        else if (selectedDate != null && "Snack".equals(lastMealType) && lastParentMeal != null) {
            String menuName = sharedStepsViewModel.getMenuName();
            if (menuName != null && !menuName.trim().isEmpty()) {
                android.util.Log.d("CalendarFragment", "onResume avec ViewModel: MenuName=" + menuName +
                        ", LastParent=" + lastParentMeal);

                // Mettre à jour le snack avec le nom de la recette créée
                mealAdapter.updateMealInfo(
                        selectedDate,
                        "Snack",
                        menuName,
                        0,
                        sharedStepsViewModel.getTotalDuration(),
                        lastParentMeal
                );
            }
        }
    }

    @Override
    public void onMealClick(String mealType, int position) {
        if (selectedDate == null) {
            Toast.makeText(getContext(), "Please select a date first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sauvegarder le type de repas et le parent pour une utilisation ultérieure
        lastMealType = mealType;
        if ("Snack".equals(mealType)) {
            lastParentMeal = meals.get(position);
        }

        Bundle args = new Bundle();
        args.putString("selectedDate", selectedDate);
        args.putString("mealType", mealType);
        args.putInt("cardPosition", position);

        if ("Snack".equals(mealType)) {
            args.putString("parentMeal", meals.get(position));
        }

        showPopup(args);
    }

    private void showPopup(Bundle args) {
        View popupView = getLayoutInflater().inflate(R.layout.popup_menu, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        Button createRecipeButton = popupView.findViewById(R.id.createRecipeButton);
        Button chooseRecipeButton = popupView.findViewById(R.id.chooseRecipeButton);

        createRecipeButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_calendar_to_addIngredients, args);
        });

        chooseRecipeButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_calendar_to_chooseRecipe, args);
        });

        View rootView = requireActivity().getWindow().getDecorView().getRootView();
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }
}