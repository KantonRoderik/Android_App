package com.example.szakdolgozat;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Arrays;
import java.util.List;

public class FoodUploadActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_upload);

        db = FirebaseFirestore.getInstance();
        uploadDefaultFoods();
    }

    private void uploadDefaultFoods() {
        List<FoodItem> defaultFoods = Arrays.asList(
                new FoodItem("Alma", 52, 14, 0.3, 0.2),
                new FoodItem("Banán", 89, 22.8, 1.1, 0.3),
                new FoodItem("Csirkemell", 165, 0, 31, 3.6),
                new FoodItem("Rizs", 130, 28, 2.7, 0.3),
                new FoodItem("Tojás", 155, 1.1, 13, 11)
        );

        for (FoodItem food : defaultFoods) {
            db.collection("foods").add(food)
                    .addOnSuccessListener(documentReference -> {
                        food.setId(documentReference.getId());
                        db.collection("foods").document(documentReference.getId()).set(food);
                    });
        }
    }
}