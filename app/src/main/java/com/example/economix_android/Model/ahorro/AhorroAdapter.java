package com.example.economix_android.Model.ahorro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Ingreso;
import com.example.economix_android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AhorroAdapter extends RecyclerView.Adapter<AhorroAdapter.AhorroViewHolder> {

    private final List<AhorroItem> items = new ArrayList<>();
    private final Map<String, ProgresoMeta> progresoPorMeta = new HashMap<>();

    public static class ProgresoMeta {
        private final String texto;
        private final int porcentaje;

        public ProgresoMeta(String texto, int porcentaje) {
            this.texto = texto;
            this.porcentaje = porcentaje;
        }

        public String getTexto() {
            return texto;
        }

        public int getPorcentaje() {
            return porcentaje;
        }
    }

    @NonNull
    @Override
    public AhorroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ahorro, parent, false);
        return new AhorroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AhorroViewHolder holder, int position) {
        AhorroItem item = items.get(position);
        ProgresoMeta progreso = progresoPorMeta.get(item.getPeriodo());
        holder.bind(item, progreso);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void update(List<AhorroItem> nuevos, Map<String, ProgresoMeta> progresoMap) {
        items.clear();
        if (nuevos != null) {
            items.addAll(nuevos);
        }
        progresoPorMeta.clear();
        if (progresoMap != null) {
            progresoPorMeta.putAll(progresoMap);
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
        private final TextView tvProgreso;
        private final ProgressBar progresoBarra;

        AhorroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonto = itemView.findViewById(R.id.tvMontoAhorro);
            tvPeriodo = itemView.findViewById(R.id.tvPeriodoAhorro);
            tvFecha = itemView.findViewById(R.id.tvFechaAhorro);
            tvIngreso = itemView.findViewById(R.id.tvIngresoReferencia);
            tvProgreso = itemView.findViewById(R.id.tvProgresoAhorroItem);
            progresoBarra = itemView.findViewById(R.id.progresoAhorroItem);
        }

        void bind(AhorroItem item, ProgresoMeta progreso) {
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
            if (progreso != null) {
                tvProgreso.setText(progreso.getTexto());
                progresoBarra.setProgress(progreso.getPorcentaje());
            } else {
                tvProgreso.setText(itemView.getContext().getString(R.string.label_avance_ahorro));
                progresoBarra.setProgress(0);
            }
        }
    }
}
