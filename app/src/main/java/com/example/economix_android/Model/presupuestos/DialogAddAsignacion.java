package com.example.economix_android.Model.presupuestos;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.economix_android.R;
import com.example.economix_android.network.dto.AsignacionRequestDto;
import com.example.economix_android.network.dto.IngresoDisponibleDto;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DialogAddAsignacion extends DialogFragment {

    public interface OnAsignacionConfirm {
        void onConfirm(AsignacionRequestDto dto);
    }

    private final Integer categoriaId;
    private final List<IngresoDisponibleDto> ingresos;
    private final OnAsignacionConfirm listener;

    public DialogAddAsignacion(Integer categoriaId, List<IngresoDisponibleDto> ingresos, OnAsignacionConfirm listener) {
        this.categoriaId = categoriaId;
        this.ingresos = ingresos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_asignacion, null, false);
        MaterialAutoCompleteTextView etIngreso = view.findViewById(R.id.etIngresoAsignacion);
        TextInputEditText etMonto = view.findViewById(R.id.etMontoAsignacion);
        TextView tvDisponible = view.findViewById(R.id.tvDisponibleIngreso);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        for (IngresoDisponibleDto i : ingresos) {
            adapter.add(i.getNombre());
        }
        etIngreso.setAdapter(adapter);
        final IngresoDisponibleDto[] selected = {null};
        etIngreso.setOnItemClickListener((parent, v, pos, id) -> {
            selected[0] = ingresos.get(pos);
            tvDisponible.setText("Disponible en este ingreso: $" + selected[0].getDisponible());
        });

        return new AlertDialog.Builder(requireContext(), R.style.ThemeOverlay_Material3_MaterialAlertDialog)
                .setTitle("Añadir dinero")
                .setView(view)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Asignar", (dialog, which) -> {
                    if (selected[0] == null) return;
                    String montoTxt = etMonto.getText() != null ? etMonto.getText().toString().trim() : "";
                    if (TextUtils.isEmpty(montoTxt)) return;
                    BigDecimal monto = new BigDecimal(montoTxt);
                    if (monto.compareTo(BigDecimal.ZERO) <= 0 || monto.compareTo(selected[0].getDisponible()) > 0) {
                        if (getActivity() instanceof PresupuestosActivity) {
                            ((PresupuestosActivity) getActivity()).mostrarErrorCentrado(getString(R.string.error_monto_asignacion));
                        }
                        return;
                    }
                    listener.onConfirm(AsignacionRequestDto.builder()
                            .categoriaId(categoriaId)
                            .ingresoId(selected[0].getIngresoId())
                            .monto(monto)
                            .fecha(LocalDate.now())
                            .build());
                })
                .create();
    }
}
