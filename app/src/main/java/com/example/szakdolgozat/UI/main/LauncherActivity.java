package com.example.szakdolgozat.UI.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.UI.auth.Login;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.example.szakdolgozat.notification.NotificationScheduler;
import com.google.firebase.auth.FirebaseUser;

/**
 * Entry point activity that handles user routing (Login vs Main) and initial setup.
 */
public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirestoreRepository repository = FirestoreRepository.getInstance();
        FirebaseUser user = repository.getCurrentUser();

        // Initialize background tasks
        NotificationScheduler.scheduleExactAlarms(this);

        // Route user based on auth state
        if (user != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, Login.class));
        }

        finish();
    }
}
