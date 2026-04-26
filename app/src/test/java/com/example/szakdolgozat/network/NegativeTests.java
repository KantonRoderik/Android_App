package com.example.szakdolgozat.network;

import static org.junit.Assert.assertNull;

import com.example.szakdolgozat.models.FoodItem;

import org.junit.Test;

public class NegativeTests {

    @Test
    public void fromAiJson_NonJsonResponse_ReturnsNull() {
        // Scenario: AI returns a conversational sentence instead of JSON
        String aiResponse = "Sure, I can help. An apple has about 52 calories per 100g.";
        FoodItem item = FoodItem.fromAiJson(aiResponse);
        assertNull("Should return null for non-JSON input", item);
    }

    @Test
    public void fromAiJson_PartialJsonResponse_ReturnsNull() {
        // Scenario: AI response is cut off (corrupted JSON)
        String aiResponse = "{\"name\": \"Apple\", \"calories\": 5";
        FoodItem item = FoodItem.fromAiJson(aiResponse);
        assertNull("Should return null for partial/invalid JSON", item);
    }

    @Test
    public void fromAiJson_EmptyResponse_ReturnsNull() {
        // Scenario: AI returns empty string
        FoodItem item = FoodItem.fromAiJson("");
        assertNull(item);
    }
}
