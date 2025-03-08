package com.gfaim.activities.home;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.gfaim.R;
import com.gfaim.activities.CarrouselAdapter;
import com.gfaim.activities.NavigationBar;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class HomeActivity extends AppCompatActivity {

    private final Logger log = Logger.getLogger(HomeActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.home);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            NavigationBar navigationBar = new NavigationBar(this);

            int activeButtonId = getIntent().getIntExtra("activeButtonId", -1);
            if (activeButtonId != -1) {
                navigationBar.setActiveButton(activeButtonId);
            }
            setupCarrousel();
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        } catch (Exception e) {
            log.warning("[acceuil][onCreate] Problème au lancement de HomeActivity");
        }
    }

    private void setupCarrousel() {
        ViewPager2 viewPager = findViewById(R.id.carrousel);

        List<Integer> images = Arrays.asList(
                R.drawable.repas1,
                R.drawable.repas2,
                R.drawable.repas1
        );

        List<String> texts = Arrays.asList(
                "Petit Déjeuner",
                "Déjeuner",
                "Dîner"
        );

        CarrouselAdapter adapter = new CarrouselAdapter(images, texts);
        viewPager.setAdapter(adapter);
    }
}
