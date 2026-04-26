package com.example.szakdolgozat.helpers;

import android.view.Window;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import java.util.Locale;

public class UIUtils {

    /**
     * Hides the system status bar and navigation bar for an immersive experience.
     * Works on all supported API levels.
     */
    public static void hideSystemUI(Window window) {
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(window, window.getDecorView());

        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    /**
     * Calculates progress percentage safely between 0 and 100.
     */
    public static int calculateSafeProgress(double actual, double goal) {
        if (goal <= 0) return 0;
        int progress = (int) ((actual / goal) * 100);
        return Math.max(0, Math.min(progress, 100));
    }

    /**
     * Formats nutritional data into a readable string.
     */
    public static String formatNutritionText(String label, double actual, double goal, String unit) {
        double displayActual = Math.max(0, actual);
        int percent = (goal > 0) ? (int) ((displayActual / goal) * 100) : 0;
        return String.format(Locale.getDefault(), "%s: %.0f/%.0f %s (%d%%)", label, displayActual, goal, unit, percent);
    }
}
