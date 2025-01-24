package com.example.szakdolgozat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ProgressBar-ok és TextView-k referenciái
        ProgressBar progressBar1 = findViewById(R.id.progressBar_kaloria);
        ProgressBar progressBar2 = findViewById(R.id.progressBar_szenhidrat);
        ProgressBar progressBar3 = findViewById(R.id.progressBar_feherje);
        ProgressBar progressBar4 = findViewById(R.id.progressBar_zsir);

        TextView kaloria = findViewById(R.id.textView_kaloria);
        TextView szenhidrat = findViewById(R.id.textView_szenhidrat);
        TextView feherje = findViewById(R.id.textView_feherje);
        TextView zsir = findViewById(R.id.textView_zsir);

        // Személyre szabott adatok (ez lehet szerverről érkező adat is)
        int progressValue1 = 60;
        int progressValue2 = 40;
        int progressValue3 = 80;
        int progressValue4 = 90;

        // ProgressBar értékek beállítása
        progressBar1.setProgress(progressValue1);
        progressBar2.setProgress(progressValue2);
        progressBar3.setProgress(progressValue3);
        progressBar4.setProgress(progressValue4);

        // Szöveg frissítése a ProgressBar értékei alapján
        kaloria.setText("Kalória: " + progressValue1 + "%");
        szenhidrat.setText("Szénhidrát: " + progressValue2 + "%");
        feherje.setText("Fehérje: " + progressValue3 + "%");
        zsir.setText("Zsír: " + progressValue4 + "%");
    }

    public void Logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, Login.class));
        finish();
    }

}
