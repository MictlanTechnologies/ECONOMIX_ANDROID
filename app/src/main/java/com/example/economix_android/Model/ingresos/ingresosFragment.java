package com.example.economix_android.Model.ingresos;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.economix_android.Model.data.RegistroFinanciero;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentIngresosBinding;
import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Ingreso;
import com.example.economix_android.util.ProfileImageUtils;
import com.example.economix_android.util.UsuarioAnimationNavigator;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ingresosFragment extends Fragment {

    public static final String ARG_INGRESO_ID = "arg_ingreso_id";
    public static final String ARG_INGRESO_ARTICULO = "arg_ingreso_articulo";
    public static final String ARG_INGRESO_MONTO = "arg_ingreso_monto";
    public static final String ARG_INGRESO_FECHA = "arg_ingreso_fecha";
    public static final String ARG_INGRESO_PERIODO = "arg_ingreso_periodo";
    public static final String ARG_INGRESO_RECURRENTE = "arg_ingreso_recurrente";
    public static final String ARG_INGRESO_PLANTILLA = "arg_ingreso_plantilla";

    private FragmentIngresosBinding binding;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Integer ingresoEnEdicionId;
    private boolean ingresoEnEdicionRecurrente;
    private boolean enModoPlantilla;
    private boolean enModoEdicion;

    private final Map<Integer, String> chipCategoryMap = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIngresosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initChipCategoryMap();

        binding.btnVerIng.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_navigation_ingresos_to_ingresosInfo));
        binding.btnPerfil.setOnClickListener(v -> UsuarioAnimationNavigator.playAndNavigate(v, R.id.usuario));
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
            } else if (viewId == R.id.navMenuMini) {
                navigateSafely(v, R.id.menu);
            }
        };

        binding.navGastos.setOnClickListener(bottomNavListener);
        binding.navIngresos.setOnClickListener(bottomNavListener);
        binding.navAhorro.setOnClickListener(bottomNavListener);
        binding.navGraficas.setOnClickListener(bottomNavListener);
        binding.navMenuMini.setOnClickListener(bottomNavListener);

        setupDatePicker(binding.etFechaIng);
        setupChipGroupSync();
        configurarEntradasDinamicas();
        cargarDatosEdicion();
    }

    private void initChipCategoryMap() {
        chipCategoryMap.put(R.id.chipSalario, getString(R.string.cat_ing_salario));
        chipCategoryMap.put(R.id.chipFreelance, getString(R.string.cat_ing_freelance));
        chipCategoryMap.put(R.id.chipNegocio, getString(R.string.cat_ing_negocio));
        chipCategoryMap.put(R.id.chipInversiones, getString(R.string.cat_ing_inversiones));
        chipCategoryMap.put(R.id.chipVenta, getString(R.string.cat_ing_venta));
        chipCategoryMap.put(R.id.chipOtroIng, getString(R.string.cat_ing_otro));
    }

    private void setupChipGroupSync() {
        binding.chipGroupCategoriaIng.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                String category = chipCategoryMap.get(checkedIds.get(0));
                if (category != null) {
                    binding.etPeriodoIng.setText(category);
                }
            } else {
                binding.etPeriodoIng.setText("");
            }
        });
    }

    private void configurarEntradasDinamicas() {
        binding.btnAgregarCategoriaIng.setOnClickListener(v -> agregarCategoriaPersonalizada());
        binding.btnAgregarEtiquetaIng.setOnClickListener(v -> agregarEtiquetaPersonalizada());
    }

    private void agregarCategoriaPersonalizada() {
        String categoria = obtenerTexto(binding.etNuevaCategoriaIng);
        if (TextUtils.isEmpty(categoria)) {
            Toast.makeText(requireContext(), R.string.error_categoria_vacia, Toast.LENGTH_SHORT).show();
            return;
        }
        Chip chip = new Chip(requireContext());
        chip.setId(View.generateViewId());
        chip.setText(categoria);
        chip.setCheckable(true);
        chip.setCheckedIconVisible(true);
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.tealSurfaceVariant)));
        chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.tealLight)));
        chip.setChipStrokeWidth(getResources().getDisplayMetrics().density);
        binding.chipGroupCategoriaIng.addView(chip);
        chipCategoryMap.put(chip.getId(), categoria);
        chip.setChecked(true);
        binding.etNuevaCategoriaIng.setText("");
    }

    private void agregarEtiquetaPersonalizada() {
        String etiqueta = normalizarEtiqueta(obtenerTexto(binding.etNuevaEtiquetaIng));
        if (TextUtils.isEmpty(etiqueta)) {
            Toast.makeText(requireContext(), R.string.error_etiqueta_vacia, Toast.LENGTH_SHORT).show();
            return;
        }
        if (existeEtiqueta(etiqueta)) {
            binding.etNuevaEtiquetaIng.setText("");
            return;
        }
        Chip chip = new Chip(requireContext());
        chip.setId(View.generateViewId());
        chip.setText(etiqueta);
        chip.setCheckable(false);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            binding.chipGroupEtiquetasIng.removeView(chip);
            sincronizarEtiquetasOcultas();
        });
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.tealAccent)));
        chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.tealLight)));
        chip.setChipStrokeWidth(getResources().getDisplayMetrics().density);
        binding.chipGroupEtiquetasIng.addView(chip);
        binding.etNuevaEtiquetaIng.setText("");
        sincronizarEtiquetasOcultas();
    }

    private boolean existeEtiqueta(String etiqueta) {
        for (int i = 0; i < binding.chipGroupEtiquetasIng.getChildCount(); i++) {
            View child = binding.chipGroupEtiquetasIng.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (etiqueta.equalsIgnoreCase(chip.getText().toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sincronizarEtiquetasOcultas() {
        Set<String> etiquetas = new LinkedHashSet<>();
        for (int i = 0; i < binding.chipGroupEtiquetasIng.getChildCount(); i++) {
            View child = binding.chipGroupEtiquetasIng.getChildAt(i);
            if (child instanceof Chip) {
                etiquetas.add(((Chip) child).getText().toString());
            }
        }
        binding.etEtiquetasIng.setText(TextUtils.join(",", etiquetas));
    }

    private String normalizarEtiqueta(String valor) {
        if (valor == null) {
            return "";
        }
        String limpia = valor.trim().replace(" ", "");
        limpia = limpia.replaceAll("[^\\p{L}\\p{N}_-]", "");
        if (limpia.isEmpty()) {
            return "";
        }
        return limpia.startsWith("#") ? limpia : "#" + limpia;
    }

    private void selectChipForCategory(String category) {
        if (TextUtils.isEmpty(category)) return;
        for (Map.Entry<Integer, String> entry : chipCategoryMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(category)) {
                Chip chip = binding.getRoot().findViewById(entry.getKey());
                if (chip != null) {
                    chip.setChecked(true);
                }
                return;
            }
        }
        // If category doesn't match any chip, select "Otro" and put text in hidden field
        Chip otroChip = binding.getRoot().findViewById(R.id.chipOtroIng);
        if (otroChip != null) {
            otroChip.setChecked(true);
        }
        binding.etPeriodoIng.setText(category);
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

        if (TextUtils.isEmpty(articulo) || TextUtils.isEmpty(fecha) || TextUtils.isEmpty(periodo)) {
            Toast.makeText(requireContext(), R.string.error_campos_obligatorios_ingreso, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!RegistroFinanciero.esMontoValido(descripcion)) {
            Toast.makeText(requireContext(), R.string.error_monto_ingreso_invalido, Toast.LENGTH_SHORT).show();
            return;
        }

        String montoNormalizado = RegistroFinanciero.normalizarMonto(descripcion);

        Ingreso ingreso = new Ingreso(null, articulo, montoNormalizado, fecha, periodo, recurrente);
        setIngresoButtonsEnabled(false);
        DataRepository.addIngreso(requireContext(), ingreso, new DataRepository.RepositoryCallback<Ingreso>() {
            @Override
            public void onSuccess(Ingreso result) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(), R.string.mensaje_ingreso_guardado, Toast.LENGTH_SHORT).show();
                UsuarioAnimationNavigator.playOnly(binding.getRoot(), resolverAnimacionRaw("ingreso"));
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
        Toast.makeText(requireContext(), R.string.mensaje_selecciona_ingreso_eliminar, Toast.LENGTH_SHORT).show();
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
        binding.chipGroupCategoriaIng.clearCheck();
        binding.chipGroupEtiquetasIng.removeAllViews();
        binding.etEtiquetasIng.setText("");
        binding.etNuevaEtiquetaIng.setText("");
        binding.etNuevaCategoriaIng.setText("");
        enModoPlantilla = false;
        establecerModoEdicion(false, null, false);
    }

    private void cargarDatosEdicion() {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }
        boolean esPlantilla = args.getBoolean(ARG_INGRESO_PLANTILLA, false);
        String articulo = args.getString(ARG_INGRESO_ARTICULO, "");
        String monto = args.getString(ARG_INGRESO_MONTO, "");
        String fecha = args.getString(ARG_INGRESO_FECHA, "");
        String periodo = args.getString(ARG_INGRESO_PERIODO, "");
        boolean recurrente = args.getBoolean(ARG_INGRESO_RECURRENTE, false);

        binding.etArticuloIng.setText(articulo);
        binding.etDescripcionIng.setText(monto);
        binding.etFechaIng.setText(fecha);
        binding.etPeriodoIng.setText(periodo);
        selectChipForCategory(periodo);
        if (esPlantilla) {
            enModoPlantilla = true;
            binding.rbRecurrenteIng.setChecked(false);
            binding.rbRecurrenteIng.setEnabled(false);
            establecerModoEdicion(false, null, false);
        } else {
            enModoPlantilla = false;
            binding.rbRecurrenteIng.setChecked(recurrente);
            int id = args.getInt(ARG_INGRESO_ID, -1);
            if (id > 0) {
                establecerModoEdicion(true, id, recurrente);
            }
        }
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
        editText.setOnClickListener(v -> showDatePicker(editText));
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

    @RawRes
    private int resolverAnimacionRaw(@NonNull String nombre) {
        int id = requireContext().getResources().getIdentifier(
                nombre.toLowerCase(Locale.ROOT), "raw", requireContext().getPackageName());
        return id != 0 ? id : R.raw.usuario;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
