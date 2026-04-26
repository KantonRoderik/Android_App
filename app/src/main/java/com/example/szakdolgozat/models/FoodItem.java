package com.example.szakdolgozat.models;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class FoodItem {
    private String id;
    private String name;
    private double calories;
    private double carbs;
    private double fat;
    private double protein;
    private boolean isAiGenerated;
    private List<ServingUnit> commonUnits;

    public static class ServingUnit {
        private String unitName;
        private double weightG;

        public ServingUnit() {}

        public ServingUnit(String unitName, double weightG) {
            this.unitName = unitName;
            this.weightG = weightG;
        }

        public String getUnitName() { return unitName; }
        public void setUnitName(String unitName) { this.unitName = unitName; }
        public double getWeightG() { return weightG; }
        public void setWeightG(double weightG) { this.weightG = weightG; }

        @Override
        public String toString() {
            return unitName;
        }
    }

    public FoodItem() {
        this.commonUnits = new ArrayList<>();
    }

    public FoodItem(String name, double calories, double carbs, double protein, double fat) {
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.isAiGenerated = false;
        this.commonUnits = new ArrayList<>();
    }

    /**
     * Parses a JSON string from the AI into a FoodItem object.
     */
    public static FoodItem fromAiJson(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            String cleanJson = json.replaceAll("(?s)```(?:json)?\\n?|```", "").trim();
            FoodItem item = new Gson().fromJson(cleanJson, FoodItem.class);
            if (item != null) {
                item.setAiGenerated(true);
            }
            return item;
        } catch (Exception e) {
            return null;
        }
    }

    // Getterek és setterek
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }
    public double getCarbs() { return carbs; }
    public void setCarbs(double carbs) { this.carbs = carbs; }
    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }
    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }
    public boolean isAiGenerated() { return isAiGenerated; }
    public void setAiGenerated(boolean aiGenerated) { isAiGenerated = aiGenerated; }
    public List<ServingUnit> getCommonUnits() { return commonUnits; }
    public void setCommonUnits(List<ServingUnit> commonUnits) { this.commonUnits = commonUnits; }
}