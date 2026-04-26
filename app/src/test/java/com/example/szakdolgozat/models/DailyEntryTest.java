package com.example.szakdolgozat.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DailyEntryTest {

    @Test
    public void calculateTotals_AggregatesFoodNutritionCorrectly() {
        // Given
        DailyEntry entry = new DailyEntry("2024-12-24");
        
        FoodItem apple = new FoodItem("Apple", 52, 14, 0.3, 0.2);
        apple.setId("apple_id");
        ConsumedFood consumedApple = new ConsumedFood(apple, 150); // 1.5 * 52 = 78 cal
        
        FoodItem bread = new FoodItem("Bread", 265, 49, 9, 3.2);
        bread.setId("bread_id");
        ConsumedFood consumedBread = new ConsumedFood(bread, 100); // 265 cal

        Map<String, ConsumedFood> foods = new HashMap<>();
        foods.put("item1", consumedApple);
        foods.put("item2", consumedBread);
        
        entry.setConsumedFoods(foods);

        // When
        entry.calculateTotals();

        // Then
        // Totals:
        // Cals: (52 * 1.5) + 265 = 78 + 265 = 343
        // Carbs: (14 * 1.5) + 49 = 21 + 49 = 70
        // Protein: (0.3 * 1.5) + 9 = 0.45 + 9 = 9.45
        // Fat: (0.2 * 1.5) + 3.2 = 0.3 + 3.2 = 3.5

        assertEquals(343.0, entry.getTotalCalories(), 0.01);
        assertEquals(70.0, entry.getTotalCarbs(), 0.01);
        assertEquals(9.45, entry.getTotalProtein(), 0.01);
        assertEquals(3.5, entry.getTotalFat(), 0.01);
    }

    @Test
    public void calculateTotals_WithEmptyMap_SetsTotalsToZero() {
        // Given
        DailyEntry entry = new DailyEntry("2024-12-24");
        entry.setTotalCalories(100);
        entry.setConsumedFoods(new HashMap<>());

        // When
        entry.calculateTotals();

        // Then
        assertEquals(0.0, entry.getTotalCalories(), 0.01);
        assertEquals(0.0, entry.getTotalCarbs(), 0.01);
    }
}
