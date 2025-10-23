package com.example.economix_android.Model.graficas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.economix_android.R;
import com.example.economix_android.Vista.menu;
import com.example.economix_android.databinding.FragmentGraficasMenuBinding;

public class graficasMenu extends Fragment {

    private FragmentGraficasMenuBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGraficasMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnGastosCategoria.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_navigation_graficas_to_graficasMenuGastos));

        binding.btnIngresosVsGastos.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_navigation_graficas_to_graficasMenuIngresosVsGastos));

        binding.btnPerfil.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.nav_host_fragment_usuario));

        binding.btnVolverGraficas.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), menu.class);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}