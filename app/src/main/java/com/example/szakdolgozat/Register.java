package com.example.szakdolgozat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

     EditText Email;
     EditText Password;
     EditText PasswordVerify;

     Button RegisterBtn;
     TextView LoginBtn;
     FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);


        Email = findViewById(R.id.Email_input);
        Password = findViewById(R.id.Password_input);
        PasswordVerify = findViewById(R.id.Password_Verify_input);
        RegisterBtn = findViewById(R.id.Register_Button);
        LoginBtn = findViewById(R.id.Login_Button);

        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(Register.this, MainActivity.class));
        }


        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();
                String passwordVerify = PasswordVerify.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Email.setError("Email megadása kötelező!");
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


                //Adatbázisba töltés

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Regisztráció sikeres!", Toast.LENGTH_SHORT).show();
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

