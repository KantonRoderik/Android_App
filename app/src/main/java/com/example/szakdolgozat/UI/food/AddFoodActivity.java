package com.example.szakdolgozat.UI.food;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.szakdolgozat.BuildConfig;
import com.example.szakdolgozat.R;
import com.example.szakdolgozat.databinding.ActivityAddfoodBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.example.szakdolgozat.helpers.UIUtils;
import com.example.szakdolgozat.models.ConsumedFood;
import com.example.szakdolgozat.models.FoodItem;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.RequestOptions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddFoodActivity extends AppCompatActivity {

    private static final String TAG = "AddFoodActivity";
    private ActivityAddfoodBinding binding;
    private FirestoreRepository repository;
    private String selectedDate;
    private GenerativeModelFutures generativeModel;
    private List<FoodItem> availableFoods = new ArrayList<>();
    private FoodItem selectedFoodItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddfoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UIUtils.hideSystemUI(getWindow());
        repository = FirestoreRepository.getInstance();

        setupAi();
        setupDate();
        setupAutocomplete();
        setupListeners();
        
        // Initial units
        updateUnitSpinner(null);
    }

    private void setupAi() {
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.responseMimeType = "application/json";
        GenerationConfig config = configBuilder.build();

        Content systemInstruction = new Content.Builder()
                .addText("Act as a nutritionist. Provide nutrition data for 100g. " +
                        "Also provide common serving units (e.g., 'piece', 'slice') and their weight in grams. " +
                        "Return ONLY a raw JSON object. " +
                        "Fields: name (string), calories (double), carbs (double), protein (double), fat (double), " +
                        "commonUnits (array of objects with 'unitName' (string) and 'weightG' (double))")
                .build();

        GenerativeModel gm = new GenerativeModel(
                "gemini-2.5-flash",
                BuildConfig.GEMINI_API_KEY,
                config,
                null,
                new RequestOptions(30000L, "v1beta"),
                null,
                null,
                systemInstruction
        );

        generativeModel = GenerativeModelFutures.from(gm);
    }

    private void setupDate() {
        selectedDate = getIntent().getStringExtra("selected_date");
        if (selectedDate == null) {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }
    }

    private void setupAutocomplete() {
        repository.getAllFoods().addOnSuccessListener(queryDocumentSnapshots -> {
            availableFoods.clear();
            List<String> foodNames = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                FoodItem food = doc.toObject(FoodItem.class);
                if (food != null) {
                    food.setId(doc.getId());
                    availableFoods.add(food);
                    foodNames.add(food.getName());
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, foodNames);
            binding.foodNameInput.setAdapter(adapter);
        }).addOnFailureListener(e -> {
            // Even if it fails (e.g. offline and no cache), user can still type
            Log.w(TAG, "Failed to load autocomplete foods", e);
        });

        binding.foodNameInput.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            for (FoodItem food : availableFoods) {
                if (food.getName().equals(selectedName)) {
                    selectedFoodItem = food;
                    updateUnitSpinner(food);
                    binding.aiIdentifyButton.setVisibility(View.GONE);
                    break;
                }
            }
        });

        binding.foodNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                selectedFoodItem = null;
                updateUnitSpinner(null);
                
                // Show AI button if the text doesn't match any known food exactly
                boolean matchFound = false;
                for (FoodItem food : availableFoods) {
                    if (food.getName().equalsIgnoreCase(s.toString())) {
                        matchFound = true;
                        selectedFoodItem = food;
                        updateUnitSpinner(food);
                        break;
                    }
                }
                binding.aiIdentifyButton.setVisibility(matchFound ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateUnitSpinner(FoodItem food) {
        List<FoodItem.ServingUnit> units = new ArrayList<>();
        units.add(new FoodItem.ServingUnit("gram", 1.0));
        
        if (food != null && food.getCommonUnits() != null) {
            units.addAll(food.getCommonUnits());
        }

        ArrayAdapter<FoodItem.ServingUnit> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.unitSpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.searchAddButton.setOnClickListener(v -> onAddClicked());
        binding.aiIdentifyButton.setOnClickListener(v -> onAiIdentifyClicked());
        binding.backButton.setOnClickListener(v -> finish());
    }

    private void onAddClicked() {
        String foodName = binding.foodNameInput.getText().toString().trim();
        String quantityStr = binding.quantityInput.getText().toString().trim();

        if (foodName.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFoodItem == null) {
            // Try to find if it matches any available food (case insensitive)
            for (FoodItem food : availableFoods) {
                if (food.getName().equalsIgnoreCase(foodName)) {
                    selectedFoodItem = food;
                    break;
                }
            }
        }

        if (selectedFoodItem != null) {
            try {
                double quantity = Double.parseDouble(quantityStr);
                FoodItem.ServingUnit selectedUnit = (FoodItem.ServingUnit) binding.unitSpinner.getSelectedItem();
                double weightMultiplier = (selectedUnit != null) ? selectedUnit.getWeightG() : 1.0;
                double totalGrams = quantity * weightMultiplier;
                addFoodToLog(selectedFoodItem, totalGrams);
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.error_invalid_number), Toast.LENGTH_SHORT).show();
            }
        } else {
            // If not found in DB, suggest AI
            Toast.makeText(this, "Étel nem található. Használd az AI keresést!", Toast.LENGTH_LONG).show();
            binding.aiIdentifyButton.setVisibility(View.VISIBLE);
        }
    }

    private void onAiIdentifyClicked() {
        String foodName = binding.foodNameInput.getText().toString().trim();
        String quantityStr = binding.quantityInput.getText().toString().trim();
        
        if (foodName.isEmpty()) return;
        
        setLoading(true);

        Content prompt = new Content.Builder()
                .addText("Provide nutrition data for: " + foodName)
                .build();

        ListenableFuture<GenerateContentResponse> response = generativeModel.generateContent(prompt);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    try {
                        String resultText = result.getText();
                        if (resultText == null || resultText.isEmpty()) throw new Exception("Üres válasz");

                        String cleanJson = resultText.replaceAll("(?s)```(?:json)?\\n?|```", "").trim();

                        FoodItem aiFood = new Gson().fromJson(cleanJson, FoodItem.class);
                        aiFood.setAiGenerated(true);
                        if (aiFood.getName() == null || aiFood.getName().isEmpty()) aiFood.setName(foodName);
                        
                        // Set ID to name for Firestore consistency
                        aiFood.setId(aiFood.getName());

                        repository.saveFoodItemWithNameAsId(aiFood)
                                .addOnSuccessListener(aVoid -> {
                                    selectedFoodItem = aiFood;
                                    updateUnitSpinner(aiFood);
                                    binding.aiIdentifyButton.setVisibility(View.GONE);
                                    
                                    // AUTOMATICALLY ADD TO LOG IF QUANTITY IS PROVIDED
                                    if (!quantityStr.isEmpty()) {
                                        try {
                                            double quantity = Double.parseDouble(quantityStr);
                                            addFoodToLog(aiFood, quantity);
                                        } catch (NumberFormatException e) {
                                            setLoading(false);
                                            Toast.makeText(AddFoodActivity.this, "AI elemzés kész, de a mennyiség érvénytelen.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        setLoading(false);
                                        Toast.makeText(AddFoodActivity.this, "AI elemzés kész! Most már hozzáadhatod.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    setLoading(false);
                                    showErrorDialog("Mentési hiba", "Nem sikerült menteni az adatokat.");
                                });
                    } catch (Exception e) {
                        Log.e(TAG, "Parse error: " + e.getMessage());
                        setLoading(false);
                        showErrorDialog("Adat hiba", "Az AI válasza nem feldolgozható.");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Log.e(TAG, "AI Error detail: ", t);
                    showErrorDialog("AI Hiba", "Hiba történt: " + t.getMessage());
                });
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void addFoodToLog(FoodItem food, double totalGrams) {
        setLoading(true);
        ConsumedFood consumed = new ConsumedFood(food, totalGrams);
        
        repository.addConsumedFood(selectedDate, consumed)
                .addOnSuccessListener(aVoid -> {
                    setLoading(false);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Check if failure is due to being offline
                    if (e instanceof FirebaseFirestoreException && 
                        ((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.UNAVAILABLE) {
                        // Offline write is queued, we can safely finish
                        setLoading(false);
                        finish();
                    } else {
                        setLoading(false);
                        showErrorDialog("Hiba", "Nem sikerült a naplózás: " + e.getMessage());
                    }
                });
                
        // For task-based listeners in offline mode, SuccessListener might not fire immediately.
        // However, with set/update (non-transactional), Firestore queues the write.
        // To improve UX in offline mode, we can close the activity if we know it's queued.
        // Let's add a small delay or a check to see if we should just close.
        
        // Alternative: If the task hasn't completed in 500ms, assume it's queued offline and close
        binding.getRoot().postDelayed(() -> {
            if (isFinishing()) return;
            setLoading(false);
            finish();
        }, 800);
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void setLoading(boolean isLoading) {
        runOnUiThread(() -> {
            binding.loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.searchAddButton.setEnabled(!isLoading);
            binding.aiIdentifyButton.setEnabled(!isLoading);
        });
    }
}
