package com.example.szakdolgozat.helpers;

import com.example.szakdolgozat.models.ConsumedFood;
import com.example.szakdolgozat.models.DailyEntry;
import com.example.szakdolgozat.models.DailyGoals;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Repository class to handle all Firestore and Auth database operations.
 * Implemented as a Singleton for Clean Code and performance.
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
        user.put("suly", "80");
        user.put("magassag", "180");
        user.put("kor", "25");
        user.put("nem", "férfi");

        Map<String, Object> dailyGoals = new HashMap<>();
        dailyGoals.put("calories", 2000.0);
        dailyGoals.put("carbs", 250.0);
        dailyGoals.put("protein", 120.0);
        dailyGoals.put("fat", 80.0);
        dailyGoals.put("water", 2000.0);
        user.put("dailyGoals", dailyGoals);

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

    // --- Food Items ---

    public Task<QuerySnapshot> getAllFoods() {
        return db.collection("foods").get();
    }
}
