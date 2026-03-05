package com.example.economix_android.Model.presupuestos;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.R;

import java.util.List;

public class ColoresAdapter extends RecyclerView.Adapter<ColoresAdapter.VH> {

    public interface OnColorClick { void onColor(String colorHex); }

    private final List<String> colores;
    private final OnColorClick listener;
    private String selected;

    public ColoresAdapter(List<String> colores, String selected, OnColorClick listener) {
        this.colores = colores;
        this.selected = selected;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_circle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String colorHex = colores.get(position);
        GradientDrawable bg = (GradientDrawable) holder.color.getBackground();
        bg.setColor(Color.parseColor(colorHex));
        bg.setStroke(colorHex.equals(selected) ? 5 : 0, Color.WHITE);
        holder.itemView.setOnClickListener(v -> {
            selected = colorHex;
            notifyDataSetChanged();
            listener.onColor(colorHex);
        });
    }

    @Override public int getItemCount() { return colores.size(); }

    static class VH extends RecyclerView.ViewHolder {
        View color;
        VH(@NonNull View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.viewColor);
        }
    }
}
