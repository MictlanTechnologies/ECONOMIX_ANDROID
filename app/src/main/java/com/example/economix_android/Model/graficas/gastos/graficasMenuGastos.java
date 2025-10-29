package com.example.economix_android.Model.graficas.gastos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

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

        binding.btnPerfil.setOnClickListener(v -> navigateSafely(v, R.id.usuario));
        binding.btnGraficaBarras.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.graficaBarrasGastos));
        binding.btnGraficaCircular.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.graficaCircularGastos));
        binding.btnVolverMenuGraficas.setOnClickListener(v -> navigateSafely(v, R.id.navigation_graficas));
    }

    private void navigateSafely(View view, int destinationId) {
        NavController navController = Navigation.findNavController(view);
        NavDestination currentDestination = navController.getCurrentDestination();
        if (currentDestination == null || currentDestination.getId() != destinationId) {
            navController.navigate(destinationId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}