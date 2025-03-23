package com.gfaim.activities.calendar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.gfaim.R;
import com.gfaim.activities.NavigationBar;
import com.gfaim.activities.calendar.fragments.CalendarFragment;
import com.gfaim.activities.calendar.fragments.AddIngredientFragment;
import com.gfaim.activities.calendar.fragments.AddIngredientsFragment;
import com.gfaim.activities.calendar.fragments.AddStepsFragment;
import com.gfaim.activities.calendar.fragments.SummaryFragment;

public class CalendarActivity extends AppCompatActivity {

    private NavigationBar navigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.calendar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navigationBar = new NavigationBar(this);
        int activeButtonId = getIntent().getIntExtra("activeButtonId", -1);
        if (activeButtonId != -1) {
            navigationBar.setActiveButton(activeButtonId);
        }

        // Configurer le NavController pour observer les changements de destination
        new Handler(Looper.getMainLooper()).post(() -> {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                NavController navController = navHostFragment.getNavController();

                navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    // Vérifier si nous sommes dans un des fragments où la barre doit être masquée
                    if (destination.getId() == R.id.addIngredientFragment ||
                            destination.getId() == R.id.addIngredientsFragment ||
                            destination.getId() == R.id.addStepsFragment ||
                            destination.getId() == R.id.summaryFragment ||
                            destination.getId() == R.id.chooseRecipeFragment) {
                        navigationBar.hide();
                    } else {
                        navigationBar.show();
                    }
                });
            }
        });


        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}