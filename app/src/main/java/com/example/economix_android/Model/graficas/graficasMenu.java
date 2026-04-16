package com.example.economix_android.Model.graficas;

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
import com.example.economix_android.databinding.FragmentGraficasMenuBinding;
import com.example.economix_android.util.ProfileImageUtils;
import com.example.economix_android.util.UsuarioAnimationNavigator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

        binding.btnPerfil.setOnClickListener(v -> UsuarioAnimationNavigator.playAndNavigate(v, R.id.usuario));
        ProfileImageUtils.applyProfileImage(requireContext(), binding.btnPerfil);
        binding.btnAyuda.setOnClickListener(v -> mostrarAyuda());
        binding.btnGastosCategoria.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.graficasMenuGastos));
        binding.btnIngresosVsGastos.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.graficasMenuIngresosVsGastos));
        binding.btnVolverGraficas.setOnClickListener(v -> navigateSafely(v, R.id.menu));

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
            } else if (viewId == R.id.navMenuMini) {
                navigateSafely(v, R.id.menu);
            }
        };

        binding.navGastos.setOnClickListener(bottomNavListener);
        binding.navIngresos.setOnClickListener(bottomNavListener);
        binding.navAhorro.setOnClickListener(bottomNavListener);
        binding.navGraficas.setOnClickListener(bottomNavListener);
        binding.navMenuMini.setOnClickListener(bottomNavListener);
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
                .setTitle(R.string.titulo_ayuda_graficas_menu)
                .setMessage(R.string.mensaje_ayuda_graficas_menu)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
