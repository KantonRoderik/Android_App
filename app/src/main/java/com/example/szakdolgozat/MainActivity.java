package com.example.szakdolgozat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBarKaloria, progressBarSzenhidrat, progressBarFeherje, progressBarZsir;
    private TextView textViewKaloria, textViewSzenhidrat, textViewFeherje, textViewZsir;
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializálás
        progressBarKaloria = findViewById(R.id.progressBar_kaloria);
        progressBarSzenhidrat = findViewById(R.id.progressBar_szenhidrat);
        progressBarFeherje = findViewById(R.id.progressBar_feherje);
        progressBarZsir = findViewById(R.id.progressBar_zsir);

        textViewKaloria = findViewById(R.id.textView_kaloria);
        textViewSzenhidrat = findViewById(R.id.textView_szenhidrat);
        textViewFeherje = findViewById(R.id.textView_feherje);
        textViewZsir = findViewById(R.id.textView_zsir);



        // Firebase inicializálás
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Napi adatok betöltése
        loadDailyData();


    }

    private void loadDailyData() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        databaseReference.child("users").child(userId).child("dailyEntries").child(today)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Ha van már ma bejegyzés
                            DailyEntry dailyEntry = dataSnapshot.getValue(DailyEntry.class);
                            if (dailyEntry != null) {
                                updateUI(dailyEntry);
                            }
                        } else {
                            // Ha nincs még ma bejegyzés, alapértékek beállítása
                            setDefaultValues();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "Hiba az adatok betöltésekor", Toast.LENGTH_SHORT).show();
                        setDefaultValues();
                    }
                });
    }

    private void updateUI(DailyEntry dailyEntry) {
        // Felhasználó napi céljainak lekérdezése (a profilból)
        databaseReference.child("users").child(userId).child("dailyGoals")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                           DailyGoals goals = dataSnapshot.getValue(DailyGoals.class);
                            if (goals != null) {
                                // Százalékos értékek kiszámítása
                                int kaloriaPercent = (int) ((dailyEntry.getTotalCalories() / goals.getCalories()) * 100);
                                int szenhidratPercent = (int) ((dailyEntry.getTotalCarbs() / goals.getCarbs()) * 100);
                                int feherjePercent = (int) ((dailyEntry.getTotalProtein() / goals.getProtein()) * 100);
                                int zsirPercent = (int) ((dailyEntry.getTotalFat() / goals.getFat()) * 100);

                                // ProgressBar-ok frissítése (max 100%)
                                progressBarKaloria.setProgress(Math.min(kaloriaPercent, 100));
                                progressBarSzenhidrat.setProgress(Math.min(szenhidratPercent, 100));
                                progressBarFeherje.setProgress(Math.min(feherjePercent, 100));
                                progressBarZsir.setProgress(Math.min(zsirPercent, 100));

                                // TextView-ek frissítése
                                textViewKaloria.setText(String.format(Locale.getDefault(),
                                        "Kalória: %d/%d kcal (%d%%)",
                                        (int) dailyEntry.getTotalCalories(),
                                        (int) goals.getCalories(),
                                        kaloriaPercent));

                                textViewSzenhidrat.setText(String.format(Locale.getDefault(),
                                        "Szénhidrát: %d/%d g (%d%%)",
                                        (int) dailyEntry.getTotalCarbs(),
                                        (int) goals.getCarbs(),
                                        szenhidratPercent));

                                textViewFeherje.setText(String.format(Locale.getDefault(),
                                        "Fehérje: %d/%d g (%d%%)",
                                        (int) dailyEntry.getTotalProtein(),
                                        (int) goals.getProtein(),
                                        feherjePercent));

                                textViewZsir.setText(String.format(Locale.getDefault(),
                                        "Zsír: %d/%d g (%d%%)",
                                        (int) dailyEntry.getTotalFat(),
                                        (int) goals.getFat(),
                                        zsirPercent));
                            }
                        } else {
                            // Ha nincsenek célok megadva, alapértékek használata
                            setDefaultValues();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "Hiba a célok betöltésekor", Toast.LENGTH_SHORT).show();
                        setDefaultValues();
                    }
                });
    }

    private void setDefaultValues() {
        // Alapértelmezett értékek, ha nincsenek adatok
        progressBarKaloria.setProgress(0);
        progressBarSzenhidrat.setProgress(0);
        progressBarFeherje.setProgress(0);
        progressBarZsir.setProgress(0);

        textViewKaloria.setText("Kalória: 0%");
        textViewSzenhidrat.setText("Szénhidrát: 0%");
        textViewFeherje.setText("Fehérje: 0%");
        textViewZsir.setText("Zsír: 0%");
    }

    public void Logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, Login.class));
        finish();
    }

    public void openAddFoodActivity(View view) {
        startActivity(new Intent(MainActivity.this, AddFoodActivity.class));
    }

    public void Profile(View view) {
        startActivity(new Intent(MainActivity.this,Profile.class));
    }

}

// Segédosztályok a Firebase adatstruktúrához


