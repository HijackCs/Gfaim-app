package com.gfaim.activities.settings.family;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
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
    EditText memberName;
    String name;
    private final List<String> selectedAllergiesItems = new ArrayList<>();
    private final List<String> selectedDietsItems = new ArrayList<>();

    //private final String memberName = null;

    private final int[] sliderAllTitle = {
            R.string.screen0Family,
            R.string.screen1Family,
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
        TextView sliderTitle2 = view.findViewById(R.id.sliderTitle2);
        TextView placeHolder1 = view.findViewById(R.id.placeHolder1);
        TextView placeHolder2 = view.findViewById(R.id.placeHolder2);


        FlexboxLayout allergyContainer = view.findViewById(R.id.diet_container);
        sliderTitle.setText(this.sliderAllTitle[position]);
        EditText mail = view.findViewById(R.id.mail);
        EditText memberName = view.findViewById(R.id.memberName);
        LinearLayout roleDisplay = view.findViewById(R.id.role_display);

        if(position == 0){
            handleEditText(mail, memberName);
        }else{
            sliderTitle2.setVisibility(View.GONE);
            placeHolder1.setVisibility(View.GONE);
            placeHolder2.setVisibility(View.GONE);
            mail.setVisibility(View.GONE);
            memberName.setVisibility(View.GONE);
        }

        if(position ==1){
            initRoleSelect(view);
        }else{
            roleDisplay.setVisibility(View.GONE);
        }

        if (position == 2 || position == 3) {
            generateAllergyButtons(allergyContainer, position);
        } else {
            allergyContainer.setVisibility(View.GONE);
        }

        container.addView(view);
        return view;
    }

    private void handleEditText(EditText mail, EditText memberName){
        mail.setVisibility(View.VISIBLE);
        memberName.setVisibility(View.VISIBLE);

        memberName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = s.toString(); // Stocke la valeur actuelle
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }


    private void generateAllergyButtons(FlexboxLayout container, int position) {
        container.removeAllViews();

        int[] items;
        List<String> selectedItems;

        if (position == 2) { // Allergies
            items = new int[]{R.string.allergieGluten, R.string.allergieDiary, R.string.allergieEgg,
                    R.string.allergieSoy, R.string.allergiePeanut, R.string.allergieWheat,
                    R.string.allergieMilk, R.string.allergieFish};
            selectedItems = selectedAllergiesItems;
        } else if (position == 3) { // Diets
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
                    // Désélectionner
                    selectedItems.remove(itemText);
                    button.setBackground(getRoundedBorder(Color.TRANSPARENT, Color.BLACK));
                    button.setTextColor(Color.BLACK);
                } else {
                    // Sélectionner
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

    private void initRoleSelect(View view){
        LinearLayout roleChef = view.findViewById(R.id.role_chef);
        LinearLayout roleConsumer = view.findViewById(R.id.role_consumer);
        ImageView iconChef = view.findViewById(R.id.icon_chef);
        ImageView iconConsumer = view.findViewById(R.id.icon_consumer);
        TextView textChef = view.findViewById(R.id.text_chef);
        TextView textConsumer = view.findViewById(R.id.text_consumer);

        View.OnClickListener roleClickListener = v -> {
            roleChef.setBackgroundResource(R.drawable.role_unselected);
            roleConsumer.setBackgroundResource(R.drawable.role_unselected);
            iconChef.setColorFilter(ContextCompat.getColor(context, R.color.orangeBtn));
            iconConsumer.setColorFilter(ContextCompat.getColor(context, R.color.orangeBtn));
            textChef.setTextColor(ContextCompat.getColor(context, R.color.orangeBtn));
            textConsumer.setTextColor(ContextCompat.getColor(context, R.color.orangeBtn));

            if (v.getId() == R.id.role_chef) {
                roleChef.setBackgroundResource(R.drawable.role_selected);
                iconChef.setColorFilter(ContextCompat.getColor(context, R.color.white));
                textChef.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                roleConsumer.setBackgroundResource(R.drawable.role_selected);
                iconConsumer.setColorFilter(ContextCompat.getColor(context, R.color.white));
                textConsumer.setTextColor(ContextCompat.getColor(context, R.color.white));
            }
        };

        roleChef.setOnClickListener(roleClickListener);
        roleConsumer.setOnClickListener(roleClickListener);
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
