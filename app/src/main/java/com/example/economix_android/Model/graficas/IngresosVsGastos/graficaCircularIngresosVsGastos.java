package com.example.economix_android.Model.graficas.IngresosVsGastos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentGraficaCircularIngresosVsGastosBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class graficaCircularIngresosVsGastos extends Fragment {

    private FragmentGraficaCircularIngresosVsGastosBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGraficaCircularIngresosVsGastosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPerfil.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.usuario));
        binding.btnAyudaIngInf.setOnClickListener(v -> mostrarAyuda());
        binding.buttonBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.graficasMenuIngresosVsGastos));
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_grafica_circular_ingresos_vs_gastos)
                .setMessage(R.string.mensaje_ayuda_grafica_circular_ingresos_vs_gastos)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}