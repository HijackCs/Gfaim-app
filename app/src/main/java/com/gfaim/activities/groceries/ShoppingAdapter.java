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

import java.util.ArrayList;
import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ViewHolder> {
    private List<FoodItem> shoppingList;
    private List<FoodItem> selectedItems = new ArrayList<>();
    private List<FoodItem> itemList;

    private ShoppingFragment fragment;

    public ShoppingAdapter(List<FoodItem> shoppingList, ShoppingFragment fragment) {
        this.shoppingList = shoppingList;
        this.itemList = shoppingList;
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

        holder.itemCheckbox.setChecked(selectedItems.contains(item));

        holder.itemCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.add(item); // Ajouter l'élément
            } else {
                selectedItems.remove(item); // Retirer l'élément
            }

            fragment.toggleItemSelection(item, isChecked);
        });

       // holder.itemView.setOnClickListener(v -> showQuantityBottomSheet(holder, item));
    }
    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        ImageView itemDeleteButton;
        CheckBox itemCheckbox;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemDeleteButton = itemView.findViewById(R.id.itemDeleteButton);
            itemCheckbox = itemView.findViewById(R.id.itemCheckbox);
        }
    }

    public void removeItem(int position) {
        shoppingList.remove(position);
        notifyItemRemoved(position);
    }

    public void updateList(List<FoodItem> newList) {
        shoppingList = newList;  // Modifiez shoppingList directement
        notifyDataSetChanged();  // Notifiez que la liste a changé
    }



}


/*private void showQuantityBottomSheet(ShoppingAdapter.ViewHolder holder, FoodItem item) {
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