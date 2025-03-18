package com.gfaim.activities.auth.onboarding;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.GradientDrawable;

import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.gfaim.R;
import com.gfaim.api.ApiClient;
import com.gfaim.api.DietAllergyService;
import com.gfaim.api.FamilyService;
import com.gfaim.api.FetchCallback;
import com.gfaim.api.MemberService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.family.CreateFamily;
import com.gfaim.models.family.CreateFamilyBody;
import com.gfaim.models.family.FamilyBody;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateSelfMemberBody;
import com.gfaim.models.DietAllergy;
import com.gfaim.utility.auth.JwtDecoder;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPagerAdapter extends PagerAdapter {

    private final Context context;
    @Getter
    private final HashMap<Integer, String> selectedAllergiesItems = new HashMap<>();
    @Getter
    private final HashMap<Integer, String> selectedDietsItems = new HashMap<>();

    @Getter
    private String codeFamily;

    @Getter
    private Long familyId;

    private boolean displayFamilyJoin = true;

    @Getter
    @Setter
    private static Long memberId;
    private final int[] sliderAllTitle = {
            R.string.screen0,
            R.string.screen1,
            R.string.screen2,
            R.string.screen3,
    };

    private final int[] sliderAllDesc = {
            R.string.screen0Desc,
            R.string.screen1Desc,
            R.string.screen2Desc,
            R.string.screen3Desc,
    };

    private TokenManager tokenManager;


    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return sliderAllTitle.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.onboardingslider, container, false);

        TextView sliderTitle = view.findViewById(R.id.sliderTitle);
        TextView sliderDesc = view.findViewById(R.id.sliderDesc);
        FlexboxLayout allergyContainer = view.findViewById(R.id.diet_container);
        Button joinFamily = view.findViewById(R.id.joinFamily);
        Button createFamily = view.findViewById(R.id.createFamily);
        ImageView check = view.findViewById(R.id.check);
        check.setVisibility(View.GONE);

        sliderTitle.setText(this.sliderAllTitle[position]);
        sliderDesc.setText(this.sliderAllDesc[position]);

        initCarrouselImage(view, position);

        if (position == 1 || position == 2) {
            generateAllergyButtons(allergyContainer, position);
        } else {
            allergyContainer.setVisibility(View.GONE);
        }

        if (position == 3) {
            if (displayFamilyJoin) {

                joinFamily.setOnClickListener(v -> showJoinFamilyDialog(view.getContext(), view));

                createFamily.setOnClickListener(v -> showCreateFamilyDialog(view.getContext(), view));
            }


        } else {
            joinFamily.setVisibility(View.GONE);
            createFamily.setVisibility(View.GONE);
        }

        container.addView(view);
        return view;
    }

    private void initCarrouselImage(View view, int position) {
        CardView cardView = view.findViewById(R.id.cardView);
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);

        if (position == 0) {
            imageSlider.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.VISIBLE);

            ArrayList<SlideModel> slideModels = new ArrayList<>();
            slideModels.add(new SlideModel(R.raw.img1, ScaleTypes.FIT));
            slideModels.add(new SlideModel(R.raw.img2, ScaleTypes.FIT));
            slideModels.add(new SlideModel(R.raw.img3, ScaleTypes.FIT));

            imageSlider.setImageList(slideModels, ScaleTypes.FIT);
        } else {
            imageSlider.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
        }
    }

    public void fetchDietOrAllergy(boolean isDiet, FetchCallback callback) {
        DietAllergyService service = ApiClient.getClient(context).create(DietAllergyService.class);
        tokenManager = new TokenManager(context);
        String token = "Bearer " + tokenManager.getAccessToken();

        Call<List<DietAllergy>> call = isDiet ? service.getDiets(token) : service.getAllergies(token);

        call.enqueue(new Callback<List<DietAllergy>>() {
            @Override
            public void onResponse(Call<List<DietAllergy>> call, Response<List<DietAllergy>> response) {
                HashMap<Integer, String> res = new HashMap<>();
                if (response.isSuccessful() && response.body() != null) {
                    List<DietAllergy> items = response.body();

                    for (DietAllergy item : items) {
                        res.put(item.getId(), item.getName());
                    }
                    callback.onSuccess(res);
                } else {
                    Toast.makeText(context, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<List<DietAllergy>> call, Throwable t) {
                Toast.makeText(context, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onFailure();
            }
        });
    }


    private void generateAllergyButtons(FlexboxLayout container, int position) {
        container.removeAllViews();

        HashMap<Integer, String> selectedItems;

        boolean isDiet = (position == 2);
        selectedItems = isDiet ? selectedDietsItems : selectedAllergiesItems;

        fetchDietOrAllergy(isDiet, new FetchCallback() {
            @Override
            public void onSuccess(HashMap<Integer, String> items) {
                for (Map.Entry<Integer, String> entry : items.entrySet()) {
                    int itemId = entry.getKey();
                    String itemName = entry.getValue();

                    TextView button = new TextView(context);
                    button.setText(itemName);
                    button.setTextSize(16);
                    button.setTextColor(Color.BLACK);
                    button.setBackground(getRoundedBorder(Color.TRANSPARENT, Color.BLACK));
                    button.setGravity(Gravity.CENTER);
                    button.setMaxLines(2);
                    button.setWidth(250);
                    button.setHeight(100);

                    if (selectedItems.containsKey(itemId)) {
                        button.setBackground(getRoundedBorder(Color.parseColor("#A6CB96"), Color.TRANSPARENT));
                        button.setTextColor(Color.WHITE);
                    }

                    button.setOnClickListener(v -> {
                        if (selectedItems.containsKey(itemId)) {
                            // Désélectionner
                            selectedItems.remove(itemId);
                            button.setBackground(getRoundedBorder(Color.TRANSPARENT, Color.BLACK));
                            button.setTextColor(Color.BLACK);
                        } else {
                            // Sélectionner
                            selectedItems.put(itemId, itemName);
                            button.setBackground(getRoundedBorder(Color.parseColor("#A6CB96"), Color.TRANSPARENT));
                            button.setTextColor(Color.WHITE);
                        }
                    });

                    FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(15, 15, 15, 15);
                    button.setLayoutParams(params);
                    container.addView(button);
                }
            }

            @Override
            public void onFailure() {
                Toast.makeText(context, "Impossible de récupérer les données", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUserEmail() {
        String accessToken = tokenManager.getAccessToken();
        if (accessToken != null) {
            String decodedToken = JwtDecoder.decodeJWT(accessToken);
            assert decodedToken != null;
            JsonObject jsonObject = JsonParser.parseString(decodedToken).getAsJsonObject();
            if (jsonObject.has("upn")) {
                return jsonObject.get("upn").getAsString();
            }
        }
        return "";
    }

    private void createMember(String codeFamily) {
        //create member
        MemberService memberService = ApiClient.getClient(context).create(MemberService.class);
        String email = getUserEmail();
        Call<CreateMember> call = memberService.createMember("Bearer " + tokenManager.getAccessToken(), new CreateSelfMemberBody(true, codeFamily, email));
        call.enqueue(new Callback<CreateMember>() {
            @Override
            public void onResponse(Call<CreateMember> call, Response<CreateMember> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setMemberId(response.body().getId());
                    familyId = response.body().getFamilyId();
                } else {
                    Toast.makeText(context, "Erreur lors de la creation d'un membre", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateMember> call, Throwable t) {
                Toast.makeText(context, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getFamillyName(View view) {
        //create member
        FamilyService familyService = ApiClient.getClient(context).create(FamilyService.class);
        Call<FamilyBody> call = familyService.getFamily(familyId, tokenManager.getAccessToken());
        TextView tvFamilyName = view.findViewById(R.id.tv_family_name);

        call.enqueue(new Callback<FamilyBody>() {
            @Override
            public void onResponse(Call<FamilyBody> call, Response<FamilyBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvFamilyName.setText( response.body().getName());

                } else {
                    Toast.makeText(context, "Erreur lors de la creation d'un membre", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FamilyBody> call, Throwable t) {
                System.out.println("erreur " +t.getMessage());
                Toast.makeText(context, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showJoinFamilyDialog(Context context, View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view2 = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_join_family, null);


        Button createFamily = view.findViewById(R.id.createFamily);
        Button joinFamily2 = view.findViewById(R.id.joinFamily);
        ImageView check = view.findViewById(R.id.check);
        TextView tvFamilyName = view2.findViewById(R.id.tv_family_name);
        TextView tvWelcomeMessage = view2.findViewById(R.id.tv_welcome_message);
        Button joinFamily = view2.findViewById(R.id.joinFamily);
        EditText familyCode = view2.findViewById(R.id.familyCode);

        joinFamily.setEnabled(false);

        familyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isValid = !s.toString().trim().isEmpty();
                joinFamily.setEnabled(isValid);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //empty
            }
        });

        tvFamilyName.setVisibility(View.GONE);
        tvWelcomeMessage.setVisibility(View.GONE);

        joinFamily.setOnClickListener(v -> {

            codeFamily = familyCode.getText().toString();
            MemberService memberService = ApiClient.getClient(context).create(MemberService.class);
            String email = getUserEmail();
            Call<CreateMember> call = memberService.createMember("Bearer " + tokenManager.getAccessToken(), new CreateSelfMemberBody(true, codeFamily, email));
            call.enqueue(new Callback<CreateMember>() {
                @Override
                public void onResponse(Call<CreateMember> call, Response<CreateMember> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        setMemberId(response.body().getId());
                        familyId = response.body().getFamilyId();
                        displayFamilyJoin = false;
                        joinFamily.setVisibility(View.GONE);
                        familyCode.setVisibility(View.GONE);
                        tvFamilyName.setVisibility(View.VISIBLE);
                        tvWelcomeMessage.setVisibility(View.VISIBLE);
                        tokenManager = new TokenManager(context);
                        getFamillyName(view2);
                        //Supprime les boutons parents
                        createFamily.setVisibility(View.GONE);
                        joinFamily2.setVisibility(View.GONE);
                        check.setVisibility(View.VISIBLE);
                        ((Animatable) check.getDrawable()).start();
                    } else {
                        Toast.makeText(context, "Erreur lors de la creation d'un membre", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CreateMember> call, Throwable t) {
                    Toast.makeText(context, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        bottomSheetDialog.setContentView(view2);
        bottomSheetDialog.show();
    }

    private void showCreateFamilyDialog(Context context, View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view2 = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_create_family, null);

        Button createFamily = view.findViewById(R.id.createFamily);
        Button joinFamily = view.findViewById(R.id.joinFamily);
        ImageView check = view.findViewById(R.id.check);

        TextView newFam = view2.findViewById(R.id.new_fam);
        TextView tvFamilyCode = view2.findViewById(R.id.textView);
        Button createFamily2 = view2.findViewById(R.id.createFamily);
        EditText familyName = view2.findViewById(R.id.familyName);
        TextView tvFamilyName = view2.findViewById(R.id.tv_family_name);

        ImageView copyIcon = view2.findViewById(R.id.copyIcon);

        copyIcon.setOnClickListener(v -> copyToClipboard(tvFamilyCode.getText().toString()));

        tvFamilyCode.setOnClickListener(v -> copyToClipboard(tvFamilyCode.getText().toString()));

        createFamily2.setEnabled(false);

        familyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isValid = s.toString().trim().matches("[A-Za-zÀ-ÿ]+");
                createFamily2.setEnabled(isValid);
            }

            @Override
            public void afterTextChanged(Editable s) { //empty
            }
        });

        tvFamilyCode.setVisibility(View.GONE);
        copyIcon.setVisibility(View.GONE);
        tvFamilyName.setVisibility(View.GONE);
        newFam.setVisibility(View.GONE);


        createFamily2.setOnClickListener(v -> {
            displayFamilyJoin = false;
            String familyNameStr = familyName.getText().toString();
            codeFamily = "0000000";
            createFamily2.setVisibility(View.GONE);
            familyName.setVisibility(View.GONE);
            tvFamilyName.setVisibility(View.VISIBLE);
            tvFamilyName.setText(familyNameStr);
            tvFamilyCode.setVisibility(View.VISIBLE);
            copyIcon.setVisibility(View.VISIBLE);
            newFam.setVisibility(View.VISIBLE);

            tokenManager = new TokenManager(context);
            FamilyService familyService = ApiClient.getClient(context).create(FamilyService.class);

            Call<CreateFamily> call = familyService.createFamily("Bearer " + tokenManager.getAccessToken(), new CreateFamilyBody(familyNameStr));
            call.enqueue(new Callback<CreateFamily>() {
                @Override
                public void onResponse(Call<CreateFamily> call, Response<CreateFamily> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        codeFamily = response.body().getCode();
                        tvFamilyCode.setText(codeFamily);
                        createMember(codeFamily);

                    } else {
                        Toast.makeText(context, "Erreur lors de la creation d'une famille", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<CreateFamily> call, Throwable t) {
                    Toast.makeText(context, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


            //Supprime les boutons parents
            createFamily.setVisibility(View.GONE);
            joinFamily.setVisibility(View.GONE);
            check.setVisibility(View.VISIBLE);
            ((Animatable) check.getDrawable()).start();

        });

        bottomSheetDialog.setContentView(view2);
        bottomSheetDialog.show();
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text); // Création du ClipData
        clipboard.setPrimaryClip(clip);

    }


    private GradientDrawable getRoundedBorder(int backgroundColor, int borderColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(30);
        drawable.setColor(backgroundColor);
        drawable.setStroke(3, borderColor);
        return drawable;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
