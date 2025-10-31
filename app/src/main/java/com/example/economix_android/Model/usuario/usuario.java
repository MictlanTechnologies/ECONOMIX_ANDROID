package com.example.economix_android.Model.usuario;

import android.content.Intent;
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
import com.example.economix_android.activity_inicio;
import com.example.economix_android.databinding.FragmentUsuarioBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class usuario extends Fragment {

    private FragmentUsuarioBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsuarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnInfo.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.usuario_info));
        binding.btnAyudaUs.setOnClickListener(v -> mostrarAyuda());
        binding.btnGuardar.setOnClickListener(v -> cerrarSesion());

        View.OnClickListener bottomNavListener = v -> {
            int viewId = v.getId();
            if (viewId == R.id.navGastos) {
                navigateSafely(v, R.id.navigation_gastos);
            } else if (viewId == R.id.navIngresos) {
                navigateSafely(v, R.id.navigation_ingresos);
            } else if (viewId == R.id.navAhorro) {
                navigateSafely(v, R.id.navigation_ahorro);
            } else if (viewId == R.id.navGraficas) {
                navigateSafely(v, R.id.navigation_graficas);
            }
        };

        binding.navGastos.setOnClickListener(bottomNavListener);
        binding.navIngresos.setOnClickListener(bottomNavListener);
        binding.navAhorro.setOnClickListener(bottomNavListener);
        binding.navGraficas.setOnClickListener(bottomNavListener);
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
                .setTitle(R.string.titulo_ayuda_usuario)
                .setMessage(R.string.mensaje_ayuda_usuario)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void cerrarSesion() {
        Intent intent = new Intent(requireContext(), activity_inicio.class);
        intent.putExtra(activity_inicio.EXTRA_MOSTRAR_LOGIN, true);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}