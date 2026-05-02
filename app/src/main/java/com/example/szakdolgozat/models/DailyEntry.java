package com.example.szakdolgozat.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a user's nutritional intake and exercise for a specific day.
 */
public class DailyEntry {
    private String date; // Format: "yyyy-MM-dd"
    private Map<String, ConsumedFood> consumedFoods = new HashMap<>();
    private Map<String, Exercise> loggedExercises = new HashMap<>();
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

    public Map<String, Exercise> getLoggedExercises() {
        return loggedExercises;
    }

    public void setLoggedExercises(Map<String, Exercise> loggedExercises) {
        this.loggedExercises = loggedExercises;
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
        this.totalCalories = 0;
        this.totalCarbs = 0;
        this.totalFat = 0;
        this.totalProtein = 0;
        this.totalCaloriesBurned = 0;
        
        if (consumedFoods != null) {
            for (ConsumedFood food : consumedFoods.values()) {
                this.totalCalories += food.getCalories();
                this.totalCarbs += food.getCarbs();
                this.totalFat += food.getFat();
                this.totalProtein += food.getProtein();
            }
        }

        if (loggedExercises != null) {
            for (Exercise ex : loggedExercises.values()) {
                this.totalCaloriesBurned += ex.getCaloriesBurned();
            }
        }
    }
}
