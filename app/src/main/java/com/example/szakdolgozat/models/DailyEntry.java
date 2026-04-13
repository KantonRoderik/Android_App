package com.example.szakdolgozat.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a user's nutritional intake for a specific day.
 */
public class DailyEntry {
    private String date; // Format: "yyyy-MM-dd"
    private Map<String, ConsumedFood> consumedFoods = new HashMap<>();
    private double totalCalories;
    private double totalCarbs;
    private double totalFat;
    private double totalProtein;
    private double totalWater;

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

    /**
     * Recalculates all nutritional totals based on the consumed foods map.
     */
    public void calculateTotals() {
        this.totalCalories = 0;
        this.totalCarbs = 0;
        this.totalFat = 0;
        this.totalProtein = 0;
        // Note: totalWater is usually handled separately as it's often added directly
        
        if (consumedFoods == null) return;

        for (ConsumedFood food : consumedFoods.values()) {
            this.totalCalories += food.getCalories();
            this.totalCarbs += food.getCarbs();
            this.totalFat += food.getFat();
            this.totalProtein += food.getProtein();
        }
    }
}
