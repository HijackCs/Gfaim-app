package com.gfaim.activities.settings.family;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import com.gfaim.R;
import com.gfaim.api.ApiClient;
import com.gfaim.api.DietAllergyService;
import com.gfaim.api.FetchCallback;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.DietAllergy;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPagerFamilyAdapter extends PagerAdapter {

    private final Context context;
    String name;
   // private String selectedRole = "";

    @Getter
    private final HashMap<Integer, String> selectedAllergiesItems = new HashMap<>();
    @Getter
    private final HashMap<Integer, String> selectedDietsItems = new HashMap<>();

    @Getter
    @Setter
    private static Long memberId;

    private TokenManager tokenManager;


    //private final String memberName = null;

    private final int[] sliderAllTitle = {
            R.string.screen0Family,
            R.string.screen2Family,
            R.string.screen3Family,
    };



    public ViewPagerFamilyAdapter(Context context) {
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
        View view = layoutInflater.inflate(R.layout.slider_new_member, container, false);

        TextView sliderTitle = view.findViewById(R.id.sliderTitle);
        TextView placeHolder1 = view.findViewById(R.id.placeHolder1);


        FlexboxLayout allergyContainer = view.findViewById(R.id.diet_container);
        sliderTitle.setText(this.sliderAllTitle[position]);
        EditText memberName = view.findViewById(R.id.memberName);
        //LinearLayout roleDisplay = view.findViewById(R.id.role_display);

        if(position == 0){
            handleEditText(memberName);
        }else{
            placeHolder1.setVisibility(View.GONE);
            memberName.setVisibility(View.GONE);
        }

        /*if(position ==1){
            initRoleSelect(view);
        }else{
            roleDisplay.setVisibility(View.GONE);
        }*/

        if (position == 1 || position == 2) {
            generateAllergyButtons(allergyContainer, position);
        } else {
            allergyContainer.setVisibility(View.GONE);
        }

        container.addView(view);
        memberName.setText(name);

        return view;
    }

    private void handleEditText(EditText memberName){
        memberName.setVisibility(View.VISIBLE);


        // Bloquer l'autre champ lors de la saisie
        memberName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 30) {
                    memberName.setError("Maximum 30 caractères");
                    memberName.setText(s.subSequence(0, 30));
                    memberName.setSelection(30);
                }else{
                    name = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
                    memberName.setError("Seulement des lettres autorisées");
                }
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

    public void fetchDietOrAllergy(boolean isDiet, FetchCallback callback) {
        DietAllergyService service = ApiClient.getClient(context).create(DietAllergyService.class);
        tokenManager = new TokenManager(context);
        String token = "bearer "+ tokenManager.getAccessToken();

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

    /*
    private void initRoleSelect(View view) {
        LinearLayout roleChef = view.findViewById(R.id.role_chef);
        LinearLayout roleConsumer = view.findViewById(R.id.role_consumer);
        ImageView iconChef = view.findViewById(R.id.icon_chef);
        ImageView iconConsumer = view.findViewById(R.id.icon_consumer);
        TextView textChef = view.findViewById(R.id.text_chef);
        TextView textConsumer = view.findViewById(R.id.text_consumer);

        updateRoleUI(roleChef, roleConsumer, iconChef, iconConsumer, textChef, textConsumer);

        View.OnClickListener roleClickListener = v -> {
            if (v.getId() == R.id.role_chef) {
                selectedRole = String.valueOf(R.string.role_chef);
            } else {
                selectedRole = String.valueOf(R.string.role_consumer);
            }

            updateRoleUI(roleChef, roleConsumer, iconChef, iconConsumer, textChef, textConsumer);
        };

        roleChef.setOnClickListener(roleClickListener);
        roleConsumer.setOnClickListener(roleClickListener);
    }

    private void updateRoleUI(LinearLayout roleChef, LinearLayout roleConsumer, ImageView iconChef, ImageView iconConsumer, TextView textChef, TextView textConsumer) {
        roleChef.setBackgroundResource(R.drawable.role_unselected);
        roleConsumer.setBackgroundResource(R.drawable.role_unselected);
        iconChef.setColorFilter(ContextCompat.getColor(context, R.color.orangeBtn));
        iconConsumer.setColorFilter(ContextCompat.getColor(context, R.color.orangeBtn));
        textChef.setTextColor(ContextCompat.getColor(context, R.color.orangeBtn));
        textConsumer.setTextColor(ContextCompat.getColor(context, R.color.orangeBtn));

        if (selectedRole.equals(String.valueOf(R.string.role_chef))) {
            roleChef.setBackgroundResource(R.drawable.role_selected);
            iconChef.setColorFilter(ContextCompat.getColor(context, R.color.white));
            textChef.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else if (selectedRole.equals(String.valueOf(R.string.role_consumer))) {
            roleConsumer.setBackgroundResource(R.drawable.role_selected);
            iconConsumer.setColorFilter(ContextCompat.getColor(context, R.color.white));
            textConsumer.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
    }*/


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


    public String getMemberName(){
        return name;
    }

}
