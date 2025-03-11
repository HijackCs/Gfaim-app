package com.gfaim.activities.groceries;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.models.FoodItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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

        holder.itemDeleteButton.setVisibility(View.VISIBLE);

        holder.itemDeleteButton.setOnClickListener(v -> {
            if (position >= 0 && position < foodList.size()) {
                foodList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, foodList.size());
            }
        });

        //holder.itemView.setOnClickListener(v -> showQuantityBottomSheet(holder, item));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        ImageView itemDeleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemDeleteButton = itemView.findViewById(R.id.itemDeleteButton);
        }
    }
}

   /* private void showQuantityBottomSheet(ViewHolder holder, FoodItem item) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.itemView.getContext());
        View sheetView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialogue_quantity, null);
        bottomSheetDialog.setContentView(sheetView);

        TextView tvItemName = sheetView.findViewById(R.id.tvItemName);
        TextView tvQuantity = sheetView.findViewById(R.id.tvQuantity);
        TextView btnIncrease = sheetView.findViewById(R.id.btnIncrease);
        TextView btnDecrease = sheetView.findViewById(R.id.btnDecrease);
        Button btnConfirm = sheetView.findViewById(R.id.btnConfirm);

        tvItemName.setText(item.getName());
        tvQuantity.setText(String.valueOf(item.getQuantity()));

        btnIncrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(tvQuantity.getText().toString());
            tvQuantity.setText(String.valueOf(currentQuantity + 1));
        });

        btnDecrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(tvQuantity.getText().toString());
            if (currentQuantity > 1) {
                tvQuantity.setText(String.valueOf(currentQuantity - 1));
            }
        });

        btnConfirm.setOnClickListener(v -> {
            int newQuantity = Integer.parseInt(tvQuantity.getText().toString());
            item.setQuantity(newQuantity);
            holder.itemQuantity.setText(String.valueOf(newQuantity));
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }*/
