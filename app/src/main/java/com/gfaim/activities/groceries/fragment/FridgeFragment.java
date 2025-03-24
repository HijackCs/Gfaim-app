package com.gfaim.activities.groceries.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.Profile;
import com.gfaim.R;
import com.gfaim.activities.groceries.adapter.FridgeAdapter;
import com.gfaim.activities.groceries.utility.RemovableFragment;
import com.gfaim.activities.groceries.utility.SwipeToDeleteCallback;
import com.gfaim.api.ApiClient;
import com.gfaim.api.ShoppingService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.FoodItem;
import com.gfaim.models.groceries.ShoppingItem;
import com.gfaim.models.groceries.ShoppingListResponse;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.callback.OnMemberReceivedListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FridgeFragment extends Fragment implements RemovableFragment {
    private List<FoodItem> foodList = new ArrayList<>();
    private List<FoodItem> filteredList = new ArrayList<>();

    private FridgeAdapter adapter;
    private TokenManager tokenManager;
    private MemberSessionBody member;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tokenManager = new TokenManager(getContext());

        UtileProfile utileProfile = new UtileProfile(getContext());

        utileProfile.getSessionMember(new OnMemberReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {
                // Empty
            }
            @Override
            public void onSuccess(MemberSessionBody session) {
                member = session;
                loadFridgeData();
            }
            @Override
            public void onFailure(Throwable error) {
                // Empty
            }
            @Override
            public void onSuccess(CreateMember body) {
                // Empty
            }
        });
        

        adapter = new FridgeAdapter(foodList);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider));
        recyclerView.addItemDecoration(divider);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(requireContext(), this));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private void loadFridgeData() {
        ShoppingService shoppingService = ApiClient.getClient(getContext()).create(ShoppingService.class);
        String token = "Bearer " + tokenManager.getAccessToken();
        Call<ShoppingListResponse> call = shoppingService.getFridgeList(token, member.getFamilyId());

        call.enqueue(new Callback<ShoppingListResponse>() {
            @Override
            public void onResponse(Call<ShoppingListResponse> call, Response<ShoppingListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ShoppingListResponse fridgeResponse = response.body();
                    List<ShoppingItem> items = fridgeResponse.getItems();

                    foodList.clear();

                    for (ShoppingItem item : items) {
                        FoodItem foodItem = new FoodItem(
                                item.getIngredientNameFr(),
                                item.getIngredientNameEn(),
                                item.getIngredientName(),
                                item.getIngredientCatalogId()
                        );
                        foodList.add(foodItem);
                    }

                    filteredList.clear();
                    filteredList.addAll(foodList);

                    adapter.updateList(foodList);
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getContext(), R.string.errorFetching + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShoppingListResponse> call, Throwable t) {
                Toast.makeText(getContext(), R.string.impossibleFetch + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addItemsToFridge(List<FoodItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        foodList.addAll(items);
        filteredList.addAll(items);
        adapter.notifyDataSetChanged();
    }

    public void updateFridgeList(List<ShoppingItem> items) {
        if (items == null) {
            return;
        }

        foodList.clear();

        for (ShoppingItem item : items) {
            FoodItem foodItem = new FoodItem(
                    item.getIngredientNameFr(),
                    item.getIngredientNameEn(),
                    item.getIngredientName(),
                    item.getIngredientCatalogId()
            );
            foodList.add(foodItem);
        }

        filteredList.clear();
        filteredList.addAll(foodList);

        adapter.updateList(filteredList);
        adapter.notifyDataSetChanged();
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
        if(!filteredList.isEmpty()){
            adapter.updateList(filteredList);
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void removeItem(FoodItem item) {
        ShoppingService shoppingService = ApiClient.getClient(getContext()).create(ShoppingService.class);
        String token = "Bearer " + tokenManager.getAccessToken();

        Call<Void> call = shoppingService.removeIngredientFromFridge(token, member.getFamilyId(), item.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    filteredList.remove(item);
                    foodList.remove(item);
                    adapter.updateList(filteredList);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), R.string.errorDeleting + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), R.string.errorFetching + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addItem(FoodItem item) {
        foodList.add(item);
        filteredList.add(item);
        adapter.notifyDataSetChanged();
    }

    public int getNextId() {
        return foodList.size() + 1;
    }

    @Override
    public FoodItem getItemAtPosition(int position) {
        return filteredList.get(position);
    }

}

