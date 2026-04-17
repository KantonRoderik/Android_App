package com.example.szakdolgozat.UI.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.UI.auth.Login;
import com.example.szakdolgozat.UI.profile.ProfileSzerkesztes;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.example.szakdolgozat.notification.NotificationScheduler;
import com.google.firebase.auth.FirebaseUser;

/**
 * Entry point activity that handles user routing (Login vs Main vs Onboarding).
 */
public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirestoreRepository repository = FirestoreRepository.getInstance();
        FirebaseUser user = repository.getCurrentUser();

        NotificationScheduler.scheduleExactAlarms(this);

        if (user != null) {
            checkOnboarding(repository);
        } else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    private void checkOnboarding(FirestoreRepository repository) {
        repository.getUserData().addOnSuccessListener(document -> {
            Boolean isComplete = document.getBoolean("onboarding_complete");
            if (isComplete != null && isComplete) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                // If onboarding is not complete, force them to edit profile
                Intent intent = new Intent(this, ProfileSzerkesztes.class);
                intent.putExtra("IS_ONBOARDING", true);
                startActivity(intent);
            }
            finish();
        }).addOnFailureListener(e -> {
            startActivity(new Intent(this, Login.class));
            finish();
        });
    }
}
