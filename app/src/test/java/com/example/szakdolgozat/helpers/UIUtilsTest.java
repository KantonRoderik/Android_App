package com.example.szakdolgozat.helpers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UIUtilsTest {

    @Test
    public void calculateSafeProgress_NormalValues_ReturnsPercentage() {
        assertEquals(50, UIUtils.calculateSafeProgress(50, 100));
        assertEquals(100, UIUtils.calculateSafeProgress(120, 100));
        assertEquals(0, UIUtils.calculateSafeProgress(-10, 100));
    }

    @Test
    public void calculateSafeProgress_ZeroOrNegativeGoal_ReturnsZero() {
        assertEquals(0, UIUtils.calculateSafeProgress(50, 0));
        assertEquals(0, UIUtils.calculateSafeProgress(50, -10));
    }

    @Test
    public void formatNutritionText_CorrectFormat() {
        String result = UIUtils.formatNutritionText("Calories", 1500, 2000, "kcal");
        assertEquals("Calories: 1500/2000 kcal (75%)", result);
    }

    @Test
    public void formatNutritionText_NegativeActual_ShowsZero() {
        String result = UIUtils.formatNutritionText("Fat", -5, 100, "g");
        assertEquals("Fat: 0/100 g (0%)", result);
    }

    @Test
    public void formatNutritionText_ZeroGoal_ShowsZeroPercent() {
        String result = UIUtils.formatNutritionText("Protein", 50, 0, "g");
        assertEquals("Protein: 50/0 g (0%)", result);
    }
}
