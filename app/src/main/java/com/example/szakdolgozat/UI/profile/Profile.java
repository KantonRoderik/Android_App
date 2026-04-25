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
import com.example.szakdolgozat.helpers.UIUtils;
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

        UIUtils.hideSystemUI(getWindow());

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

        binding.teljesNev.setText(name != null ? name : "");
        binding.EmailInput.setText(email != null ? email : "");
        
        binding.suly.setText(String.format("%s %s", sulyStr != null ? sulyStr : "0", getString(R.string.unit_kg)));
        binding.magassag.setText(String.format("%s %s", magassagStr != null ? magassagStr : "0", getString(R.string.unit_cm)));
        binding.kor.setText(String.format("%s %s", korStr != null ? korStr : "0", getString(R.string.unit_years)));
        
        // Localized gender display
        if (nem != null) {
            if (nem.equalsIgnoreCase("male") || nem.equalsIgnoreCase("férfi")) {
                binding.nem.setText(getString(R.string.gender_male));
            } else if (nem.equalsIgnoreCase("female") || nem.equalsIgnoreCase("nő")) {
                binding.nem.setText(getString(R.string.gender_female));
            } else {
                binding.nem.setText(nem);
            }
        } else {
            binding.nem.setText("");
        }

        if (name != null && name.length() >= 2) {
            String initials = "";
            if (name.contains(" ")) {
                initials = name.substring(0, 1).toUpperCase() + name.substring(name.lastIndexOf(" ") + 1, name.lastIndexOf(" ") + 2).toUpperCase();
            } else {
                initials = name.substring(0, 1).toUpperCase();
            }
            binding.profileInitials.setText(initials);
        } else if (name != null && name.length() > 0) {
            binding.profileInitials.setText(name.substring(0, 1).toUpperCase());
        }

        // Logic for streak - hardcoded for now as example, should come from Firestore
        long streak = 12; 
        binding.streakTitle.setText(getString(R.string.label_active_streak, (int)streak));
        binding.streakMotivation.setText(getString(R.string.streak_motivation, name != null ? name : ""));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileListener != null) {
            profileListener.remove();
        }
    }
}
