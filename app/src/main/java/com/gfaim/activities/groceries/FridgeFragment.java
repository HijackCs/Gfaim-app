package com.gfaim.activities.groceries;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class FridgeFragment extends Fragment {
    private RecyclerView recyclerView;
    private FridgeAdapter adapter;
    private List<FoodItem> foodList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        foodList = new ArrayList<>();
        foodList.add(new FoodItem("Escalope de poule", 1, 2));

        adapter = new FridgeAdapter(foodList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
