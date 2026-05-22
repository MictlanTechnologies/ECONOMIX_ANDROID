package com.example.economix_android.Model.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.R;

import java.util.ArrayList;
import java.util.List;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> {

    public interface OnRegistroActionListener {
        void onEdit(RegistroFinanciero registro);
        void onDelete(RegistroFinanciero registro);
    }

    public interface OnRegistroDoubleClickListener {
        void onRegistroDoubleClick(RegistroFinanciero registro);
    }

    private static final long DOUBLE_CLICK_DELAY_MS = 400;
    private final List<RegistroFinanciero> registros = new ArrayList<>();
    private OnRegistroDoubleClickListener doubleClickListener;
    private OnRegistroActionListener actionListener;

    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro_financiero, parent, false);
        return new RegistroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroViewHolder holder, int position) {
        RegistroFinanciero registro = registros.get(position);
        holder.bind(registro, doubleClickListener, actionListener);
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

    public void setOnRegistroActionListener(OnRegistroActionListener listener) {
        this.actionListener = listener;
    }

    static class RegistroViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTipo;
        private final TextView tvArticulo;
        private final TextView tvDescripcion;
        private final TextView tvFecha;
        private final TextView tvPeriodo;
        private final TextView tvRecurrente;
        private final ImageButton btnEditar;
        private final ImageButton btnEliminar;
        private long lastClickTime;

        RegistroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvArticulo = itemView.findViewById(R.id.tvArticulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvPeriodo = itemView.findViewById(R.id.tvPeriodo);
            tvRecurrente = itemView.findViewById(R.id.tvRecurrente);
            btnEditar = itemView.findViewById(R.id.btnEditarRegistro);
            btnEliminar = itemView.findViewById(R.id.btnEliminarRegistro);
        }

        void bind(RegistroFinanciero registro, OnRegistroDoubleClickListener listener, OnRegistroActionListener actionListener) {
            tvTipo.setText(registro.getTipo());
            tvArticulo.setText(registro.getArticulo());
            String descripcion = registro.getDescripcion();
            tvDescripcion.setText(descripcion == null || descripcion.trim().isEmpty() ? "Sin descripción" : descripcion);
            String fecha = registro.getFecha();
            tvFecha.setText((fecha == null || fecha.trim().isEmpty())
                    ? itemView.getContext().getString(R.string.label_fecha_desconocida)
                    : fecha);
            String periodo = registro.getPeriodo();
            tvPeriodo.setText((periodo == null || periodo.trim().isEmpty())
                    ? itemView.getContext().getString(R.string.label_periodo_desconocido)
                    : periodo);
            tvRecurrente.setVisibility(registro.isRecurrente() ? View.VISIBLE : View.GONE);
            btnEditar.setOnClickListener(v -> { if (actionListener != null) actionListener.onEdit(registro); });
            btnEliminar.setOnClickListener(v -> { if (actionListener != null) actionListener.onDelete(registro); });
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
