package com.example.szakdolgozat.models;

public enum DietaryTemplate {
    BALANCED("Balanced", 0.25, 0.50, 0.25),
    LOW_CARB("Low Carb", 0.40, 0.20, 0.40),
    HIGH_PROTEIN("High Protein", 0.35, 0.45, 0.20),
    KETOGENIC("Ketogenic", 0.25, 0.05, 0.70);

    private final String name;
    private final double proteinRatio;
    private final double carbsRatio;
    private final double fatRatio;

    DietaryTemplate(String name, double proteinRatio, double carbsRatio, double fatRatio) {
        this.name = name;
        this.proteinRatio = proteinRatio;
        this.carbsRatio = carbsRatio;
        this.fatRatio = fatRatio;
    }

    public String getName() {
        return name;
    }

    public double getProteinRatio() {
        return proteinRatio;
    }

    public double getCarbsRatio() {
        return carbsRatio;
    }

    public double getFatRatio() {
        return fatRatio;
    }

    public DailyGoals calculateGoals(double totalCalories, double waterGoal) {
        DailyGoals goals = new DailyGoals();
        goals.setCalories(totalCalories);
        goals.setProtein((totalCalories * proteinRatio) / 4.0);
        goals.setCarbs((totalCalories * carbsRatio) / 4.0);
        goals.setFat((totalCalories * fatRatio) / 9.0);
        goals.setWater(waterGoal);
        return goals;
    }
}
