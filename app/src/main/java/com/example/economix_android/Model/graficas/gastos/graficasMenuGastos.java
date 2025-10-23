package com.example.economix_android.Model.graficas.gastos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentGraficasMenuGastosBinding;

public class graficasMenuGastos extends Fragment {

    private FragmentGraficasMenuGastosBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGraficasMenuGastosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnGraficaBarras.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_graficasMenuGastos_to_graficaBarrasGastos));

        binding.btnGraficaCircular.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_graficasMenuGastos_to_graficaCircularGastos));

        binding.btnPerfil.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.nav_host_fragment_usuario));

        binding.btnVolverMenuGraficas.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}