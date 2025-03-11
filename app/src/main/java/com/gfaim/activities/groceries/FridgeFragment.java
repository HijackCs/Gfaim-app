package com.gfaim.activities.groceries;

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
import com.gfaim.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class FridgeFragment extends Fragment {
    private List<FoodItem> foodList = new ArrayList<>();
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

        return view;
    }

    public void addItemsToFridge(List<FoodItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        foodList.addAll(items);
        adapter.notifyDataSetChanged();
    }
}

