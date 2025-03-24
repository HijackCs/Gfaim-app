package com.gfaim.activities.auth.onboarding;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.gfaim.R;
import com.gfaim.activities.home.HomeActivity;
import com.gfaim.api.ApiClient;
import com.gfaim.api.MemberService;
import com.gfaim.auth.TokenManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnBoardingActivity extends AppCompatActivity {

    ViewPager slideViewPager;
    LinearLayout dotIndicator;
    Button backButton;
    Button nextButton;
    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;
    HashMap<String, Object> registerInfo;
    private final Logger log = Logger.getLogger(OnBoardingActivity.class.getName());

    private static final String ALLERGIES = "allergies";

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
            if (position == 3) {
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
        setContentView(R.layout.navigation);
        viewPagerAdapter = new ViewPagerAdapter(this);

        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);

        backButton.setOnClickListener(v -> {
            if (getItem(0) > 0) {
                slideViewPager.setCurrentItem(getItem(-1), true);
            }
        });

        nextButton.setOnClickListener(v -> {
            if (getItem(0) < 3)
                slideViewPager.setCurrentItem(getItem(1), true);
            else {
                String familyCode = viewPagerAdapter.getCodeFamily();
                if (familyCode == null || familyCode.isEmpty() || familyCode.equals("000000")) {
                    Toast.makeText(this, R.string.enterValidFamily, Toast.LENGTH_SHORT).show();
                    return;
                }
                postSelections(this, ALLERGIES, viewPagerAdapter.getSelectedAllergiesItems());
                postSelections(this, "diets", viewPagerAdapter.getSelectedDietsItems());

                Intent i = new Intent(OnBoardingActivity.this, HomeActivity.class);
                log.info("[OnBoardingActivity][OnCreate] (finish) Register info: " + registerInfo);

                startActivity(i);
                finish();
            }
        });

        slideViewPager = findViewById(R.id.slideViewPager);
        dotIndicator = findViewById(R.id.dotIndicator);

        slideViewPager.setAdapter(viewPagerAdapter);

        setDotIndicator(0);
        slideViewPager.addOnPageChangeListener(viewPagerListener);
    }


    private void postSelections(Context context, String type, HashMap<Integer, String> selectedItems) {
        if (selectedItems == null || selectedItems.isEmpty()) {
            return;
        }

        MemberService service = ApiClient.getClient(this).create(MemberService.class);
        TokenManager tokenManager = new TokenManager(this);
        String token = "Bearer " + tokenManager.getAccessToken();
        Long memberId = ViewPagerAdapter.getMemberId();

        List<Map<String, Integer>> body = new ArrayList<>();
        for (Integer id : selectedItems.keySet()) {
            Map<String, Integer> item = new HashMap<>();
            item.put(type.equals(ALLERGIES) ? "allergy_id" : "diet_id", id);
            body.add(item);
        }

        Call<Void> call = type.equals(ALLERGIES) ?
                service.postAllergies(memberId, token, body) :
                service.postDiets(memberId, token, body);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    log.info("Data successfully send.");
                } else {
                    Toast.makeText(context, R.string.errorSending, Toast.LENGTH_SHORT).show();
                    log.info("Error while sending data.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                log.info("Error: " + t.getMessage());
            }
        });
    }


    public void setDotIndicator(int position) {
        int totalDots = 4;
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