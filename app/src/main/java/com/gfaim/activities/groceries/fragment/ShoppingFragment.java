package com.gfaim.activities.groceries.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.groceries.GroceryActivity;
import com.gfaim.activities.groceries.utility.RemovableFragment;
import com.gfaim.activities.groceries.adapter.ShoppingAdapter;
import com.gfaim.activities.groceries.utility.SwipeToDeleteCallback;
import com.gfaim.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingFragment extends Fragment implements RemovableFragment {
    private RecyclerView recyclerView;
    private List<FoodItem> shoppingList = new ArrayList<>();
    private List<FoodItem> filteredList = new ArrayList<>();
    private List<FoodItem> selectedItems = new ArrayList<>();
    private ShoppingAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //a supp
        shoppingList.add(new FoodItem("Escalope de poulet", 1));
        shoppingList.add(new FoodItem("Crème fraîche", 2));
        shoppingList.add(new FoodItem("Pommes de terre", 3));
        shoppingList.add(new FoodItem("Tomates", 4));
        shoppingList.add(new FoodItem("Riz basmati", 5));
        shoppingList.add(new FoodItem("Pâtes complètes", 6));
        shoppingList.add(new FoodItem("Lait entier", 7));
        shoppingList.add(new FoodItem("Œufs", 8));
        shoppingList.add(new FoodItem("Fromage râpé", 9));
        shoppingList.add(new FoodItem("Jambon blanc", 10));
        shoppingList.add(new FoodItem("Saumon fumé", 11));
        shoppingList.add(new FoodItem("Beurre doux", 12));
        shoppingList.add(new FoodItem("Farine de blé",13));
        shoppingList.add(new FoodItem("Sucre en poudre",14));
        shoppingList.add(new FoodItem("Huile d'olive",15));
        shoppingList.add(new FoodItem("Pain de mie",16));
        shoppingList.add(new FoodItem("Yaourts nature",17));
        shoppingList.add(new FoodItem("Jus d'orange",18));
        shoppingList.add(new FoodItem("Chocolat noir",19));
        shoppingList.add(new FoodItem("Sel fin",20));
        shoppingList.add(new FoodItem("Poivre noir",21));
        shoppingList.add(new FoodItem("Poulet rôti",22));
        shoppingList.add(new FoodItem("Steak haché",23));
        shoppingList.add(new FoodItem("Haricots verts",24));
        shoppingList.add(new FoodItem("Carottes",25));
        shoppingList.add(new FoodItem("Oignons",26));
        shoppingList.add(new FoodItem("Courgettes",27));
        shoppingList.add(new FoodItem("Pommes",28));
        shoppingList.add(new FoodItem("Bananes",29));
        shoppingList.add(new FoodItem("Oranges",30));

        filteredList.addAll(shoppingList);


        adapter = new ShoppingAdapter(shoppingList, this);

        recyclerView.setAdapter(adapter);

        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider));
        recyclerView.addItemDecoration(divider);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(requireContext(), this));
        itemTouchHelper.attachToRecyclerView(recyclerView);



        return view;
    }

    public void filterItems(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(shoppingList);
        } else {
            for (FoodItem item : shoppingList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.updateList(filteredList);
        adapter.notifyDataSetChanged();

    }
    public List<FoodItem> getSelectedItems() {
        return selectedItems;
    }

    @Override
    public void removeItem(FoodItem item) {
        filteredList.remove(item);

        shoppingList.remove(item);

        adapter.updateList(filteredList);
        adapter.notifyDataSetChanged();
    }


    public void clearSelectedItems() {
        selectedItems.clear();
    }


    @Override
    public FoodItem getItemAtPosition(int position) {
        return filteredList.get(position);
    }

    public void toggleItemSelection(FoodItem item, boolean isChecked) {
        if (isChecked) {
            if (!selectedItems.contains(item)) {
                selectedItems.add(item);
            }
        } else {
            selectedItems.remove(item);
        }

        GroceryActivity activity = (GroceryActivity) getActivity();
        if (activity != null) {
            activity.toggleSendButtonVisibility(!selectedItems.isEmpty());
        }
    }

    public void addItemsToShopping(List<FoodItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        shoppingList.addAll(items);
        filteredList.addAll(items);
        adapter.notifyDataSetChanged();
    }

}