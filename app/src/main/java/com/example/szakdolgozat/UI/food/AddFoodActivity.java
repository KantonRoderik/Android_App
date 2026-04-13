package com.example.szakdolgozat.UI.food;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.databinding.ActivityAddfoodBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.example.szakdolgozat.models.ConsumedFood;
import com.example.szakdolgozat.models.FoodItem;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity for adding a consumed food item to the daily log.
 */
public class AddFoodActivity extends AppCompatActivity {

    private static final String TAG = "AddFoodActivity";

    private ActivityAddfoodBinding binding;
    private FirestoreRepository repository;
    private final List<FoodItem> foodItems = new ArrayList<>();
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddfoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = FirestoreRepository.getInstance();

        // Get the date passed from MainActivity, default to today if not provided
        selectedDate = getIntent().getStringExtra("selected_date");
        if (selectedDate == null) {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }

        loadFoodItems();

        binding.addButton.setOnClickListener(v -> onAddButtonClicked());
    }

    private void loadFoodItems() {
        repository.getAllFoods()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        foodItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FoodItem food = document.toObject(FoodItem.class);
                            food.setId(document.getId());
                            foodItems.add(food);
                        }
                        setupSpinner();
                    } else {
                        Toast.makeText(this, getString(R.string.food_load_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSpinner() {
        List<String> foodNames = new ArrayList<>();
        for (FoodItem food : foodItems) {
            foodNames.add(food.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, foodNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.foodSpinner.setAdapter(adapter);
    }

    private void onAddButtonClicked() {
        String quantityStr = binding.quantityInput.getText().toString().trim();

        if (quantityStr.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_quantity_required), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, getString(R.string.error_invalid_quantity), Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedPosition = binding.foodSpinner.getSelectedItemPosition();
            if (selectedPosition == Spinner.INVALID_POSITION || foodItems.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_no_food_selected), Toast.LENGTH_SHORT).show();
                return;
            }

            FoodItem selectedFood = foodItems.get(selectedPosition);
            saveConsumedFood(selectedFood, quantity);

        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.error_invalid_number), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveConsumedFood(FoodItem foodItem, double quantity) {
        ConsumedFood consumedFood = new ConsumedFood(foodItem, quantity);

        repository.addConsumedFood(selectedDate, consumedFood)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, getString(R.string.food_add_success), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding food: " + e.getMessage());
                    Toast.makeText(this, getString(R.string.food_add_error) + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
