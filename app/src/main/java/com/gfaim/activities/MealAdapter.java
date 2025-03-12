package com.gfaim.activities;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.calendar.AddIngredientsCalendar;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private final List<String> mealList;
    private final Context context;

    public MealAdapter(List<String> mealList, Context context) {
        this.mealList = mealList;
        this.context = context;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_card, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        String meal = mealList.get(position);
        holder.textTitle.setText(meal);

        holder.snackImage.setImageResource(R.drawable.ic_add_green);

        holder.snackImage.setOnClickListener(v -> {
            showSnackPopup(holder);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddIngredientsCalendar.class);
            intent.putExtra("mealType", meal);
            context.startActivity(intent);
        });
    }

    private void showSnackPopup(MealViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add a snack");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String snack = input.getText().toString().trim();
            if (!snack.isEmpty()) {
                holder.snackText.setText(snack);
                holder.snackImage.setImageResource(R.drawable.ic_snack);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        ImageView snackImage;
        TextView snackText;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            snackImage = itemView.findViewById(R.id.snack_image);
            snackText = itemView.findViewById(R.id.snack_text);
        }
    }
}