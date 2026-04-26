package com.example.szakdolgozat.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FoodItemTest {

    @Test
    public void fromAiJson_ValidJson_ReturnsFoodItem() {
        String json = "{\"name\": \"Banana\", \"calories\": 89, \"carbs\": 23, \"protein\": 1.1, \"fat\": 0.3}";
        FoodItem item = FoodItem.fromAiJson(json);
        
        assertNotNull(item);
        assertEquals("Banana", item.getName());
        assertEquals(89.0, item.getCalories(), 0.01);
        assertTrue(item.isAiGenerated());
    }

    @Test
    public void fromAiJson_JsonWithMarkdown_ReturnsFoodItem() {
        String json = "```json\n{\"name\": \"Apple\", \"calories\": 52}\n```";
        FoodItem item = FoodItem.fromAiJson(json);
        
        assertNotNull(item);
        assertEquals("Apple", item.getName());
        assertEquals(52.0, item.getCalories(), 0.01);
    }

    @Test
    public void fromAiJson_InvalidJson_ReturnsNull() {
        String json = "{invalid}";
        FoodItem item = FoodItem.fromAiJson(json);
        assertNull(item);
    }

    @Test
    public void fromAiJson_NullOrEmpty_ReturnsNull() {
        assertNull(FoodItem.fromAiJson(null));
        assertNull(FoodItem.fromAiJson(""));
    }
}
