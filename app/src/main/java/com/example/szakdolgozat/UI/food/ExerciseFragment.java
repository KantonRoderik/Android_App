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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
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
                "gemini-1.5-flash",
                BuildConfig.GEMINI_API_KEY,
                config,
                null,
                new RequestOptions(30000L, "v1beta"),
                null, null, systemInstruction
        );
        generativeModel = GenerativeModelFutures.from(gm);
    }

    private void fetchUserWeight() {
        // addOnCompleteListener használata a betöltési állapot robusztusabb kezeléséhez
        repository.getUserData().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                updateWeightFromDoc(task.getResult());
            } else {
                // Offline fallback: explicit gyorsítótár lekérés
                String uid = (repository.getCurrentUser() != null) ? repository.getCurrentUser().getUid() : null;
                if (uid != null) {
                    FirebaseFirestore.getInstance().collection("users").document(uid)
                            .get(Source.CACHE)
                            .addOnSuccessListener(this::updateWeightFromDoc);
                }
            }
        });
    }

    private void updateWeightFromDoc(DocumentSnapshot doc) {
        if (doc != null && doc.exists() && doc.contains("suly")) {
            try {
                String weightStr = doc.getString("suly");
                if (weightStr != null && !weightStr.isEmpty()) {
                    userWeight = Double.parseDouble(weightStr);
                }
            } catch (Exception ignored) {}
        }
    }

    private void setupAutocomplete() {
        // Autocomplete Cache kezelése addOnCompleteListener-rel
        repository.getAllExercises().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                updateExerciseList(task.getResult());
            } else {
                // Ha offline vagy nincs adat, próbáljuk meg a helyi gyorsítótárból
                FirebaseFirestore.getInstance().collection("exercises")
                        .get(Source.CACHE)
                        .addOnCompleteListener(cacheTask -> {
                            if (cacheTask.isSuccessful() && cacheTask.getResult() != null) {
                                updateExerciseList(cacheTask.getResult());
                            }
                        });
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

    private void updateExerciseList(QuerySnapshot querySnapshot) {
        if (querySnapshot == null) return;
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
                            throw new Exception("Érvénytelen AI válasz adatok.");
                        }

                        if (aiEx.getName() == null) aiEx.setName(name);
                        aiEx.setId(aiEx.getName());

                        // Végtelen töltés fix: addOnCompleteListener használata
                        repository.saveExerciseItemWithNameAsId(aiEx)
                                .addOnCompleteListener(task -> {
                                    setLoading(false);
                                    if (isAdded()) {
                                        selectedExercise = aiEx;
                                        if (binding != null) {
                                            binding.aiCalculateExerciseButton.setVisibility(View.GONE);
                                        }
                                        Toast.makeText(getContext(), "AI elemzés kész!", Toast.LENGTH_SHORT).show();
                                    }
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
                        setLoading(false); // Végtelen töltés fix
                        showErrorDialog("Hiba", "AI hiba (internet szükséges): " + t.getMessage());
                    });
                }
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    public void onAddExerciseClicked() {
        if (binding == null) return;
        
        String exerciseName = binding.exerciseNameInput.getText().toString().trim();
        String durationStr = binding.durationInput.getText().toString().trim();

        if (exerciseName.isEmpty() || durationStr.isEmpty()) {
            Toast.makeText(getContext(), "Kérlek töltsd ki az összes mezőt!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedExercise == null) {
            Toast.makeText(getContext(), "Válassz egy érvényes edzést!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int duration = Integer.parseInt(durationStr);
            double calculatedCalories = 0.0175 * selectedExercise.getMetValue() * userWeight * duration;

            setLoading(true);
            
            // Offline UX fallback: ne várjunk a hálózati visszaigazolásra, ha nincs net
            Runnable fallbackFinish = () -> {
                if (isAdded() && binding != null && binding.loadingIndicator.getVisibility() == View.VISIBLE) {
                    setLoading(false);
                    Toast.makeText(getContext(), "Mozgás rögzítve (offline mentés)", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) getActivity().finish();
                }
            };
            binding.getRoot().postDelayed(fallbackFinish, 1500);

            repository.addExerciseToDailyLog(selectedDate, exerciseName, duration, calculatedCalories)
                    .addOnCompleteListener(task -> {
                        binding.getRoot().removeCallbacks(fallbackFinish);
                        if (isAdded()) {
                            setLoading(false); // Végtelen töltés fix
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Mozgás rögzítve, kalóriák levonva!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Mozgás mentve (szinkronizálás folyamatban)", Toast.LENGTH_SHORT).show();
                            }
                            if (getActivity() != null) getActivity().finish();
                        }
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
