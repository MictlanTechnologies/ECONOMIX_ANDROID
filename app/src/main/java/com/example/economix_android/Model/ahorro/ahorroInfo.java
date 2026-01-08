package com.example.economix_android.Model.ahorro;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.economix_android.R;
import com.example.economix_android.Model.data.RegistroFinanciero;
import com.example.economix_android.databinding.FragmentAhorroInfoBinding;
import com.example.economix_android.network.dto.AhorroDto;
import com.example.economix_android.network.repository.AhorroRepository;
import com.example.economix_android.util.ProfileImageUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ahorroInfo extends Fragment {

    private FragmentAhorroInfoBinding binding;
    private final AhorroRepository ahorroRepository = new AhorroRepository();
    private AhorroInfoAdapter adapter;
    private final DateTimeFormatter uiDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAhorroInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPerfil.setOnClickListener(v -> navigateSafely(v, R.id.usuario));
        ProfileImageUtils.applyProfileImage(requireContext(), binding.btnPerfil);
        binding.btnAyudaAhorroInf.setOnClickListener(v -> mostrarAyuda());

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
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarAhorros();
    }

    private void configurarLista() {
        adapter = new AhorroInfoAdapter(new AhorroInfoAdapter.OnAhorroActionListener() {
            @Override
            public void onModificar(AhorroItem item) {
                mostrarDialogoModificar(item);
            }

            @Override
            public void onAgregar(AhorroItem item) {
                mostrarDialogoAgregar(item);
            }
        });
        binding.listaAhorros.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listaAhorros.setAdapter(adapter);
    }

    private void cargarAhorros() {
        ahorroRepository.obtenerAhorros(new Callback<List<AhorroDto>>() {
            @Override
            public void onResponse(Call<List<AhorroDto>> call, Response<List<AhorroDto>> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful()) {
                    List<AhorroItem> items = new ArrayList<>();
                    List<AhorroDto> body = response.body();
                    if (body != null) {
                        for (AhorroDto dto : body) {
                            AhorroItem item = convertir(dto);
                            if (item != null) {
                                items.add(item);
                            }
                        }
                    }
                    adapter.update(items);
                    binding.tvAhorrosVacio.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
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

    private void mostrarDialogoModificar(AhorroItem item) {
        if (item == null || item.getIdAhorro() == null) {
            mostrarMensajeError(getString(R.string.error_ahorro_id));
            return;
        }
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_editar_ahorro, null, false);
        TextInputEditText etTitulo = dialogView.findViewById(R.id.etTituloAhorro);
        TextInputEditText etMonto = dialogView.findViewById(R.id.etMontoAhorro);
        etTitulo.setText(item.getPeriodo());
        etMonto.setText(item.getMonto());

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_editar_ahorro)
                .setView(dialogView)
                .setPositiveButton(R.string.label_modificar, (dialog, which) -> {
                    String titulo = obtenerTexto(etTitulo);
                    String montoTexto = obtenerTexto(etMonto);
                    if (TextUtils.isEmpty(titulo) || !RegistroFinanciero.esMontoValido(montoTexto)) {
                        mostrarMensajeError(getString(R.string.error_campos_obligatorios_ahorro));
                        return;
                    }
                    BigDecimal monto = parseMontoSeguro(montoTexto);
                    actualizarAhorro(item, titulo, monto);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void mostrarDialogoAgregar(AhorroItem item) {
        if (item == null || item.getIdAhorro() == null) {
            mostrarMensajeError(getString(R.string.error_ahorro_id));
            return;
        }
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_agregar_ahorro, null, false);
        TextInputEditText etMonto = dialogView.findViewById(R.id.etMontoAgregar);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_agregar_ahorro)
                .setView(dialogView)
                .setPositiveButton(R.string.label_agregar, (dialog, which) -> {
                    String montoTexto = obtenerTexto(etMonto);
                    if (!RegistroFinanciero.esMontoValido(montoTexto)) {
                        mostrarMensajeError(getString(R.string.error_monto_ahorro_obligatorio));
                        return;
                    }
                    BigDecimal adicional = parseMontoSeguro(montoTexto);
                    BigDecimal actual = parseMontoSeguro(item.getMonto());
                    BigDecimal nuevoTotal = actual.add(adicional);
                    actualizarAhorro(item, item.getPeriodo(), nuevoTotal);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void actualizarAhorro(AhorroItem item, String titulo, BigDecimal monto) {
        AhorroDto dto = AhorroDto.builder()
                .idAhorro(item.getIdAhorro())
                .idIngresos(item.getIngresoId())
                .periodoTAhorro(titulo)
                .montoAhorro(monto)
                .fechaAhorro(parseFecha(item.getFecha()))
                .build();

        ahorroRepository.actualizarAhorro(item.getIdAhorro(), dto, new Callback<AhorroDto>() {
            @Override
            public void onResponse(Call<AhorroDto> call, Response<AhorroDto> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), R.string.mensaje_ahorro_actualizado, Toast.LENGTH_SHORT).show();
                    cargarAhorros();
                } else {
                    mostrarMensajeError(null);
                }
            }

            @Override
            public void onFailure(Call<AhorroDto> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensajeError(null);
            }
        });
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
            return fecha.format(uiDateFormatter);
        } catch (Exception ex) {
            try {
                return LocalDate.parse(fecha.toString(), uiDateFormatter).format(uiDateFormatter);
            } catch (DateTimeParseException ignored) {
                return fecha.toString();
            }
        }
    }

    private LocalDate parseFecha(String fechaTexto) {
        if (TextUtils.isEmpty(fechaTexto)) {
            return null;
        }
        try {
            return LocalDate.parse(fechaTexto, uiDateFormatter);
        } catch (DateTimeParseException ignored) {
            return null;
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

    private String obtenerTexto(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_ahorro_info)
                .setMessage(R.string.mensaje_ayuda_ahorro_info)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void mostrarMensajeError(String message) {
        String texto = message != null ? message : getString(R.string.mensaje_error_servidor);
        Toast.makeText(requireContext(), texto, Toast.LENGTH_SHORT).show();
    }

    private void navigateSafely(View view, int destinationId) {
        NavController navController = Navigation.findNavController(view);
        NavDestination currentDestination = navController.getCurrentDestination();
        if (currentDestination == null || currentDestination.getId() != destinationId) {
            navController.navigate(destinationId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
