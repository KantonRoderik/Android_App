package com.example.szakdolgozat.network;

import com.google.gson.annotations.SerializedName;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OpenFoodFactsApi {
    @GET("api/v0/product/{barcode}.json")
    Call<ProductResponse> getProduct(@Path("barcode") String barcode);

    class ProductResponse {
        @SerializedName("status")
        public int status;
        @SerializedName("product")
        public Product product;
    }

    class Product {
        @SerializedName("product_name")
        public String productName;
        @SerializedName("nutriments")
        public Nutriments nutriments;
    }

    class Nutriments {
        @SerializedName("energy-kcal_100g")
        public double calories;
        @SerializedName("carbohydrates_100g")
        public double carbs;
        @SerializedName("fat_100g")
        public double fat;
        @SerializedName("proteins_100g")
        public double protein;
    }
}
