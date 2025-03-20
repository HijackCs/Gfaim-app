package com.gfaim.api;

import com.gfaim.models.IngredientCatalogItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface IngredientCatalogService {
    @GET("ingredient-catalog")
    Call<List<IngredientCatalogItem>> getIngredientCatalog(
            @Header("Authorization") String token
    );
}
