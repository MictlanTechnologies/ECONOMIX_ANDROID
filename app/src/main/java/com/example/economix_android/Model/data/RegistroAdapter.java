package com.example.economix_android.Model.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.R;

import java.util.ArrayList;
import java.util.List;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> {

    public interface OnRegistroDoubleClickListener {
        void onRegistroDoubleClick(RegistroFinanciero registro);
    }

    private static final long DOUBLE_CLICK_DELAY_MS = 400;
    private final List<RegistroFinanciero> registros = new ArrayList<>();
    private OnRegistroDoubleClickListener doubleClickListener;

    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro_financiero, parent, false);
        return new RegistroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroViewHolder holder, int position) {
        RegistroFinanciero registro = registros.get(position);
        holder.bind(registro, doubleClickListener);
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    public void updateData(List<? extends RegistroFinanciero> nuevosRegistros) {
        registros.clear();
        if (nuevosRegistros != null) {
            registros.addAll(nuevosRegistros);
        }
        notifyDataSetChanged();
    }

    public void setOnRegistroDoubleClickListener(OnRegistroDoubleClickListener listener) {
        this.doubleClickListener = listener;
    }

    static class RegistroViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTipo;
        private final TextView tvArticulo;
        private final TextView tvDescripcion;
        private final TextView tvFecha;
        private final TextView tvPeriodo;
        private final TextView tvRecurrente;
        private long lastClickTime;

        RegistroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvArticulo = itemView.findViewById(R.id.tvArticulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvPeriodo = itemView.findViewById(R.id.tvPeriodo);
            tvRecurrente = itemView.findViewById(R.id.tvRecurrente);
        }

        void bind(RegistroFinanciero registro, OnRegistroDoubleClickListener listener) {
            tvTipo.setText(registro.getTipo());
            tvArticulo.setText(registro.getArticulo());
            String descripcion = registro.getDescripcion();
            tvDescripcion.setText(descripcion == null || descripcion.trim().isEmpty() ? "Sin descripción" : descripcion);
            tvFecha.setText(registro.getFecha());
            tvPeriodo.setText(registro.getPeriodo());
            tvRecurrente.setVisibility(registro.isRecurrente() ? View.VISIBLE : View.GONE);
            itemView.setOnClickListener(v -> {
                long now = SystemClock.elapsedRealtime();
                if (now - lastClickTime < DOUBLE_CLICK_DELAY_MS) {
                    if (listener != null) {
                        listener.onRegistroDoubleClick(registro);
                    }
                }
                lastClickTime = now;
            });
        }
    }
}
