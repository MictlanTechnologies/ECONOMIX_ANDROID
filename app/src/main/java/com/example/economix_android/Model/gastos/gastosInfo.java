package com.example.economix_android.Model.gastos;

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
import com.example.economix_android.databinding.FragmentGastosInfoBinding;
import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Gasto;
import com.example.economix_android.Model.data.RegistroAdapter;
import com.example.economix_android.util.ProfileImageUtils;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class gastosInfo extends Fragment {

    private FragmentGastosInfoBinding binding;
    private RegistroAdapter gastosAdapter;
    private RegistroAdapter recurrentesAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGastosInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPerfil.setOnClickListener(v -> navigateSafely(v, R.id.usuario));
        ProfileImageUtils.applyProfileImage(requireContext(), binding.btnPerfil);
        binding.btnAyudaGasInf.setOnClickListener(v -> mostrarAyuda());

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
        gastosAdapter = new RegistroAdapter();
        recurrentesAdapter = new RegistroAdapter();
        RegistroAdapter.OnRegistroDoubleClickListener listener = registro -> {
            if (registro instanceof Gasto) {
                abrirEdicionGasto((Gasto) registro);
            }
        };
        gastosAdapter.setOnRegistroDoubleClickListener(listener);
        recurrentesAdapter.setOnRegistroDoubleClickListener(listener);

        binding.tablaGastos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.tablaGastos.setAdapter(gastosAdapter);

        binding.listaRecurrentes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listaRecurrentes.setAdapter(recurrentesAdapter);
    }

    private void actualizarDatos() {
        DataRepository.refreshGastos(new DataRepository.RepositoryCallback<List<Gasto>>() {
            @Override
            public void onSuccess(List<Gasto> result) {
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
        gastosAdapter.updateData(DataRepository.getGastos());
        recurrentesAdapter.updateData(DataRepository.getGastosRecurrentes());

        binding.tvGastosVacio.setVisibility(gastosAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        binding.tvRecurrentesVacio.setVisibility(recurrentesAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void abrirEdicionGasto(Gasto gasto) {
        if (gasto == null || gasto.getId() == null) {
            mostrarMensaje(getString(R.string.error_gasto_id));
            return;
        }
        Bundle args = new Bundle();
        args.putInt(gastosFragment.ARG_GASTO_ID, gasto.getId());
        args.putString(gastosFragment.ARG_GASTO_ARTICULO, gasto.getArticulo());
        args.putString(gastosFragment.ARG_GASTO_MONTO, gasto.getDescripcion());
        args.putString(gastosFragment.ARG_GASTO_FECHA, gasto.getFecha());
        args.putString(gastosFragment.ARG_GASTO_PERIODO, gasto.getPeriodo());
        args.putBoolean(gastosFragment.ARG_GASTO_RECURRENTE, gasto.isRecurrente());
        navigateSafely(binding.getRoot(), R.id.action_gastosInfo_to_navigation_gastos, args);
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_gastos_info)
                .setMessage(R.string.mensaje_ayuda_gastos_info)
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
