package com.example.szakdolgozat.UI.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.UI.main.MainActivity;
import com.example.szakdolgozat.R;
import com.example.szakdolgozat.helpers.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText Email;
    EditText Password;
    Button Login_btn;
    TextView Register_btn;
    TextView google_signup;
    FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.Email_input);
        Password = findViewById(R.id.Password_input);
        Login_btn = findViewById(R.id.login_btn);
        Register_btn = findViewById(R.id.Register_btn);
        google_signup = findViewById(R.id.google_signup);
        mAuth = FirebaseAuth.getInstance();



        LoadingDialog loadingDialog = new LoadingDialog(Login.this);



        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Email.setError("Email megadása kötelező!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Password.setError("Jelszó megadása kötelező!");
                    return;
                }



                //Adatok Autentikálása

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //Kötelező loading animáció

                        loadingDialog.startLoadingDialog();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dialog.dismiss();
                            }
                        }, 5000);


                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Bejelentkezés sikeres!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, MainActivity.class));
                        }
                        else{
                            Toast.makeText(Login.this, "Bejelentkezés sikertelen!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        Register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });


    }
}