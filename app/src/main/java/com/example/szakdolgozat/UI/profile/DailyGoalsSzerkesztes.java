package com.example.szakdolgozat.UI.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.databinding.ActivityDailygoalsEditBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.example.szakdolgozat.models.DailyGoals;
import com.example.szakdolgozat.models.DietaryTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for editing daily nutritional goals with template support.
 */
public class DailyGoalsSzerkesztes extends AppCompatActivity {

    private static final String TAG = "DailyGoalsSzerkesztes";

    private ActivityDailygoalsEditBinding binding;
    private FirestoreRepository repository;
    private boolean isInitialLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDailygoalsEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = FirestoreRepository.getInstance();

        setupTemplateSpinner();
        loadCurrentGoals();

        binding.Mentes.setOnClickListener(v -> onSaveButtonClicked());
    }

    private void setupTemplateSpinner() {
        List<String> templateNames = new ArrayList<>();
        templateNames.add("Custom"); // Default option
        for (DietaryTemplate template : DietaryTemplate.values()) {
            templateNames.add(template.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, templateNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.templateSpinner.setAdapter(adapter);

        binding.templateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isInitialLoad) {
                    isInitialLoad = false;
                    return;
                }
                
                if (position > 0) { // Not "Custom"
                    DietaryTemplate selectedTemplate = DietaryTemplate.values()[position - 1];
                    applyTemplate(selectedTemplate);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void applyTemplate(DietaryTemplate template) {
        double calories = parseInput(binding.caloria);
        double water = parseInput(binding.water);
        
        if (calories <= 0) {
            Toast.makeText(this, "Please enter calories first", Toast.LENGTH_SHORT).show();
            binding.templateSpinner.setSelection(0); // Reset to Custom
            return;
        }

        DailyGoals goals = template.calculateGoals(calories, water);
        binding.protein.setText(String.valueOf((int) goals.getProtein()));
        binding.carbs.setText(String.valueOf((int) goals.getCarbs()));
        binding.fat.setText(String.valueOf((int) goals.getFat()));
    }

    private void loadCurrentGoals() {
        repository.getUserData().addOnSuccessListener(document -> {
            if (document.exists()) {
                DailyGoals goals = document.get("dailyGoals", DailyGoals.class);
                String selectedTemplateName = document.getString("selectedTemplate");

                if (goals != null) {
                    binding.caloria.setText(String.valueOf((int) goals.getCalories()));
                    binding.carbs.setText(String.valueOf((int) goals.getCarbs()));
                    binding.protein.setText(String.valueOf((int) goals.getProtein()));
                    binding.fat.setText(String.valueOf((int) goals.getFat()));
                    binding.water.setText(String.valueOf((int) goals.getWater()));
                }

                if (selectedTemplateName != null) {
                    try {
                        DietaryTemplate template = DietaryTemplate.valueOf(selectedTemplateName);
                        // +1 because of "Custom" at index 0
                        binding.templateSpinner.setSelection(template.ordinal() + 1);
                    } catch (IllegalArgumentException e) {
                        binding.templateSpinner.setSelection(0);
                    }
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

            int templatePos = binding.templateSpinner.getSelectedItemPosition();
            if (templatePos > 0) {
                DietaryTemplate template = DietaryTemplate.values()[templatePos - 1];
                repository.updateDietaryTemplate(template, newGoals.getCalories(), newGoals.getWater())
                        .addOnSuccessListener(aVoid -> handleSuccess())
                        .addOnFailureListener(this::handleError);
            } else {
                repository.updateDailyGoals(newGoals)
                        .addOnSuccessListener(aVoid -> handleSuccess())
                        .addOnFailureListener(this::handleError);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.error_invalid_number), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSuccess() {
        Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void handleError(Exception e) {
        Log.e(TAG, "Update failed: " + e.getMessage());
        Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
    }

    private double parseInput(EditText editText) {
        String text = editText.getText().toString().trim();
        return TextUtils.isEmpty(text) ? 0 : Double.parseDouble(text);
    }
}
