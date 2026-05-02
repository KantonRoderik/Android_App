package com.example.szakdolgozat.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a user's nutritional intake and exercise for a specific day.
 */
@IgnoreExtraProperties
public class DailyEntry {
    private String date; // Format: "yyyy-MM-dd"
    private Map<String, ConsumedFood> consumedFoods = new HashMap<>();
    
    @PropertyName("exercisesDone")
    private Map<String, Map<String, Object>> exercisesDone = new HashMap<>();
    
    private double totalCalories;
    private double totalCarbs;
    private double totalFat;
    private double totalProtein;
    private double totalWater;
    private double totalCaloriesBurned;

    public DailyEntry() {
        // Required for Firebase
    }

    public DailyEntry(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, ConsumedFood> getConsumedFoods() {
        return consumedFoods;
    }

    public void setConsumedFoods(Map<String, ConsumedFood> consumedFoods) {
        this.consumedFoods = consumedFoods;
    }

    @PropertyName("exercisesDone")
    public Map<String, Map<String, Object>> getExercisesDone() {
        return exercisesDone;
    }

    @PropertyName("exercisesDone")
    public void setExercisesDone(Map<String, Map<String, Object>> exercisesDone) {
        this.exercisesDone = exercisesDone;
    }

    public double getTotalWater() {
        return totalWater;
    }

    public void setTotalWater(double totalWater) {
        this.totalWater = totalWater;
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public double getTotalCarbs() {
        return totalCarbs;
    }

    public void setTotalCarbs(double totalCarbs) {
        this.totalCarbs = totalCarbs;
    }

    public double getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(double totalFat) {
        this.totalFat = totalFat;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(double totalProtein) {
        this.totalProtein = totalProtein;
    }

    public double getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(double totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    /**
     * Recalculates all nutritional totals and burned calories based on the maps.
     */
    public void calculateTotals() {
        double intakeCalories = 0;
        this.totalCarbs = 0;
        this.totalFat = 0;
        this.totalProtein = 0;
        this.totalCaloriesBurned = 0;
        
        if (consumedFoods != null) {
            for (ConsumedFood food : consumedFoods.values()) {
                if (food != null) {
                    intakeCalories += food.getCalories();
                    this.totalCarbs += food.getCarbs();
                    this.totalFat += food.getFat();
                    this.totalProtein += food.getProtein();
                }
            }
        }

        if (exercisesDone != null) {
            for (Map<String, Object> ex : exercisesDone.values()) {
                if (ex != null) {
                    Object cal = ex.get("caloriesBurned");
                    if (cal instanceof Number) {
                        this.totalCaloriesBurned += ((Number) cal).doubleValue();
                    }
                }
            }
        }

        // Net calories = Intake - Burned
        this.totalCalories = intakeCalories - this.totalCaloriesBurned;
    }
}
