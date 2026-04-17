package com.example.szakdolgozat.UI.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolgozat.databinding.ItemConsumedFoodBinding;
import com.example.szakdolgozat.models.ConsumedFood;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConsumedFoodAdapter extends RecyclerView.Adapter<ConsumedFoodAdapter.ViewHolder> {

    private final List<String> keys = new ArrayList<>();
    private final List<ConsumedFood> foods = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(String key, ConsumedFood food);
    }

    public ConsumedFoodAdapter(OnDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    public void updateData(Map<String, ConsumedFood> consumedFoods) {
        keys.clear();
        foods.clear();
        if (consumedFoods != null) {
            for (Map.Entry<String, ConsumedFood> entry : consumedFoods.entrySet()) {
                keys.add(entry.getKey());
                foods.add(entry.getValue());
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
        ConsumedFood food = foods.get(position);
        String key = keys.get(position);
        
        holder.binding.foodName.setText(food.getFoodName());
        String details = String.format(java.util.Locale.getDefault(), 
                "%.0fg - %.0f kcal", food.getQuantity(), food.getCalories());
        holder.binding.foodDetails.setText(details);

        holder.binding.deleteBtn.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(key, food);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemConsumedFoodBinding binding;

        ViewHolder(ItemConsumedFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
