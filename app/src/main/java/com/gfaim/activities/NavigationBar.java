package com.gfaim.activities;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.gfaim.R;

import java.util.Arrays;
import java.util.List;

public class NavigationBar {

    private final List<ImageButton> navButtons;
    private final List<TextView> navTexts;
    private final Context context;

    public NavigationBar(Activity activity) {
        this.context = activity;
        navButtons = Arrays.asList(
                activity.findViewById(R.id.btn_home),
                activity.findViewById(R.id.btn_groceries),
                activity.findViewById(R.id.btn_recipes),
                activity.findViewById(R.id.btn_calendar)
        );

        navTexts = Arrays.asList(
                activity.findViewById(R.id.txt_home),
                activity.findViewById(R.id.txt_groceries),
                activity.findViewById(R.id.txt_recipes),
                activity.findViewById(R.id.txt_calendar)
        );

        initNavBar();
    }

    private void initNavBar() {
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
            navButtons.get(i).setColorFilter(ContextCompat.getColor(context, R.color.black));
            navTexts.get(i).setTextColor(ContextCompat.getColor(context, R.color.black));
        }
        selectedButton.setColorFilter(ContextCompat.getColor(context, R.color.orangeBtn));
        selectedText.setTextColor(ContextCompat.getColor(context, R.color.orangeBtn));
    }
}
