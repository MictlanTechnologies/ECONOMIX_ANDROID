package com.example.economix_android.Model.ingresos;

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
import com.example.economix_android.databinding.FragmentIngresosBinding;
import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Ingreso;
import com.example.economix_android.util.ProfileImageUtils;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ingresosFragment extends Fragment {

    public static final String ARG_INGRESO_ID = "arg_ingreso_id";
    public static final String ARG_INGRESO_ARTICULO = "arg_ingreso_articulo";
    public static final String ARG_INGRESO_MONTO = "arg_ingreso_monto";
    public static final String ARG_INGRESO_FECHA = "arg_ingreso_fecha";
    public static final String ARG_INGRESO_PERIODO = "arg_ingreso_periodo";
    public static final String ARG_INGRESO_RECURRENTE = "arg_ingreso_recurrente";

    private FragmentIngresosBinding binding;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Integer ingresoEnEdicionId;
    private boolean ingresoEnEdicionRecurrente;
    private boolean enModoEdicion;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIngresosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnVerIng.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_navigation_ingresos_to_ingresosInfo));
        binding.btnPerfil.setOnClickListener(v -> navigateSafely(v, R.id.usuario));
        ProfileImageUtils.applyProfileImage(requireContext(), binding.btnPerfil);
        binding.btnAyudaIng.setOnClickListener(v -> mostrarAyuda());

        binding.btnGuardarIng.setOnClickListener(v -> guardarIngreso());
        binding.btnEliminarIng.setOnClickListener(v -> eliminarIngreso());
        binding.btnLimpiarIng.setOnClickListener(v -> limpiarCampos());

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

        setupDatePicker(binding.etFechaIng);
        cargarDatosEdicion();
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_ingresos)
                .setMessage(R.string.mensaje_ayuda_ingresos)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void guardarIngreso() {
        if (enModoEdicion) {
            actualizarIngreso();
            return;
        }
        String articulo = obtenerTexto(binding.etArticuloIng);
        String descripcion = obtenerTexto(binding.etDescripcionIng);
        String fecha = obtenerTexto(binding.etFechaIng);
        String periodo = obtenerTexto(binding.etPeriodoIng);
        boolean recurrente = binding.rbRecurrenteIng.isChecked();

        if (TextUtils.isEmpty(articulo)
                || (!recurrente && (TextUtils.isEmpty(fecha) || TextUtils.isEmpty(periodo)))
                || (recurrente && TextUtils.isEmpty(periodo))) {
            Toast.makeText(requireContext(), R.string.error_campos_obligatorios_ingreso, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!RegistroFinanciero.esMontoValido(descripcion)) {
            Toast.makeText(requireContext(), R.string.error_monto_ingreso_invalido, Toast.LENGTH_SHORT).show();
            return;
        }

        String montoNormalizado = RegistroFinanciero.normalizarMonto(descripcion);

        Ingreso ingreso = new Ingreso(null, articulo, montoNormalizado, recurrente ? "" : fecha, periodo, recurrente);
        setIngresoButtonsEnabled(false);
        DataRepository.addIngreso(requireContext(), ingreso, new DataRepository.RepositoryCallback<Ingreso>() {
            @Override
            public void onSuccess(Ingreso result) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(), R.string.mensaje_ingreso_guardado, Toast.LENGTH_SHORT).show();
                limpiarCampos();
                setIngresoButtonsEnabled(true);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(),
                        message != null ? message : getString(R.string.mensaje_error_operacion),
                        Toast.LENGTH_SHORT).show();
                setIngresoButtonsEnabled(true);
            }
        });
    }

    private void eliminarIngreso() {
        if (enModoEdicion) {
            eliminarIngresoSeleccionado();
            return;
        }
        setIngresoButtonsEnabled(false);
        DataRepository.removeLastIngreso(new DataRepository.RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean eliminado) {
                if (!isAdded()) {
                    return;
                }
                int mensaje = Boolean.TRUE.equals(eliminado)
                        ? R.string.mensaje_ingreso_eliminado
                        : R.string.error_sin_ingresos;
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
                setIngresoButtonsEnabled(true);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(),
                        message != null ? message : getString(R.string.mensaje_error_operacion),
                        Toast.LENGTH_SHORT).show();
                setIngresoButtonsEnabled(true);
            }
        });
    }

    private void setIngresoButtonsEnabled(boolean enabled) {
        binding.btnGuardarIng.setEnabled(enabled);
        binding.btnEliminarIng.setEnabled(enabled);
    }

    private void limpiarCampos() {
        binding.etArticuloIng.setText("");
        binding.etDescripcionIng.setText("");
        binding.etFechaIng.setText("");
        binding.etPeriodoIng.setText("");
        binding.rbRecurrenteIng.setChecked(false);
        binding.rbRecurrenteIng.setEnabled(true);
        establecerModoEdicion(false, null, false);
    }

    private void cargarDatosEdicion() {
        Bundle args = getArguments();
        if (args == null || !args.containsKey(ARG_INGRESO_ID)) {
            return;
        }
        int id = args.getInt(ARG_INGRESO_ID, -1);
        if (id <= 0) {
            return;
        }
        String articulo = args.getString(ARG_INGRESO_ARTICULO, "");
        String monto = args.getString(ARG_INGRESO_MONTO, "");
        String fecha = args.getString(ARG_INGRESO_FECHA, "");
        String periodo = args.getString(ARG_INGRESO_PERIODO, "");
        boolean recurrente = args.getBoolean(ARG_INGRESO_RECURRENTE, false);

        binding.etArticuloIng.setText(articulo);
        binding.etDescripcionIng.setText(monto);
        binding.etFechaIng.setText(fecha);
        binding.etPeriodoIng.setText(periodo);
        binding.rbRecurrenteIng.setChecked(recurrente);
        establecerModoEdicion(true, id, recurrente);
        args.clear();
    }

    private void establecerModoEdicion(boolean habilitar, Integer ingresoId, boolean recurrente) {
        enModoEdicion = habilitar;
        ingresoEnEdicionId = habilitar ? ingresoId : null;
        ingresoEnEdicionRecurrente = habilitar && recurrente;
        binding.btnGuardarIng.setText(habilitar ? getString(R.string.label_actualizar) : getString(R.string.label_guardar));
        binding.btnEliminarIng.setText(getString(R.string.label_eliminar));
        binding.rbRecurrenteIng.setEnabled(!habilitar);
    }

    private void actualizarIngreso() {
        String articulo = obtenerTexto(binding.etArticuloIng);
        String descripcion = obtenerTexto(binding.etDescripcionIng);
        String fecha = obtenerTexto(binding.etFechaIng);
        String periodo = obtenerTexto(binding.etPeriodoIng);

        if (ingresoEnEdicionId == null) {
            Toast.makeText(requireContext(), R.string.error_ingreso_id, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(articulo)
                || (!ingresoEnEdicionRecurrente && (TextUtils.isEmpty(fecha) || TextUtils.isEmpty(periodo)))
                || (ingresoEnEdicionRecurrente && TextUtils.isEmpty(periodo))) {
            Toast.makeText(requireContext(), R.string.error_campos_obligatorios_ingreso, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!RegistroFinanciero.esMontoValido(descripcion)) {
            Toast.makeText(requireContext(), R.string.error_monto_ingreso_invalido, Toast.LENGTH_SHORT).show();
            return;
        }

        String montoNormalizado = RegistroFinanciero.normalizarMonto(descripcion);
        Ingreso ingreso = new Ingreso(ingresoEnEdicionId, articulo, montoNormalizado,
                ingresoEnEdicionRecurrente ? "" : fecha, periodo, ingresoEnEdicionRecurrente);
        setIngresoButtonsEnabled(false);
        DataRepository.updateIngreso(requireContext(), ingreso, new DataRepository.RepositoryCallback<Ingreso>() {
            @Override
            public void onSuccess(Ingreso result) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(), R.string.mensaje_ingreso_actualizado, Toast.LENGTH_SHORT).show();
                limpiarCampos();
                setIngresoButtonsEnabled(true);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(),
                        message != null ? message : getString(R.string.mensaje_error_operacion),
                        Toast.LENGTH_SHORT).show();
                setIngresoButtonsEnabled(true);
            }
        });
    }

    private void eliminarIngresoSeleccionado() {
        if (ingresoEnEdicionId == null) {
            Toast.makeText(requireContext(), R.string.error_ingreso_id, Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_confirmar_eliminacion)
                .setMessage(R.string.mensaje_confirmar_eliminacion)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> ejecutarEliminacionSeleccionada())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void ejecutarEliminacionSeleccionada() {
        setIngresoButtonsEnabled(false);
        DataRepository.RepositoryCallback<Boolean> callback = new DataRepository.RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean eliminado) {
                if (!isAdded()) {
                    return;
                }
                int mensaje = Boolean.TRUE.equals(eliminado)
                        ? R.string.mensaje_ingreso_eliminado_seleccionado
                        : R.string.error_sin_ingresos;
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
                limpiarCampos();
                setIngresoButtonsEnabled(true);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(),
                        message != null ? message : getString(R.string.mensaje_error_operacion),
                        Toast.LENGTH_SHORT).show();
                setIngresoButtonsEnabled(true);
            }
        };

        if (ingresoEnEdicionRecurrente) {
            DataRepository.removeIngresoRecurrenteById(ingresoEnEdicionId, callback);
        } else {
            DataRepository.removeIngresoById(ingresoEnEdicionId, callback);
        }
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
