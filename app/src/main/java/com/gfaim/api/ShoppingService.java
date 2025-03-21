package com.gfaim.api;

import com.gfaim.models.groceries.AddShoppingItemRequest;
import com.gfaim.models.groceries.ShoppingItem;
import com.gfaim.models.groceries.ShoppingItemResponse;
import com.gfaim.models.groceries.ShoppingListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ShoppingService {
    @GET("families/{familyId}/shopping")
    Call<ShoppingListResponse> getShoppingList(
            @Header("Authorization") String token,
            @Path("familyId") Long familyId
    );

    @GET("families/{familyId}/fridge")
    Call<ShoppingListResponse> getFridgeList(
            @Header("Authorization") String token,
            @Path("familyId") Long familyId
    );

    @POST("families/{familyId}/shopping/ingredients")
    Call<List<ShoppingListResponse>> addIngredientsToShoppingList(
            @Header("Authorization") String token,
            @Path("familyId") Long familyId,
            @Body List<AddShoppingItemRequest> ingredients
    );

    @POST("families/{familyId}/fridge/ingredients")
    Call<List<ShoppingListResponse>> addIngredientsToFridge(
            @Header("Authorization") String token,
            @Path("familyId") Long familyId,
            @Body List<AddShoppingItemRequest> ingredients
    );

    @POST("families/{familyId}/fridge/ingredients")
    Call<Object> addIngredientsToFridgeFlexible(
            @Header("Authorization") String token,
            @Path("familyId") Long familyId,
            @Body List<AddShoppingItemRequest> ingredients
    );

    @POST("families/{familyId}/shopping/bought")
    Call<Void> markIngredientsAsBought(
            @Header("Authorization") String token,
            @Path("familyId") Long familyId,
            @Body List<AddShoppingItemRequest> ingredients
    );

    @DELETE("families/{familyId}/shopping/ingredients/{ingredientCatalogId}")
    Call<Void> removeIngredientFromShoppingList(
            @Header("Authorization") String token,
            @Path("familyId") Long familyId,
            @Path("ingredientCatalogId") Long ingredientCatalogId
    );

    @DELETE("families/{familyId}/fridge/ingredients/{ingredientCatalogId}")
    Call<Void> removeIngredientFromFridge(
            @Header("Authorization") String token,
            @Path("familyId") Long familyId,
            @Path("ingredientCatalogId") Long ingredientCatalogId
    );
}
