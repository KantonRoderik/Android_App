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
import com.example.szakdolgozat.databinding.FragmentExerciseBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.example.szakdolgozat.models.Exercise;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ExerciseFragment extends Fragment {

    private FragmentExerciseBinding binding;
    private FirestoreRepository repository;
    private GenerativeModelFutures generativeModel;
    private final List<Exercise> availableExercises = new ArrayList<>();
    private Exercise selectedExercise = null;
    private String selectedDate;
    private double userWeight = 75.0; // Alapértelmezett súly

    public static ExerciseFragment newInstance(String date) {
        ExerciseFragment fragment = new ExerciseFragment();
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
        fetchUserWeight();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExerciseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAutocomplete();
        setupListeners();
    }

    private void setupAi() {
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.responseMimeType = "application/json";
        GenerationConfig config = configBuilder.build();

        Content systemInstruction = new Content.Builder()
                .addText("Act as a fitness expert. Provide MET (Metabolic Equivalent of Task) value for exercises. " +
                        "Return ONLY a single raw JSON object. " +
                        "Example format: {\"name\": \"Futás\", \"met_value\": 8.0, \"category\": \"Kardió\"}. " +
                        "Do not include any other text or markdown outside the JSON.")
                .build();

        GenerativeModel gm = new GenerativeModel(
                "gemini-2.5-flash",
                BuildConfig.GEMINI_API_KEY,
                config,
                null,
                new RequestOptions(30000L, "v1beta"),
                null, null, systemInstruction
        );
        generativeModel = GenerativeModelFutures.from(gm);
    }

    private void fetchUserWeight() {
        repository.getUserData().addOnSuccessListener(doc -> {
            if (doc.exists() && doc.contains("suly")) {
                try {
                    String weightStr = doc.getString("suly");
                    if (weightStr != null && !weightStr.isEmpty()) {
                        userWeight = Double.parseDouble(weightStr);
                    }
                } catch (Exception e) {
                    userWeight = 75.0;
                }
            }
        });
    }

    private void setupAutocomplete() {
        repository.getAllExercises().addOnSuccessListener(querySnapshot -> {
            availableExercises.clear();
            List<String> ids = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                Exercise ex = doc.toObject(Exercise.class);
                if (ex != null) {
                    ex.setId(doc.getId());
                    availableExercises.add(ex);
                    ids.add(doc.getId());
                }
            }
            if (getContext() != null && binding != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, ids);
                binding.exerciseNameInput.setThreshold(1);
                binding.exerciseNameInput.setAdapter(adapter);
            }
        });

        binding.exerciseNameInput.setOnItemClickListener((parent, view, position, id) -> {
            String selectedId = (String) parent.getItemAtPosition(position);
            for (Exercise ex : availableExercises) {
                if (ex.getId().equals(selectedId)) {
                    selectedExercise = ex;
                    binding.aiCalculateExerciseButton.setVisibility(View.GONE);
                    break;
                }
            }
        });

        binding.exerciseNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                selectedExercise = null;
                boolean matchFound = false;
                for (Exercise ex : availableExercises) {
                    if (ex.getId().equalsIgnoreCase(s.toString())) {
                        matchFound = true;
                        selectedExercise = ex;
                        break;
                    }
                }
                if (binding != null) {
                    binding.aiCalculateExerciseButton.setVisibility(matchFound ? View.GONE : View.VISIBLE);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupListeners() {
        binding.aiCalculateExerciseButton.setOnClickListener(v -> onAiCalculateClicked());
    }

    private void onAiCalculateClicked() {
        String name = binding.exerciseNameInput.getText().toString().trim();
        if (name.isEmpty()) return;

        setLoading(true);
        Content prompt = new Content.Builder().addText("Provide MET data for: " + name).build();

        ListenableFuture<GenerateContentResponse> response = generativeModel.generateContent(prompt);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    try {
                        String text = result.getText();
                        if (text == null) throw new Exception("AI válasz szövege üres");

                        String cleanJson = text.replaceAll("(?s)```(?:json)?\\n?|```", "").trim();
                        Exercise aiEx = new Gson().fromJson(cleanJson, Exercise.class);
                        
                        if (aiEx == null || aiEx.getMetValue() <= 0) {
                            throw new Exception("Érvénytelen vagy hiányos AI válasz.");
                        }

                        if (aiEx.getName() == null) aiEx.setName(name);
                        aiEx.setId(aiEx.getName());

                        repository.saveExerciseItemWithNameAsId(aiEx)
                                .addOnSuccessListener(aVoid -> {
                                    setLoading(false);
                                    selectedExercise = aiEx;
                                    if (binding != null) {
                                        binding.aiCalculateExerciseButton.setVisibility(View.GONE);
                                    }
                                    Toast.makeText(getContext(), "AI elemzés kész!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    setLoading(false);
                                    showErrorDialog("Hiba", "Nem sikerült menteni az AI eredményt: " + e.getMessage());
                                });
                    } catch (Exception e) {
                        setLoading(false);
                        showErrorDialog("Hiba", "Az AI válasza nem feldolgozható: " + e.getMessage());
                    }
                });
            }
            @Override public void onFailure(@NonNull Throwable t) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        setLoading(false);
                        showErrorDialog("Hiba", "AI hiba: " + t.getMessage());
                    });
                }
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    public void onAddExerciseClicked() {
        if (binding == null) return;
        String durationStr = binding.durationInput.getText().toString().trim();
        if (selectedExercise == null || durationStr.isEmpty()) {
            Toast.makeText(getContext(), "Válassz mozgást és adj meg időt!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double duration = Double.parseDouble(durationStr);
            // Kalória számítás: 0.0175 * MET * testsúly (kg) * idő (perc)
            double calories = 0.0175 * selectedExercise.getMetValue() * userWeight * duration;

            // Új példány a naplózáshoz
            Exercise logExercise = new Exercise(selectedExercise.getName(), selectedExercise.getMetValue(), selectedExercise.getCategory());
            logExercise.setId(selectedExercise.getId());
            logExercise.setDuration(duration);
            logExercise.setCaloriesBurned(calories);

            setLoading(true);
            repository.addExerciseToLog(selectedDate, logExercise)
                    .addOnSuccessListener(aVoid -> {
                        setLoading(false);
                        if (getActivity() != null) getActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        setLoading(false);
                        showErrorDialog("Hiba", "Nem sikerült a mentés.");
                    });
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), getString(R.string.error_invalid_number), Toast.LENGTH_SHORT).show();
        }
    }

    private void setLoading(boolean isLoading) {
        if (getActivity() == null || binding == null) return;
        getActivity().runOnUiThread(() -> {
            binding.loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.aiCalculateExerciseButton.setEnabled(!isLoading);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
