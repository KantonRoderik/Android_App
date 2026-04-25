package com.example.szakdolgozat.UI.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.UI.main.MainActivity;
import com.example.szakdolgozat.UI.profile.ProfileSzerkesztes;
import com.example.szakdolgozat.databinding.ActivityRegisterBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.example.szakdolgozat.helpers.UIUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity for user registration. Redirects to onboarding after creation.
 */
public class Register extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private ActivityRegisterBinding binding;
    private FirestoreRepository repository;
    private GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null) {
                            firebaseAuthWithGoogle(account.getIdToken());
                        }
                    } catch (ApiException e) {
                        Log.w(TAG, "Google sign in failed", e);
                        Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = FirestoreRepository.getInstance();

        UIUtils.hideSystemUI(getWindow());

        if (repository.getCurrentUser() != null) {
            checkIfProfileExists(repository.getCurrentUser());
        }

        setupGoogleSignIn();

        binding.RegisterButton.setOnClickListener(v -> performRegistration());
        binding.LoginButton.setOnClickListener(v -> startActivity(new Intent(this, Login.class)));
        
        binding.googleBtn.setOnClickListener(v -> signInWithGoogle());
        binding.googleText.setOnClickListener(v -> signInWithGoogle());
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        repository.signInWithGoogle(idToken).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = repository.getCurrentUser();
                checkIfProfileExists(user);
            } else {
                Toast.makeText(Register.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfProfileExists(FirebaseUser user) {
        repository.getUserData().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                // First time Google login, create profile and go to onboarding
                repository.createUserProfile(user.getEmail(), user.getDisplayName())
                        .addOnSuccessListener(unused -> navigateToOnboarding());
            } else {
                // Profile exists, check onboarding status
                Boolean isComplete = documentSnapshot.getBoolean("onboarding_complete");
                if (isComplete != null && isComplete) {
                    // Profile is complete, go to main screen
                    startActivity(new Intent(Register.this, MainActivity.class));
                    finish();
                } else {
                    // Profile exists but onboarding not finished
                    navigateToOnboarding();
                }
            }
        }).addOnFailureListener(e -> {
            // Error fetching data, fallback to safe redirection
            navigateToOnboarding();
        });
    }

    private void navigateToOnboarding() {
        Intent intent = new Intent(Register.this, ProfileSzerkesztes.class);
        intent.putExtra("IS_ONBOARDING", true);
        startActivity(intent);
        finish();
    }

    private void performRegistration() {
        String email = binding.EmailInput.getText().toString().trim();
        String fullName = binding.teljesNev.getText().toString().trim();
        String password = binding.PasswordInput.getText().toString().trim();
        String passwordVerify = binding.PasswordVerifyInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.EmailInput.setError(getString(R.string.error_email_required));
            return;
        }

        if (TextUtils.isEmpty(fullName)) {
            binding.teljesNev.setError(getString(R.string.error_name_required));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.PasswordInput.setError(getString(R.string.error_password_required));
            return;
        }
        if (password.length() < 6) {
            binding.PasswordInput.setError(getString(R.string.error_password_length));
            return;
        }

        if (!password.equals(passwordVerify)) {
            binding.PasswordVerifyInput.setError(getString(R.string.error_passwords_dont_match));
            return;
        }

        repository.register(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Register.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();

                repository.createUserProfile(email, fullName).addOnSuccessListener(unused -> {
                    navigateToOnboarding();
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Profile creation failed", e);
                    Toast.makeText(Register.this, getString(R.string.register_failed), Toast.LENGTH_SHORT).show();
                });

            } else {
                String error = task.getException() != null ? task.getException().getMessage() : getString(R.string.error_generic);
                Toast.makeText(Register.this, getString(R.string.register_failed) + ": " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
