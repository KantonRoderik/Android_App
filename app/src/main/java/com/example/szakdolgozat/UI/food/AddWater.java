package com.example.szakdolgozat.UI.food;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.UI.auth.Login;
import com.example.szakdolgozat.UI.main.MainActivity;
import com.example.szakdolgozat.models.FoodItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddWater{



    public FirebaseFirestore db;
    public FirebaseAuth mAuth;

    public AddWater() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }


    public void addWater(Context context) {
        int adag_viz = 100;
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(context, "Nincs bejelentkezve!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DocumentReference docRef = db.collection("users").document(userId)
                .collection("dailyEntries").document(today);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(docRef);
            Map<String, Object> updates = new HashMap<>();

            long currentWater = snapshot.getLong("totalWater") != null ? snapshot.getLong("totalWater") : 0;
            updates.put("totalWater", currentWater + adag_viz);

            if (!snapshot.exists()) {
                updates.put("date", today);
                updates.put("totalCalories", 0);
                updates.put("totalCarbs", 0.0);
                updates.put("totalFat", 0.0);
                updates.put("totalProtein", 0.0);
            }

            transaction.set(docRef, updates, SetOptions.merge());
            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(context, "+100ml víz hozzáadva! 💧", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Hiba: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

}