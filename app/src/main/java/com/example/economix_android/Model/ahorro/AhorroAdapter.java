package com.example.economix_android.Model.ahorro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Ingreso;
import com.example.economix_android.R;

import java.util.ArrayList;
import java.util.List;

public class AhorroAdapter extends RecyclerView.Adapter<AhorroAdapter.AhorroViewHolder> {

    private final List<AhorroItem> items = new ArrayList<>();

    @NonNull
    @Override
    public AhorroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ahorro, parent, false);
        return new AhorroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AhorroViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void update(List<AhorroItem> nuevos) {
        items.clear();
        if (nuevos != null) {
            items.addAll(nuevos);
        }
        notifyDataSetChanged();
    }

    public AhorroItem getLast() {
        if (items.isEmpty()) {
            return null;
        }
        return items.get(items.size() - 1);
    }

    static class AhorroViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMonto;
        private final TextView tvPeriodo;
        private final TextView tvFecha;
        private final TextView tvIngreso;

        AhorroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonto = itemView.findViewById(R.id.tvMontoAhorro);
            tvPeriodo = itemView.findViewById(R.id.tvPeriodoAhorro);
            tvFecha = itemView.findViewById(R.id.tvFechaAhorro);
            tvIngreso = itemView.findViewById(R.id.tvIngresoReferencia);
        }

        void bind(AhorroItem item) {
            tvMonto.setText(itemView.getContext().getString(R.string.label_monto_ahorro_item, item.getMonto()));
            tvPeriodo.setText(itemView.getContext().getString(R.string.label_periodo_ahorro_item, item.getPeriodo()));
            tvFecha.setText(itemView.getContext().getString(R.string.label_fecha_ahorro_item, item.getFecha()));
            Ingreso ingresoRelacionado = item.getIngresoId() != null
                    ? DataRepository.getIngresoById(item.getIngresoId())
                    : null;
            String ingresoNombre = ingresoRelacionado != null
                    ? ingresoRelacionado.getArticulo()
                    : itemView.getContext().getString(R.string.label_no_ingreso);
            String ingresoTexto = itemView.getContext().getString(R.string.label_ingreso_relacionado, ingresoNombre);
            tvIngreso.setText(ingresoTexto);
        }
    }
}
