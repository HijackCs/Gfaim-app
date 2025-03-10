package com.gfaim.activities.groceries;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.groceries.ShoppingAdapter;
import com.gfaim.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingFragment extends Fragment {
    private RecyclerView recyclerView;
    private ShoppingAdapter adapter;
    private List<FoodItem> shoppingList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        shoppingList = new ArrayList<>();
        shoppingList.add(new FoodItem("Escalope de poulet",  1, 2));
        shoppingList.add(new FoodItem("Crème fraîche",  1, 1));

        adapter = new ShoppingAdapter(shoppingList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
