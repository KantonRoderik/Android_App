package com.example.szakdolgozat.UI.profile;

import static com.example.szakdolgozat.UI.auth.Register.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DailyGoalsSzerkesztes  extends AppCompatActivity {


    EditText Kaloria, Szenhidrat, Zsir, Feherje, Viz;

    Button Mentes;


    FirebaseAuth mAuth;
    String userID;
    FirebaseFirestore db;

    int kcal;
    int carbs;
    int protein;
    int fat;
    int water;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dailygoals_edit);


        Kaloria = findViewById(R.id.caloria);
        Szenhidrat = findViewById(R.id.carbs);
        Zsir = findViewById(R.id.fat);
        Feherje = findViewById(R.id.protein);
        Viz = findViewById(R.id.water);
        Mentes = findViewById(R.id.Mentes);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();


        Mentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String kaloria = Kaloria.getText().toString().trim();
                String szenhidrat = Szenhidrat.getText().toString().trim();
                String zsir = Zsir.getText().toString().trim();
                String feherje = Feherje.getText().toString().trim();
                String viz = Viz.getText().toString().trim();



                db.collection("users").document(userID)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // A dailyGoals HashMap kinyerése
                                    Map<String, Object> dailyGoals = (Map<String, Object>) document.get("dailyGoals");

                                    if (dailyGoals != null) {
                                        // Adatok használata például:
                                         kcal = ((Long) dailyGoals.get("calories")).intValue();
                                         carbs = ((Long) dailyGoals.get("carbs")).intValue();
                                         protein = ((Long) dailyGoals.get("protein")).intValue();
                                         fat = ((Long) dailyGoals.get("fat")).intValue();
                                         water = ((Long) dailyGoals.get("water")).intValue();

                                        Log.d(TAG, "Minden adat kinyerve: " + kcal + " " + carbs + " " + protein + " " + fat + " " + water);
                                    } else {
                                        Log.d(TAG, "A dailyGoals még nincs beállítva");
                                    }
                                }
                            } else {
                                Log.w(TAG, "Hiba történt a lekérdezésben", task.getException());
                            }
                        });


                // ÚJ: Alapértelmezett napi célok hozzáadása
                Map<String, Object> dailyGoals = new HashMap<>();
                dailyGoals.put("calories", kcal);
                dailyGoals.put("carbs", carbs);
                dailyGoals.put("protein", protein);
                dailyGoals.put("fat", fat);
                dailyGoals.put("water", water);



                if (!TextUtils.isEmpty(kaloria)) {
                    try {
                        int mennyiseg = Integer.parseInt(kaloria);
                        //    db.collection("users").document(userID).update("suly", kaloria);
                        dailyGoals.put("calories", mennyiseg);
                    } catch (NumberFormatException e) {
                        Kaloria.setError("Érvényes számot adj meg!");
                    }
                }


                if (!TextUtils.isEmpty(szenhidrat)) {
                    try {
                        int mennyiseg = Integer.parseInt(szenhidrat);
                        //  db.collection("users").document(userID).update("suly", szenhidrat);
                        dailyGoals.put("carbs", mennyiseg);
                    } catch (NumberFormatException e) {
                        Szenhidrat.setError("Érvényes számot adj meg!");
                    }
                }


                if (!TextUtils.isEmpty(zsir)) {
                    try {
                        int mennyiseg = Integer.parseInt(zsir);
                        // db.collection("users").document(userID).update("magassag", zsir);
                        dailyGoals.put("fat", mennyiseg);
                    } catch (NumberFormatException e) {
                        Zsir.setError("Érvényes számot adj meg!");
                    }
                }


                if (!TextUtils.isEmpty(feherje)) {
                    try {
                        int mennyiseg = Integer.parseInt(feherje);
                        //db.collection("users").document(userID).update("kor", feherje);
                        dailyGoals.put("protein", mennyiseg);
                    } catch (NumberFormatException e) {
                        Feherje.setError("Érvényes számot adj meg!");
                    }
                }

                if (!TextUtils.isEmpty(viz)) {
                    try {
                        int mennyiseg = Integer.parseInt(viz);
                        //db.collection("users").document(userID).update("kor", viz);
                        dailyGoals.put("water", mennyiseg);
                    } catch (NumberFormatException e) {
                        Viz.setError("Érvényes számot adj meg!");
                    }
                }

                db.collection("users").document(userID)
                        .update("dailyGoals", dailyGoals)
                        .addOnSuccessListener(aVoid -> {
                            // Az Activity kontextusának lekérése (pl. MyActivity.this)
                            Toast.makeText(DailyGoalsSzerkesztes.this, "Sikeres frissítés!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(DailyGoalsSzerkesztes.this, "Hiba történt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });





                startActivity(new Intent(DailyGoalsSzerkesztes.this, Profile.class));
                finish();

            }
        });


    }
}


