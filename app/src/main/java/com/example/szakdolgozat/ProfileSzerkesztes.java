package com.example.szakdolgozat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;




public class ProfileSzerkesztes extends AppCompatActivity {


    EditText TeljesNev, Suly, Magassag, Kor, Password, PasswordVerify;
    Spinner Nem;
    Button Mentes;



    FirebaseAuth mAuth;
    String userID;
    FirebaseFirestore db;
    boolean PasswordError = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_szerkesztes);

        // Nemi lehetőségek
        String[] genders = {"Férfi", "Nő"};


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


                String teljesnev = TeljesNev.getText().toString().trim();
                String sulyStr = Suly.getText().toString().trim();
                String magassagStr = Magassag.getText().toString().trim();
                String korStr = Kor.getText().toString().trim();
                String nem = Nem.getSelectedItem().toString().trim();
                String NewPassword = Password.getText().toString().trim();
                String NewPasswordVerify = PasswordVerify.getText().toString().trim();





                if (!TextUtils.isEmpty(NewPassword) && NewPassword.length() < 6) {
                    Password.setError("Legalább 6 karakteres jelszónak kell lennie!");
                    PasswordError = true;
                    return;
                }


                if (!TextUtils.equals(NewPassword, NewPasswordVerify)) {
                    PasswordVerify.setError("A megadott jelszavak nem egyeznek!");
                    Password.setError("A megadott jelszavak nem egyeznek!");
                    PasswordError = true;
                    return;
                }


                /**
                 *
                 * Miért vették ki az updateEmail funkciót???????
                 *
                 * Itt igazából az aljáig csak a változtatásokat mentem el
                 *
                 */


                if(PasswordError == false && !TextUtils.isEmpty(NewPassword) && !TextUtils.isEmpty(NewPasswordVerify)){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;{
                        user.updatePassword(NewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    db.collection("users").document(userID).update("password", NewPassword);
                                    Toast.makeText(ProfileSzerkesztes.this, "Jelszó sikeresen megváltoztatva!", Toast.LENGTH_SHORT).show();

                                }
                                else {
                                    Toast.makeText(ProfileSzerkesztes.this, "Jelszó megváltoztatása sikertelen!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }


                if (!TextUtils.isEmpty(sulyStr)) {
                    try {
                        int suly = Integer.parseInt(sulyStr);

                        if (suly <= 0 || suly >= 500) {
                            Suly.setError("A súlynak 0 és 500 között kell lennie!");
                            return;
                        }
                        else{
                            db.collection("users").document(userID).update("suly", sulyStr);
                        }
                    } catch (NumberFormatException e) {
                        Suly.setError("Érvényes számot adj meg!");
                    }
                }


                if (!TextUtils.isEmpty(magassagStr)) {
                    try {
                        int magassag = Integer.parseInt(magassagStr);

                        if (magassag <= 0 || magassag >= 300) {
                            Magassag.setError("A magasságnak 0 és 300 között kell lennie!");
                            return;
                        }
                        else{
                            db.collection("users").document(userID).update("magassag", magassagStr);
                        }
                    } catch (NumberFormatException e) {
                        Magassag.setError("Érvényes számot adj meg!");
                    }
                }



                if (!TextUtils.isEmpty(korStr)) {
                    try {
                        int kor = Integer.parseInt(korStr);

                        if (kor <= 0 || kor >= 100) {
                            Kor.setError("A kornak 0 és 100 között kell lennie!");
                            return;
                        }
                        else{
                            db.collection("users").document(userID).update("kor", korStr);
                        }
                    } catch (NumberFormatException e) {
                        Kor.setError("Érvényes számot adj meg!");
                    }
                }









                if(!TextUtils.isEmpty(teljesnev)){
                    db.collection("users").document(userID).update("nev", teljesnev);
                }


                if(!TextUtils.isEmpty(nem)){
                    db.collection("users").document(userID).update("nem", nem);
                }


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