package com.example.szakdolgozat.models;

import java.util.HashMap;
import java.util.Map;

public class DailyEntry {
    private String date; // Formátum: "yyyy-MM-dd"
    private Map<String, ConsumedFood> consumedFoods;
    private double totalCalories;
    private double totalCarbs;
    private double totalFat;
    private double totalProtein;
    private double totalWater;

    public DailyEntry() {
        consumedFoods = new HashMap<>(); // Inicializálás HashMap-kel
    }

    public DailyEntry(String date) {
        this();
        this.date = date;
    }

    // Getterek és setterek
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    // Getterek és setterek
    public Map<String, ConsumedFood> getConsumedFoods() { return consumedFoods; }
    public void setConsumedFoods(Map<String, ConsumedFood> consumedFoods) {
        this.consumedFoods = consumedFoods;
    }

    public double getTotalwater() { return totalWater; }
    public void setTotalwater(double totalwater) { this.totalWater = totalwater; }
    public double getTotalCalories() { return totalCalories; }
    public void setTotalCalories(double totalCalories) { this.totalCalories = totalCalories; }
    public double getTotalCarbs() { return totalCarbs; }
    public void setTotalCarbs(double totalCarbs) { this.totalCarbs = totalCarbs; }
    public double getTotalFat() { return totalFat; }
    public void setTotalFat(double totalFat) { this.totalFat = totalFat; }
    public double getTotalProtein() { return totalProtein; }
    public void setTotalProtein(double totalProtein) { this.totalProtein = totalProtein; }

    // Segédmetódus az összesítések frissítéséhez
    public void calculateTotals() {
        totalCalories = 0;
        totalCarbs = 0;
        totalFat = 0;
        totalProtein = 0;
        totalWater = 0;

        for (ConsumedFood food : consumedFoods.values()) {
            totalCalories += food.getCalories();
            totalCarbs += food.getCarbs();
            totalFat += food.getFat();
            totalProtein += food.getProtein();

        }
    }
}