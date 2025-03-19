package com.gfaim.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.gfaim.R;
import com.gfaim.activities.calendar.CalendarActivity;
import com.gfaim.activities.home.HomeActivity;

import java.util.Arrays;
import java.util.List;

public class NavigationBar {

    private final List<ImageButton> navButtons;
    private final List<TextView> navTexts;
    private final Context context;
    private final View bottomBar;


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
        bottomBar = activity.findViewById(R.id.bottom_bar);
        initNavBar();
    }

    private void initNavBar() {
        for (int i = 0; i < navButtons.size(); i++) {
            final int index = i;
            if (navButtons.get(i) == null) {
                Log.e("NavigationBar", "Button at index " + i + " is null");
                continue;
            }
            navButtons.get(i).setClickable(true);
            navButtons.get(i).setFocusable(true);
            navButtons.get(i).bringToFront();
            navButtons.get(i).setOnClickListener(v -> onNavItemSelected(navButtons.get(index)));
        }
    }

    private void onNavItemSelected(ImageButton selectedButton) {
        Intent intent = null;

        if (selectedButton.getId() == R.id.btn_calendar) {
            intent = new Intent(context, CalendarActivity.class);
        } else if (selectedButton.getId() == R.id.btn_home) {
            intent = new Intent(context, HomeActivity.class);
        } /*else if (selectedButton.getId() == R.id.btn_groceries) {
            intent = new Intent(context, GroceriesActivity.class);
        } else if (selectedButton.getId() == R.id.btn_recipes) {
            intent = new Intent(context, RecipesActivity.class);
        }*/

        if (intent != null) {
            intent.putExtra("activeButtonId", selectedButton.getId());
            context.startActivity(intent);
        }
    }

    public void setActiveButton(int activeButtonId) {
        for (int i = 0; i < navButtons.size(); i++) {
            navButtons.get(i).setColorFilter(ContextCompat.getColor(context, R.color.black));
            navTexts.get(i).setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        for (int i = 0; i < navButtons.size(); i++) {
            if (navButtons.get(i).getId() == activeButtonId) {
                navButtons.get(i).setColorFilter(ContextCompat.getColor(context, R.color.orangeBtn));
                navTexts.get(i).setTextColor(ContextCompat.getColor(context, R.color.orangeBtn));
                break;
            }
        }
    }

    public void hide() {
        if (bottomBar != null) {
            bottomBar.setVisibility(View.GONE);
        }
    }

    public void show() {
        if (bottomBar != null) {
            bottomBar.setVisibility(View.VISIBLE);
        }
    }
}
