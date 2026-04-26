package com.example.szakdolgozat.helpers;

import com.example.szakdolgozat.models.DailyGoals;

public class NutritionCalculator {

    public enum Gender {
        MALE, FEMALE
    }

    public static Gender parseGender(String genderStr) {
        if (genderStr == null) return Gender.MALE;
        String lower = genderStr.toLowerCase().trim();
        if (lower.equals("female") || lower.equals("nő") || lower.equals("no") || 
            lower.startsWith("fem") || lower.equals("n")) {
            return Gender.FEMALE;
        }
        return Gender.MALE;
    }

    public static double calculateBMR(double weight, double height, double age, Gender gender) {
        if (weight <= 0 || height <= 0 || age <= 0) return 0;
        double bmr;
        if (gender == Gender.MALE) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }
        return Math.max(0, bmr);
    }

    public static DailyGoals calculateDailyGoals(double weightKg, double heightCm, int age, Gender gender) {
        double bmr = calculateBMR(weightKg, heightCm, age, gender);

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
