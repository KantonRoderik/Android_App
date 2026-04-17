package com.example.szakdolgozat.UI.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.UI.main.MainActivity;
import com.example.szakdolgozat.databinding.ActivityProfileSzerkesztesBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.example.szakdolgozat.helpers.NutritionCalculator;
import com.example.szakdolgozat.models.DailyGoals;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for editing user profile information. Handles onboarding flow.
 */
public class ProfileSzerkesztes extends AppCompatActivity {

    private static final String TAG = "ProfileSzerkesztes";

    private ActivityProfileSzerkesztesBinding binding;
    private FirestoreRepository repository;
    private boolean isOnboarding = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProfileSzerkesztesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = FirestoreRepository.getInstance();
        isOnboarding = getIntent().getBooleanExtra("IS_ONBOARDING", false);

        if (isOnboarding) {
            binding.editTitle.setText("Welcome! Complete your profile");
            Toast.makeText(this, "Please fill in your stats to get started!", Toast.LENGTH_LONG).show();
        }

        setupGenderSpinner();

        binding.Mentes.setOnClickListener(v -> onSaveButtonClicked());
        binding.AutoCalculate.setOnClickListener(v -> onAutoCalculateClicked());
    }

    private void setupGenderSpinner() {
        String[] genders = {getString(R.string.gender_male), getString(R.string.gender_female)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.genderSpinner.setAdapter(adapter);
    }

    private void onAutoCalculateClicked() {
        if (validateInputs()) {
            calculateAndSaveGoals();
        }
    }

    private boolean validateInputs() {
        try {
            Double.parseDouble(binding.sulyInput.getText().toString().trim());
            Double.parseDouble(binding.magassag.getText().toString().trim());
            Integer.parseInt(binding.kor.getText().toString().trim());
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please fill in weight, height, and age first!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void calculateAndSaveGoals() {
        double weight = Double.parseDouble(binding.sulyInput.getText().toString().trim());
        double height = Double.parseDouble(binding.magassag.getText().toString().trim());
        int age = Integer.parseInt(binding.kor.getText().toString().trim());
        
        String genderStr = binding.genderSpinner.getSelectedItem().toString();
        NutritionCalculator.Gender gender = genderStr.equalsIgnoreCase(getString(R.string.gender_male)) 
                ? NutritionCalculator.Gender.MALE 
                : NutritionCalculator.Gender.FEMALE;

        DailyGoals autoGoals = NutritionCalculator.calculateDailyGoals(weight, height, age, gender);
        
        repository.updateDailyGoals(autoGoals).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Daily goals calculated successfully!", Toast.LENGTH_SHORT).show();
        });
    }

    private void onSaveButtonClicked() {
        if (isOnboarding && !validateInputs()) return;

        String fullName = binding.teljesNev.getText().toString().trim();
        String weightStr = binding.sulyInput.getText().toString().trim();
        String heightStr = binding.magassag.getText().toString().trim();
        String ageStr = binding.kor.getText().toString().trim();
        String gender = binding.genderSpinner.getSelectedItem().toString();

        Map<String, Object> profileUpdates = new HashMap<>();
        if (!TextUtils.isEmpty(fullName)) profileUpdates.put("nev", fullName);
        profileUpdates.put("nem", gender);
        
        if (isOnboarding) {
            profileUpdates.put("onboarding_complete", true);
            // Auto-calculate goals on first save
            calculateAndSaveGoals();
        }

        if (validateAndAddNumber(weightStr, binding.sulyInput, "suly", 0, 500, profileUpdates) &&
            validateAndAddNumber(heightStr, binding.magassag, "magassag", 0, 300, profileUpdates) &&
            validateAndAddNumber(ageStr, binding.kor, "kor", 0, 100, profileUpdates)) {
            
            saveProfileUpdates(profileUpdates);
        }
    }

    private boolean validateAndAddNumber(String valueStr, android.widget.EditText editText, String key, int min, int max, Map<String, Object> updates) {
        if (TextUtils.isEmpty(valueStr)) return !isOnboarding;
        try {
            int value = Integer.parseInt(valueStr);
            if (value <= min || value >= max) {
                editText.setError(getString(R.string.error_invalid_value_range, min, max));
                return false;
            }
            updates.put(key, valueStr);
            return true;
        } catch (NumberFormatException e) {
            editText.setError(getString(R.string.error_invalid_number));
            return false;
        }
    }

    private void saveProfileUpdates(Map<String, Object> updates) {
        repository.updateProfile(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    navigateBack();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Update failed", e);
                    Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateBack() {
        if (isOnboarding) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, Profile.class));
        }
        finish();
    }
}
