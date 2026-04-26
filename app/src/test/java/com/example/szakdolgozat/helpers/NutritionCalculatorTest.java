package com.example.szakdolgozat.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.szakdolgozat.models.DailyGoals;

import org.junit.Test;

public class NutritionCalculatorTest {

    @Test
    public void calculateDailyGoals_Male_ReturnsCorrectValues() {
        // Given
        double weight = 80.0;
        double height = 180.0;
        int age = 30;
        NutritionCalculator.Gender gender = NutritionCalculator.Gender.MALE;

        // BMR = (10 * 80) + (6.25 * 180) - (5 * 30) + 5 = 1780
        // TDEE = 1780 * 1.2 = 2136
        // Protein = 80 * 1.8 = 144g
        // Fat = 80 * 0.9 = 72g
        // Carbs grams = 228g
        // Water = 80 * 35 = 2800ml

        // When
        DailyGoals goals = NutritionCalculator.calculateDailyGoals(weight, height, age, gender);

        // Then
        assertNotNull(goals);
        assertEquals(2136.0, goals.getCalories(), 0.1);
        assertEquals(144.0, goals.getProtein(), 0.1);
        assertEquals(72.0, goals.getFat(), 0.1);
        assertEquals(228.0, goals.getCarbs(), 0.1);
        assertEquals(2800.0, goals.getWater(), 0.1);
    }

    @Test
    public void calculateDailyGoals_Female_ReturnsCorrectValues() {
        // Given
        double weight = 60.0;
        double height = 165.0;
        int age = 25;
        NutritionCalculator.Gender gender = NutritionCalculator.Gender.FEMALE;

        // BMR = (10 * 60) + (6.25 * 165) - (5 * 25) - 161 = 1345.25
        // TDEE = 1345.25 * 1.2 = 1614.3

        // When
        DailyGoals goals = NutritionCalculator.calculateDailyGoals(weight, height, age, gender);

        // Then
        assertNotNull(goals);
        assertEquals(1614.3, goals.getCalories(), 0.1);
        assertEquals(108.0, goals.getProtein(), 0.1);
        assertEquals(54.0, goals.getFat(), 0.1);
        assertEquals(174.075, goals.getCarbs(), 0.1);
        assertEquals(2100.0, goals.getWater(), 0.1);
    }

    @Test
    public void calculateBMR_Male_CorrectValues() {
        double bmr = NutritionCalculator.calculateBMR(80, 180, 30, NutritionCalculator.Gender.MALE);
        assertEquals(1780.0, bmr, 0.01);
    }

    @Test
    public void calculateBMR_Female_CorrectValues() {
        double bmr = NutritionCalculator.calculateBMR(60, 165, 25, NutritionCalculator.Gender.FEMALE);
        assertEquals(1345.25, bmr, 0.01);
    }

    @Test
    public void calculateBMR_InvalidInput_ReturnsZero() {
        assertEquals(0.0, NutritionCalculator.calculateBMR(0, 180, 30, NutritionCalculator.Gender.MALE), 0.01);
        assertEquals(0.0, NutritionCalculator.calculateBMR(80, -1, 30, NutritionCalculator.Gender.MALE), 0.01);
        assertEquals(0.0, NutritionCalculator.calculateBMR(80, 180, 0, NutritionCalculator.Gender.MALE), 0.01);
    }

    @Test
    public void parseGender_HandlesVariousInputs() {
        assertEquals(NutritionCalculator.Gender.FEMALE, NutritionCalculator.parseGender("female"));
        assertEquals(NutritionCalculator.Gender.FEMALE, NutritionCalculator.parseGender("nő"));
        assertEquals(NutritionCalculator.Gender.FEMALE, NutritionCalculator.parseGender("no"));
        assertEquals(NutritionCalculator.Gender.MALE, NutritionCalculator.parseGender("male"));
        assertEquals(NutritionCalculator.Gender.MALE, NutritionCalculator.parseGender("férfi"));
        assertEquals(NutritionCalculator.Gender.MALE, NutritionCalculator.parseGender(null));
        assertEquals(NutritionCalculator.Gender.MALE, NutritionCalculator.parseGender("unknown"));
        assertEquals(NutritionCalculator.Gender.FEMALE, NutritionCalculator.parseGender("  FEMALE  "));
    }
}
