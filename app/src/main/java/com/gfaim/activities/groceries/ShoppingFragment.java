package com.gfaim.activities.groceries;

import android.os.Bundle;
import android.util.Log;
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
import com.gfaim.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<FoodItem> shoppingList = new ArrayList<>();
    private List<FoodItem> selectedItems = new ArrayList<>();
    private List<FoodItem> filteredList = new ArrayList<>();

    private ShoppingAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        shoppingList.add(new FoodItem("Escalope de poulet", 1, 2));
        shoppingList.add(new FoodItem("Crème fraîche", 1, 1));
        shoppingList.add(new FoodItem("Pommes de terre", 2, 1));
        shoppingList.add(new FoodItem("Tomates", 1, 1));

        adapter = new ShoppingAdapter(shoppingList, this);

        recyclerView.setAdapter(adapter);

        // Ajouter un séparateur
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider));
        recyclerView.addItemDecoration(divider);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(requireContext(), adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        filteredList.addAll(shoppingList);


        return view;
    }

    public void filterItems(String query) {
        Log.d("ShoppingFragment", "Filtrage avec la recherche : " + query);  // Ajout d'un log

        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(shoppingList); // Afficher tous les éléments si la recherche est vide
        } else {
            for (FoodItem item : shoppingList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        Log.d("ShoppingFragment", "Liste filtrée : " + filteredList.size());  // Affiche la taille de la liste filtrée

        adapter.updateList(filteredList);  // Met à jour la liste filtrée dans l'adaptateur
    }


    public List<FoodItem> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public void removeSelectedItems(List<FoodItem> selectedItems) {
        shoppingList.removeAll(selectedItems);
        adapter.notifyDataSetChanged();
    }

    public void toggleItemSelection(FoodItem item, boolean isChecked) {
        if (isChecked) {
            selectedItems.add(item);
        } else {
            selectedItems.remove(item);
        }

        GroceryActivity activity = (GroceryActivity) getActivity();
        if (activity != null) {
            activity.toggleSendButtonVisibility(!selectedItems.isEmpty());
        }
    }
}