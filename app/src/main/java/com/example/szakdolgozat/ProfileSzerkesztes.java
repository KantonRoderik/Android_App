package com.example.szakdolgozat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileSzerkesztes extends AppCompatActivity {


    EditText Email, TeljesNev, Suly, Magassag, Kor, Password, PasswordVerify;
    Spinner Nem;
    Button Mentes;



    FirebaseAuth mAuth;
    String userID;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_szerkesztes);

        // Nemi lehetőségek
        String[] genders = {"Férfi", "Nő"};


        Email = findViewById(R.id.Email_input);
        TeljesNev = findViewById(R.id.teljes_nev);
        Suly = findViewById(R.id.suly_input);
        Magassag = findViewById(R.id.magassag);
        Kor = findViewById(R.id.kor);
        Nem = findViewById(R.id.gender_spinner);
        Mentes = findViewById(R.id.Mentes);
        Password = findViewById(R.id.jelszo_input);
        PasswordVerify = findViewById(R.id.jelszoVerify_input);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();






        Mentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = Email.getText().toString().trim();
                String teljesnev = TeljesNev.getText().toString().trim();
                String suly = Suly.getText().toString().trim();
                String magassag = Magassag.getText().toString().trim();
                String kor = Kor.getText().toString().trim();
                String nem = Nem.getSelectedItem().toString().trim();
                String password = Password.getText().toString().trim();
                String passwordVerify = PasswordVerify.getText().toString().trim();




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


                db.collection("users").document(userID).update("email", email);
                mAuth.getCurrentUser().updateEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Log.d("Email", "Email sikeresen frissítve!");
                                } else {

                                    Log.d("Email", "Hiba, Email frissítés sikertelen: " + task.getException().getMessage());
                                }
                            }
                        });


                db.collection("users").document(userID).update("nev", teljesnev);
                db.collection("users").document(userID).update("suly", suly);
                db.collection("users").document(userID).update("magassag", magassag);
                db.collection("users").document(userID).update("kor", kor);
                db.collection("users").document(userID).update("nem", nem);


                startActivity(new Intent(ProfileSzerkesztes.this, Profile.class));
                finish();

            }
        });






        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Nem.setAdapter(adapter);

        // Kiválasztott elem kezelése
        Nem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = genders[position];
                Toast.makeText(ProfileSzerkesztes.this, "Kiválasztott nem: " + selectedGender, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ha nincs kiválasztva semmi, akkor nem történik semmi
            }
        });

    }


}