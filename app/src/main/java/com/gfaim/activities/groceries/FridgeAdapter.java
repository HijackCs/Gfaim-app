package com.gfaim.activities.groceries;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.models.FoodItem;

import java.util.List;

public class FridgeAdapter extends RecyclerView.Adapter<FridgeAdapter.ViewHolder> {
    private List<FoodItem> foodList;

    public FridgeAdapter(List<FoodItem> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = foodList.get(position);
        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText(String.valueOf(item.getCount()));

        holder.itemDeleteButton.setVisibility(View.VISIBLE);
        holder.itemCheckbox.setVisibility(View.GONE);

        holder.itemDeleteButton.setOnClickListener(v -> {
            foodList.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemKcal, itemQuantity;
        ImageView itemDeleteButton;
        CheckBox itemCheckbox;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemKcal = itemView.findViewById(R.id.itemKcal);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemDeleteButton = itemView.findViewById(R.id.itemDeleteButton);
            itemCheckbox = itemView.findViewById(R.id.itemCheckbox);
        }
    }
}
