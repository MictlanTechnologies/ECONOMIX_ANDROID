package com.example.economix_android.Model.ahorro;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.economix_android.R;
import com.example.economix_android.Model.ahorro.AhorroItem;
import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Ingreso;
import com.example.economix_android.Model.data.RegistroFinanciero;
import com.example.economix_android.databinding.FragmentAhorroBinding;
import com.example.economix_android.network.dto.AhorroDto;
import com.example.economix_android.network.repository.AhorroRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.math.RoundingMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ahorroFragment extends Fragment {

    private static final String PREFS_AHORRO = "ahorro_prefs";
    private static final String KEY_META_NOMBRE = "meta_nombre";
    private static final String KEY_META_PRECIO = "meta_precio";

    private FragmentAhorroBinding binding;
    private final AhorroRepository ahorroRepository = new AhorroRepository();
    private AhorroAdapter ahorroAdapter;
    private ArrayAdapter<String> ingresosAdapter;
    private final List<Ingreso> ingresosDisponibles = new ArrayList<>();
    private Ingreso ingresoSeleccionado;
    private BigDecimal metaPrecio = BigDecimal.ZERO;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());

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
        binding.btnEliminar.setOnClickListener(v -> eliminarUltimoAhorro());
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

        configurarLista();
        configurarIngresos();
        cargarMetaGuardada();
        cargarIngresos();
        cargarAhorros();
    }

    private void configurarLista() {
        ahorroAdapter = new AhorroAdapter();
        binding.listaAhorros.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listaAhorros.setAdapter(ahorroAdapter);
    }

    private void configurarIngresos() {
        AutoCompleteTextView ingresoView = (AutoCompleteTextView) binding.etIngresoSeleccion;
        ingresosAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        ingresoView.setAdapter(ingresosAdapter);
        ingresoView.setThreshold(0);
        ingresoView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < ingresosDisponibles.size()) {
                ingresoSeleccionado = ingresosDisponibles.get(position);
                binding.tilIngresoSeleccion.setError(null);
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
            ((AutoCompleteTextView) binding.etIngresoSeleccion).setText("", false);
            actualizarIngresoDisponible();
            return;
        }
        if (ingresoSeleccionado != null) {
            Integer idSeleccionado = ingresoSeleccionado.getId();
            ingresoSeleccionado = null;
            for (int i = 0; i < ingresosDisponibles.size(); i++) {
                if (idSeleccionado != null && idSeleccionado.equals(ingresosDisponibles.get(i).getId())) {
                    ingresoSeleccionado = ingresosDisponibles.get(i);
                    ((AutoCompleteTextView) binding.etIngresoSeleccion)
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
        binding.tvIngresoDisponible.setText(getString(R.string.label_disponible_valor, disponible));
    }

    private void cargarAhorros() {
        ahorroRepository.obtenerAhorros(new Callback<List<AhorroDto>>() {
            @Override
            public void onResponse(Call<List<AhorroDto>> call, Response<List<AhorroDto>> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful()) {
                    List<AhorroDto> body = response.body();
                    List<AhorroItem> items = new ArrayList<>();
                    if (body != null) {
                        for (AhorroDto dto : body) {
                            AhorroItem item = convertir(dto);
                            if (item != null) {
                                items.add(item);
                            }
                        }
                    }
                    ahorroAdapter.update(items);
                    binding.tvAhorrosVacio.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                    actualizarProgreso(items);
                } else {
                    mostrarMensajeError(null);
                }
            }

            @Override
            public void onFailure(Call<List<AhorroDto>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensajeError(null);
            }
        });
    }

    private void guardarAhorro() {
        limpiarErrores();
        String meta = obtenerTexto(binding.etMetaAhorro);
        String precioTexto = obtenerTexto(binding.etPrecioMeta);
        String aporteTexto = obtenerTexto(binding.etAporteAhorro);

        boolean hayError = false;

        if (TextUtils.isEmpty(meta)) {
            binding.tilMetaAhorro.setError(getString(R.string.error_meta_obligatoria));
            hayError = true;
        }

        if (!RegistroFinanciero.esMontoValido(precioTexto)) {
            binding.tilPrecioMeta.setError(getString(R.string.error_precio_objetivo_obligatorio));
            hayError = true;
        }

        if (ingresoSeleccionado == null) {
            binding.tilIngresoSeleccion.setError(getString(R.string.error_ingreso_obligatorio));
            hayError = true;
        }

        if (!RegistroFinanciero.esMontoValido(aporteTexto)) {
            binding.tilAporteAhorro.setError(getString(R.string.error_aporte_obligatorio));
            hayError = true;
        }

        BigDecimal precio = parseMontoSeguro(precioTexto);
        BigDecimal aporte = parseMontoSeguro(aporteTexto);
        BigDecimal disponible = ingresoSeleccionado != null
                ? parseMontoSeguro(ingresoSeleccionado.getDescripcion())
                : BigDecimal.ZERO;

        if (aporte.compareTo(disponible) > 0) {
            binding.tilAporteAhorro.setError(getString(R.string.error_aporte_excede_ingreso));
            hayError = true;
        }

        if (hayError) {
            return;
        }

        guardarMeta(meta, precio);
        setButtonsEnabled(false);
        BigDecimal nuevoMontoIngreso = disponible.subtract(aporte);
        BigDecimal montoOriginal = disponible;
        DataRepository.updateIngresoMonto(requireContext(), ingresoSeleccionado, nuevoMontoIngreso,
                new DataRepository.RepositoryCallback<Ingreso>() {
            @Override
            public void onSuccess(Ingreso result) {
                if (!isAdded()) {
                    return;
                }
                crearAhorro(meta, aporte, result, montoOriginal);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                setButtonsEnabled(true);
                mostrarMensajeError(message);
            }
        });
    }

    private void crearAhorro(String meta, BigDecimal aporte, Ingreso ingresoActualizado, BigDecimal montoOriginal) {
        AhorroDto dto = AhorroDto.builder()
                .montoAhorro(aporte)
                .periodoTAhorro(meta)
                .idIngresos(ingresoActualizado != null ? ingresoActualizado.getId() : null)
                .build();

        ahorroRepository.crearAhorro(dto, new Callback<AhorroDto>() {
            @Override
            public void onResponse(Call<AhorroDto> call, Response<AhorroDto> response) {
                if (!isAdded()) {
                    return;
                }
                setButtonsEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), R.string.mensaje_ahorro_guardado, Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    cargarIngresos();
                    cargarAhorros();
                } else {
                    revertirIngreso(ingresoActualizado, montoOriginal);
                    mostrarMensajeError(null);
                }
            }

            @Override
            public void onFailure(Call<AhorroDto> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                setButtonsEnabled(true);
                revertirIngreso(ingresoActualizado, montoOriginal);
                mostrarMensajeError(null);
            }
        });
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

    private void eliminarUltimoAhorro() {
        AhorroItem ultimo = ahorroAdapter.getLast();
        if (ultimo == null || ultimo.getIdAhorro() == null) {
            Toast.makeText(requireContext(), R.string.error_sin_ahorros, Toast.LENGTH_SHORT).show();
            return;
        }
        setButtonsEnabled(false);
        ahorroRepository.eliminarAhorro(ultimo.getIdAhorro(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) {
                    return;
                }
                setButtonsEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), R.string.mensaje_ahorro_eliminado, Toast.LENGTH_SHORT).show();
                    cargarAhorros();
                } else {
                    mostrarMensajeError(null);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                setButtonsEnabled(true);
                mostrarMensajeError(null);
            }
        });
    }

    private void limpiarCampos() {
        binding.etAporteAhorro.setText("");
        binding.etIngresoSeleccion.setText("");
        ingresoSeleccionado = null;
        actualizarIngresoDisponible();
    }

    private void limpiarErrores() {
        binding.tilMetaAhorro.setError(null);
        binding.tilPrecioMeta.setError(null);
        binding.tilIngresoSeleccion.setError(null);
        binding.tilAporteAhorro.setError(null);
    }

    private void setButtonsEnabled(boolean enabled) {
        binding.btnGuardar.setEnabled(enabled);
        binding.btnEliminar.setEnabled(enabled);
        binding.btnLimpiar.setEnabled(enabled);
    }

    private String obtenerTexto(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void cargarMetaGuardada() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_AHORRO, Context.MODE_PRIVATE);
        String meta = prefs.getString(KEY_META_NOMBRE, "");
        String precio = prefs.getString(KEY_META_PRECIO, "");
        if (!TextUtils.isEmpty(meta)) {
            binding.etMetaAhorro.setText(meta);
        }
        if (!TextUtils.isEmpty(precio)) {
            binding.etPrecioMeta.setText(precio);
            metaPrecio = parseMontoSeguro(precio);
        }
        actualizarTextoProgreso(BigDecimal.ZERO, metaPrecio, 0);
    }

    private void guardarMeta(String meta, BigDecimal precio) {
        metaPrecio = precio;
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_AHORRO, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_META_NOMBRE, meta)
                .putString(KEY_META_PRECIO, precio.stripTrailingZeros().toPlainString())
                .apply();
    }

    private AhorroItem convertir(AhorroDto dto) {
        if (dto == null) {
            return null;
        }
        String monto = dto.getMontoAhorro() != null ? dto.getMontoAhorro().stripTrailingZeros().toPlainString() : "0";
        String periodo = dto.getPeriodoTAhorro() != null ? dto.getPeriodoTAhorro() : getString(R.string.label_periodo_sin_definir);
        String fecha = formatearFecha(dto.getFechaAhorro());
        return new AhorroItem(dto.getIdAhorro(), monto, periodo, fecha, dto.getIdIngresos());
    }

    private String formatearFecha(LocalDate fecha) {
        if (fecha == null) {
            return getString(R.string.label_fecha_desconocida);
        }
        try {
            return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()));
        } catch (Exception ex) {
            try {
                return LocalDate.parse(fecha.toString(), dateFormatter).format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()));
            } catch (DateTimeParseException ignored) {
                return fecha.toString();
            }
        }
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

    private void actualizarProgreso(List<AhorroItem> items) {
        String meta = obtenerTexto(binding.etMetaAhorro);
        BigDecimal total = BigDecimal.ZERO;
        for (AhorroItem item : items) {
            if (TextUtils.isEmpty(meta) || meta.equalsIgnoreCase(item.getPeriodo())) {
                total = total.add(parseMontoSeguro(item.getMonto()));
            }
        }
        BigDecimal objetivo = metaPrecio;
        if (objetivo.compareTo(BigDecimal.ZERO) <= 0) {
            objetivo = parseMontoSeguro(obtenerTexto(binding.etPrecioMeta));
        }
        int progress = 0;
        if (objetivo.compareTo(BigDecimal.ZERO) > 0) {
            progress = total.multiply(BigDecimal.valueOf(100))
                    .divide(objetivo, 0, RoundingMode.HALF_UP)
                    .intValue();
            progress = Math.min(progress, 100);
        }
        actualizarTextoProgreso(total, objetivo, progress);
        binding.progresoAhorro.setProgress(progress);
    }

    private void actualizarTextoProgreso(BigDecimal total, BigDecimal objetivo, int porcentaje) {
        String totalTexto = total.stripTrailingZeros().toPlainString();
        String objetivoTexto = objetivo.stripTrailingZeros().toPlainString();
        binding.tvProgresoAhorro.setText(getString(R.string.label_progreso_ahorro, totalTexto, objetivoTexto, porcentaje));
    }

    private void navigateSafely(View view, int destinationId) {
        NavController navController = Navigation.findNavController(view);
        NavDestination currentDestination = navController.getCurrentDestination();
        if (currentDestination == null || currentDestination.getId() != destinationId) {
            navController.navigate(destinationId);
        }
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_ahorro)
                .setMessage(R.string.mensaje_ayuda_ahorro)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void mostrarMensajeError(String message) {
        String texto = message != null ? message : getString(R.string.mensaje_error_servidor);
        Toast.makeText(requireContext(), texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
