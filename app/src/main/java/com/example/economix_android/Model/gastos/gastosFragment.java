package com.example.economix_android.Model.gastos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import com.example.economix_android.Model.data.RegistroFinanciero;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentGastosBinding;
import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Gasto;
import com.example.economix_android.Model.data.Ingreso;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class gastosFragment extends Fragment {

    private FragmentGastosBinding binding;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private ArrayAdapter<String> ingresosAdapter;
    private final List<Ingreso> ingresosDisponibles = new ArrayList<>();
    private Ingreso ingresoSeleccionado;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGastosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnVerGas.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_navigation_gastos_to_gastosInfo));
        binding.btnPerfil.setOnClickListener(v -> navigateSafely(v, R.id.usuario));
        binding.btnAyudaGas.setOnClickListener(v -> mostrarAyuda());

        binding.btnGuardarGas.setOnClickListener(v -> guardarGasto());
        binding.btnEliminarGas.setOnClickListener(v -> eliminarGasto());
        binding.btnLimpiarGas.setOnClickListener(v -> limpiarCampos());

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

        setupDatePicker(binding.etFechaGas);
        configurarIngresos();
        cargarIngresos();
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_gastos)
                .setMessage(R.string.mensaje_ayuda_gastos)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void guardarGasto() {
        limpiarErrores();
        String articulo = obtenerTexto(binding.etArticuloGas);
        String descripcion = obtenerTexto(binding.etDescripcionGas);
        String fecha = obtenerTexto(binding.etFechaGas);
        String periodo = obtenerTexto(binding.etPeriodoGas);
        boolean recurrente = binding.rbRecurrenteGas.isChecked();

        if (TextUtils.isEmpty(articulo) || TextUtils.isEmpty(fecha) || TextUtils.isEmpty(periodo)) {
            Toast.makeText(requireContext(), R.string.error_campos_obligatorios_gasto, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!RegistroFinanciero.esMontoValido(descripcion)) {
            binding.tilMontoGasto.setError(getString(R.string.error_monto_gasto_invalido));
            return;
        }

        String montoNormalizado = RegistroFinanciero.normalizarMonto(descripcion);
        BigDecimal montoGasto = parseMontoSeguro(montoNormalizado);

        if (ingresoSeleccionado == null) {
            binding.tilIngresoSeleccionGasto.setError(getString(R.string.error_ingreso_obligatorio));
            return;
        }

        BigDecimal disponible = parseMontoSeguro(ingresoSeleccionado.getDescripcion());
        if (montoGasto.compareTo(disponible) > 0) {
            binding.tilMontoGasto.setError(getString(R.string.error_gasto_excede_ingreso));
            return;
        }

        Gasto gasto = new Gasto(null, articulo, montoNormalizado, fecha, periodo, recurrente);
        setGastoButtonsEnabled(false);
        BigDecimal nuevoMontoIngreso = disponible.subtract(montoGasto);
        BigDecimal montoOriginal = disponible;
        DataRepository.updateIngresoMonto(requireContext(), ingresoSeleccionado, nuevoMontoIngreso,
                new DataRepository.RepositoryCallback<Ingreso>() {
                    @Override
                    public void onSuccess(Ingreso result) {
                        if (!isAdded()) {
                            return;
                        }
                        crearGasto(gasto, result, montoOriginal);
                    }

                    @Override
                    public void onError(String message) {
                        if (!isAdded()) {
                            return;
                        }
                        setGastoButtonsEnabled(true);
                        mostrarMensajeError(message);
                    }
                });
    }

    private void crearGasto(Gasto gasto, Ingreso ingresoActualizado, BigDecimal montoOriginal) {
        DataRepository.addGasto(requireContext(), gasto, new DataRepository.RepositoryCallback<Gasto>() {
            @Override
            public void onSuccess(Gasto result) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(), R.string.mensaje_gasto_guardado, Toast.LENGTH_SHORT).show();
                limpiarCampos();
                cargarIngresos();
                setGastoButtonsEnabled(true);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                revertirIngreso(ingresoActualizado, montoOriginal);
                setGastoButtonsEnabled(true);
                mostrarMensajeError(message);
            }
        });
    }

    private void eliminarGasto() {
        setGastoButtonsEnabled(false);
        DataRepository.removeLastGasto(new DataRepository.RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean eliminado) {
                if (!isAdded()) {
                    return;
                }
                int mensaje = Boolean.TRUE.equals(eliminado)
                        ? R.string.mensaje_gasto_eliminado
                        : R.string.error_sin_gastos;
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
                setGastoButtonsEnabled(true);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(),
                        message != null ? message : getString(R.string.mensaje_error_operacion),
                        Toast.LENGTH_SHORT).show();
                setGastoButtonsEnabled(true);
            }
        });
    }

    private void setGastoButtonsEnabled(boolean enabled) {
        binding.btnGuardarGas.setEnabled(enabled);
        binding.btnEliminarGas.setEnabled(enabled);
    }

    private void limpiarCampos() {
        binding.etArticuloGas.setText("");
        binding.etDescripcionGas.setText("");
        binding.etFechaGas.setText("");
        binding.etPeriodoGas.setText("");
        binding.rbRecurrenteGas.setChecked(false);
        binding.etIngresoSeleccionGasto.setText("");
        ingresoSeleccionado = null;
        actualizarIngresoDisponible();
    }

    private void limpiarErrores() {
        binding.tilMontoGasto.setError(null);
        binding.tilIngresoSeleccionGasto.setError(null);
    }

    private String obtenerTexto(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void configurarIngresos() {
        AutoCompleteTextView ingresoView = (AutoCompleteTextView) binding.etIngresoSeleccionGasto;
        ingresosAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        ingresoView.setAdapter(ingresosAdapter);
        ingresoView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < ingresosDisponibles.size()) {
                ingresoSeleccionado = ingresosDisponibles.get(position);
                binding.tilIngresoSeleccionGasto.setError(null);
                actualizarIngresoDisponible();
            }
        });
    }

    private void cargarIngresos() {
        DataRepository.refreshIngresos(new DataRepository.RepositoryCallback<List<Ingreso>>() {
            @Override
            public void onSuccess(List<Ingreso> result) {
                if (!isAdded()) {
                    return;
                }
                ingresosDisponibles.clear();
                ingresosDisponibles.addAll(result);
                actualizarIngresoAdapter();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensajeError(message);
            }
        });
    }

    private void actualizarIngresoAdapter() {
        ingresosAdapter.clear();
        for (Ingreso ingreso : ingresosDisponibles) {
            String monto = RegistroFinanciero.normalizarMonto(ingreso.getDescripcion());
            ingresosAdapter.add(getString(R.string.label_ingreso_item, ingreso.getArticulo(), monto));
        }
        ingresosAdapter.notifyDataSetChanged();
        if (ingresosDisponibles.isEmpty()) {
            ingresoSeleccionado = null;
            ((AutoCompleteTextView) binding.etIngresoSeleccionGasto).setText("", false);
            actualizarIngresoDisponible();
            return;
        }
        if (ingresoSeleccionado != null) {
            Integer idSeleccionado = ingresoSeleccionado.getId();
            ingresoSeleccionado = null;
            for (int i = 0; i < ingresosDisponibles.size(); i++) {
                if (idSeleccionado != null && idSeleccionado.equals(ingresosDisponibles.get(i).getId())) {
                    ingresoSeleccionado = ingresosDisponibles.get(i);
                    ((AutoCompleteTextView) binding.etIngresoSeleccionGasto)
                            .setText(ingresosAdapter.getItem(i), false);
                    break;
                }
            }
        }
        actualizarIngresoDisponible();
    }

    private void actualizarIngresoDisponible() {
        String disponible = "0";
        if (ingresoSeleccionado != null) {
            disponible = RegistroFinanciero.normalizarMonto(ingresoSeleccionado.getDescripcion());
        }
        binding.tvIngresoDisponibleGasto.setText(getString(R.string.label_disponible_valor, disponible));
    }

    private void navigateSafely(View view, int destinationId) {
        NavController navController = Navigation.findNavController(view);
        NavDestination currentDestination = navController.getCurrentDestination();
        if (currentDestination == null || currentDestination.getId() != destinationId) {
            navController.navigate(destinationId);
        }
    }

    private void setupDatePicker(TextInputEditText editText) {
        editText.setShowSoftInputOnFocus(false);
        editText.setOnClickListener(v -> showDatePicker(editText));
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker(editText);
            }
        });
    }

    private void showDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        String currentText = editText.getText() != null ? editText.getText().toString() : "";

        if (!currentText.isEmpty()) {
            try {
                java.util.Date parsedDate = dateFormatter.parse(currentText);
                if (parsedDate != null) {
                    calendar.setTime(parsedDate);
                }
            } catch (ParseException ignored) {
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    editText.setText(dateFormatter.format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        editText.clearFocus();
        datePickerDialog.show();
    }

    private void revertirIngreso(Ingreso ingresoActualizado, BigDecimal montoOriginal) {
        if (ingresoActualizado == null || ingresoActualizado.getId() == null) {
            return;
        }
        DataRepository.updateIngresoMonto(requireContext(), ingresoActualizado, montoOriginal,
                new DataRepository.RepositoryCallback<Ingreso>() {
                    @Override
                    public void onSuccess(Ingreso result) {
                        if (!isAdded()) {
                            return;
                        }
                        cargarIngresos();
                    }

                    @Override
                    public void onError(String message) {
                        if (!isAdded()) {
                            return;
                        }
                        cargarIngresos();
                    }
                });
    }

    private BigDecimal parseMontoSeguro(String valor) {
        if (!RegistroFinanciero.esMontoValido(valor)) {
            return BigDecimal.ZERO;
        }
        String normalizado = RegistroFinanciero.normalizarMonto(valor);
        try {
            return new BigDecimal(normalizado);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private void mostrarMensajeError(String message) {
        String texto = message != null ? message : getString(R.string.mensaje_error_operacion);
        Toast.makeText(requireContext(), texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
