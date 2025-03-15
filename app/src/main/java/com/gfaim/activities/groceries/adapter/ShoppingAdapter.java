package com.gfaim.activities.groceries.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.groceries.fragment.ShoppingFragment;
import com.gfaim.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ViewHolder> /*implements RemovableAdapter*/ {
    private List<FoodItem> shoppingList;
    private List<FoodItem> selectedItems = new ArrayList<>();
    private ShoppingFragment fragment;

    public ShoppingAdapter(List<FoodItem> shoppingList, ShoppingFragment fragment) {
        this.shoppingList = shoppingList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingAdapter.ViewHolder holder, int position) {
        FoodItem item = shoppingList.get(position);
        holder.itemName.setText(item.getName());
        holder.itemCheckbox.setVisibility(View.VISIBLE);

        // Vérifier si l'élément est sélectionné
        holder.itemCheckbox.setOnCheckedChangeListener(null); // Empêche les boucles infinies
        holder.itemCheckbox.setChecked(fragment.getSelectedItems().contains(item));

        holder.itemCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            fragment.toggleItemSelection(item, isChecked);
        });
    }


    @Override
    public int getItemCount() {
        return shoppingList.size();
    }
    public void updateList(List<FoodItem> newList) {
        this.shoppingList = new ArrayList<>(newList);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        CheckBox itemCheckbox;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemCheckbox = itemView.findViewById(R.id.itemCheckbox);
        }
    }





}