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

public class AhorroInfoAdapter extends RecyclerView.Adapter<AhorroInfoAdapter.AhorroInfoViewHolder> {

    public interface OnAhorroActionListener {
        void onModificar(AhorroItem item);
        void onAgregar(AhorroItem item);
    }

    private final List<AhorroItem> items = new ArrayList<>();
    private final OnAhorroActionListener listener;

    public AhorroInfoAdapter(OnAhorroActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AhorroInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ahorro_info, parent, false);
        return new AhorroInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AhorroInfoViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
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

    static class AhorroInfoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMonto;
        private final TextView tvPeriodo;
        private final TextView tvFecha;
        private final TextView tvIngreso;
        private final View btnModificar;
        private final View btnAgregar;

        AhorroInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonto = itemView.findViewById(R.id.tvMontoAhorro);
            tvPeriodo = itemView.findViewById(R.id.tvPeriodoAhorro);
            tvFecha = itemView.findViewById(R.id.tvFechaAhorro);
            tvIngreso = itemView.findViewById(R.id.tvIngresoReferencia);
            btnModificar = itemView.findViewById(R.id.btnModificarAhorro);
            btnAgregar = itemView.findViewById(R.id.btnAgregarAhorro);
        }

        void bind(AhorroItem item, OnAhorroActionListener listener) {
            tvMonto.setText(itemView.getContext().getString(R.string.label_monto_ahorro_item, item.getMonto()));
            tvPeriodo.setText(itemView.getContext().getString(R.string.label_periodo_ahorro_item, item.getPeriodo()));
            tvFecha.setText(itemView.getContext().getString(R.string.label_fecha_ahorro_item, item.getFecha()));
            Ingreso ingresoRelacionado = item.getIngresoId() != null
                    ? DataRepository.getIngresoById(item.getIngresoId())
                    : null;
            String ingresoNombre = ingresoRelacionado != null
                    ? ingresoRelacionado.getArticulo()
                    : itemView.getContext().getString(R.string.label_no_ingreso);
            tvIngreso.setText(itemView.getContext().getString(R.string.label_ingreso_relacionado, ingresoNombre));

            btnModificar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onModificar(item);
                }
            });
            btnAgregar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAgregar(item);
                }
            });
        }
    }
}
