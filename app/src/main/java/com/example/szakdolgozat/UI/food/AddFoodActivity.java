package com.example.szakdolgozat.UI.food;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.szakdolgozat.BuildConfig;
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
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddFoodActivity extends AppCompatActivity {

    private static final String TAG = "AddFoodActivity";
    private ActivityAddfoodBinding binding;
    private FirestoreRepository repository;
    private String selectedDate;
    private GenerativeModelFutures generativeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddfoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UIUtils.hideSystemUI(getWindow());
        repository = FirestoreRepository.getInstance();

        // Konfiguráció JSON válaszhoz
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.responseMimeType = "application/json";
        GenerationConfig config = configBuilder.build();

        // Rendszer utasítás
        Content systemInstruction = new Content.Builder()
                .addText("Act as a nutritionist. Provide nutrition data for 100g. " +
                        "Return ONLY a raw JSON object. Fields: name (string), calories (double), " +
                        "carbs (double), protein (double), fat (double)")
                .build();

        // A listázás alapján a gemini-2.5-flash modell érhető el
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

        selectedDate = getIntent().getStringExtra("selected_date");
        if (selectedDate == null) {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }

        binding.searchAddButton.setOnClickListener(v -> onSearchAddClicked());
        binding.backButton.setOnClickListener(v -> finish());
    }

    private void onSearchAddClicked() {
        String foodName = binding.foodNameInput.getText().toString().trim();
        String quantityStr = binding.quantityInput.getText().toString().trim();

        if (foodName.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Kérlek tölts ki minden mezőt", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            performSearchAndProcess(foodName, quantity);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Érvénytelen mennyiség", Toast.LENGTH_SHORT).show();
        }
    }

    private void performSearchAndProcess(String name, double quantity) {
        setLoading(true);
        repository.searchFoodByName(name).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                FoodItem food = task.getResult().getDocuments().get(0).toObject(FoodItem.class);
                if (food != null) {
                    food.setId(task.getResult().getDocuments().get(0).getId());
                    addFoodToLog(food, quantity);
                } else {
                    generateWithAiAndSave(name, quantity);
                }
            } else {
                generateWithAiAndSave(name, quantity);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Search failed: " + e.getMessage());
            generateWithAiAndSave(name, quantity);
        });
    }

    private void generateWithAiAndSave(String name, double quantity) {
        setLoading(true);

        Content prompt = new Content.Builder()
                .addText("Provide nutrition data for: " + name)
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
                        if (aiFood.getName() == null || aiFood.getName().isEmpty()) aiFood.setName(name);

                        repository.saveFoodItemWithNameAsId(aiFood)
                                .addOnSuccessListener(aVoid -> addFoodToLog(aiFood, quantity))
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

    private void addFoodToLog(FoodItem food, double quantity) {
        ConsumedFood consumed = new ConsumedFood(food, quantity);
        repository.addConsumedFood(selectedDate, consumed)
                .addOnSuccessListener(aVoid -> {
                    setLoading(false);
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showErrorDialog("Hiba", "Nem sikerült a naplózás.");
                });
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
        });
    }
}
