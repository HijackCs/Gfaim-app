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

import com.gfaim.R;
import com.gfaim.activities.groceries.GroceryActivity;
import com.gfaim.activities.groceries.utility.RemovableFragment;
import com.gfaim.activities.groceries.adapter.ShoppingAdapter;
import com.gfaim.activities.groceries.utility.SwipeToDeleteCallback;
import com.gfaim.api.ApiClient;
import com.gfaim.api.FamilyService;
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

public class ShoppingFragment extends Fragment implements RemovableFragment {
    private List<FoodItem> shoppingList = new ArrayList<>();
    private List<FoodItem> filteredList = new ArrayList<>();
    private List<FoodItem> selectedItems = new ArrayList<>();
    private ShoppingAdapter adapter;
    private MemberSessionBody member;
    private TokenManager tokenManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        UtileProfile utileProfile = new UtileProfile(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tokenManager = new TokenManager(getContext());
        ShoppingService shoppingService = ApiClient.getClient(getContext()).create(ShoppingService.class);

        String token = "Bearer " + tokenManager.getAccessToken();

        utileProfile.getSessionMember(new OnMemberReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {
                //empty
            }
            @Override
            public void onSuccess(MemberSessionBody session) {
                member = session;
                Call<ShoppingListResponse> call = shoppingService.getShoppingList(token, member.getFamilyId());

                call.enqueue(new Callback<ShoppingListResponse>() {
                    @Override
                    public void onResponse(Call<ShoppingListResponse> call, Response<ShoppingListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ShoppingListResponse shoppingListResponse = response.body();
                            List<ShoppingItem> items = shoppingListResponse.getItems();

                            shoppingList.clear();

                            for (ShoppingItem item : items) {
                                FoodItem foodItem = new FoodItem(
                                        item.getIngredientNameFr(),
                                        item.getIngredientNameEn(),
                                        item.getIngredientName(),
                                        item.getIngredientCatalogId()
                                );
                                shoppingList.add(foodItem);
                            }

                            filteredList.clear();
                            filteredList.addAll(shoppingList);
                            adapter.notifyDataSetChanged();

                            Toast.makeText(getContext(), "Liste récupérée avec succès", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Erreur lors de la récupération", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ShoppingListResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(Throwable error) {
                //Empty
            }
            @Override
            public void onSuccess(CreateMember body) {
                //Empty
            }
        });

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
        ShoppingService shoppingService = ApiClient.getClient(getContext()).create(ShoppingService.class);
        String token = "Bearer " + tokenManager.getAccessToken();

        Call<Void> call = shoppingService.removeIngredientFromShoppingList(token, member.getFamilyId(), item.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    filteredList.remove(item);
                    shoppingList.remove(item);
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

    public void updateShoppingList(List<ShoppingItem> items) {
        if (items == null) {
            return;
        }

        shoppingList.clear();

        for (ShoppingItem item : items) {
            FoodItem foodItem = new FoodItem(
                    item.getIngredientNameFr(),
                    item.getIngredientNameEn(),
                    item.getIngredientName(),
                    item.getIngredientCatalogId()
            );
            shoppingList.add(foodItem);
        }

        filteredList.clear();
        filteredList.addAll(shoppingList);

        adapter.updateList(filteredList);
        adapter.notifyDataSetChanged();
    }

}