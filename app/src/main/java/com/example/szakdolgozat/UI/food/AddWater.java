package com.example.szakdolgozat.UI.food;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import java.util.Map;

/**
 * Helper class for adding water intake.
 * Note: Consider moving this logic entirely into FirestoreRepository or a ViewModel.
 */
public class AddWater {

    private final FirestoreRepository repository;

    public AddWater() {
        this.repository = FirestoreRepository.getInstance();
    }

    public void addWater(Context context) {
        double adag_viz = 100.0;
        
        if (repository.getCurrentUser() == null) {
            Toast.makeText(context, context.getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
            return;
        }

        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());

        repository.addWater(today, adag_viz)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, context.getString(R.string.water_added_toast), Toast.LENGTH_SHORT).show();
                    if (context instanceof Activity) {
                        ((Activity) context).recreate();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, context.getString(R.string.error_generic) + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
