package com.gfaim.activities.home;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.gfaim.R;
import com.gfaim.activities.NavigationBar;
import com.gfaim.activities.settings.SettingsActivity;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.callback.OnMemberReceivedListener;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class HomeActivity extends AppCompatActivity {

    private final Logger log = Logger.getLogger(HomeActivity.class.getName());
    private NavigationBar navigationBar;

    private MemberSessionBody member;

    private UtileProfile utileProfile;

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

            navigationBar.setActiveButton(R.id.btn_home);

            utileProfile = new UtileProfile(this);
            utileProfile.getSessionMember(new OnMemberReceivedListener() {
                @Override
                public void onSuccess(CreateMemberNoAccount session) {
                    //empty
                }
                @Override
                public void onSuccess(MemberSessionBody session) {

                    member = session;

                    TextView user = findViewById(R.id.textView3);
                    user.setText("Salut "+ member.getFirstName() +" \uD83D\uDC4B");

                }
                @Override
                public void onFailure(Throwable error) {
                    //empty
                }
                @Override
                public void onSuccess(CreateMember body) {
                    //empty
                }
            });

        } catch (Exception e) {
            log.warning("[acceuil][onCreate] Problème au lancement de HomeActivity");
        }

        setupProfilBtn();
    }

    private void setupProfilBtn(){
        FrameLayout circleProfile = findViewById(R.id.circleProfile);
        circleProfile.setOnClickListener( v->{
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });
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
                "Diner"
        );
/*
        List<String> texts = Arrays.asList(
                "Breakfast",
                "Lunch",
                "Dinner"
        );
*/
        CarrouselAdapter adapter = new CarrouselAdapter(images, texts);
        viewPager.setAdapter(adapter);
    }
}
