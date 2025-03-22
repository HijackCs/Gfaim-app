package com.gfaim.activities.settings.family;

import android.app.Activity;
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
import com.gfaim.activities.auth.onboarding.ViewPagerAdapter;
import com.gfaim.api.ApiClient;
import com.gfaim.api.MemberService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.family.CreateFamilyBody;
import com.gfaim.models.family.FamilyBody;
import com.gfaim.models.family.LeaveFamilyBody;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.callback.OnFamilyReceivedListener;
import com.gfaim.utility.callback.OnMemberReceivedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewMemberActivity extends AppCompatActivity {

    ViewPager slideViewPager;
    LinearLayout dotIndicator;
    Button backButton;
    Button nextButton;
    Button cancelButton;
    TextView[] dots;
    ViewPagerFamilyAdapter viewPagerAdapter;

    @Getter
    @Setter
    private static Long memberId;

    private static final String ALLERGIES = "allergies";


    private final Activity activity = this;


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
        setContentView(R.layout.navigation_new_member);
        utileProfile = new UtileProfile(this);
        getAndSetInfo();

        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        cancelButton = findViewById(R.id.cancelButton);

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
                if (!name.isEmpty()) {

                    utileProfile.createMember(new OnMemberReceivedListener() {
                        @Override
                        public void onSuccess(CreateMemberNoAccount session) {
                            //empty
                        }

                        @Override
                        public void onSuccess(MemberSessionBody session) {
                            //empty
                        }

                        @Override
                        public void onFailure(Throwable error) {
//empty
                        }

                        @Override
                        public void onSuccess(CreateMember session) {
                            setMemberId(session.getId());  // Stocke l'ID du membre nouvellement créé

                            postSelections(activity, ALLERGIES, viewPagerAdapter.getSelectedAllergiesItems());
                            postSelections(activity, "diets", viewPagerAdapter.getSelectedDietsItems());

                            activity.finish();
                            Intent intent = new Intent(activity, FamilyActivity.class);
                            activity.startActivity(intent);
                            log.info("[NewMemberActivity][OnCreate] (fin) nouveau membre " + name);
                        }
                    }, family.getCode(), name, family.getName());


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


    public void getAndSetInfo() {
        utileProfile.getSessionMember(new OnMemberReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {
                //empty
            }

            @Override
            public void onSuccess(MemberSessionBody session) {
                member = session; // Stocke dans l'Activity
                utileProfile.getFamily(new OnFamilyReceivedListener() {
                    @Override
                    public void onSuccess() {
                        //empty
                    }

                    @Override
                    public void onSuccess(LeaveFamilyBody family) {
                        //empty
                    }

                    @Override
                    public void onSuccess(CreateFamilyBody session) {
                        //empty
                    }

                    @Override
                    public void onSuccess(FamilyBody session) {
                        family = session;
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        log.info("Error fetching family : " + error.getMessage());
                    }
                }, member.getFamilyId());
            }

            @Override
            public void onFailure(Throwable error) {
                log.info("Error fetching session : " + error.getMessage());
            }

            @Override
            public void onSuccess(CreateMember body) {
                //empty
            }
        });
    }


    public void setDotIndicator(int position) {
        int totalDots = 3;
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

    private void postSelections(Context context, String type, HashMap<Integer, String> selectedItems) {
        if (selectedItems == null || selectedItems.isEmpty()) {
            return;
        }

        MemberService service = ApiClient.getClient(this).create(MemberService.class);
        TokenManager tokenManager = new TokenManager(this);
        String token = "Bearer " + tokenManager.getAccessToken();

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
                    log.info("Successfully saved data");

                } else {
                    log.info("Error sending data");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                log.info("Error: " + t.getMessage());
            }
        });
    }

    private int getItem(int i) {
        return slideViewPager.getCurrentItem() + i;
    }
}