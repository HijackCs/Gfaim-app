package com.gfaim.activities;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.gfaim.R;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class HomeActivity extends AppCompatActivity {

    private final Logger log = Logger.getLogger(HomeActivity.class.getName());
    private List<ImageButton> navButtons;
    private List<TextView> navTexts;

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
            initNavigationBar();

            ViewPager2 viewPager = findViewById(R.id.viewPager);

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
        } catch (Exception e) {
            log.warning("[acceuil][onCreate] Problème au lancement de HomeActivity");
        }
    }

    private void initNavigationBar() {
        navButtons = Arrays.asList(
                findViewById(R.id.btn_home),
                findViewById(R.id.btn_groceries),
                findViewById(R.id.btn_recipes),
                findViewById(R.id.btn_calendar)
        );

        navTexts = Arrays.asList(
                findViewById(R.id.txt_home),
                findViewById(R.id.txt_groceries),
                findViewById(R.id.txt_recipes),
                findViewById(R.id.txt_calendar)
        );

        for (int i = 0; i < navButtons.size(); i++) {
            final int index = i;
            navButtons.get(i).setClickable(true);
            navButtons.get(i).setFocusable(true);
            navButtons.get(i).bringToFront();
            navButtons.get(i).setOnClickListener(v -> onNavItemSelected(navButtons.get(index), navTexts.get(index)));
        }
    }

    private void onNavItemSelected(ImageButton selectedButton, TextView selectedText) {
        for (int i = 0; i < navButtons.size(); i++) {
            navButtons.get(i).setColorFilter(ContextCompat.getColor(this, R.color.black));
            navTexts.get(i).setTextColor(ContextCompat.getColor(this, R.color.black));
        }
        selectedButton.setColorFilter(ContextCompat.getColor(this, R.color.orangeBtn));
        selectedText.setTextColor(ContextCompat.getColor(this, R.color.orangeBtn));
    }
}
