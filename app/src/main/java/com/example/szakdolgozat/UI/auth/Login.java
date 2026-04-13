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
import com.example.szakdolgozat.databinding.ActivityLoginBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity for user login.
 */
public class Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
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
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = FirestoreRepository.getInstance();

        // Check if user is already logged in
        if (repository.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setupGoogleSignIn();

        binding.loginBtn.setOnClickListener(v -> performLogin());
        binding.RegisterBtn.setOnClickListener(v -> startActivity(new Intent(this, Register.class)));
        
        // Click on the Google Icon Layout
        binding.googleLayout.setOnClickListener(v -> signInWithGoogle());
        // Click on the "Or sign in with Google" text
        binding.googleSignup.setOnClickListener(v -> signInWithGoogle());
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
                Toast.makeText(Login.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfProfileExists(FirebaseUser user) {
        repository.getUserData().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                // If profile doesn't exist, create it (happens on first Google login)
                repository.createUserProfile(user.getEmail(), user.getDisplayName())
                        .addOnSuccessListener(unused -> {
                            startActivity(new Intent(Login.this, MainActivity.class));
                            finish();
                        });
            } else {
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(e -> {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        });
    }

    private void performLogin() {
        String email = binding.EmailInput.getText().toString().trim();
        String password = binding.PasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.EmailInput.setError(getString(R.string.error_email_required));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            binding.PasswordInput.setError(getString(R.string.error_password_required));
            return;
        }

        repository.login(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Login.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            } else {
                String error = task.getException() != null ? task.getException().getMessage() : getString(R.string.error_generic);
                Toast.makeText(Login.this, getString(R.string.login_failed) + ": " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
