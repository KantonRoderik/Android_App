package com.example.szakdolgozat.UI.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.databinding.ActivityDailygoalsEditBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.example.szakdolgozat.models.DailyGoals;

/**
 * Activity for editing daily nutritional goals.
 */
public class DailyGoalsSzerkesztes extends AppCompatActivity {

    private static final String TAG = "DailyGoalsSzerkesztes";

    private ActivityDailygoalsEditBinding binding;
    private FirestoreRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDailygoalsEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = FirestoreRepository.getInstance();

        loadCurrentGoals();

        binding.Mentes.setOnClickListener(v -> onSaveButtonClicked());
    }

    private void loadCurrentGoals() {
        repository.getUserData().addOnSuccessListener(document -> {
            if (document.exists()) {
                DailyGoals goals = document.get("dailyGoals", DailyGoals.class);
                if (goals != null) {
                    binding.caloria.setText(String.valueOf((int) goals.getCalories()));
                    binding.carbs.setText(String.valueOf((int) goals.getCarbs()));
                    binding.protein.setText(String.valueOf((int) goals.getProtein()));
                    binding.fat.setText(String.valueOf((int) goals.getFat()));
                    binding.water.setText(String.valueOf((int) goals.getWater()));
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to load goals: " + e.getMessage()));
    }

    private void onSaveButtonClicked() {
        try {
            DailyGoals newGoals = new DailyGoals();
            newGoals.setCalories(parseInput(binding.caloria));
            newGoals.setCarbs(parseInput(binding.carbs));
            newGoals.setProtein(parseInput(binding.protein));
            newGoals.setFat(parseInput(binding.fat));
            newGoals.setWater(parseInput(binding.water));

            repository.updateDailyGoals(newGoals)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, Profile.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Update failed: " + e.getMessage());
                        Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
                    });

        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.error_invalid_number), Toast.LENGTH_SHORT).show();
        }
    }

    private double parseInput(EditText editText) {
        String text = editText.getText().toString().trim();
        return TextUtils.isEmpty(text) ? 0 : Double.parseDouble(text);
    }
}
