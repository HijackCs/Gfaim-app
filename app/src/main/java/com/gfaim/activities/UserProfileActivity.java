package com.gfaim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;
import com.gfaim.activities.auth.LoginActivity;
import com.gfaim.api.ApiClient;
import com.gfaim.api.AuthService;
import com.gfaim.api.MealService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.Meal;
import com.gfaim.models.RefreshRequest;
import com.gfaim.models.User;
import com.gfaim.utility.auth.JwtDecoder;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private TextView tvUserName;

    private AuthService authService;
    private MealService mealService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tokenManager = new TokenManager(this);
        tvUserName = findViewById(R.id.tvUserName);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnGetMeals = findViewById(R.id.btnGetMeals);

        // Services API
        authService = ApiClient.getClient(this).create(AuthService.class);
        mealService = ApiClient.getClient(this).create(MealService.class);

        // Affichage du nom de l'utilisateur
        displayUserName();

        // Gestion du logout
        btnLogout.setOnClickListener(v -> logout());

        // Appel de l'API /meals
        btnGetMeals.setOnClickListener(v -> fetchMeals());
    }

    private void displayUserName() {
        String accessToken = tokenManager.getAccessToken();
        if (accessToken != null) {
            String decodedToken = JwtDecoder.decodeJWT(accessToken);
            User user = new Gson().fromJson(decodedToken, User.class);
            tvUserName.setText("Bienvenue, " + user.getName());
        }
    }

    private void logout() {
        Call<Void> call = authService.logout(new RefreshRequest(tokenManager.getRefreshToken()));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                tokenManager.clearTokens();
                Toast.makeText(UserProfileActivity.this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMeals() {
        Call<List<Meal>> call = mealService.getMeals("Bearer " + tokenManager.getAccessToken());
        call.enqueue(new Callback<List<Meal>>() {
            @Override
            public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Meal> meals = response.body();
                    StringBuilder mealList = new StringBuilder("Repas:\n");
                    for (Meal meal : meals) {
                        mealList.append("- ").append(meal.getName()).append("\n");
                    }
                    Toast.makeText(UserProfileActivity.this, mealList.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UserProfileActivity.this, "Erreur lors du chargement des repas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Meal>> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
