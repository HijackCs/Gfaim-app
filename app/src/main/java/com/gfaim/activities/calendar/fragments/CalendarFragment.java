package com.gfaim.activities.calendar.fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.gfaim.api.ApiClient;
import com.gfaim.api.MealService;
import com.gfaim.models.MealResponseBody;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.callback.OnMemberReceivedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarFragment extends Fragment implements OnMealClickListener {
    private RecyclerView mealRecyclerView;
    private MealAdapter mealAdapter;
    private String selectedDate;
    private final List<String> meals = Arrays.asList("Breakfast", "Lunch", "Dinner", "Snack");
    private SharedStepsViewModel sharedStepsViewModel;
    private String lastMealType;
    private String lastParentMeal;

    private UtileProfile utileProfile;
    private MemberSessionBody member;

    private Context context;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        context = requireContext();



        // Initialiser le ViewModel
        sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);

        // Initialiser le RecyclerView
        mealRecyclerView = view.findViewById(R.id.mealRecyclerView);
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mealAdapter = new MealAdapter(meals, this);
        mealRecyclerView.setAdapter(mealAdapter);

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
            int duration = getArguments().getInt("duration", 0);
            String parentMeal = getArguments().getString("parentMeal");

            if (date != null && mealType != null) {
                if (menuName != null) {
                    mealAdapter.updateMealInfo(date, mealType, menuName, duration, parentMeal);
                }
                else if ("Snack".equals(mealType) && sharedStepsViewModel.getMenuName() != null) {
                    String viewModelMenuName = sharedStepsViewModel.getMenuName();
                    if (viewModelMenuName != null && !viewModelMenuName.trim().isEmpty()) {
                        mealAdapter.updateMealInfo(
                                date,
                                mealType,
                                viewModelMenuName,
                                sharedStepsViewModel.getTotalDuration(),
                                parentMeal
                        );
                    }
                }

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
            int duration = args.getInt("duration", 0);
            String parentMeal = args.getString("parentMeal");

            // Logs pour déboguer
            android.util.Log.d("CalendarFragment", "onResume avec args: Date=" + date +
                    ", MealType=" + mealType + ", MenuName=" + menuName +
                    ", Parent=" + parentMeal);

            if (date != null && mealType != null && menuName != null) {
                selectedDate = date;
                mealAdapter.setSelectedDate(date);
                mealAdapter.updateMealInfo(date, mealType, menuName, duration, parentMeal);

                // Réinitialiser les arguments après utilisation
                setArguments(null);
                // Réinitialiser le ViewModel après avoir créé un repas
                sharedStepsViewModel.reset();
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

    public void getMeal(String mealType) {
        MealService service = ApiClient.getClient(context).create(MealService.class);

        Log.d(TAG, "Début du chargement des repas pour le type: " + mealType);
        Call<List<MealResponseBody>> call = service.getMeal(member.getFamilyId(), selectedDate, mealType);

        call.enqueue(new Callback<List<MealResponseBody>>() {
            @Override
            public void onResponse(Call<List<MealResponseBody>> call, Response<List<MealResponseBody>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    MealResponseBody meal = response.body().get(0);
                    // Mettre à jour l'adaptateur avec les informations du repas
                        mealAdapter.updateMealInfo(
                        selectedDate,
                        mealType,
                        meal.getRecipe().getName(),
                        meal.getRecipe().getReadyInMinutes(),
                        null // Pas de parent meal car c'est un repas principal
                    );
                    Log.d(TAG, "Repas chargé avec succès: ");
                } else {
                    String errorCode = String.valueOf(response.code());
                    Log.e(TAG, "Erreur lors du chargement des repas: " + errorCode);
                }
            }

            @Override
            public void onFailure(Call<List<MealResponseBody>> call, Throwable t) {
                Log.e(TAG, "Erreur de connexion: " + t.getMessage());
            }
        });
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
            // Réinitialiser le ViewModel avant de commencer un nouveau repas
            sharedStepsViewModel.reset();
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