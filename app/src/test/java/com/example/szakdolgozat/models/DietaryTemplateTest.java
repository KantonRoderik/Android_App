package com.example.szakdolgozat.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class DietaryTemplateTest {

    @Test
    public void calculateGoals_Balanced_ReturnsCorrectValues() {
        // Given
        DietaryTemplate template = DietaryTemplate.BALANCED;
        double calories = 2000.0;
        double water = 2500.0;

        // Balanced: 25% Protein, 50% Carbs, 25% Fat
        // Protein: (2000 * 0.25) / 4 = 125g
        // Carbs: (2000 * 0.50) / 4 = 250g
        // Fat: (2000 * 0.25) / 9 = 55.55g

        // When
        DailyGoals goals = template.calculateGoals(calories, water);

        // Then
        assertNotNull(goals);
        assertEquals(2000.0, goals.getCalories(), 0.01);
        assertEquals(125.0, goals.getProtein(), 0.01);
        assertEquals(250.0, goals.getCarbs(), 0.01);
        assertEquals(55.55, goals.getFat(), 0.01);
        assertEquals(2500.0, goals.getWater(), 0.01);
    }

    @Test
    public void calculateGoals_Ketogenic_ReturnsCorrectValues() {
        // Given
        DietaryTemplate template = DietaryTemplate.KETOGENIC;
        double calories = 2000.0;
        double water = 3000.0;

        // Ketogenic: 25% Protein, 5% Carbs, 70% Fat
        // Protein: (2000 * 0.25) / 4 = 125g
        // Carbs: (2000 * 0.05) / 4 = 25g
        // Fat: (2000 * 0.70) / 9 = 155.55g

        // When
        DailyGoals goals = template.calculateGoals(calories, water);

        // Then
        assertNotNull(goals);
        assertEquals(2000.0, goals.getCalories(), 0.01);
        assertEquals(125.0, goals.getProtein(), 0.01);
        assertEquals(25.0, goals.getCarbs(), 0.01);
        assertEquals(155.55, goals.getFat(), 0.01);
        assertEquals(3000.0, goals.getWater(), 0.01);
    }
}
