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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.groceries.AddGroceriesActivity;
import com.gfaim.activities.groceries.GroceryActivity;
import com.gfaim.activities.groceries.adapter.AddGroceriesAdapter;
import com.gfaim.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class AddGroceriesFragment extends Fragment {
    private List<FoodItem> foodList = new ArrayList<>();
    private List<FoodItem> filteredList = new ArrayList<>();
    private List<FoodItem> selectedItems = new ArrayList<>();

    private AddGroceriesAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        foodList.add(new FoodItem("Escalope de poulet", 1));
        foodList.add(new FoodItem("Crème fraîche", 2));
        foodList.add(new FoodItem("Pommes de terre", 3));
        foodList.add(new FoodItem("Tomates", 4));
        foodList.add(new FoodItem("Riz basmati", 5));
        foodList.add(new FoodItem("Pâtes complètes", 6));
        foodList.add(new FoodItem("Lait entier", 7));
        foodList.add(new FoodItem("Œufs", 8));
        foodList.add(new FoodItem("Fromage râpé", 9));
        foodList.add(new FoodItem("Jambon blanc", 10));
        foodList.add(new FoodItem("Saumon fumé", 11));
        foodList.add(new FoodItem("Beurre doux", 12));
        foodList.add(new FoodItem("Farine de blé",13));
        foodList.add(new FoodItem("Sucre en poudre",14));
        foodList.add(new FoodItem("Huile d'olive",15));
        foodList.add(new FoodItem("Pain de mie",16));
        foodList.add(new FoodItem("Yaourts nature",17));
        foodList.add(new FoodItem("Jus d'orange",18));
        foodList.add(new FoodItem("Chocolat noir",19));
        foodList.add(new FoodItem("Sel fin",20));
        foodList.add(new FoodItem("Poivre noir",21));
        foodList.add(new FoodItem("Poulet rôti",22));
        foodList.add(new FoodItem("Steak haché",23));
        foodList.add(new FoodItem("Haricots verts",24));
        foodList.add(new FoodItem("Carottes",25));
        foodList.add(new FoodItem("Oignons",26));
        foodList.add(new FoodItem("Courgettes",27));
        foodList.add(new FoodItem("Pommes",28));
        foodList.add(new FoodItem("Bananes",29));
        foodList.add(new FoodItem("Oranges",30));


        filteredList.addAll(foodList);

        adapter = new AddGroceriesAdapter(foodList, this);
        recyclerView.setAdapter(adapter);




        // Ajouter un séparateur
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider));
        recyclerView.addItemDecoration(divider);


        return view;
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
        adapter.updateList(filteredList);  // Met à jour la liste filtrée dans l'adaptateur
        adapter.notifyDataSetChanged();

    }

    public void clearSelectedItems() {
        selectedItems.clear();
    }

    public List<FoodItem> getSelectedItems() {
        return selectedItems;
    }

    public void toggleItemSelection(FoodItem item, boolean isChecked) {
        if (isChecked) {
            if (!selectedItems.contains(item)) {
                selectedItems.add(item);
            }
        } else {
            selectedItems.remove(item);
        }

        AddGroceriesActivity activity = (AddGroceriesActivity) getActivity();
        if (activity != null) {
            activity.toggleSendButtonVisibility(!selectedItems.isEmpty());
        }
    }

}

