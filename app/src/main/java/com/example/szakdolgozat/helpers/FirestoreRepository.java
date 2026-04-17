package com.example.szakdolgozat.helpers;

import com.example.szakdolgozat.models.ConsumedFood;
import com.example.szakdolgozat.models.DailyEntry;
import com.example.szakdolgozat.models.DailyGoals;
import com.example.szakdolgozat.models.DietaryTemplate;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository class to handle all Firestore and Auth database operations.
 */
public class FirestoreRepository {
    private static FirestoreRepository instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private FirestoreRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public static synchronized FirestoreRepository getInstance() {
        if (instance == null) {
            instance = new FirestoreRepository();
        }
        return instance;
    }

    // --- Auth Operations ---

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> register(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signInWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return auth.signInWithCredential(credential);
    }

    public void signOut() {
        auth.signOut();
    }

    // --- User Profile Operations ---

    private DocumentReference getUserDoc() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return null;
        return db.collection("users").document(user.getUid());
    }

    public Task<Void> createUserProfile(String email, String name) {
        DocumentReference userDoc = getUserDoc();
        if (userDoc == null) return Tasks.forException(new Exception("User not logged in"));

        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("nev", name);
        // Initial values set to null or empty to trigger onboarding
        user.put("suly", "");
        user.put("magassag", "");
        user.put("kor", "");
        user.put("nem", "");
        user.put("onboarding_complete", false);

        return userDoc.set(user);
    }

    public Task<DocumentSnapshot> getUserData() {
        DocumentReference userDoc = getUserDoc();
        if (userDoc == null) return Tasks.forException(new Exception("User not logged in"));
        return userDoc.get();
    }

    public ListenerRegistration listenToUserData(EventListener<DocumentSnapshot> listener) {
        DocumentReference userDoc = getUserDoc();
        if (userDoc == null) return null;
        return userDoc.addSnapshotListener(listener);
    }

    public Task<Void> updateProfile(Map<String, Object> profileData) {
        DocumentReference userDoc = getUserDoc();
        if (userDoc == null) return Tasks.forException(new Exception("User not logged in"));
        return userDoc.update(profileData);
    }

    public Task<Void> updatePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return Tasks.forException(new Exception("User not logged in"));
        return user.updatePassword(newPassword);
    }

    // --- Daily Entry Operations ---

    public Task<DocumentSnapshot> getDailyEntry(String date) {
        DocumentReference userDoc = getUserDoc();
        if (userDoc == null) return Tasks.forException(new Exception("User not logged in"));
        return userDoc.collection("dailyEntries").document(date).get();
    }

    public Task<Void> addConsumedFood(String date, ConsumedFood food) {
        DocumentReference userDoc = getUserDoc();
        if (userDoc == null) return Tasks.forException(new Exception("User not logged in"));
        
        DocumentReference entryRef = userDoc.collection("dailyEntries").document(date);
        
        return db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(entryRef);
            
            Map<String, Object> updates = new HashMap<>();
            String foodKey = "consumedFoods." + System.currentTimeMillis();
            updates.put(foodKey, food);
            updates.put("totalCalories", FieldValue.increment(food.getCalories()));
            updates.put("totalCarbs", FieldValue.increment(food.getCarbs()));
            updates.put("totalFat", FieldValue.increment(food.getFat()));
            updates.put("totalProtein", FieldValue.increment(food.getProtein()));

            if (!snapshot.exists()) {
                updates.put("date", date);
                updates.put("totalWater", 0.0);
                transaction.set(entryRef, updates);
            } else {
                transaction.update(entryRef, updates);
            }
            return null;
        });
    }

    public Task<Void> removeConsumedFood(String date, String foodKey, ConsumedFood food) {
        DocumentReference userDoc = getUserDoc();
        if (userDoc == null) return Tasks.forException(new Exception("User not logged in"));
        
        DocumentReference entryRef = userDoc.collection("dailyEntries").document(date);

        return db.runTransaction(transaction -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("consumedFoods." + foodKey, FieldValue.delete());
            updates.put("totalCalories", FieldValue.increment(-food.getCalories()));
            updates.put("totalCarbs", FieldValue.increment(-food.getCarbs()));
            updates.put("totalFat", FieldValue.increment(-food.getFat()));
            updates.put("totalProtein", FieldValue.increment(-food.getProtein()));

            transaction.update(entryRef, updates);
            return null;
        });
    }

    public Task<Void> addWater(String date, double amount) {
        DocumentReference userDoc = getUserDoc();
        if (userDoc == null) return Tasks.forException(new Exception("User not logged in"));
        
        DocumentReference entryRef = userDoc.collection("dailyEntries").document(date);

        return db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(entryRef);
            
            if (!snapshot.exists()) {
                DailyEntry newEntry = new DailyEntry(date);
                newEntry.setTotalWater(amount);
                transaction.set(entryRef, newEntry);
            } else {
                transaction.update(entryRef, "totalWater", FieldValue.increment(amount));
            }
            return null;
        });
    }

    public Task<Void> updateDailyGoals(DailyGoals goals) {
        DocumentReference userDoc = getUserDoc();
        if (userDoc == null) return Tasks.forException(new Exception("User not logged in"));
        return userDoc.update("dailyGoals", goals);
    }

    public Task<Void> updateDietaryTemplate(DietaryTemplate template, double calories, double water) {
        DocumentReference userDoc = getUserDoc();
        if (userDoc == null) return Tasks.forException(new Exception("User not logged in"));

        DailyGoals newGoals = template.calculateGoals(calories, water);
        
        WriteBatch batch = db.batch();
        batch.update(userDoc, "selectedTemplate", template.name());
        batch.update(userDoc, "dailyGoals", newGoals);
        
        return batch.commit();
    }

    public Task<QuerySnapshot> getAllFoods() {
        return db.collection("foods").get();
    }
}
