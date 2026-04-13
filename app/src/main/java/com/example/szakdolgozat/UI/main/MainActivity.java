package com.example.szakdolgozat.UI.main;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.UI.auth.Login;
import com.example.szakdolgozat.UI.food.AddFoodActivity;
import com.example.szakdolgozat.UI.profile.Profile;
import com.example.szakdolgozat.databinding.ActivityMainBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.example.szakdolgozat.models.DailyEntry;
import com.example.szakdolgozat.models.DailyGoals;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Main dashboard of the application showing daily progress.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    private ActivityMainBinding binding;
    private FirestoreRepository repository;
    private Calendar currentDate = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        repository = FirestoreRepository.getInstance();
        
        initializeUI();
        checkNotificationPermission();
        updateDateDisplay();
        loadDailyData();
    }

    private void initializeUI() {
        binding.addFoodBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddFoodActivity.class);
            intent.putExtra("selected_date", getFormattedDate());
            startActivity(intent);
        });

        binding.vizGomb.setOnClickListener(v -> addWater());

        binding.ProfileBtn.setOnClickListener(v -> 
                startActivity(new Intent(this, Profile.class)));

        binding.Logout.setOnClickListener(v -> logout());

        binding.btnNext.setOnClickListener(v -> changeDay(1));
        binding.btnPrevious.setOnClickListener(v -> changeDay(-1));
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    private void loadDailyData() {
        String selectedDate = getFormattedDate();

        repository.getDailyEntry(selectedDate).addOnSuccessListener(entrySnapshot -> {
            DailyEntry dailyEntry = entrySnapshot.exists() ? entrySnapshot.toObject(DailyEntry.class) : new DailyEntry(selectedDate);
            
            repository.getUserData().addOnSuccessListener(userSnapshot -> {
                DailyGoals goals = userSnapshot.get("dailyGoals", DailyGoals.class);
                if (goals != null && dailyEntry != null) {
                    updateUI(dailyEntry, goals, userSnapshot);
                } else {
                    setDefaultUIValues();
                }
            }).addOnFailureListener(e -> handleDataError(getString(R.string.error_loading_data), e));
            
        }).addOnFailureListener(e -> handleDataError(getString(R.string.error_loading_data), e));
    }

    private void updateUI(DailyEntry entry, DailyGoals goals, DocumentSnapshot userSnapshot) {
        updateProgressBars(entry, goals);
        updateTextViews(entry, goals);
        updateBMR(userSnapshot);
    }

    private void updateBMR(DocumentSnapshot userSnapshot) {
        String sulyStr = userSnapshot.getString("suly");
        String magassagStr = userSnapshot.getString("magassag");
        String korStr = userSnapshot.getString("kor");
        String nem = userSnapshot.getString("nem");

        try {
            double weight = sulyStr != null ? Double.parseDouble(sulyStr) : 0;
            double height = magassagStr != null ? Double.parseDouble(magassagStr) : 0;
            double age = korStr != null ? Double.parseDouble(korStr) : 0;
            
            double bmr = calculateBMR(weight, height, age, nem);
            binding.bmrDisplay.setText(getString(R.string.label_bmr, bmr));
        } catch (NumberFormatException e) {
            binding.bmrDisplay.setText(getString(R.string.label_bmr, 0.0));
        }
    }

    private double calculateBMR(double weight, double height, double age, String gender) {
        if (weight <= 0 || height <= 0 || age <= 0) return 0;
        double bmr = (10 * weight) + (6.25 * height) - (5 * age);
        if (gender != null && (gender.equalsIgnoreCase("nő") || gender.equalsIgnoreCase("female"))) {
            bmr -= 161;
        } else {
            bmr += 5;
        }
        return Math.max(0, bmr);
    }

    private void updateProgressBars(DailyEntry entry, DailyGoals goals) {
        animateProgressBar(binding.progressBarKaloria, calculateSafeProgress(entry.getTotalCalories(), goals.getCalories()));
        animateProgressBar(binding.progressBarSzenhidrat, calculateSafeProgress(entry.getTotalCarbs(), goals.getCarbs()));
        animateProgressBar(binding.progressBarFeherje, calculateSafeProgress(entry.getTotalProtein(), goals.getProtein()));
        animateProgressBar(binding.progressBarZsir, calculateSafeProgress(entry.getTotalFat(), goals.getFat()));
        animateProgressBar(binding.progressBarViz, calculateSafeProgress(entry.getTotalWater(), goals.getWater()));
    }

    private void animateProgressBar(ProgressBar progressBar, int targetProgress) {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress",
                progressBar.getProgress(), targetProgress);
        progressAnimator.setDuration(800);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.start();
    }

    private int calculateSafeProgress(double actual, double goal) {
        if (goal <= 0) return 0;
        return (int) Math.min((actual / goal) * 100, 100);
    }

    private void updateTextViews(DailyEntry entry, DailyGoals goals) {
        binding.textViewKaloria.setText(formatNutritionText(getString(R.string.label_calories), entry.getTotalCalories(), goals.getCalories(), getString(R.string.unit_kcal)));
        binding.textViewSzenhidrat.setText(formatNutritionText(getString(R.string.label_carbs), entry.getTotalCarbs(), goals.getCarbs(), getString(R.string.unit_g)));
        binding.textViewFeherje.setText(formatNutritionText(getString(R.string.label_protein), entry.getTotalProtein(), goals.getProtein(), getString(R.string.unit_g)));
        binding.textViewZsir.setText(formatNutritionText(getString(R.string.label_fat), entry.getTotalFat(), goals.getFat(), getString(R.string.unit_g)));
        binding.textViewViz.setText(formatNutritionText(getString(R.string.label_water), entry.getTotalWater(), goals.getWater(), getString(R.string.unit_ml)));
    }

    private String formatNutritionText(String label, double actual, double goal, String unit) {
        int percent = calculateSafeProgress(actual, goal);
        return getString(R.string.nutrition_format, label, actual, goal, unit, percent);
    }

    private void setDefaultUIValues() {
        binding.progressBarKaloria.setProgress(0);
        binding.progressBarSzenhidrat.setProgress(0);
        binding.progressBarFeherje.setProgress(0);
        binding.progressBarZsir.setProgress(0);
        binding.progressBarViz.setProgress(0);

        binding.textViewKaloria.setText(getString(R.string.nutrition_default_format, getString(R.string.label_calories), getString(R.string.unit_kcal)));
        binding.textViewSzenhidrat.setText(getString(R.string.nutrition_default_format, getString(R.string.label_carbs), getString(R.string.unit_g)));
        binding.textViewFeherje.setText(getString(R.string.nutrition_default_format, getString(R.string.label_protein), getString(R.string.unit_g)));
        binding.textViewZsir.setText(getString(R.string.nutrition_default_format, getString(R.string.label_fat), getString(R.string.unit_g)));
        binding.textViewViz.setText(getString(R.string.nutrition_default_format, getString(R.string.label_water), getString(R.string.unit_ml)));
    }

    private void handleDataError(String message, Exception e) {
        Log.e(TAG, message + ": " + e.getMessage());
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateDateDisplay() {
        binding.tvCurrentDate.setText(getFormattedDate());
    }

    private String getFormattedDate() {
        return dateFormat.format(currentDate.getTime());
    }

    private void addWater() {
        repository.addWater(getFormattedDate(), 100)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, R.string.water_added_toast, Toast.LENGTH_SHORT).show();
                    loadDailyData();
                })
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.error_generic) + ": " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void logout() {
        repository.signOut();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    private void changeDay(int amount) {
        currentDate.add(Calendar.DAY_OF_YEAR, amount);
        updateDateDisplay();
        loadDailyData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDailyData();
    }
}
