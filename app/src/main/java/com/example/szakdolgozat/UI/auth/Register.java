package com.example.szakdolgozat.UI.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.UI.main.MainActivity;
import com.example.szakdolgozat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText Email;
    EditText TeljesNev;
    EditText Password;
    EditText PasswordVerify;

    Button RegisterBtn;
    TextView LoginBtn;
    FirebaseAuth mAuth;
    String userID;
    FirebaseFirestore db;

    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        Email = findViewById(R.id.Email_input);
        TeljesNev = findViewById(R.id.teljes_nev);
        Password = findViewById(R.id.Password_input);
        PasswordVerify = findViewById(R.id.Password_Verify_input);
        RegisterBtn = findViewById(R.id.Register_Button);
        LoginBtn = findViewById(R.id.Login_Button);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(Register.this, MainActivity.class));
        }

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Email.getText().toString().trim();
                String teljesnev = TeljesNev.getText().toString().trim();
                String password = Password.getText().toString().trim();
                String passwordVerify = PasswordVerify.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Email.setError("Email megadása kötelező!");
                    return;
                }

                if (TextUtils.isEmpty(teljesnev)) {
                    TeljesNev.setError("Név megadása kötelező!");  // Javítottam: Email helyett TeljesNev
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Password.setError("Jelszó megadása kötelező!");
                    return;
                }
                if (password.length() < 6) {
                    Password.setError("Legalább 6 karakteres jelszónak kell lennie!");
                    return;
                }

                if (!TextUtils.equals(password, passwordVerify)) {
                    PasswordVerify.setError("A megadott jelszavak nem egyeznek!");
                    Password.setError("A megadott jelszavak nem egyeznek!");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Regisztráció sikeres!", Toast.LENGTH_SHORT).show();
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference dbUsers = db.collection("users").document(userID);

                            Map<String, Object> user = new HashMap<>();
                            user.put("email", email);
                            user.put("nev", teljesnev);
                            user.put("password", password);
                            user.put("suly", "80");
                            user.put("magassag", "180");
                            user.put("kor", "25");
                            user.put("nem", "férfi");


                            // ÚJ: Alapértelmezett napi célok hozzáadása
                            Map<String, Object> dailyGoals = new HashMap<>();
                            dailyGoals.put("calories", 2000);
                            dailyGoals.put("carbs", 250);
                            dailyGoals.put("protein", 120);
                            dailyGoals.put("fat", 80);
                            user.put("dailyGoals", dailyGoals);

                            dbUsers.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: user profile is created for " + userID);
                                    Toast.makeText(Register.this, "Adatok sikeresen feltöltve!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            startActivity(new Intent(Register.this, MainActivity.class));
                        } else {
                            Toast.makeText(Register.this, "Regisztráció sikertelen!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}