package com.example.economix_android.Model.presupuestos;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.R;
import com.example.economix_android.network.dto.PresupuestoResumenDto;

import java.util.ArrayList;
import java.util.List;

public class PresupuestosListAdapter extends RecyclerView.Adapter<PresupuestosListAdapter.VH> {

    public interface OnItemClick {
        void onClick(PresupuestoResumenDto item);
    }

    private final List<PresupuestoResumenDto> items = new ArrayList<>();
    private final OnItemClick onItemClick;

    public PresupuestosListAdapter(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void submit(List<PresupuestoResumenDto> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_presupuesto, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PresupuestoResumenDto item = items.get(position);
        holder.tvNombre.setText(item.getNombre());
        holder.tvMonto.setText("$" + (item.getAsignado() != null ? item.getAsignado().toPlainString() : "0"));
        holder.tvIcono.setText(IconMapper.iconFromKey(item.getIconKey()));
        try {
            holder.tvIcono.setBackgroundColor(Color.parseColor(item.getColorHex()));
        } catch (Exception ignore) {
            holder.tvIcono.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.itemView.setOnClickListener(v -> onItemClick.onClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvIcono, tvNombre, tvMonto;
        VH(@NonNull View itemView) {
            super(itemView);
            tvIcono = itemView.findViewById(R.id.tvIcono);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvMonto = itemView.findViewById(R.id.tvMonto);
        }
    }
}
