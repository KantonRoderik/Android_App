package com.example.szakdolgozat.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.szakdolgozat.models.FoodItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkIntegrationTest {

    private MockWebServer mockWebServer;
    private OpenFoodFactsApi api;

    @Before
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        api = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenFoodFactsApi.class);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void getProduct_ValidResponse_ParsesCorrectly() throws IOException {
        // Given
        String jsonResponse = "{\n" +
                "  \"status\": 1,\n" +
                "  \"product\": {\n" +
                "    \"product_name\": \"Test Apple\",\n" +
                "    \"nutriments\": {\n" +
                "      \"energy-kcal_100g\": 52,\n" +
                "      \"carbohydrates_100g\": 14,\n" +
                "      \"proteins_100g\": 0.3,\n" +
                "      \"fat_100g\": 0.2\n" +
                "    }\n" +
                "  }\n" +
                "}";
        
        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .setResponseCode(200));

        // When
        Response<OpenFoodFactsApi.ProductResponse> response = api.getProduct("123456").execute();

        // Then
        assertNotNull(response.body());
        assertEquals(1, response.body().status);
        assertEquals("Test Apple", response.body().product.productName);
        assertEquals(52.0, response.body().product.nutriments.calories, 0.01);
    }
}
