package com.gfaim.activities.settings.family;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.gfaim.R;
import com.gfaim.activities.HomeActivity;
import com.gfaim.activities.settings.family.ViewPagerFamilyAdapter;
import com.gfaim.models.family.CreateFamilyBody;
import com.gfaim.models.family.FamilyBody;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.callback.OnFamilyReceivedListener;
import com.gfaim.utility.callback.OnSessionReceivedListener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class NewMemberActivity extends AppCompatActivity {

    ViewPager slideViewPager;
    LinearLayout dotIndicator;
    Button backButton;
    Button nextButton;
    Button cancelButton;
    TextView[] dots;
    ViewPagerFamilyAdapter viewPagerAdapter;

    private final Logger log = Logger.getLogger(NewMemberActivity.class.getName());
    private UtileProfile utileProfile;
    private MemberSessionBody member;
    private FamilyBody family;

    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //empty
        }
        @Override
        public void onPageSelected(int position) {

            setDotIndicator(position);

            if (position > 0) {
                backButton.setVisibility(View.VISIBLE);
            } else {
                backButton.setVisibility(View.INVISIBLE);
            }
            if (position == 3){
                nextButton.setText(R.string.finish);
            } else {
                nextButton.setText(R.string.next);
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
            //empty
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_new_member);
        utileProfile = new UtileProfile(this);
        getAndSetInfo();

        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Nav bar
       /*
        new NavigationBar(this);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        */

        backButton.setOnClickListener(v -> {
            if (getItem(0) > 0) {
                slideViewPager.setCurrentItem(getItem(-1), true);
            }
        });

        nextButton.setOnClickListener(v -> {
            if (getItem(0) < 2)
                slideViewPager.setCurrentItem(getItem(1), true);
            else {
                String name = viewPagerAdapter.getMemberName();
                log.info("[NewMemberActivity][OnCreate] (fin) nouveau membre " + name);
                if (!name.isEmpty()) {

                        utileProfile.createMember(new OnSessionReceivedListener() {
                            @Override
                            public void onSuccess(CreateMemberNoAccount session) {
                                Intent i = new Intent(NewMemberActivity.this, FamilyActivity.class);
                                finish();
                                log.info("[NewMemberActivity][OnCreate] (fin) nouveau membre " + name);
                            }

                            @Override
                            public void onSuccess(MemberSessionBody session) {

                            }

                            @Override
                            public void onFailure(Throwable error) {

                            }
                        },family.getCode(), name, family.getName());
                    }
            }
        });
        //annule
        cancelButton.setOnClickListener(v -> {
            Intent i = new Intent(NewMemberActivity.this, FamilyActivity.class);
            startActivity(i);
            finish();
        });

        slideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        dotIndicator = (LinearLayout) findViewById(R.id.dotIndicator);

        viewPagerAdapter = new ViewPagerFamilyAdapter(this);
        slideViewPager.setAdapter(viewPagerAdapter);


        setDotIndicator(0);
        slideViewPager.addOnPageChangeListener(viewPagerListener);
    }


    public void getAndSetInfo(){
        utileProfile.getSessionMember(new OnSessionReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {

            }

            @Override
            public void onSuccess(MemberSessionBody session) {
                member = session; // Stocke dans l'Activity
                utileProfile.getFamily(new OnFamilyReceivedListener() {
                    @Override
                    public void onSuccess(CreateFamilyBody session) {
                    }

                    @Override
                    public void onSuccess(FamilyBody session) {
                        family = session;
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        System.err.println("Erreur lors de la récupération de la famille : " + error.getMessage());
                    }
                }, member.getFamilyId());
            }
            @Override
            public void onFailure(Throwable error) {
                System.err.println("Erreur lors de la récupération de la session : " + error.getMessage());
            }
        });
    }


    public void setDotIndicator(int position) {
        int totalDots = 3; // Nombre total d'étapes
        dots = new TextView[totalDots];
        dotIndicator.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(String.valueOf(i + 1));
            dots[i].setTextSize(16);
            dots[i].setTypeface(null, Typeface.BOLD);
            dots[i].setGravity(Gravity.CENTER);

            int sizeInDp = 32;
            int sizeInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, sizeInDp, getResources().getDisplayMetrics());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);



            dots[i].setLayoutParams(params);

            if (i < position) {
                dots[i].setBackgroundResource(R.drawable.circle_border);
                dots[i].setTextColor(getResources().getColor(R.color.black, getApplicationContext().getTheme()));
                if (i != 0) {
                    params.setMargins(15, 0, 0, 0);
                }
            } else if (i == position) {
                dots[i].setBackgroundResource(R.drawable.circle_filled);
                dots[i].setTextColor(getResources().getColor(R.color.white, getApplicationContext().getTheme()));
                if (i != 0) {
                    params.setMargins(15, 0, 0, 0);
                }
            } else {
                dots[i].setBackgroundResource(R.drawable.circle_empty);
                dots[i].setTextColor(getResources().getColor(R.color.black, getApplicationContext().getTheme()));
            }

            dotIndicator.addView(dots[i]);
        }
    }

    private int getItem(int i) {
        return slideViewPager.getCurrentItem() + i;
    }
}