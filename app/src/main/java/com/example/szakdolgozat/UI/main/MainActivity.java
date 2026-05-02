package com.example.szakdolgozat.UI.main;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.UI.auth.Login;
import com.example.szakdolgozat.UI.food.AddFoodActivity;
import com.example.szakdolgozat.UI.food.BarcodeScannerActivity;
import com.example.szakdolgozat.UI.profile.Profile;
import com.example.szakdolgozat.databinding.ActivityMainBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.example.szakdolgozat.helpers.NutritionCalculator;
import com.example.szakdolgozat.helpers.UIUtils;
import com.example.szakdolgozat.models.ConsumedFood;
import com.example.szakdolgozat.models.DailyEntry;
import com.example.szakdolgozat.models.DailyGoals;
import com.example.szakdolgozat.models.FoodItem;
import com.example.szakdolgozat.network.OpenFoodFactsApi;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Main dashboard of the application showing daily progress.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    private ActivityMainBinding binding;
    private FirestoreRepository repository;
    private Calendar currentDate = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private ConsumedFoodAdapter foodAdapter;
    private ExerciseAdapter exerciseAdapter;
    private OpenFoodFactsApi api;
    private GestureDetector gestureDetector;
    private ListenerRegistration dailyEntryListener;
    private ListenerRegistration userListener;

    private DailyEntry currentEntry;
    private DailyGoals currentGoals;
    private DocumentSnapshot currentUserSnapshot;

    private final ActivityResultLauncher<Intent> barcodeScannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String barcode = result.getData().getStringExtra("barcode");
                    if (barcode != null) {
                        fetchProductFromApi(barcode);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        UIUtils.hideSystemUI(getWindow());
        
        repository = FirestoreRepository.getInstance();
        
        if (repository.getCurrentUser() == null) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        setupRetrofit();
        initializeUI();
        setupRecyclerViews();
        checkNotificationPermission();
        updateDateDisplay();
        setupSwipeGestures();
        
        startListeningToData();
    }

    private void startListeningToData() {
        stopListening();
        String selectedDate = getFormattedDate();

        dailyEntryListener = repository.listenToDailyEntry(selectedDate, (snapshot, error) -> {
            if (error != null) {
                Log.e(TAG, "Daily entry listen failed", error);
                return;
            }

            if (snapshot != null) {
                DailyEntry entry = snapshot.exists() ? snapshot.toObject(DailyEntry.class) : new DailyEntry(selectedDate);
                if (entry != null) {
                    entry.calculateTotals();
                    this.currentEntry = entry;
                    foodAdapter.updateData(entry.getConsumedFoods());
                    
                    if (entry.getExercisesDone() != null) {
                        Log.d(TAG, "Edzések száma: " + entry.getExercisesDone().size());
                        exerciseAdapter.updateData(entry.getExercisesDone());
                    } else {
                        exerciseAdapter.updateData(new HashMap<>());
                    }
                    
                    updateUI();
                }
            }
        });

        userListener = repository.listenToUserData((snapshot, error) -> {
            if (error != null) {
                Log.e(TAG, "User data listen failed", error);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                currentUserSnapshot = snapshot;
                currentGoals = snapshot.get("dailyGoals", DailyGoals.class);
                updateUI();
            }
        });
    }

    private void updateUI() {
        if (currentEntry == null) return;

        DailyGoals goals = currentGoals;
        if (goals == null) {
            goals = new DailyGoals();
            goals.setCalories(2000);
            goals.setCarbs(250);
            goals.setProtein(150);
            goals.setFat(70);
            goals.setWater(2000);
        }

        updateProgressBars(currentEntry, goals);
        updateTextViews(currentEntry, goals);
        
        if (currentUserSnapshot != null) {
            updateBMRDisplay(currentUserSnapshot);
        }
    }

    private void stopListening() {
        if (dailyEntryListener != null) {
            dailyEntryListener.remove();
            dailyEntryListener = null;
        }
        if (userListener != null) {
            userListener.remove();
            userListener = null;
        }
    }

    private void setupSwipeGestures() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            changeDay(-1);
                        } else {
                            changeDay(1);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://world.openfoodfacts.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(OpenFoodFactsApi.class);
    }

    private void initializeUI() {
        binding.addFoodBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddFoodActivity.class);
            intent.putExtra("selected_date", getFormattedDate());
            startActivity(intent);
        });

        binding.scanBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeScannerActivity.class);
            barcodeScannerLauncher.launch(intent);
        });

        binding.vizGomb.setOnClickListener(v -> addWater());
        binding.statsBtn.setOnClickListener(v -> startActivity(new Intent(this, StatisticsActivity.class)));
        binding.ProfileBtn.setOnClickListener(v -> startActivity(new Intent(this, Profile.class)));
        binding.Logout.setOnClickListener(v -> logout());
        binding.btnNext.setOnClickListener(v -> changeDay(1));
        binding.btnPrevious.setOnClickListener(v -> changeDay(-1));
    }

    private void fetchProductFromApi(String barcode) {
        api.getProduct(barcode).enqueue(new Callback<OpenFoodFactsApi.ProductResponse>() {
            @Override
            public void onResponse(Call<OpenFoodFactsApi.ProductResponse> call, Response<OpenFoodFactsApi.ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status == 1) {
                    showQuantityDialog(response.body().product);
                } else {
                    Toast.makeText(MainActivity.this, "Termék nem található", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OpenFoodFactsApi.ProductResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Hálózati hiba", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showQuantityDialog(OpenFoodFactsApi.Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(product.productName);
        builder.setMessage("Mennyiség grammban:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("100");
        builder.setView(input);

        builder.setPositiveButton("Hozzáadás", (dialog, which) -> {
            String quantityStr = input.getText().toString().trim();
            double quantity = quantityStr.isEmpty() ? 100.0 : Double.parseDouble(quantityStr);
            addScannedFood(product, quantity);
        });
        builder.setNegativeButton("Mégse", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addScannedFood(OpenFoodFactsApi.Product product, double quantity) {
        FoodItem foodItem = new FoodItem();
        foodItem.setName(product.productName != null ? product.productName : "Ismeretlen termék");
        foodItem.setCalories(product.nutriments.calories);
        foodItem.setCarbs(product.nutriments.carbs);
        foodItem.setFat(product.nutriments.fat);
        foodItem.setProtein(product.nutriments.protein);
        foodItem.setId(foodItem.getName());

        ConsumedFood consumedFood = new ConsumedFood(foodItem, quantity);
        
        repository.saveFoodItemWithNameAsId(foodItem).addOnCompleteListener(task -> {
            repository.addConsumedFood(getFormattedDate(), consumedFood)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Hozzáadva: " + foodItem.getName(), Toast.LENGTH_SHORT).show();
                        startListeningToData();
                    })
                    .addOnFailureListener(e -> startListeningToData());
        });
        
        binding.getRoot().postDelayed(this::startListeningToData, 1000);
    }

    private void setupRecyclerViews() {
        foodAdapter = new ConsumedFoodAdapter((key, food) -> {
            repository.removeConsumedFood(getFormattedDate(), key, food)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, R.string.error_generic, Toast.LENGTH_SHORT).show());
        });
        binding.consumedFoodsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.consumedFoodsRecyclerView.setAdapter(foodAdapter);

        exerciseAdapter = new ExerciseAdapter((key, caloriesBurned) -> {
            repository.removeExerciseFromDailyLog(getFormattedDate(), key, caloriesBurned)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Mozgás törölve", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Hiba a törlés során", Toast.LENGTH_SHORT).show());
        });
        binding.doneExercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.doneExercisesRecyclerView.setAdapter(exerciseAdapter);
        binding.doneExercisesRecyclerView.setNestedScrollingEnabled(false);
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    private void updateBMRDisplay(DocumentSnapshot userSnapshot) {
        String sulyStr = userSnapshot.getString("suly");
        String magassagStr = userSnapshot.getString("magassag");
        String korStr = userSnapshot.getString("kor");
        String nemStr = userSnapshot.getString("nem");

        try {
            double weight = sulyStr != null ? Double.parseDouble(sulyStr) : 0;
            double height = magassagStr != null ? Double.parseDouble(magassagStr) : 0;
            double age = korStr != null ? Double.parseDouble(korStr) : 0;
            
            NutritionCalculator.Gender gender = NutritionCalculator.parseGender(nemStr);
            double bmr = NutritionCalculator.calculateBMR(weight, height, age, gender);
            
            binding.bmrDisplay.setText(getString(R.string.label_bmr, bmr));
        } catch (NumberFormatException e) {
            binding.bmrDisplay.setText(getString(R.string.label_bmr, 0.0));
        }
    }

    private void updateProgressBars(DailyEntry entry, DailyGoals goals) {
        animateProgressBar(binding.progressBarKaloria, UIUtils.calculateSafeProgress(entry.getTotalCalories(), goals.getCalories()));
        animateProgressBar(binding.progressBarSzenhidrat, UIUtils.calculateSafeProgress(entry.getTotalCarbs(), goals.getCarbs()));
        animateProgressBar(binding.progressBarFeherje, UIUtils.calculateSafeProgress(entry.getTotalProtein(), goals.getProtein()));
        animateProgressBar(binding.progressBarZsir, UIUtils.calculateSafeProgress(entry.getTotalFat(), goals.getFat()));
        animateProgressBar(binding.progressBarViz, UIUtils.calculateSafeProgress(entry.getTotalWater(), goals.getWater()));
    }

    private void animateProgressBar(ProgressBar progressBar, int targetProgress) {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress",
                progressBar.getProgress(), targetProgress);
        progressAnimator.setDuration(800);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.start();
    }

    private void updateTextViews(DailyEntry entry, DailyGoals goals) {
        binding.textViewKaloria.setText(UIUtils.formatNutritionText(getString(R.string.label_calories), entry.getTotalCalories(), goals.getCalories(), getString(R.string.unit_kcal)));
        binding.textViewBurned.setText(String.format(Locale.getDefault(), "🔥 Elégetve: %.0f kcal", entry.getTotalCaloriesBurned()));
        
        binding.textViewSzenhidrat.setText(UIUtils.formatNutritionText(getString(R.string.label_carbs), entry.getTotalCarbs(), goals.getCarbs(), getString(R.string.unit_g)));
        binding.textViewFeherje.setText(UIUtils.formatNutritionText(getString(R.string.label_protein), entry.getTotalProtein(), goals.getProtein(), getString(R.string.unit_g)));
        binding.textViewZsir.setText(UIUtils.formatNutritionText(getString(R.string.label_fat), entry.getTotalFat(), goals.getFat(), getString(R.string.unit_g)));
        binding.textViewViz.setText(UIUtils.formatNutritionText(getString(R.string.label_water), entry.getTotalWater(), goals.getWater(), getString(R.string.unit_ml)));
    }

    private void updateDateDisplay() {
        binding.tvCurrentDate.setText(getFormattedDate());
    }

    private String getFormattedDate() {
        return dateFormat.format(currentDate.getTime());
    }

    private void addWater() {
        Toast.makeText(this, R.string.water_added_toast, Toast.LENGTH_SHORT).show();
        repository.addWater(getFormattedDate(), 100)
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_SHORT).show());
    }

    private void logout() {
        stopListening();
        repository.signOut();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    private void changeDay(int amount) {
        currentDate.add(Calendar.DAY_OF_YEAR, amount);
        updateDateDisplay();
        startListeningToData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startListeningToData();
        UIUtils.hideSystemUI(getWindow());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopListening();
    }

    private void setupRecyclerView() {
        setupRecyclerViews();
    }
}
