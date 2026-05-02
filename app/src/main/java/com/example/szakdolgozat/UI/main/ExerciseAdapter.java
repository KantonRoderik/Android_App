package com.example.szakdolgozat.UI.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolgozat.databinding.ItemConsumedFoodBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private final List<String> keys = new ArrayList<>();
    private final List<Map<String, Object>> exercises = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(String key, double caloriesBurned);
    }

    public ExerciseAdapter(OnDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    public void updateData(Map<String, Map<String, Object>> exercisesDone) {
        keys.clear();
        exercises.clear();
        if (exercisesDone != null) {
            for (Map.Entry<String, Map<String, Object>> entry : exercisesDone.entrySet()) {
                keys.add(entry.getKey());
                exercises.add(entry.getValue());
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConsumedFoodBinding binding = ItemConsumedFoodBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> exercise = exercises.get(position);
        String key = keys.get(position);

        String name = exercise.containsKey("name") ? String.valueOf(exercise.get("name")) : "Ismeretlen mozgás";
        Number duration = (Number) exercise.get("duration");
        Number burned = (Number) exercise.get("caloriesBurned");

        double calories = burned != null ? burned.doubleValue() : 0.0;
        int mins = duration != null ? duration.intValue() : 0;

        // Requirement 2: 🔥 emoji prefix
        holder.binding.foodName.setText("🔥 " + name);
        
        // Requirement 4: Display duration and negative calories
        holder.binding.foodDetails.setText(String.format(Locale.getDefault(), "%d perc • -%.0f kcal", mins, calories));

        // Az edzéseknél nincs szükség a makrókra, ezért elrejtjük (identikus kártya design, de tartalom nélkül)
        holder.binding.macroContainer.setVisibility(View.GONE);

        holder.binding.deleteBtn.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                // Requirement 5: Return key and burned calories
                deleteClickListener.onDeleteClick(key, calories);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemConsumedFoodBinding binding;

        ViewHolder(ItemConsumedFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
