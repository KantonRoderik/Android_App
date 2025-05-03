package com.example.szakdolgozat.helpers;

import com.example.szakdolgozat.models.DailyEntry;
import com.example.szakdolgozat.models.FoodItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatabaseHelper {
    private DatabaseReference databaseReference;
    private String userId;

    public FirebaseDatabaseHelper() {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    // Ételek hozzáadása a globális "foods" táblához
    public void addFoodItem(FoodItem foodItem, DatabaseReference.CompletionListener completionListener) {
        DatabaseReference newFoodRef = databaseReference.child("foods").push();
        foodItem.setId(newFoodRef.getKey());
        newFoodRef.setValue(foodItem, completionListener);
    }

    // Napi bejegyzés hozzáadása/frissítése
    public void addOrUpdateDailyEntry(DailyEntry entry, DatabaseReference.CompletionListener completionListener) {
        entry.calculateTotals();
        databaseReference.child("users").child(userId).child("dailyEntries").child(entry.getDate())
                .setValue(entry, completionListener);
    }
/**
 *
 *
 *
    // Étel hozzáadása a napi bejegyzéshez
    public void addFoodToDailyEntry(String date, ConsumedFood food, DatabaseReference.CompletionListener completionListener) {
        DatabaseReference entryRef = databaseReference.child("users").child(userId).child("dailyEntries").child(date);

        entryRef.child("consumedFoods").push().setValue(food, (error, ref) -> {
            if (error == null) {
                // Frissítsük az összesített értékeket
                entryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DailyEntry entry = dataSnapshot.getValue(DailyEntry.class);
                        if (entry == null) {
                            entry = new DailyEntry(date);
                        }
                        entry.getConsumedFoods().add(food);
                        entry.calculateTotals();
                        entryRef.setValue(entry, completionListener);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        completionListener.onComplete(databaseError, entryRef);
                    }
                });
            } else {
                completionListener.onComplete(error, ref);
            }
        });
    }
*/

    // Ételek lekérése
    public void getAllFoodItems(ValueEventListener listener) {
        databaseReference.child("foods").addListenerForSingleValueEvent(listener);
    }

    // Napi bejegyzés lekérése
    public void getDailyEntry(String date, ValueEventListener listener) {
        databaseReference.child("users").child(userId).child("dailyEntries").child(date)
                .addListenerForSingleValueEvent(listener);
    }
}