package com.example.szakdolgozat.UI.main;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.UI.auth.Login;
import com.example.szakdolgozat.UI.food.AddFoodActivity;
import com.example.szakdolgozat.UI.profile.Profile;
import com.example.szakdolgozat.models.DailyEntry;
import com.example.szakdolgozat.models.DailyGoals;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBarKaloria, progressBarSzenhidrat, progressBarFeherje, progressBarZsir;
    private TextView textViewKaloria, textViewSzenhidrat, textViewFeherje, textViewZsir;
    private FirebaseFirestore db;
    private String userId;

    private TextView tvCurrentDate;
    private Calendar currentDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        initializeFirebase();
        loadDailyData();

        // Dátum kezelése
        updateDateDisplay();
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(currentDate.getTime());
        tvCurrentDate.setText(formattedDate);
    }

    private String getSelectedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(currentDate.getTime());
    }

    private void initializeUI() {
        progressBarKaloria = findViewById(R.id.progressBar_kaloria);
        progressBarSzenhidrat = findViewById(R.id.progressBar_szenhidrat);
        progressBarFeherje = findViewById(R.id.progressBar_feherje);
        progressBarZsir = findViewById(R.id.progressBar_zsir);

        textViewKaloria = findViewById(R.id.textView_kaloria);
        textViewSzenhidrat = findViewById(R.id.textView_szenhidrat);
        textViewFeherje = findViewById(R.id.textView_feherje);
        textViewZsir = findViewById(R.id.textView_zsir);

        tvCurrentDate = findViewById(R.id.tv_current_date);
    }

    private void initializeFirebase() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
    }

    private void loadDailyData() {
        String selectedDate = getSelectedDate();

        // 1. Napi bejegyzés betöltése
        db.collection("users").document(userId)
                .collection("dailyEntries").document(selectedDate)
                .get()
                .addOnSuccessListener(dailyEntrySnapshot -> {
                    if (dailyEntrySnapshot.exists()) {
                        DailyEntry dailyEntry = dailyEntrySnapshot.toObject(DailyEntry.class);

                        // 2. Célok betöltése a user dokumentumból
                        db.collection("users").document(userId)
                                .get()
                                .addOnSuccessListener(userSnapshot -> {
                                    DailyGoals goals = userSnapshot.get("dailyGoals", DailyGoals.class);
                                    if (dailyEntry != null && goals != null) {
                                        updateUI(dailyEntry, goals);
                                    } else {
                                        setDefaultUIValues();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Célok betöltési hiba: " + e.getMessage());
                                    showErrorToast();
                                });
                    } else {
                        setDefaultUIValues();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Napi bejegyzés hiba: " + e.getMessage());
                    showErrorToast();
                });
    }

    private void updateUI(DailyEntry entry, DailyGoals goals) {
        // Progressbar-ok frissítése
        updateProgressBars(entry, goals);

        // Szöveges értékek frissítése
        updateTextViews(entry, goals);
    }

    private void updateProgressBars(DailyEntry entry, DailyGoals goals) {
        int kaloriaProgress = calculateSafeProgress(entry.getTotalCalories(), goals.getCalories());
        int szenhidratProgress = calculateSafeProgress(entry.getTotalCarbs(), goals.getCarbs());
        int feherjeProgress = calculateSafeProgress(entry.getTotalProtein(), goals.getProtein());
        int zsirProgress = calculateSafeProgress(entry.getTotalFat(), goals.getFat());

        animateProgressBar(progressBarKaloria, kaloriaProgress);
        animateProgressBar(progressBarSzenhidrat, szenhidratProgress);
        animateProgressBar(progressBarFeherje, feherjeProgress);
        animateProgressBar(progressBarZsir, zsirProgress);
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
        int progress = (int) ((actual / goal) * 100);
        return Math.min(progress, 100);
    }

    private void updateTextViews(DailyEntry entry, DailyGoals goals) {
        textViewKaloria.setText(formatNutritionText(
                "Kalória",
                entry.getTotalCalories(),
                goals.getCalories(),
                "kcal"
        ));

        textViewSzenhidrat.setText(formatNutritionText(
                "Szénhidrát",
                entry.getTotalCarbs(),
                goals.getCarbs(),
                "g"
        ));

        textViewFeherje.setText(formatNutritionText(
                "Fehérje",
                entry.getTotalProtein(),
                goals.getProtein(),
                "g"
        ));

        textViewZsir.setText(formatNutritionText(
                "Zsír",
                entry.getTotalFat(),
                goals.getFat(),
                "g"
        ));
    }

    private String formatNutritionText(String label, double actual, double goal, String unit) {
        return String.format(Locale.getDefault(),
                "%s: %.0f/%.0f %s (%d%%)",
                label,
                actual,
                goal,
                unit,
                calculateSafeProgress(actual, goal)
        );
    }

    private void setDefaultUIValues() {
        progressBarKaloria.setProgress(0);
        progressBarSzenhidrat.setProgress(0);
        progressBarFeherje.setProgress(0);
        progressBarZsir.setProgress(0);

        textViewKaloria.setText("Kalória: 0/0 kcal (0%)");
        textViewSzenhidrat.setText("Szénhidrát: 0/0 g (0%)");
        textViewFeherje.setText("Fehérje: 0/0 g (0%)");
        textViewZsir.setText("Zsír: 0/0 g (0%)");
    }

    private void showErrorToast() {
        Toast.makeText(this, "Hiba az adatok betöltésekor", Toast.LENGTH_SHORT).show();
    }

    public void Logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    public void openAddFoodActivity(View view) {
        startActivity(new Intent(this, AddFoodActivity.class));
    }

    public void Profile(View view) {
        startActivity(new Intent(this, Profile.class));
    }

    public void nextDay(View view) {
        currentDate.add(Calendar.DAY_OF_YEAR, 1);
        updateDateDisplay();
        loadDailyData();
    }

    public void previousDay(View view) {
        currentDate.add(Calendar.DAY_OF_YEAR, -1);
        updateDateDisplay();
        loadDailyData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDailyData();
    }
}