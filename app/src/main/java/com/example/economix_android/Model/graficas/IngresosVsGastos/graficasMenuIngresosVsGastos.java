package com.example.economix_android.Model.graficas.IngresosVsGastos;

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
import com.example.economix_android.databinding.FragmentGraficasMenuIngresosVsGastosBinding;
import com.example.economix_android.util.ProfileImageUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class graficasMenuIngresosVsGastos extends Fragment {

    private FragmentGraficasMenuIngresosVsGastosBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGraficasMenuIngresosVsGastosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPerfil.setOnClickListener(v -> navigateSafely(v, R.id.usuario));
        ProfileImageUtils.applyProfileImage(requireContext(), binding.btnPerfil);
        binding.btnAyuda.setOnClickListener(v -> mostrarAyuda());
        binding.btnGraficaCircular.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.graficaCircularIngresosVsGastos));
        binding.btnGraficaBarras.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.graficaBarrasIngreosVsGastos));
        binding.btnVolverMenuGraficas.setOnClickListener(v -> navigateSafely(v, R.id.navigation_graficas));
    }

    private void navigateSafely(View view, int destinationId) {
        NavController navController = Navigation.findNavController(view);
        NavDestination currentDestination = navController.getCurrentDestination();
        if (currentDestination == null || currentDestination.getId() != destinationId) {
            navController.navigate(destinationId);
        }
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_graficas_ingresos_vs_gastos)
                .setMessage(R.string.mensaje_ayuda_graficas_ingresos_vs_gastos)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
