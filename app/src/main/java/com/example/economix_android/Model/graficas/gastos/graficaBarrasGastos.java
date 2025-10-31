package com.example.economix_android.Model.graficas.gastos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentGraficaBarrasGastosBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class graficaBarrasGastos extends Fragment {

    private FragmentGraficaBarrasGastosBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGraficaBarrasGastosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPerfil.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.usuario));
        binding.btnAyudaIngInf.setOnClickListener(v -> mostrarAyuda());
        binding.buttonBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.graficasMenuGastos));
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_grafica_barras_gastos)
                .setMessage(R.string.mensaje_ayuda_grafica_barras_gastos)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}