package com.example.szakdolgozat.helpers;

import com.example.szakdolgozat.models.DailyGoals;

public class NutritionCalculator {

    public enum Gender {
        MALE, FEMALE
    }

    public static DailyGoals calculateDailyGoals(double weightKg, double heightCm, int age, Gender gender) {
        // 1. BMR Calculation
        double bmr;
        if (gender == Gender.MALE) {
            bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5;
        } else {
            bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161;
        }

        // 2. Daily Calorie Goal (TDEE: BMR * 1.2 base maintenance)
        double totalCalories = bmr * 1.2;

        // 3. Macronutrient Distribution
        double proteinGrams = weightKg * 1.8;
        double fatGrams = weightKg * 0.9;
        
        // Carbohydrates: The remainder
        double proteinCalories = proteinGrams * 4.0;
        double fatCalories = fatGrams * 9.0;
        double carbsGrams = (totalCalories - proteinCalories - fatCalories) / 4.0;

        // 4. Water Target: weightKg * 35 (in ml)
        double waterMl = weightKg * 35;

        // Create and return DailyGoals object
        DailyGoals goals = new DailyGoals();
        goals.setCalories(totalCalories);
        goals.setProtein(proteinGrams);
        goals.setFat(fatGrams);
        goals.setCarbs(carbsGrams);
        goals.setWater(waterMl);

        return goals;
    }
}
