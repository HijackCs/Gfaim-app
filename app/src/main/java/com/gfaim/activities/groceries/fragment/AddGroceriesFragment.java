package com.gfaim.activities.groceries.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.groceries.AddGroceriesActivity;
import com.gfaim.activities.groceries.adapter.AddGroceriesAdapter;
import com.gfaim.api.ApiClient;
import com.gfaim.api.IngredientCatalogService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.FoodItem;
import com.gfaim.models.IngredientCatalogItem;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroceriesFragment extends Fragment {
    private static final String TAG = "AddGroceriesFragment";

    private List<FoodItem> foodList = new ArrayList<>();
    private List<FoodItem> filteredList = new ArrayList<>();
    @Getter
    private List<FoodItem> selectedItems = new ArrayList<>();

    private AddGroceriesAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        progressBar = view.findViewById(R.id.progressBar);
        if (progressBar == null) {
            progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleLarge);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            ((ViewGroup) recyclerView.getParent()).addView(progressBar, layoutParams);
            progressBar.setVisibility(View.GONE);
        }

        emptyTextView = view.findViewById(R.id.emptyTextView);
        if (emptyTextView == null) {
            emptyTextView = new TextView(getContext());
            emptyTextView.setText(R.string.noIngredient);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            ((ViewGroup) recyclerView.getParent()).addView(emptyTextView, layoutParams);
            emptyTextView.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AddGroceriesAdapter(foodList, this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider));
        recyclerView.addItemDecoration(divider);

        loadIngredientsFromAPI();

        return view;
    }

    private void loadIngredientsFromAPI() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        if (emptyTextView != null) emptyTextView.setVisibility(View.GONE);

        TokenManager tokenManager = new TokenManager(requireContext());
        IngredientCatalogService service = ApiClient.getClient(requireContext()).create(IngredientCatalogService.class);

        String token = "Bearer " + tokenManager.getAccessToken();
        Call<List<IngredientCatalogItem>> call = service.getIngredientCatalog(token);

        Log.d(TAG, "Loading ingredients from api.");

        call.enqueue(new Callback<List<IngredientCatalogItem>>() {
            @Override
            public void onResponse(Call<List<IngredientCatalogItem>> call, Response<List<IngredientCatalogItem>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<IngredientCatalogItem> catalogItems = response.body();

                    foodList.clear();
                    for (IngredientCatalogItem item : catalogItems) {
                        FoodItem foodItem = new FoodItem(
                                item.getNameFr(),
                                item.getNameEn(),
                                item.getName(),
                                item.getId()
                        );
                        foodList.add(foodItem);
                    }

                    filteredList.clear();
                    filteredList.addAll(foodList);

                    adapter.updateList(filteredList);

                    if (foodList.isEmpty()) {
                        if (emptyTextView != null) emptyTextView.setVisibility(View.VISIBLE);
                        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
                    } else {
                        if (emptyTextView != null) emptyTextView.setVisibility(View.GONE);
                        if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
                    }

                    Log.i(TAG, "Number of ingredients: " + foodList.size());

                } else {
                    String errorCode = String.valueOf(response.code());
                    Log.e(TAG, R.string.errorFetching + errorCode);
                    Toast.makeText(getContext(), R.string.errorFetching + errorCode, Toast.LENGTH_SHORT).show();

                    if (emptyTextView != null) {
                        emptyTextView.setText(R.string.errorFetching + errorCode);
                        emptyTextView.setVisibility(View.VISIBLE);
                    }
                    if (recyclerView != null) recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<IngredientCatalogItem>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                String errorMessage = t.getMessage();
                Log.e(TAG, "error loading ingredients: " + errorMessage, t);
                Toast.makeText(getContext(), R.string.errorConnection + errorMessage, Toast.LENGTH_SHORT).show();

                if (emptyTextView != null) {
                    emptyTextView.setText(R.string.impossibleConnection);
                    emptyTextView.setVisibility(View.VISIBLE);
                }
                if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            }
        });
    }

    public void filterItems(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(foodList);
        } else {
            for (FoodItem item : foodList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.updateList(filteredList);
        adapter.notifyDataSetChanged();
    }

    public void clearSelectedItems() {
        selectedItems.clear();
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

