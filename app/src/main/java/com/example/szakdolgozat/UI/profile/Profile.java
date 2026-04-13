package com.example.szakdolgozat.UI.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.UI.main.MainActivity;
import com.example.szakdolgozat.databinding.ActivityProfileBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Locale;

/**
 * Activity for displaying user profile information.
 */
public class Profile extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private ActivityProfileBinding binding;
    private FirestoreRepository repository;
    private ListenerRegistration profileListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = FirestoreRepository.getInstance();

        setupClickListeners();
        startListeningToProfile();
    }

    private void setupClickListeners() {
        binding.Szerkeszt.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileSzerkesztes.class));
            finish();
        });

        binding.celok.setOnClickListener(v -> {
            startActivity(new Intent(this, DailyGoalsSzerkesztes.class));
            finish();
        });

        binding.backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void startListeningToProfile() {
        profileListener = repository.listenToUserData((value, error) -> {
            if (error != null) {
                Log.e(TAG, "Listen failed.", error);
                return;
            }

            if (value != null && value.exists()) {
                updateUI(value);
            }
        });
    }

    private void updateUI(DocumentSnapshot value) {
        String name = value.getString("nev");
        String email = value.getString("email");
        String sulyStr = value.getString("suly");
        String magassagStr = value.getString("magassag");
        String korStr = value.getString("kor");
        String nem = value.getString("nem");

        binding.teljesNev.setText(name);
        binding.EmailInput.setText(email);
        
        binding.suly.setText(sulyStr != null ? sulyStr + " " + getString(R.string.unit_kg) : "0 " + getString(R.string.unit_kg));
        binding.magassag.setText(magassagStr != null ? magassagStr + " " + getString(R.string.unit_cm) : "0 " + getString(R.string.unit_cm));
        binding.kor.setText(korStr != null ? korStr + " " + getString(R.string.unit_years) : "0 " + getString(R.string.unit_years));
        binding.nem.setText(nem);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileListener != null) {
            profileListener.remove();
        }
    }
}
