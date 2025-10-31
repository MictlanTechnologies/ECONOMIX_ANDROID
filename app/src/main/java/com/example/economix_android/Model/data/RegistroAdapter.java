package com.example.economix_android.Model.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.R;

import java.util.ArrayList;
import java.util.List;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> {

    private final List<RegistroFinanciero> registros = new ArrayList<>();

    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro_financiero, parent, false);
        return new RegistroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroViewHolder holder, int position) {
        RegistroFinanciero registro = registros.get(position);
        holder.bind(registro);
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

    static class RegistroViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTipo;
        private final TextView tvArticulo;
        private final TextView tvDescripcion;
        private final TextView tvFecha;
        private final TextView tvPeriodo;
        private final TextView tvRecurrente;

        RegistroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvArticulo = itemView.findViewById(R.id.tvArticulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvPeriodo = itemView.findViewById(R.id.tvPeriodo);
            tvRecurrente = itemView.findViewById(R.id.tvRecurrente);
        }

        void bind(RegistroFinanciero registro) {
            tvTipo.setText(registro.getTipo());
            tvArticulo.setText(registro.getArticulo());
            String descripcion = registro.getDescripcion();
            tvDescripcion.setText(descripcion == null || descripcion.trim().isEmpty() ? "Sin descripción" : descripcion);
            tvFecha.setText(registro.getFecha());
            tvPeriodo.setText(registro.getPeriodo());
            tvRecurrente.setVisibility(registro.isRecurrente() ? View.VISIBLE : View.GONE);
        }
    }
}