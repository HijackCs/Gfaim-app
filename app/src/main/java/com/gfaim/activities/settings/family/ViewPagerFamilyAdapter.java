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
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import com.gfaim.R;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerFamilyAdapter extends PagerAdapter {

    private final Context context;
    String name;
   // private String selectedRole = "";

    private final List<String> selectedAllergiesItems = new ArrayList<>();
    private final List<String> selectedDietsItems = new ArrayList<>();

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

        int[] items;
        List<String> selectedItems;

        if (position == 1) { // Allergies
            items = new int[]{R.string.allergieGluten, R.string.allergieDiary, R.string.allergieEgg,
                    R.string.allergieSoy, R.string.allergiePeanut, R.string.allergieWheat,
                    R.string.allergieMilk, R.string.allergieFish};
            selectedItems = selectedAllergiesItems;
        } else if (position == 2) { // Diets
            items = new int[]{R.string.dietsVegan, R.string.dietsPaleo,
                    R.string.dietsDukan, R.string.dietsVegetarian,
                    R.string.dietsAktin, R.string.dietsInterFast};
            selectedItems = selectedDietsItems;
        } else {
            return;
        }


        for (int item : items) {
            TextView button = new TextView(context);
            button.setText(context.getString(item));
            button.setTextSize(16);
            button.setTextColor(Color.BLACK);
            button.setBackground(getRoundedBorder(Color.TRANSPARENT, Color.BLACK));
            button.setGravity(Gravity.CENTER);
            button.setMaxLines(2);
            button.setWidth(250);
            button.setHeight(100);

            String itemText = context.getString(item);
            if (selectedItems.contains(itemText)) {
                button.setBackground(getRoundedBorder(Color.parseColor("#A6CB96"), Color.TRANSPARENT));
                button.setTextColor(Color.WHITE);
            }

            button.setOnClickListener(v -> {
                if (selectedItems.contains(itemText)) {
                    selectedItems.remove(itemText);
                    button.setBackground(getRoundedBorder(Color.TRANSPARENT, Color.BLACK));
                    button.setTextColor(Color.BLACK);
                } else {
                    selectedItems.add(itemText);
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

    public List<String> getSelectedAllergiesItems() {
        return selectedAllergiesItems;
    }

    public List<String> getSelectedDietsItems() {
        return selectedDietsItems;
    }


    public String getMemberName(){
        return name;
    }

}
