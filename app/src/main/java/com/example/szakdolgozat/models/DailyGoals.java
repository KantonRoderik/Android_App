package com.example.szakdolgozat.models;

public class DailyGoals {
    private double calories;
    private double carbs;
    private double fat;
    private double protein;
    private double water;

    public DailyGoals() {}

    // Getterek
    public double getCalories() { return calories; }
    public double getCarbs() { return carbs; }
    public double getFat() { return fat; }
    public double getProtein() { return protein; }
    public double getWater() { return water; }



    // Setterek
    public void setCalories(double calories) { this.calories = calories; }
    public void setCarbs(double carbs) { this.carbs = carbs; }
    public void setFat(double fat) { this.fat = fat; }
    public void setProtein(double protein) { this.protein = protein; }
    public void setWater(double water) { this.water = water; }
}
