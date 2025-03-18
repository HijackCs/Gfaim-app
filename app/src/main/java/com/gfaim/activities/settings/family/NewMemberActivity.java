package com.gfaim.activities.settings.family;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.gfaim.R;
import com.gfaim.activities.HomeActivity;
import com.gfaim.activities.settings.family.ViewPagerFamilyAdapter;

import java.util.HashMap;
import java.util.logging.Logger;

public class NewMemberActivity extends AppCompatActivity {

    ViewPager slideViewPager;
    LinearLayout dotIndicator;
    Button backButton;
    Button nextButton;
    Button cancelButton;
    TextView[] dots;
    ViewPagerFamilyAdapter viewPagerAdapter;
    private String memberName;
    private String email;

    private final Logger log = Logger.getLogger(NewMemberActivity.class.getName());


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
            if (getItem(0) < 3)
                slideViewPager.setCurrentItem(getItem(1), true);
            else {

                String name = viewPagerAdapter.getMemberName();
                log.info("[NewMemberActivity][OnCreate] (fin) nouveau membre " + name);
                if (!name.isEmpty()) {
                        Intent i = new Intent(NewMemberActivity.this, FamilyActivity.class);
                        i.putExtra("MEMBER_NAME",name );
                        setResult(RESULT_OK, i);
                        finish();
                        log.info("[NewMemberActivity][OnCreate] (fin) nouveau membre " + name);
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

    public void setDotIndicator(int position) {
        int totalDots = 4; // Nombre total d'étapes
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