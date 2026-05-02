package com.example.szakdolgozat.models;

import com.google.firebase.firestore.PropertyName;
import com.google.gson.annotations.SerializedName;

public class Exercise {
    private String id;
    private String name;
    
    @SerializedName("met_value")
    private double metValue;

    private String category;
    private double duration; // minutes
    private double caloriesBurned;

    public Exercise() {}

    public Exercise(String name, double metValue, String category) {
        this.name = name;
        this.metValue = metValue;
        this.category = category;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @PropertyName("met_value")
    public double getMetValue() { return metValue; }

    @PropertyName("met_value")
    public void setMetValue(double metValue) { this.metValue = metValue; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }

    public double getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(double caloriesBurned) { this.caloriesBurned = caloriesBurned; }
}