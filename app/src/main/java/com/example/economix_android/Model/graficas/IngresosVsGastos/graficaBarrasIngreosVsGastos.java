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
import com.example.economix_android.databinding.FragmentGraficaBarrasIngreosVsGastosBinding;

public class graficaBarrasIngreosVsGastos extends Fragment {

    private FragmentGraficaBarrasIngreosVsGastosBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGraficaBarrasIngreosVsGastosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPerfil.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.usuario));
        binding.buttonBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.graficasMenuIngresosVsGastos));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}