package com.gfaim.api;

import com.gfaim.models.IngredientCatalogItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IngredientCatalogService {
    @GET("/ingredient-catalog")
    Call<List<IngredientCatalogItem>> getIngredientCatalog();
}
