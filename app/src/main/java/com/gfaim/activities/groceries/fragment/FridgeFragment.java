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
import com.gfaim.activities.groceries.adapter.FridgeAdapter;
import com.gfaim.activities.groceries.utility.RemovableFragment;
import com.gfaim.activities.groceries.utility.SwipeToDeleteCallback;
import com.gfaim.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class FridgeFragment extends Fragment implements RemovableFragment {
    private List<FoodItem> foodList = new ArrayList<>();
    private List<FoodItem> filteredList = new ArrayList<>();

    private FridgeAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FridgeAdapter(foodList);
        recyclerView.setAdapter(adapter);

        // Ajouter un séparateur
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider));
        recyclerView.addItemDecoration(divider);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(requireContext(), this));
        itemTouchHelper.attachToRecyclerView(recyclerView);


        return view;
    }

    public void addItemsToFridge(List<FoodItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        foodList.addAll(items);
        filteredList.addAll(foodList);
        adapter.notifyDataSetChanged();
    }


    public void filterItems(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(foodList); // Afficher tous les éléments si la recherche est vide
        } else {
            for (FoodItem item : foodList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        if(!filteredList.isEmpty()){
            adapter.updateList(filteredList);  // Met à jour la liste filtrée dans l'adaptateur
            adapter.notifyDataSetChanged();

        }

    }


    @Override
    public void removeItem(FoodItem item) {
        filteredList.remove(item);

        foodList.remove(item);

        adapter.updateList(filteredList);
        adapter.notifyDataSetChanged();
    }

    public void addItem(FoodItem item) {
        foodList.add(item);
        filteredList.add(item);
        adapter.notifyDataSetChanged();
    }

    public int getNextId() {
        return foodList.size() + 1; // Génère un nouvel ID unique
    }

    @Override
    public FoodItem getItemAtPosition(int position) {
        return filteredList.get(position);
    }

}

