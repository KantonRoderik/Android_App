package com.example.szakdolgozat.UI.food;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.databinding.ActivityAddfoodBinding;
import com.example.szakdolgozat.helpers.UIUtils;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddFoodActivity extends AppCompatActivity {

    private ActivityAddfoodBinding binding;
    private String selectedDate;
    private FoodFragment foodFragment;
    private ExerciseFragment exerciseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddfoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UIUtils.hideSystemUI(getWindow());
        setupDate();
        setupViewPager();
        setupListeners();
    }

    private void setupDate() {
        selectedDate = getIntent().getStringExtra("selected_date");
        if (selectedDate == null) {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }
    }

    private void setupViewPager() {
        foodFragment = FoodFragment.newInstance(selectedDate);
        exerciseFragment = ExerciseFragment.newInstance(selectedDate);

        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return position == 0 ? foodFragment : exerciseFragment;
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(binding.toggleTabs, binding.viewPager, (tab, position) -> {
            tab.setText(position == 0 ? R.string.tab_food : R.string.tab_exercise);
        }).attach();

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    binding.searchAddButton.setText(R.string.addFoodDone);
                    binding.addFoodTitle.setText(R.string.addFoodTitle);
                } else {
                    binding.searchAddButton.setText(R.string.addExerciseDone);
                    binding.addFoodTitle.setText("Új mozgás");
                }
            }
        });
    }

    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> finish());
        binding.searchAddButton.setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() == 0) {
                if (foodFragment != null) foodFragment.onAddClicked();
            } else {
                if (exerciseFragment != null) exerciseFragment.onAddExerciseClicked();
            }
        });
    }
}
