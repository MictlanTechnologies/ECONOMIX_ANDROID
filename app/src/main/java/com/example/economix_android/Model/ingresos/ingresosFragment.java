package com.example.economix_android.Model.ingresos;

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

import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentIngresosBinding;
import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Ingreso;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ingresosFragment extends Fragment {

    private FragmentIngresosBinding binding;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

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
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_ingresos)
                .setMessage(R.string.mensaje_ayuda_ingresos)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void guardarIngreso() {
        String articulo = obtenerTexto(binding.etArticuloIng);
        String descripcion = obtenerTexto(binding.etDescripcionIng);
        String fecha = obtenerTexto(binding.etFechaIng);
        String periodo = obtenerTexto(binding.etPeriodoIng);
        boolean recurrente = binding.rbRecurrenteIng.isChecked();

        if (TextUtils.isEmpty(articulo) || TextUtils.isEmpty(fecha) || TextUtils.isEmpty(periodo)) {
            Toast.makeText(requireContext(), R.string.error_campos_obligatorios_ingreso, Toast.LENGTH_SHORT).show();
            return;
        }

        Ingreso ingreso = new Ingreso(articulo, descripcion, fecha, periodo, recurrente);
        DataRepository.addIngreso(ingreso);
        Toast.makeText(requireContext(), R.string.mensaje_ingreso_guardado, Toast.LENGTH_SHORT).show();
        limpiarCampos();
    }

    private void eliminarIngreso() {
        boolean eliminado = DataRepository.removeLastIngreso();
        int mensaje = eliminado ? R.string.mensaje_ingreso_eliminado : R.string.error_sin_ingresos;
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private void limpiarCampos() {
        binding.etArticuloIng.setText("");
        binding.etDescripcionIng.setText("");
        binding.etFechaIng.setText("");
        binding.etPeriodoIng.setText("");
        binding.rbRecurrenteIng.setChecked(false);
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