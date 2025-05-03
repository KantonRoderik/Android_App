package com.example.szakdolgozat.models;

public class ConsumedFood {
    private String foodId;
    private String foodName;
    private double quantity; // grammban
    private double calories;
    private double carbs;
    private double fat;
    private double protein;

    public ConsumedFood() {}

    public ConsumedFood(FoodItem foodItem, double quantity) {
        this.foodId = foodItem.getId();
        this.foodName = foodItem.getName();
        this.quantity = quantity;
        this.calories = foodItem.getCalories() * quantity / 100;
        this.carbs = foodItem.getCarbs() * quantity / 100;
        this.fat = foodItem.getFat() * quantity / 100;
        this.protein = foodItem.getProtein() * quantity / 100;
    }


    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }
}