package com.example.economix_android.Model.ingresos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.example.economix_android.Model.data.Ingreso;
import com.example.economix_android.Model.data.RegistroAdapter;
import com.example.economix_android.util.ProfileImageUtils;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

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
        ProfileImageUtils.applyProfileImage(requireContext(), binding.btnPerfil);
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
        RegistroAdapter.OnRegistroDoubleClickListener listener = registro -> {
            if (registro instanceof Ingreso) {
                abrirEdicionIngreso((Ingreso) registro);
            }
        };
        ingresosAdapter.setOnRegistroDoubleClickListener(listener);
        recurrentesAdapter.setOnRegistroDoubleClickListener(listener);

        binding.tablaGastos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.tablaGastos.setAdapter(ingresosAdapter);

        binding.listaRecurrentes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listaRecurrentes.setAdapter(recurrentesAdapter);
    }

    private void actualizarDatos() {
        DataRepository.refreshIngresos(requireContext(), new DataRepository.RepositoryCallback<List<Ingreso>>() {
            @Override
            public void onSuccess(List<Ingreso> result) {
                if (!isAdded()) {
                    return;
                }
                actualizarListasLocales();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(message);
                actualizarListasLocales();
            }
        });
    }

    private void actualizarListasLocales() {
        ingresosAdapter.updateData(DataRepository.getIngresos());
        recurrentesAdapter.updateData(DataRepository.getIngresosRecurrentes());

        binding.tvIngresosVacio.setVisibility(ingresosAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        binding.tvRecurrentesVacio.setVisibility(recurrentesAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void abrirEdicionIngreso(Ingreso ingreso) {
        if (ingreso == null || ingreso.getId() == null) {
            mostrarMensaje(getString(R.string.error_ingreso_id));
            return;
        }
        Bundle args = new Bundle();
        args.putInt(ingresosFragment.ARG_INGRESO_ID, ingreso.getId());
        args.putString(ingresosFragment.ARG_INGRESO_ARTICULO, ingreso.getArticulo());
        args.putString(ingresosFragment.ARG_INGRESO_MONTO, ingreso.getDescripcion());
        args.putString(ingresosFragment.ARG_INGRESO_FECHA, ingreso.getFecha());
        args.putString(ingresosFragment.ARG_INGRESO_PERIODO, ingreso.getPeriodo());
        args.putBoolean(ingresosFragment.ARG_INGRESO_RECURRENTE, ingreso.isRecurrente());
        navigateSafely(binding.getRoot(), R.id.action_ingresosInfo_to_navigation_ingresos, args);
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_ingresos_info)
                .setMessage(R.string.mensaje_ayuda_ingresos_info)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void mostrarMensaje(String message) {
        String texto = message != null ? message : getString(R.string.mensaje_error_servidor);
        Toast.makeText(requireContext(), texto, Toast.LENGTH_SHORT).show();
    }

    private void navigateSafely(View view, int destinationId) {
        navigateSafely(view, destinationId, null);
    }

    private void navigateSafely(View view, int destinationId, Bundle args) {
        NavController navController = Navigation.findNavController(view);
        NavDestination currentDestination = navController.getCurrentDestination();
        if (currentDestination == null || currentDestination.getId() != destinationId) {
            navController.navigate(destinationId, args);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
