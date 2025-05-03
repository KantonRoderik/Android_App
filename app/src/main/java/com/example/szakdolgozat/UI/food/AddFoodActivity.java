package com.example.szakdolgozat.UI.food;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.models.FoodItem;
import com.example.szakdolgozat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddFoodActivity extends AppCompatActivity {

    private Spinner foodSpinner;
    private EditText quantityInput;
    private Button addButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<FoodItem> foodItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        foodSpinner = findViewById(R.id.food_spinner);
        quantityInput = findViewById(R.id.quantity_input);
        addButton = findViewById(R.id.add_button);

        loadFoodItems();

        addButton.setOnClickListener(v -> addSelectedFood());
    }

    private void loadFoodItems() {
        db.collection("foods").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        foodItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FoodItem food = document.toObject(FoodItem.class);
                            food.setId(document.getId());
                            foodItems.add(food);
                        }
                        setupSpinner();
                    } else {
                        Toast.makeText(this, "Hiba az ételek betöltésekor", Toast.LENGTH_SHORT).show();
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
        foodSpinner.setAdapter(adapter);
    }

    private void addSelectedFood() {
        String quantityStr = quantityInput.getText().toString().trim();






        // Mennyiség ellenőrzése
        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Adja meg a mennyiséget!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "A mennyiség nem lehet nulla vagy negatív!", Toast.LENGTH_SHORT).show();
                return;
            }

            FoodItem selectedFood = foodItems.get(foodSpinner.getSelectedItemPosition());

            // Ellenőrizd, hogy a felhasználó be van-e jelentkezve
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Nincs bejelentkezve!", Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = mAuth.getCurrentUser().getUid();

            // Tápanyagok kiszámolása
            double calories = selectedFood.getCalories() * quantity / 100;
            double carbs = selectedFood.getCarbs() * quantity / 100;
            double fat = selectedFood.getFat() * quantity / 100;
            double protein = selectedFood.getProtein() * quantity / 100;

            // ConsumedFood létrehozása
            Map<String, Object> consumedFood = new HashMap<>();
            consumedFood.put("foodId", selectedFood.getId());
            consumedFood.put("foodName", selectedFood.getName());
            consumedFood.put("quantity", quantity);
            consumedFood.put("calories", calories);
            consumedFood.put("carbs", carbs);
            consumedFood.put("fat", fat);
            consumedFood.put("protein", protein);

            // Dátum formázása
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            Log.d("Firestore", "Dátum: " + today); // Ellenőrizd a Logcat-ben



            // Firestore műveletek
            db.collection("users").document(userId)
                    .collection("dailyEntries").document(today)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // FRISSÍTÉS: Létező nap frissítése
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("consumedFoods." + System.currentTimeMillis(), consumedFood);
                            updates.put("totalCalories", FieldValue.increment(calories));
                            updates.put("totalCarbs", FieldValue.increment(carbs));
                            updates.put("totalFat", FieldValue.increment(fat));
                            updates.put("totalProtein", FieldValue.increment(protein));

                            db.collection("users").document(userId)
                                    .collection("dailyEntries").document(today)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Sikeres Frissítés", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Hiba a frissítéskor: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            // LÉTREHOZÁS: Új nap indítása
                            Map<String, Object> dailyEntry = new HashMap<>();
                            dailyEntry.put("date", today);
                            dailyEntry.put("totalCalories", calories);
                            dailyEntry.put("totalCarbs", carbs);
                            dailyEntry.put("totalFat", fat);
                            dailyEntry.put("totalProtein", protein);

                            Map<String, Object> consumedFoods = new HashMap<>();
                            consumedFoods.put(String.valueOf(System.currentTimeMillis()), consumedFood);
                            dailyEntry.put("consumedFoods", consumedFoods);

                            db.collection("users").document(userId)
                                    .collection("dailyEntries").document(today)
                                    .set(dailyEntry)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Sikeres létrehozás!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Hiba a létrehozáskor: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Hiba az ellenőrzéskor: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Érvénytelen számformátum!", Toast.LENGTH_SHORT).show();
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this, "Nincs kiválasztva étel!", Toast.LENGTH_SHORT).show();
        }
    }
}