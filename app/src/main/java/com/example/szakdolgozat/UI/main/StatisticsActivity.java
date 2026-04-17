package com.example.szakdolgozat.UI.main;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.databinding.ActivityStatisticsBinding;
import com.example.szakdolgozat.helpers.FirestoreRepository;
import com.example.szakdolgozat.models.DailyEntry;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "StatisticsActivity";
    private ActivityStatisticsBinding binding;
    private FirestoreRepository repository;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    // Macro Colors
    private final int COLOR_CARBS = Color.parseColor("#FF9800"); // Orange
    private final int COLOR_PROTEIN = Color.parseColor("#2196F3"); // Blue
    private final int COLOR_FAT = Color.parseColor("#9C27B0"); // Purple

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = FirestoreRepository.getInstance();

        if (binding.backButton != null) {
            binding.backButton.setOnClickListener(v -> finish());
        }

        styleCharts();
        loadWeeklyData();
        loadTodayMacros();
    }

    private void styleCharts() {
        // Line Chart styling
        binding.lineChart.setBackgroundColor(Color.TRANSPARENT);
        binding.lineChart.setDrawGridBackground(false);
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.getLegend().setEnabled(true);
        
        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        
        YAxis leftAxis = binding.lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        binding.lineChart.getAxisRight().setEnabled(false);

        // Pie Chart styling
        binding.pieChart.setUsePercentValues(true);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setExtraOffsets(5, 10, 5, 5);
        binding.pieChart.setDragDecelerationFrictionCoef(0.95f);
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleColor(Color.WHITE);
        binding.pieChart.setTransparentCircleRadius(61f);
        binding.pieChart.setEntryLabelColor(Color.BLACK);
        binding.pieChart.setEntryLabelTextSize(12f);
    }

    private void loadWeeklyData() {
        List<String> last7Days = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            last7Days.add(dateFormat.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        Collections.reverse(last7Days);

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < last7Days.size(); i++) {
            final int index = i;
            String date = last7Days.get(i);
            labels.add(date.substring(5)); // MM-dd

            repository.getDailyEntry(date).addOnSuccessListener(documentSnapshot -> {
                double calories = 0;
                if (documentSnapshot.exists()) {
                    DailyEntry entry = documentSnapshot.toObject(DailyEntry.class);
                    if (entry != null) {
                        entry.calculateTotals();
                        calories = Math.max(0, entry.getTotalCalories());
                    }
                }
                entries.add(new Entry(index, (float) calories));

                if (entries.size() == 7) {
                    Collections.sort(entries, (e1, e2) -> Float.compare(e1.getX(), e2.getX()));
                    updateLineChart(entries, labels);
                }
            });
        }
    }

    private void updateLineChart(List<Entry> entries, List<String> labels) {
        LineDataSet dataSet = new LineDataSet(entries, "Daily Calories");
        dataSet.setColor(Color.parseColor("#4CAF50")); // Primary Green
        dataSet.setCircleColor(Color.parseColor("#388E3C"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(true);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#4CAF50"));
        dataSet.setFillAlpha(50);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        binding.lineChart.setData(lineData);
        binding.lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.lineChart.animateX(1200);
        binding.lineChart.invalidate();
    }

    private void loadTodayMacros() {
        String today = dateFormat.format(Calendar.getInstance().getTime());
        repository.getDailyEntry(today).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                DailyEntry entry = documentSnapshot.toObject(DailyEntry.class);
                if (entry != null) {
                    entry.calculateTotals();
                    if (entry.getTotalCarbs() > 0 || entry.getTotalProtein() > 0 || entry.getTotalFat() > 0) {
                        setupPieChart(entry);
                    }
                }
            }
        });
    }

    private void setupPieChart(DailyEntry entry) {
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry((float) Math.max(0, entry.getTotalCarbs()), "Carbs"));
        pieEntries.add(new PieEntry((float) Math.max(0, entry.getTotalProtein()), "Protein"));
        pieEntries.add(new PieEntry((float) Math.max(0, entry.getTotalFat()), "Fat"));

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        
        // Use consistent colors for macros
        List<Integer> colors = new ArrayList<>();
        colors.add(COLOR_CARBS);
        colors.add(COLOR_PROTEIN);
        colors.add(COLOR_FAT);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.WHITE);

        binding.pieChart.setData(data);
        binding.pieChart.setCenterText("Macros");
        binding.pieChart.setCenterTextSize(18f);
        binding.pieChart.animateY(1400);
        binding.pieChart.invalidate();
    }
}
