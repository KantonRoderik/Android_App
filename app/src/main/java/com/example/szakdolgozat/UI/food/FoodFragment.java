package com.example.szakdolgozat.UI.food;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.szakdolgozat.BuildConfig;
import com.example.szakdolgozat.R;
import com.example.szakdolgozat.databinding.FragmentFoodBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment {

    private FragmentFoodBinding binding;
    private FirestoreRepository repository;
    private GenerativeModelFutures generativeModel;
    private List<FoodItem> availableFoods = new ArrayList<>();
    private FoodItem selectedFoodItem = null;
    private String selectedDate;

    public static FoodFragment newInstance(String date) {
        FoodFragment fragment = new FoodFragment();
        Bundle args = new Bundle();
        args.putString("selected_date", date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedDate = getArguments().getString("selected_date");
        }
        repository = FirestoreRepository.getInstance();
        setupAi();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFoodBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAutocomplete();
        setupListeners();
        updateUnitSpinner(null);
    }

    private void setupAi() {
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.responseMimeType = "application/json";
        GenerationConfig config = configBuilder.build();

        Content systemInstruction = new Content.Builder()
                .addText("Act as a fitness and nutrition expert. Provide nutrition data for 100g and common units." +
                        " Return ONLY a raw JSON object. Fields: name, calories, carbs, protein, fat, commonUnits (array).")
                .build();

        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
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

    private void setupAutocomplete() {
        repository.getAllFoods().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                updateFoodList(task.getResult());
            } else {
                // Offline fallback: try cache
                FirebaseFirestore.getInstance().collection("foods")
                        .get(Source.CACHE)
                        .addOnCompleteListener(cacheTask -> {
                            if (cacheTask.isSuccessful() && cacheTask.getResult() != null) {
                                updateFoodList(cacheTask.getResult());
                            }
                        });
            }
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

    private void updateFoodList(QuerySnapshot queryDocumentSnapshots) {
        if (queryDocumentSnapshots == null) return;
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

        if (getContext() != null && binding != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, foodNames);
            binding.foodNameInput.setAdapter(adapter);
        }
    }

    private void updateUnitSpinner(FoodItem food) {
        List<FoodItem.ServingUnit> units = new ArrayList<>();
        units.add(new FoodItem.ServingUnit("gram", 1.0));
        
        if (food != null && food.getCommonUnits() != null) {
            units.addAll(food.getCommonUnits());
        }

        if (getContext() != null && binding != null) {
            ArrayAdapter<FoodItem.ServingUnit> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, units);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.unitSpinner.setAdapter(adapter);
        }
    }

    private void setupListeners() {
        binding.aiIdentifyButton.setOnClickListener(v -> onAiIdentifyClicked());
    }

    public void onAddClicked() {
        if (binding == null) return;
        String foodName = binding.foodNameInput.getText().toString().trim();
        String quantityStr = binding.quantityInput.getText().toString().trim();

        if (foodName.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(getContext(), "Kérlek töltsd ki az összes mezőt!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFoodItem != null) {
            try {
                double quantity = Double.parseDouble(quantityStr);
                FoodItem.ServingUnit selectedUnit = (FoodItem.ServingUnit) binding.unitSpinner.getSelectedItem();
                double weightMultiplier = (selectedUnit != null) ? selectedUnit.getWeightG() : 1.0;
                addFoodToLog(selectedFoodItem, quantity * weightMultiplier);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), getString(R.string.error_invalid_number), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Étel nem található. Használd az AI keresést!", Toast.LENGTH_LONG).show();
        }
    }

    private void onAiIdentifyClicked() {
        String foodName = binding.foodNameInput.getText().toString().trim();
        if (foodName.isEmpty()) return;
        
        setLoading(true);
        Content prompt = new Content.Builder()
                .addText("Provide nutrition data for: " + foodName)
                .build();

        ListenableFuture<GenerateContentResponse> response = generativeModel.generateContent(prompt);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    try {
                        String text = result.getText();
                        if (text == null) throw new Exception("AI response empty");
                        String cleanJson = text.replaceAll("(?s)```(?:json)?\\n?|```", "").trim();
                        FoodItem aiFood = new Gson().fromJson(cleanJson, FoodItem.class);
                        aiFood.setAiGenerated(true);
                        if (aiFood.getName() == null) aiFood.setName(foodName);
                        aiFood.setId(aiFood.getName());

                        repository.saveFoodItemWithNameAsId(aiFood)
                                .addOnCompleteListener(task -> {
                                    setLoading(false);
                                    if (isAdded()) {
                                        selectedFoodItem = aiFood;
                                        updateUnitSpinner(aiFood);
                                        binding.aiIdentifyButton.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "AI elemzés kész!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (Exception e) {
                        setLoading(false);
                        showErrorDialog("Hiba", "Az AI válasza nem feldolgozható.");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    setLoading(false);
                    showErrorDialog("AI Hiba", "Hiba történt (internet szükséges): " + t.getMessage());
                });
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void addFoodToLog(FoodItem food, double totalGrams) {
        setLoading(true);
        ConsumedFood consumed = new ConsumedFood(food, totalGrams);

        // Offline fallback logic: don't wait forever for network confirm
        Runnable fallbackFinish = () -> {
            if (isAdded() && binding != null && binding.loadingIndicator.getVisibility() == View.VISIBLE) {
                setLoading(false);
                Toast.makeText(getContext(), "Étel mentve (offline mód)", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) getActivity().finish();
            }
        };
        binding.getRoot().postDelayed(fallbackFinish, 1500);

        repository.addConsumedFood(selectedDate, consumed)
                .addOnCompleteListener(task -> {
                    binding.getRoot().removeCallbacks(fallbackFinish);
                    if (isAdded()) {
                        setLoading(false);
                        if (task.isSuccessful()) {
                            if (getActivity() != null) getActivity().finish();
                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFirestoreException && ((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.UNAVAILABLE) {
                                if (getActivity() != null) getActivity().finish();
                            } else {
                                Toast.makeText(getContext(), "Mentve (szinkronizálás folyamatban)", Toast.LENGTH_SHORT).show();
                                if (getActivity() != null) getActivity().finish();
                            }
                        }
                    }
                });
    }

    private void showErrorDialog(String title, String message) {
        if (getContext() != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void setLoading(boolean isLoading) {
        if (getActivity() == null || binding == null) return;
        getActivity().runOnUiThread(() -> {
            binding.loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.aiIdentifyButton.setEnabled(!isLoading);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
