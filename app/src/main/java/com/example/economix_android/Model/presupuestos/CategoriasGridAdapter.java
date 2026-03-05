package com.example.economix_android.Model.presupuestos;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.R;
import com.example.economix_android.network.dto.CategoriaPresupuestoDto;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class CategoriasGridAdapter extends RecyclerView.Adapter<CategoriasGridAdapter.VH> {

    public interface OnClick {
        void onCategoria(CategoriaPresupuestoDto dto);
        void onAdd();
    }

    private final List<CategoriaPresupuestoDto> data = new ArrayList<>();
    private final OnClick listener;

    public CategoriasGridAdapter(OnClick listener) {
        this.listener = listener;
    }

    public void submit(List<CategoriaPresupuestoDto> categorias) {
        data.clear();
        if (categorias != null) data.addAll(categorias);
        notifyDataSetChanged();
    }

    @Override public int getItemCount() { return data.size() + 1; }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int p) {
        if (p == data.size()) {
            h.tvNombre.setText("Añadir categoría");
            h.tvIcon.setText("+");
            h.card.setCardBackgroundColor(Color.TRANSPARENT);
            h.card.setBackgroundResource(R.drawable.bg_add_categoria_dashed);
            h.itemView.setOnClickListener(v -> listener.onAdd());
            return;
        }
        CategoriaPresupuestoDto dto = data.get(p);
        h.tvNombre.setText(dto.getNombre());
        h.tvIcon.setText(IconMapper.iconFromKey(dto.getIconKey()));
        try { h.card.setCardBackgroundColor(Color.parseColor(dto.getColorHex())); } catch (Exception ignore) {}
        h.itemView.setOnClickListener(v -> listener.onCategoria(dto));
    }

    static class VH extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvIcon, tvNombre;
        VH(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardCategoria);
            tvIcon = itemView.findViewById(R.id.tvIconoCategoria);
            tvNombre = itemView.findViewById(R.id.tvNombreCategoria);
        }
    }
}
