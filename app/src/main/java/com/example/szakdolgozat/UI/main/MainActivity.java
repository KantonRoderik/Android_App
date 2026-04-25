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
import com.example.szakdolgozat.helpers.UIUtils;
import com.example.szakdolgozat.models.ConsumedFood;
import com.example.szakdolgozat.models.DailyEntry;
import com.example.szakdolgozat.models.DailyGoals;
import com.example.szakdolgozat.models.FoodItem;
import com.example.szakdolgozat.network.OpenFoodFactsApi;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private OpenFoodFactsApi api;
    private GestureDetector gestureDetector;

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
        setupRetrofit();
        
        initializeUI();
        setupRecyclerView();
        checkNotificationPermission();
        updateDateDisplay();
        loadDailyData();
        setupSwipeGestures();
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
                            changeDay(-1); // Swipe Jobbra -> Előző nap
                        } else {
                            changeDay(1);  // Swipe Balra -> Következő nap
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

        binding.statsBtn.setOnClickListener(v -> 
                startActivity(new Intent(this, StatisticsActivity.class)));

        binding.ProfileBtn.setOnClickListener(v -> 
                startActivity(new Intent(this, Profile.class)));

        binding.Logout.setOnClickListener(v -> logout());

        binding.btnNext.setOnClickListener(v -> changeDay(1));
        binding.btnPrevious.setOnClickListener(v -> changeDay(-1));
    }

    private void fetchProductFromApi(String barcode) {
        api.getProduct(barcode).enqueue(new Callback<OpenFoodFactsApi.ProductResponse>() {
            @Override
            public void onResponse(Call<OpenFoodFactsApi.ProductResponse> call, Response<OpenFoodFactsApi.ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status == 1) {
                    OpenFoodFactsApi.Product product = response.body().product;
                    showQuantityDialog(product);
                } else {
                    Toast.makeText(MainActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OpenFoodFactsApi.ProductResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showQuantityDialog(OpenFoodFactsApi.Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(product.productName);
        builder.setMessage("Enter quantity in grams:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("100");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String quantityStr = input.getText().toString().trim();
            double quantity = quantityStr.isEmpty() ? 100.0 : Double.parseDouble(quantityStr);
            addScannedFood(product, quantity);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addScannedFood(OpenFoodFactsApi.Product product, double quantity) {
        FoodItem foodItem = new FoodItem();
        foodItem.setName(product.productName);
        foodItem.setCalories(product.nutriments.calories);
        foodItem.setCarbs(product.nutriments.carbs);
        foodItem.setFat(product.nutriments.fat);
        foodItem.setProtein(product.nutriments.protein);
        foodItem.setId("barcode_" + System.currentTimeMillis());

        ConsumedFood consumedFood = new ConsumedFood(foodItem, quantity);
        
        repository.addConsumedFood(getFormattedDate(), consumedFood)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Added: " + product.productName, Toast.LENGTH_SHORT).show();
                    loadDailyData();
                });
    }

    private void setupRecyclerView() {
        foodAdapter = new ConsumedFoodAdapter((key, food) -> {
            repository.removeConsumedFood(getFormattedDate(), key, food)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
                        loadDailyData();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, R.string.error_generic, Toast.LENGTH_SHORT).show());
        });
        binding.consumedFoodsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.consumedFoodsRecyclerView.setAdapter(foodAdapter);
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    private void loadDailyData() {
        String selectedDate = getFormattedDate();

        repository.getDailyEntry(selectedDate).addOnSuccessListener(entrySnapshot -> {
            DailyEntry dailyEntry = entrySnapshot.exists() ? entrySnapshot.toObject(DailyEntry.class) : new DailyEntry(selectedDate);
            
            if (dailyEntry != null) {
                dailyEntry.calculateTotals();
                foodAdapter.updateData(dailyEntry.getConsumedFoods());
            }

            repository.getUserData().addOnSuccessListener(userSnapshot -> {
                DailyGoals goals = userSnapshot.get("dailyGoals", DailyGoals.class);
                if (goals != null && dailyEntry != null) {
                    updateUI(dailyEntry, goals, userSnapshot);
                } else {
                    setDefaultUIValues();
                }
            }).addOnFailureListener(e -> handleDataError(getString(R.string.error_loading_data), e));
            
        }).addOnFailureListener(e -> handleDataError(getString(R.string.error_loading_data), e));
    }

    private void updateUI(DailyEntry entry, DailyGoals goals, DocumentSnapshot userSnapshot) {
        updateProgressBars(entry, goals);
        updateTextViews(entry, goals);
        updateBMR(userSnapshot);
    }

    private void updateBMR(DocumentSnapshot userSnapshot) {
        String sulyStr = userSnapshot.getString("suly");
        String magassagStr = userSnapshot.getString("magassag");
        String korStr = userSnapshot.getString("kor");
        String nem = userSnapshot.getString("nem");

        try {
            double weight = sulyStr != null ? Double.parseDouble(sulyStr) : 0;
            double height = magassagStr != null ? Double.parseDouble(magassagStr) : 0;
            double age = korStr != null ? Double.parseDouble(korStr) : 0;
            
            double bmr = calculateBMR(weight, height, age, nem);
            binding.bmrDisplay.setText(getString(R.string.label_bmr, bmr));
        } catch (NumberFormatException e) {
            binding.bmrDisplay.setText(getString(R.string.label_bmr, 0.0));
        }
    }

    private double calculateBMR(double weight, double height, double age, String gender) {
        if (weight <= 0 || height <= 0 || age <= 0) return 0;
        double bmr = (10 * weight) + (6.25 * height) - (5 * age);
        if (gender != null && (gender.equalsIgnoreCase("n\u0151") || gender.equalsIgnoreCase("female"))) {
            bmr -= 161;
        } else {
            bmr += 5;
        }
        return Math.max(0, bmr);
    }

    private void updateProgressBars(DailyEntry entry, DailyGoals goals) {
        animateProgressBar(binding.progressBarKaloria, calculateSafeProgress(entry.getTotalCalories(), goals.getCalories()));
        animateProgressBar(binding.progressBarSzenhidrat, calculateSafeProgress(entry.getTotalCarbs(), goals.getCarbs()));
        animateProgressBar(binding.progressBarFeherje, calculateSafeProgress(entry.getTotalProtein(), goals.getProtein()));
        animateProgressBar(binding.progressBarZsir, calculateSafeProgress(entry.getTotalFat(), goals.getFat()));
        animateProgressBar(binding.progressBarViz, calculateSafeProgress(entry.getTotalWater(), goals.getWater()));
    }

    private void animateProgressBar(ProgressBar progressBar, int targetProgress) {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress",
                progressBar.getProgress(), targetProgress);
        progressAnimator.setDuration(800);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.start();
    }

    private int calculateSafeProgress(double actual, double goal) {
        if (goal <= 0) return 0;
        int progress = (int) ((actual / goal) * 100);
        return Math.max(0, Math.min(progress, 100));
    }

    private void updateTextViews(DailyEntry entry, DailyGoals goals) {
        binding.textViewKaloria.setText(formatNutritionText(getString(R.string.label_calories), entry.getTotalCalories(), goals.getCalories(), getString(R.string.unit_kcal)));
        binding.textViewSzenhidrat.setText(formatNutritionText(getString(R.string.label_carbs), entry.getTotalCarbs(), goals.getCarbs(), getString(R.string.unit_g)));
        binding.textViewFeherje.setText(formatNutritionText(getString(R.string.label_protein), entry.getTotalProtein(), goals.getProtein(), getString(R.string.unit_g)));
        binding.textViewZsir.setText(formatNutritionText(getString(R.string.label_fat), entry.getTotalFat(), goals.getFat(), getString(R.string.unit_g)));
        binding.textViewViz.setText(formatNutritionText(getString(R.string.label_water), entry.getTotalWater(), goals.getWater(), getString(R.string.unit_ml)));
    }

    private String formatNutritionText(String label, double actual, double goal, String unit) {
        double displayActual = Math.max(0, actual);
        int percent = (goal > 0) ? (int) ((displayActual / goal) * 100) : 0;
        return String.format(Locale.getDefault(), "%s: %.0f/%.0f %s (%d%%)", label, displayActual, goal, unit, percent);
    }

    private void setDefaultUIValues() {
        binding.progressBarKaloria.setProgress(0);
        binding.progressBarSzenhidrat.setProgress(0);
        binding.progressBarFeherje.setProgress(0);
        binding.progressBarZsir.setProgress(0);
        binding.progressBarViz.setProgress(0);

        binding.textViewKaloria.setText(getString(R.string.nutrition_default_format, getString(R.string.label_calories), getString(R.string.unit_kcal)));
        binding.textViewSzenhidrat.setText(getString(R.string.nutrition_default_format, getString(R.string.label_carbs), getString(R.string.unit_g)));
        binding.textViewFeherje.setText(getString(R.string.nutrition_default_format, getString(R.string.label_protein), getString(R.string.unit_g)));
        binding.textViewZsir.setText(getString(R.string.nutrition_default_format, getString(R.string.label_fat), getString(R.string.unit_g)));
        binding.textViewViz.setText(getString(R.string.nutrition_default_format, getString(R.string.label_water), getString(R.string.unit_ml)));
    }

    private void handleDataError(String message, Exception e) {
        Log.e(TAG, message + ": " + e.getMessage());
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateDateDisplay() {
        binding.tvCurrentDate.setText(getFormattedDate());
    }

    private String getFormattedDate() {
        return dateFormat.format(currentDate.getTime());
    }

    private void addWater() {
        repository.addWater(getFormattedDate(), 100)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, R.string.water_added_toast, Toast.LENGTH_SHORT).show();
                    loadDailyData();
                })
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.error_generic) + ": " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void logout() {
        repository.signOut();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    private void changeDay(int amount) {
        currentDate.add(Calendar.DAY_OF_YEAR, amount);
        updateDateDisplay();
        loadDailyData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDailyData();
        UIUtils.hideSystemUI(getWindow());
    }
}
