package com.example.economix_android.Model.presupuestos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.R;
import com.example.economix_android.network.dto.PresupuestoResumenDto;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.List;

public class BottomSheetEditPresupuesto extends BottomSheetDialogFragment {

    public interface Callback {
        void onColorChanged(PresupuestoResumenDto item, String colorHex);
        void onDelete(PresupuestoResumenDto item);
        void onAddMoney(PresupuestoResumenDto item);
    }

    private final PresupuestoResumenDto item;
    private final Callback callback;
    private String selectedColor;

    public BottomSheetEditPresupuesto(PresupuestoResumenDto item, Callback callback) {
        this.item = item;
        this.callback = callback;
        this.selectedColor = item.getColorHex();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_edit_presupuesto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView tvIcon = view.findViewById(R.id.tvIconoBottom);
        TextView tvNombre = view.findViewById(R.id.tvNombreBottom);
        TextView tvAsignado = view.findViewById(R.id.tvAsignadoActual);
        RecyclerView rvColors = view.findViewById(R.id.rvColores);

        tvIcon.setText(IconMapper.iconFromKey(item.getIconKey()));
        tvNombre.setText(item.getNombre());
        tvAsignado.setText("Asignado este mes: $" + (item.getAsignado() != null ? item.getAsignado() : "0"));

        List<String> colores = Arrays.asList("#2FB6B0", "#6FD8D2", "#0E4B56", "#3B8DF0", "#2E4FCF", "#F3D373");
        rvColors.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        rvColors.setAdapter(new ColoresAdapter(colores, selectedColor, color -> selectedColor = color));

        view.findViewById(R.id.btnCerrarBottom).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnGuardarBottom).setOnClickListener(v -> {
            callback.onColorChanged(item, selectedColor);
            dismiss();
        });
        view.findViewById(R.id.btnAddDinero).setOnClickListener(v -> callback.onAddMoney(item));
        view.findViewById(R.id.btnEditarColor).setOnClickListener(v -> { });
        view.findViewById(R.id.btnEliminarPresupuesto).setOnClickListener(v -> {
            callback.onDelete(item);
            dismiss();
        });
    }
}
