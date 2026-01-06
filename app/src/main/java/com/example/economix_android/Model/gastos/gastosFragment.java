package com.example.economix_android.Model.gastos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class gastosFragment extends Fragment {

    private FragmentGastosBinding binding;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

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
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_gastos)
                .setMessage(R.string.mensaje_ayuda_gastos)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void guardarGasto() {
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
            Toast.makeText(requireContext(), R.string.error_monto_gasto_invalido, Toast.LENGTH_SHORT).show();
            return;
        }

        String montoNormalizado = RegistroFinanciero.normalizarMonto(descripcion);

        Gasto gasto = new Gasto(null, articulo, montoNormalizado, fecha, periodo, recurrente);
        setGastoButtonsEnabled(false);
        DataRepository.addGasto(requireContext(), gasto, new DataRepository.RepositoryCallback<Gasto>() {
            @Override
            public void onSuccess(Gasto result) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(), R.string.mensaje_gasto_guardado, Toast.LENGTH_SHORT).show();
                limpiarCampos();
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
    }

    private String obtenerTexto(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}