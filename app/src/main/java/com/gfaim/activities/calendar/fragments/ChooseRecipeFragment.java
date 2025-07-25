package com.gfaim.activities.calendar.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.calendar.model.Recipe;
import com.gfaim.activities.calendar.adapter.RecipeAdapter;
import com.gfaim.api.ApiClient;
import com.gfaim.api.MealService;
import com.gfaim.api.RecipeService;
import com.gfaim.models.CreateMealBody;
import com.gfaim.models.FoodItem;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.callback.OnMemberReceivedListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseRecipeFragment extends Fragment {
    private static final String TAG = "ChooseRecipeFragment";
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<Recipe> recipes;

    private List<Recipe> filteredList = new ArrayList<Recipe>();

    private NavController navController;

    private UtileProfile utileProfile;
    private MemberSessionBody member;

    private EditText searchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_recipe_calendar, container, false);

        utileProfile = new UtileProfile(getContext());

        utileProfile.getSessionMember(new OnMemberReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {}
            @Override
            public void onSuccess(MemberSessionBody session) {
                member = session;
                loadRecipesFromApi();
            }
            @Override
            public void onFailure(Throwable error) {}
            @Override
            public void onSuccess(CreateMember body) {}
        });

        recyclerView = view.findViewById(R.id.recipesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipes = new ArrayList<>();
        adapter = new RecipeAdapter(getContext(), recipes);
        recyclerView.setAdapter(adapter);

        // Charger les recettes depuis l'API

        // Initialiser l'EditText de recherche
        searchEditText = view.findViewById(R.id.searchEditTextAdd);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void loadRecipesFromApi() {
        RecipeService recipeService = ApiClient.getClient(requireContext()).create(RecipeService.class);

        Call<List<Recipe>> call = recipeService.getRecipeSuggestions(member.getFamilyId());
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recipes.clear();
                    recipes.addAll(response.body());
                    filteredList.addAll(recipes);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Recettes chargées avec succès: " + recipes.size());
                } else {
                    Log.e(TAG, "Erreur lors du chargement des recettes: " + response.code());
                    Toast.makeText(getContext(), "Erreur lors du chargement des recettes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, "Erreur réseau lors du chargement des recettes", t);
                Toast.makeText(getContext(), "Erreur réseau lors du chargement des recettes", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void filterItems(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(recipes);
        } else {
            for (Recipe item : recipes) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.updateRecipes(filteredList);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // Configuration du listener pour le clic sur une recette
        adapter.setOnItemClickListener(position -> {
            createMeal(position);
        });

        // Bouton retour
        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> navController.navigateUp());
    }

    private void createMeal(int position) {
        Recipe selectedRecipe = filteredList.get(position);

        MealService service = ApiClient.getClient(requireContext()).create(MealService.class);
        CreateMealBody mealBody = new CreateMealBody();

        // Définir la date
        String dateString = getArguments().getString("selectedDate");
        mealBody.setDate(dateString);

        String mealType = getArguments().getString("mealType");

        switch (mealType) {
            case "BREAKFAST":
                mealBody.setMealType("BREAKFAST");
                break;
            case "Petit déjeuner":
                mealBody.setMealType("BREAKFAST");
                break;
            case "LUNCH":
                mealBody.setMealType("LUNCH");
                break;
            case "Déjeuner":
                mealBody.setMealType("LUNCH");
                break;
            case "DINNER":
                mealBody.setMealType("DINNER");
                break;
            case "Diner":
                mealBody.setMealType("DINNER");
                break;
            case "Snack":
                mealBody.setMealType("SNACK");
                break;
            default:
                Toast.makeText(requireContext(), "repas invalide" + mealType, Toast.LENGTH_SHORT).show();
                return;
        }
        mealBody.setRecipeId(selectedRecipe.getId());

        Log.d(TAG, "Création du repas avec les données : " + mealBody);
        Call<CreateMealBody> call = service.createMeal(member.getFamilyId(), mealBody);

        call.enqueue(new Callback<CreateMealBody>() {
            @Override
            public void onResponse(Call<CreateMealBody> call, Response<CreateMealBody> response) {
                if(response.isSuccessful()) {
                    navController.navigate(R.id.action_chooseRecipe_to_calendar);
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de la création du repas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateMealBody> call, Throwable t) {
                Toast.makeText(requireContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}