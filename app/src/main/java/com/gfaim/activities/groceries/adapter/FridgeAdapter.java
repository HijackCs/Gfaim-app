package com.gfaim.activities.groceries.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class FridgeAdapter extends RecyclerView.Adapter<FridgeAdapter.ViewHolder>{
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
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateList(List<FoodItem> newList) {
        foodList = new ArrayList<>(newList);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
        }
    }
}
