package com.example.economix_android.Model.ingresos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
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
import com.example.economix_android.Model.data.RegistroFinanciero;
import com.example.economix_android.util.ProfileImageUtils;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ingresosInfo extends Fragment {

    private FragmentIngresosInfoBinding binding;
    private RegistroAdapter ingresosAdapter;
    private RegistroAdapter recurrentesAdapter;
    private final List<Ingreso> ingresosBase = new ArrayList<>();
    private final List<Ingreso> recurrentesBase = new ArrayList<>();
    private String filtroConcepto = "";
    private LocalDate filtroInicio;
    private LocalDate filtroFin;
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

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
        configurarFiltros();
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
        ingresosBase.clear();
        ingresosBase.addAll(DataRepository.getIngresos());
        recurrentesBase.clear();
        recurrentesBase.addAll(DataRepository.getIngresosRecurrentes());

        aplicarFiltros();
    }

    private void configurarFiltros() {
        binding.etBuscarIngresos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // no-op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtroConcepto = s == null ? "" : s.toString().trim().toLowerCase(Locale.getDefault());
                aplicarFiltros();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // no-op
            }
        });

        binding.btnFiltroFechaIngresos.setOnClickListener(v -> mostrarSelectorFechas());
        binding.btnFiltroFechaIngresos.setOnLongClickListener(v -> {
            limpiarFiltroFechas();
            return true;
        });
        actualizarTextoRango();
    }

    private void mostrarSelectorFechas() {
        MaterialDatePicker<Pair<Long, Long>> picker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText(R.string.titulo_seleccionar_periodo)
                        .build();
        picker.addOnPositiveButtonClickListener(selection -> {
            if (selection == null || selection.first == null || selection.second == null) {
                return;
            }
            filtroInicio = Instant.ofEpochMilli(selection.first)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            filtroFin = Instant.ofEpochMilli(selection.second)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            actualizarTextoRango();
            aplicarFiltros();
        });
        picker.show(getParentFragmentManager(), "ingresos_date_range");
    }

    private void limpiarFiltroFechas() {
        filtroInicio = null;
        filtroFin = null;
        actualizarTextoRango();
        aplicarFiltros();
    }

    private void actualizarTextoRango() {
        if (filtroInicio == null || filtroFin == null) {
            binding.tvRangoFechasIngresos.setText(getString(R.string.label_todas_fechas));
            return;
        }
        String rango = getString(R.string.label_rango_fechas,
                dateFormatter.format(filtroInicio),
                dateFormatter.format(filtroFin));
        binding.tvRangoFechasIngresos.setText(rango);
    }

    private void aplicarFiltros() {
        List<Ingreso> filtrados = filtrarRegistros(ingresosBase);
        List<Ingreso> recurrentesFiltrados = filtrarRegistros(recurrentesBase);

        ingresosAdapter.updateData(filtrados);
        recurrentesAdapter.updateData(recurrentesFiltrados);

        binding.tvIngresosVacio.setVisibility(ingresosAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        binding.tvRecurrentesVacio.setVisibility(recurrentesAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private <T extends RegistroFinanciero> List<T> filtrarRegistros(List<T> registros) {
        List<T> resultado = new ArrayList<>();
        for (T registro : registros) {
            if (!coincideConcepto(registro) || !coincideFecha(registro)) {
                continue;
            }
            resultado.add(registro);
        }
        return resultado;
    }

    private boolean coincideConcepto(RegistroFinanciero registro) {
        if (filtroConcepto.isEmpty()) {
            return true;
        }
        String articulo = registro.getArticulo();
        if (articulo == null) {
            return false;
        }
        return articulo.toLowerCase(Locale.getDefault()).contains(filtroConcepto);
    }

    private boolean coincideFecha(RegistroFinanciero registro) {
        if (filtroInicio == null || filtroFin == null) {
            return true;
        }
        LocalDate fechaRegistro = parseFecha(registro.getFecha());
        if (fechaRegistro == null) {
            return false;
        }
        return (!fechaRegistro.isBefore(filtroInicio) && !fechaRegistro.isAfter(filtroFin));
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(fecha, dateFormatter);
        } catch (DateTimeParseException ex) {
            return null;
        }
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
