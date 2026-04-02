package com.example.economix_android.Model.gastos;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.example.economix_android.R;
import com.example.economix_android.databinding.FragmentGastosBinding;
import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Gasto;
import com.example.economix_android.Model.data.Ingreso;
import com.example.economix_android.util.ProfileImageUtils;
import com.example.economix_android.util.UsuarioAnimationNavigator;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class gastosFragment extends Fragment {

    public static final String ARG_GASTO_ID = "arg_gasto_id";
    public static final String ARG_GASTO_ARTICULO = "arg_gasto_articulo";
    public static final String ARG_GASTO_MONTO = "arg_gasto_monto";
    public static final String ARG_GASTO_FECHA = "arg_gasto_fecha";
    public static final String ARG_GASTO_PERIODO = "arg_gasto_periodo";
    public static final String ARG_GASTO_RECURRENTE = "arg_gasto_recurrente";
    public static final String ARG_GASTO_PLANTILLA = "arg_gasto_plantilla";

    private FragmentGastosBinding binding;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private ArrayAdapter<String> ingresosAdapter;
    private final List<Ingreso> ingresosDisponibles = new ArrayList<>();
    private Ingreso ingresoSeleccionado;
    private Integer gastoEnEdicionId;
    private boolean gastoEnEdicionRecurrente;
    private boolean enModoPlantilla;
    private boolean enModoEdicion;

    private final Map<Integer, String> chipCategoryMap = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGastosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initChipCategoryMap();

        binding.btnVerGas.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_navigation_gastos_to_gastosInfo));
        binding.btnPerfil.setOnClickListener(v -> UsuarioAnimationNavigator.playAndNavigate(v, R.id.usuario));
        ProfileImageUtils.applyProfileImage(requireContext(), binding.btnPerfil);
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
            } else if (viewId == R.id.navMenuMini) {
                navigateSafely(v, R.id.menu);
            }
        };

        binding.navGastos.setOnClickListener(bottomNavListener);
        binding.navIngresos.setOnClickListener(bottomNavListener);
        binding.navAhorro.setOnClickListener(bottomNavListener);
        binding.navGraficas.setOnClickListener(bottomNavListener);
        binding.navMenuMini.setOnClickListener(bottomNavListener);

        setupDatePicker(binding.etFechaGas);
        setupChipGroupSync();
        configurarEntradasDinamicas();
        configurarIngresos();
        cargarIngresos();
        cargarDatosEdicion();
    }

    private void initChipCategoryMap() {
        chipCategoryMap.put(R.id.chipAlimentacion, getString(R.string.cat_alimentacion));
        chipCategoryMap.put(R.id.chipTransporte, getString(R.string.cat_transporte));
        chipCategoryMap.put(R.id.chipEntretenimiento, getString(R.string.cat_entretenimiento));
        chipCategoryMap.put(R.id.chipSalud, getString(R.string.cat_salud));
        chipCategoryMap.put(R.id.chipEducacion, getString(R.string.cat_educacion));
        chipCategoryMap.put(R.id.chipServicios, getString(R.string.cat_servicios));
        chipCategoryMap.put(R.id.chipHogar, getString(R.string.cat_hogar));
        chipCategoryMap.put(R.id.chipOtroGas, getString(R.string.cat_otro));
    }

    private void setupChipGroupSync() {
        binding.chipGroupCategoriaGas.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                String category = chipCategoryMap.get(checkedIds.get(0));
                if (category != null) {
                    binding.etPeriodoGas.setText(category);
                }
            } else {
                binding.etPeriodoGas.setText("");
            }
        });
    }

    private void configurarEntradasDinamicas() {
        binding.btnAgregarCategoriaGas.setOnClickListener(v -> agregarCategoriaPersonalizada());
        binding.btnAgregarEtiquetaGas.setOnClickListener(v -> agregarEtiquetaPersonalizada());
    }

    private void agregarCategoriaPersonalizada() {
        String categoria = obtenerTexto(binding.etNuevaCategoriaGas);
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
        binding.chipGroupCategoriaGas.addView(chip);
        chipCategoryMap.put(chip.getId(), categoria);
        chip.setChecked(true);
        binding.etNuevaCategoriaGas.setText("");
    }

    private void agregarEtiquetaPersonalizada() {
        String etiqueta = normalizarEtiqueta(obtenerTexto(binding.etNuevaEtiquetaGas));
        if (TextUtils.isEmpty(etiqueta)) {
            Toast.makeText(requireContext(), R.string.error_etiqueta_vacia, Toast.LENGTH_SHORT).show();
            return;
        }
        if (existeEtiqueta(etiqueta)) {
            binding.etNuevaEtiquetaGas.setText("");
            return;
        }
        Chip chip = new Chip(requireContext());
        chip.setId(View.generateViewId());
        chip.setText(etiqueta);
        chip.setCheckable(false);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            binding.chipGroupEtiquetasGas.removeView(chip);
            sincronizarEtiquetasOcultas();
        });
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.tealAccent)));
        chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.tealLight)));
        chip.setChipStrokeWidth(getResources().getDisplayMetrics().density);
        binding.chipGroupEtiquetasGas.addView(chip);
        binding.etNuevaEtiquetaGas.setText("");
        sincronizarEtiquetasOcultas();
    }

    private boolean existeEtiqueta(String etiqueta) {
        for (int i = 0; i < binding.chipGroupEtiquetasGas.getChildCount(); i++) {
            View child = binding.chipGroupEtiquetasGas.getChildAt(i);
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
        for (int i = 0; i < binding.chipGroupEtiquetasGas.getChildCount(); i++) {
            View child = binding.chipGroupEtiquetasGas.getChildAt(i);
            if (child instanceof Chip) {
                etiquetas.add(((Chip) child).getText().toString());
            }
        }
        binding.etEtiquetasGas.setText(TextUtils.join(",", etiquetas));
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
        Chip otroChip = binding.getRoot().findViewById(R.id.chipOtroGas);
        if (otroChip != null) {
            otroChip.setChecked(true);
        }
        binding.etPeriodoGas.setText(category);
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_gastos)
                .setMessage(R.string.mensaje_ayuda_gastos)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void guardarGasto() {
        if (enModoEdicion) {
            actualizarGasto();
            return;
        }
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
                        mostrarIngresoAgotado(result);
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
        Integer ingresoVinculadoId = ingresoActualizado != null ? ingresoActualizado.getId() : null;
        DataRepository.addGasto(requireContext(), gasto, ingresoVinculadoId, new DataRepository.RepositoryCallback<Gasto>() {
            @Override
            public void onSuccess(Gasto result) {
                if (!isAdded()) {
                    return;
                }
                if (result != null && result.getId() != null && ingresoActualizado != null && ingresoActualizado.getId() != null) {
                    DataRepository.vincularGastoConIngreso(result.getId(), ingresoActualizado.getId());
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
        if (enModoEdicion) {
            eliminarGastoSeleccionado();
            return;
        }
        Toast.makeText(requireContext(), R.string.mensaje_selecciona_gasto_eliminar, Toast.LENGTH_SHORT).show();
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
        binding.rbRecurrenteGas.setEnabled(true);
        binding.etIngresoSeleccionGasto.setText("");
        binding.chipGroupCategoriaGas.clearCheck();
        binding.chipGroupEtiquetasGas.removeAllViews();
        binding.etEtiquetasGas.setText("");
        binding.etNuevaEtiquetaGas.setText("");
        binding.etNuevaCategoriaGas.setText("");
        ingresoSeleccionado = null;
        actualizarIngresoDisponible();
        enModoPlantilla = false;
        establecerModoEdicion(false, null, false);
    }

    private void cargarDatosEdicion() {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }
        boolean esPlantilla = args.getBoolean(ARG_GASTO_PLANTILLA, false);
        String articulo = args.getString(ARG_GASTO_ARTICULO, "");
        String monto = args.getString(ARG_GASTO_MONTO, "");
        String fecha = args.getString(ARG_GASTO_FECHA, "");
        String periodo = args.getString(ARG_GASTO_PERIODO, "");
        boolean recurrente = args.getBoolean(ARG_GASTO_RECURRENTE, false);

        binding.etArticuloGas.setText(articulo);
        binding.etDescripcionGas.setText(monto);
        binding.etFechaGas.setText(fecha);
        binding.etPeriodoGas.setText(periodo);
        selectChipForCategory(periodo);
        if (esPlantilla) {
            enModoPlantilla = true;
            binding.rbRecurrenteGas.setChecked(false);
            binding.rbRecurrenteGas.setEnabled(false);
            establecerModoEdicion(false, null, false);
        } else {
            enModoPlantilla = false;
            binding.rbRecurrenteGas.setChecked(recurrente);
            int id = args.getInt(ARG_GASTO_ID, -1);
            if (id > 0) {
                establecerModoEdicion(true, id, recurrente);
            }
        }
        args.clear();
    }

    private void establecerModoEdicion(boolean habilitar, Integer gastoId, boolean recurrente) {
        enModoEdicion = habilitar;
        gastoEnEdicionId = habilitar ? gastoId : null;
        gastoEnEdicionRecurrente = habilitar && recurrente;
        binding.btnGuardarGas.setText(habilitar ? getString(R.string.label_actualizar) : getString(R.string.label_guardar));
        binding.btnEliminarGas.setText(getString(R.string.label_eliminar));
        binding.rbRecurrenteGas.setEnabled(!habilitar);
    }

    private void actualizarGasto() {
        limpiarErrores();
        String articulo = obtenerTexto(binding.etArticuloGas);
        String descripcion = obtenerTexto(binding.etDescripcionGas);
        String fecha = obtenerTexto(binding.etFechaGas);
        String periodo = obtenerTexto(binding.etPeriodoGas);

        if (gastoEnEdicionId == null) {
            Toast.makeText(requireContext(), R.string.error_gasto_id, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(articulo)
                || (!gastoEnEdicionRecurrente && (TextUtils.isEmpty(fecha) || TextUtils.isEmpty(periodo)))
                || (gastoEnEdicionRecurrente && TextUtils.isEmpty(periodo))) {
            Toast.makeText(requireContext(), R.string.error_campos_obligatorios_gasto, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!RegistroFinanciero.esMontoValido(descripcion)) {
            binding.tilMontoGasto.setError(getString(R.string.error_monto_gasto_invalido));
            return;
        }

        String montoNormalizado = RegistroFinanciero.normalizarMonto(descripcion);
        Gasto gasto = new Gasto(gastoEnEdicionId, articulo, montoNormalizado,
                gastoEnEdicionRecurrente ? "" : fecha, periodo, gastoEnEdicionRecurrente);
        setGastoButtonsEnabled(false);
        DataRepository.updateGasto(requireContext(), gasto, new DataRepository.RepositoryCallback<Gasto>() {
            @Override
            public void onSuccess(Gasto result) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(), R.string.mensaje_gasto_actualizado, Toast.LENGTH_SHORT).show();
                limpiarCampos();
                setGastoButtonsEnabled(true);
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

    private void eliminarGastoSeleccionado() {
        if (gastoEnEdicionId == null) {
            Toast.makeText(requireContext(), R.string.error_gasto_id, Toast.LENGTH_SHORT).show();
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
        setGastoButtonsEnabled(false);
        final Integer gastoId = gastoEnEdicionId;
        final Gasto gastoEliminado = DataRepository.getGastoById(gastoId);
        final Integer ingresoVinculadoId = DataRepository.getIngresoIdVinculadoAGasto(gastoId);

        DataRepository.RepositoryCallback<Boolean> callback = new DataRepository.RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean eliminado) {
                if (!isAdded()) {
                    return;
                }
                int mensaje = Boolean.TRUE.equals(eliminado)
                        ? R.string.mensaje_gasto_eliminado_seleccionado
                        : R.string.error_sin_gastos;
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
                if (Boolean.TRUE.equals(eliminado) && gastoEliminado != null && ingresoVinculadoId != null) {
                    reembolsarMontoAlIngreso(ingresoVinculadoId, parseMontoSeguro(gastoEliminado.getDescripcion()));
                } else {
                    limpiarCampos();
                    setGastoButtonsEnabled(true);
                }
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
        };

        if (gastoEnEdicionRecurrente) {
            DataRepository.removeGastoRecurrenteById(gastoEnEdicionId, callback);
        } else {
            DataRepository.removeGastoById(gastoEnEdicionId, callback);
        }
    }

    private void reembolsarMontoAlIngreso(@NonNull Integer ingresoId, @NonNull BigDecimal montoReembolso) {
        if (montoReembolso.compareTo(BigDecimal.ZERO) <= 0) {
            limpiarCampos();
            setGastoButtonsEnabled(true);
            return;
        }
        Ingreso ingreso = DataRepository.getIngresoById(ingresoId);
        if (ingreso == null) {
            for (Ingreso historial : DataRepository.getIngresosHistorial()) {
                if (ingresoId.equals(historial.getId())) {
                    ingreso = historial;
                    break;
                }
            }
        }
        if (ingreso == null) {
            cargarIngresos();
            limpiarCampos();
            setGastoButtonsEnabled(true);
            return;
        }

        BigDecimal montoActual = parseMontoSeguro(ingreso.getDescripcion());
        BigDecimal nuevoMonto = montoActual.add(montoReembolso);
        DataRepository.updateIngresoMonto(requireContext(), ingreso, nuevoMonto,
                new DataRepository.RepositoryCallback<Ingreso>() {
                    @Override
                    public void onSuccess(Ingreso result) {
                        if (!isAdded()) {
                            return;
                        }
                        cargarIngresos();
                        limpiarCampos();
                        setGastoButtonsEnabled(true);
                    }

                    @Override
                    public void onError(String message) {
                        if (!isAdded()) {
                            return;
                        }
                        cargarIngresos();
                        limpiarCampos();
                        setGastoButtonsEnabled(true);
                        mostrarMensajeError(message);
                    }
                });
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
        ingresoView.setThreshold(0);
        ingresoView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < ingresosDisponibles.size()) {
                ingresoSeleccionado = ingresosDisponibles.get(position);
                binding.tilIngresoSeleccionGasto.setError(null);
                actualizarIngresoDisponible();
            }
        });
        ingresoView.setOnClickListener(v -> ingresoView.showDropDown());
        ingresoView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                ingresoView.showDropDown();
            }
        });
    }

    private void cargarIngresos() {
        DataRepository.refreshIngresos(requireContext(), new DataRepository.RepositoryCallback<List<Ingreso>>() {
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

    private void mostrarIngresoAgotado(Ingreso ingreso) {
        if (ingreso == null) {
            return;
        }
        BigDecimal disponible = parseMontoSeguro(ingreso.getDescripcion());
        if (disponible.compareTo(BigDecimal.ZERO) <= 0) {
            Toast.makeText(requireContext(), R.string.mensaje_ingreso_agotado, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
