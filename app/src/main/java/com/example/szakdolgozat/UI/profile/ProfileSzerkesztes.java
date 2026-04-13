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
import com.example.szakdolgozat.databinding.ActivityProfileSzerkesztesBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for editing user profile information.
 */
public class ProfileSzerkesztes extends AppCompatActivity {

    private static final String TAG = "ProfileSzerkesztes";

    private ActivityProfileSzerkesztesBinding binding;
    private FirestoreRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProfileSzerkesztesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = FirestoreRepository.getInstance();

        setupGenderSpinner();

        binding.Mentes.setOnClickListener(v -> onSaveButtonClicked());
    }

    private void setupGenderSpinner() {
        String[] genders = {getString(R.string.gender_male), getString(R.string.gender_female)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.genderSpinner.setAdapter(adapter);
    }

    private void onSaveButtonClicked() {
        String fullName = binding.teljesNev.getText().toString().trim();
        String weightStr = binding.sulyInput.getText().toString().trim();
        String heightStr = binding.magassag.getText().toString().trim();
        String ageStr = binding.kor.getText().toString().trim();
        String gender = binding.genderSpinner.getSelectedItem().toString();
        String newPassword = binding.jelszoInput.getText().toString().trim();
        String verifyPassword = binding.jelszoVerifyInput.getText().toString().trim();

        // 1. Password validation
        if (!TextUtils.isEmpty(newPassword)) {
            if (newPassword.length() < 6) {
                binding.jelszoInput.setError(getString(R.string.error_password_length));
                return;
            }
            if (!newPassword.equals(verifyPassword)) {
                binding.jelszoVerifyInput.setError(getString(R.string.error_passwords_dont_match));
                return;
            }
            updatePassword(newPassword);
        }

        // 2. Profile data validation and collection
        Map<String, Object> profileUpdates = new HashMap<>();
        
        if (!TextUtils.isEmpty(fullName)) profileUpdates.put("nev", fullName);
        profileUpdates.put("nem", gender);

        if (validateAndAddNumber(weightStr, binding.sulyInput, "suly", 0, 500, profileUpdates) &&
            validateAndAddNumber(heightStr, binding.magassag, "magassag", 0, 300, profileUpdates) &&
            validateAndAddNumber(ageStr, binding.kor, "kor", 0, 100, profileUpdates)) {
            
            saveProfileUpdates(profileUpdates);
        }
    }

    private boolean validateAndAddNumber(String valueStr, android.widget.EditText editText, String key, int min, int max, Map<String, Object> updates) {
        if (TextUtils.isEmpty(valueStr)) return true;
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

    private void updatePassword(String password) {
        repository.updatePassword(password)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, getString(R.string.password_changed_success), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.password_changed_failed), Toast.LENGTH_SHORT).show());
    }

    private void saveProfileUpdates(Map<String, Object> updates) {
        if (updates.isEmpty()) {
            navigateBack();
            return;
        }

        repository.updateProfile(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    navigateBack();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Update failed", e);
                    Toast.makeText(this, getString(R.string.food_add_error), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateBack() {
        startActivity(new Intent(this, Profile.class));
        finish();
    }
}
