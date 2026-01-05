package com.example.economix_android.Model.ahorro;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.economix_android.Model.data.Ahorro;
import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.RegistroFinanciero;
import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentAhorroBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ahorroFragment extends Fragment {

    private FragmentAhorroBinding binding;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAhorroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPerfil.setOnClickListener(v -> navigateSafely(v, R.id.usuario));
        binding.btnAyuda.setOnClickListener(v -> mostrarAyuda());

        binding.btnGuardar.setOnClickListener(v -> guardarAhorro());
        binding.btnEliminar.setOnClickListener(v -> eliminarAhorro());
        binding.btnLimpiar.setOnClickListener(v -> limpiarCampos());

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

        setupDatePicker(binding.etFechaLimite);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarAhorros();
    }

    private void navigateSafely(View view, int destinationId) {
        NavController navController = Navigation.findNavController(view);
        NavDestination currentDestination = navController.getCurrentDestination();
        if (currentDestination == null || currentDestination.getId() != destinationId) {
            navController.navigate(destinationId);
        }
    }

    private void guardarAhorro() {
        String objetivo = obtenerTexto(binding.etObjetivo);
        String descripcion = obtenerTexto(binding.etDescripcion);
        String meta = obtenerTexto(binding.etMeta);
        String ahorrado = obtenerTexto(binding.etAhorrado);
        String fechaLimite = obtenerTexto(binding.etFechaLimite);

        if (TextUtils.isEmpty(ahorrado)) {
            ahorrado = "0";
        }

        if (TextUtils.isEmpty(objetivo) || TextUtils.isEmpty(meta)) {
            mostrarMensaje(R.string.error_campos_obligatorios_ahorro);
            return;
        }

        if (!RegistroFinanciero.esMontoValido(meta) || !RegistroFinanciero.esMontoValido(ahorrado)) {
            mostrarMensaje(R.string.error_montos_ahorro_invalidos);
            return;
        }

        float metaValor = RegistroFinanciero.parseMonto(meta);
        float ahorradoValor = RegistroFinanciero.parseMonto(ahorrado);
        if (metaValor <= 0f || ahorradoValor < 0f || ahorradoValor > metaValor) {
            mostrarMensaje(R.string.error_montos_ahorro_rango);
            return;
        }

        Ahorro ahorro = new Ahorro(
                null,
                objetivo,
                descripcion,
                RegistroFinanciero.normalizarMonto(meta),
                RegistroFinanciero.normalizarMonto(ahorrado),
                fechaLimite
        );

        setButtonsEnabled(false);
        DataRepository.addAhorro(ahorro, new DataRepository.RepositoryCallback<Ahorro>() {
            @Override
            public void onSuccess(Ahorro result) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(R.string.mensaje_ahorro_guardado);
                actualizarUi(result);
                setButtonsEnabled(true);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(message);
                setButtonsEnabled(true);
            }
        });
    }

    private void eliminarAhorro() {
        setButtonsEnabled(false);
        DataRepository.removeLastAhorro(new DataRepository.RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean eliminado) {
                if (!isAdded()) {
                    return;
                }
                int mensaje = Boolean.TRUE.equals(eliminado)
                        ? R.string.mensaje_ahorro_eliminado
                        : R.string.error_sin_ahorros;
                mostrarMensaje(mensaje);
                if (Boolean.TRUE.equals(eliminado)) {
                    actualizarUi(obtenerUltimoAhorro());
                }
                setButtonsEnabled(true);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(message);
                setButtonsEnabled(true);
            }
        });
    }

    private void cargarAhorros() {
        setButtonsEnabled(false);
        DataRepository.refreshAhorros(new DataRepository.RepositoryCallback<java.util.List<Ahorro>>() {
            @Override
            public void onSuccess(java.util.List<Ahorro> result) {
                if (!isAdded()) {
                    return;
                }
                actualizarUi(obtenerUltimoAhorro());
                setButtonsEnabled(true);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(message);
                actualizarUi(obtenerUltimoAhorro());
                setButtonsEnabled(true);
            }
        });
    }

    private void actualizarUi(@Nullable Ahorro ahorro) {
        if (ahorro == null) {
            limpiarCampos();
            binding.progresoAhorro.setProgress(0);
            return;
        }

        binding.etObjetivo.setText(ahorro.getObjetivo());
        binding.etDescripcion.setText(ahorro.getDescripcion());
        binding.etMeta.setText(ahorro.getMeta());
        binding.etAhorrado.setText(ahorro.getAhorrado());
        binding.etFechaLimite.setText(ahorro.getFechaLimite());
        binding.progresoAhorro.setProgress(ahorro.getPorcentajeAvance());
    }

    private void setButtonsEnabled(boolean enabled) {
        binding.btnGuardar.setEnabled(enabled);
        binding.btnEliminar.setEnabled(enabled);
        binding.btnLimpiar.setEnabled(enabled);
    }

    private void limpiarCampos() {
        binding.etObjetivo.setText("");
        binding.etDescripcion.setText("");
        binding.etMeta.setText("");
        binding.etAhorrado.setText("");
        binding.etFechaLimite.setText("");
        binding.progresoAhorro.setProgress(0);
    }

    private Ahorro obtenerUltimoAhorro() {
        java.util.List<Ahorro> lista = DataRepository.getAhorros();
        if (lista.isEmpty()) {
            return null;
        }
        return lista.get(lista.size() - 1);
    }

    private String obtenerTexto(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
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

    private void mostrarMensaje(int mensajeRes) {
        Toast.makeText(requireContext(), mensajeRes, Toast.LENGTH_SHORT).show();
    }

    private void mostrarMensaje(String message) {
        String texto = message != null ? message : getString(R.string.mensaje_error_servidor);
        Toast.makeText(requireContext(), texto, Toast.LENGTH_SHORT).show();
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_ahorro)
                .setMessage(R.string.mensaje_ayuda_ahorro)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}