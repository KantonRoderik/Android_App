package com.example.szakdolgozat.UI.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for editing daily nutritional goals with template support and BMR reference.
 */
public class DailyGoalsSzerkesztes extends AppCompatActivity {

    private static final String TAG = "DailyGoalsSzerkesztes";

    private ActivityDailygoalsEditBinding binding;
    private FirestoreRepository repository;
    private boolean isInitialLoad = true;
    private boolean isApplyingTemplate = false;
    private double calculatedBmr = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDailygoalsEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = FirestoreRepository.getInstance();

        setupTemplateSpinner();
        setupManualInputListeners();
        loadCurrentGoalsAndProfile();

        binding.Mentes.setOnClickListener(v -> onSaveButtonClicked());
        binding.btnUseBmr.setOnClickListener(v -> useBmrAsGoal());
    }

    private void setupTemplateSpinner() {
        List<String> templateNames = new ArrayList<>();
        templateNames.add("Custom");
        for (DietaryTemplate template : DietaryTemplate.values()) {
            templateNames.add(template.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, templateNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.templateSpinner.setAdapter(adapter);

        binding.templateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isInitialLoad) return;
                
                if (position > 0 && !isApplyingTemplate) {
                    DietaryTemplate selectedTemplate = DietaryTemplate.values()[position - 1];
                    applyTemplate(selectedTemplate);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupManualInputListeners() {
        TextWatcher manualInputWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isApplyingTemplate && !isInitialLoad) {
                    binding.templateSpinner.setSelection(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.caloria.addTextChangedListener(manualInputWatcher);
        binding.carbs.addTextChangedListener(manualInputWatcher);
        binding.protein.addTextChangedListener(manualInputWatcher);
        binding.fat.addTextChangedListener(manualInputWatcher);
        binding.water.addTextChangedListener(manualInputWatcher);
    }

    private void applyTemplate(DietaryTemplate template) {
        isApplyingTemplate = true;
        double calories = parseInput(binding.caloria);
        double water = parseInput(binding.water);
        
        if (calories <= 0) {
            Toast.makeText(this, "Please enter calories first", Toast.LENGTH_SHORT).show();
            binding.templateSpinner.setSelection(0);
            isApplyingTemplate = false;
            return;
        }

        DailyGoals goals = template.calculateGoals(calories, water);
        binding.protein.setText(String.valueOf((int) goals.getProtein()));
        binding.carbs.setText(String.valueOf((int) goals.getCarbs()));
        binding.fat.setText(String.valueOf((int) goals.getFat()));
        isApplyingTemplate = false;
    }

    private void loadCurrentGoalsAndProfile() {
        repository.getUserData().addOnSuccessListener(document -> {
            if (document.exists()) {
                // 1. Calculate BMR from profile
                calculateAndDisplayBmr(document);

                // 2. Load current goals
                DailyGoals goals = document.get("dailyGoals", DailyGoals.class);
                String selectedTemplateName = document.getString("selectedTemplate");

                isApplyingTemplate = true;
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
                        binding.templateSpinner.setSelection(template.ordinal() + 1);
                    } catch (IllegalArgumentException e) {
                        binding.templateSpinner.setSelection(0);
                    }
                }
                isApplyingTemplate = false;
                isInitialLoad = false;
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to load data: " + e.getMessage()));
    }

    private void calculateAndDisplayBmr(DocumentSnapshot userSnapshot) {
        try {
            String sulyStr = userSnapshot.getString("suly");
            String magassagStr = userSnapshot.getString("magassag");
            String korStr = userSnapshot.getString("kor");
            String nem = userSnapshot.getString("nem");

            double weight = (sulyStr != null && !sulyStr.isEmpty()) ? Double.parseDouble(sulyStr) : 0;
            double height = (magassagStr != null && !magassagStr.isEmpty()) ? Double.parseDouble(magassagStr) : 0;
            double age = (korStr != null && !korStr.isEmpty()) ? Double.parseDouble(korStr) : 0;

            if (weight > 0 && height > 0 && age > 0) {
                // Mifflin-St Jeor Equation
                calculatedBmr = (10 * weight) + (6.25 * height) - (5 * age);
                if (nem != null && (nem.equalsIgnoreCase("n\u0151") || nem.equalsIgnoreCase("female"))) {
                    calculatedBmr -= 161;
                } else {
                    calculatedBmr += 5;
                }
                binding.bmrInfoText.setText("Your BMR: " + (int)calculatedBmr + " kcal");
            } else {
                binding.bmrCard.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            binding.bmrCard.setVisibility(View.GONE);
        }
    }

    private void useBmrAsGoal() {
        if (calculatedBmr > 0) {
            binding.caloria.setText(String.valueOf((int)calculatedBmr));
            // Trigger re-calculation if a template is selected
            int templatePos = binding.templateSpinner.getSelectedItemPosition();
            if (templatePos > 0) {
                DietaryTemplate template = DietaryTemplate.values()[templatePos - 1];
                applyTemplate(template);
            }
            Toast.makeText(this, "Calories set to BMR", Toast.LENGTH_SHORT).show();
        }
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
