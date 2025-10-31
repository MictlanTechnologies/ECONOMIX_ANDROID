package com.example.economix_android.Model.ingresos;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentIngresosInfoBinding;
import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.RegistroAdapter;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ingresosInfo extends Fragment {

    private FragmentIngresosInfoBinding binding;
    private RegistroAdapter ingresosAdapter;
    private RegistroAdapter recurrentesAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentIngresosInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPerfil.setOnClickListener(v -> navigateSafely(v, R.id.usuario));
        binding.btnAyudaIngInf.setOnClickListener(v -> mostrarAyuda());

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

        configurarListas();
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizarDatos();
    }

    private void configurarListas() {
        ingresosAdapter = new RegistroAdapter();
        recurrentesAdapter = new RegistroAdapter();

        binding.tablaGastos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.tablaGastos.setAdapter(ingresosAdapter);

        binding.listaRecurrentes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listaRecurrentes.setAdapter(recurrentesAdapter);
    }

    private void actualizarDatos() {
        ingresosAdapter.updateData(DataRepository.getIngresos());
        recurrentesAdapter.updateData(DataRepository.getIngresosRecurrentes());

        binding.tvIngresosVacio.setVisibility(ingresosAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        binding.tvRecurrentesVacio.setVisibility(recurrentesAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_ingresos_info)
                .setMessage(R.string.mensaje_ayuda_ingresos_info)
                .setPositiveButton(android.R.string.ok, null)
                .show();
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