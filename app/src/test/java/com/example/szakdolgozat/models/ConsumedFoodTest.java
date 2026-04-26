package com.example.szakdolgozat.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConsumedFoodTest {

    @Test
    public void constructor_CalculatesNutritionCorrectly() {
        // Given
        FoodItem food = new FoodItem("Apple", 52, 14, 0.3, 0.2);
        food.setId("apple_id");
        double quantity = 200.0; // 200g

        // When
        ConsumedFood consumed = new ConsumedFood(food, quantity);

        // Then
        assertEquals("apple_id", consumed.getFoodId());
        assertEquals("Apple", consumed.getFoodName());
        assertEquals(200.0, consumed.getQuantity(), 0.01);
        assertEquals(104.0, consumed.getCalories(), 0.01); // 52 * 2
        assertEquals(28.0, consumed.getCarbs(), 0.01);   // 14 * 2
        assertEquals(0.4, consumed.getFat(), 0.01);      // 0.2 * 2
        assertEquals(0.6, consumed.getProtein(), 0.01);  // 0.3 * 2
    }
}
