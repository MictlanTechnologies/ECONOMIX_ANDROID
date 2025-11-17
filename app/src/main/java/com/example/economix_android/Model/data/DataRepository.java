package com.example.economix_android.Model.data;

import com.example.economix_android.network.dto.GastoDto;
import com.example.economix_android.network.dto.IngresoDto;
import com.example.economix_android.network.repository.GastoRepository;
import com.example.economix_android.network.repository.IngresoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class DataRepository {

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    private static final List<Ingreso> ingresos = new ArrayList<>();
    private static final List<Ingreso> ingresosRecurrentes = new ArrayList<>();
    private static final List<Gasto> gastos = new ArrayList<>();
    private static final List<Gasto> gastosRecurrentes = new ArrayList<>();

    private static final IngresoRepository ingresoRepository = new IngresoRepository();
    private static final GastoRepository gastoRepository = new GastoRepository();

    private static final int DEFAULT_USER_ID = 1;
    private static final int DEFAULT_FUENTE_ID = 1;
    private static final int DEFAULT_CATEGORIA_ID = 1;
    private static final int DEFAULT_PRESUPUESTO_ID = 1;

    private static final DateTimeFormatter UI_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

    private DataRepository() {
        // No instances
    }

    public static List<Ingreso> getIngresos() {
        return Collections.unmodifiableList(ingresos);
    }

    public static List<Ingreso> getIngresosRecurrentes() {
        return Collections.unmodifiableList(ingresosRecurrentes);
    }

    public static List<Gasto> getGastos() {
        return Collections.unmodifiableList(gastos);
    }

    public static List<Gasto> getGastosRecurrentes() {
        return Collections.unmodifiableList(gastosRecurrentes);
    }

    public static void refreshIngresos(RepositoryCallback<List<Ingreso>> callback) {
        ingresoRepository.obtenerIngresos(new Callback<List<IngresoDto>>() {
            @Override
            public void onResponse(Call<List<IngresoDto>> call, Response<List<IngresoDto>> response) {
                if (response.isSuccessful()) {
                    List<IngresoDto> body = response.body();
                    List<Ingreso> nuevos = new ArrayList<>();
                    if (body != null) {
                        for (IngresoDto dto : body) {
                            Ingreso ingreso = fromDto(dto);
                            if (ingreso != null) {
                                nuevos.add(ingreso);
                            }
                        }
                    }
                    replaceIngresos(nuevos);
                    notifySuccess(callback, getIngresos());
                } else {
                    notifyError(callback, "No se pudo obtener los ingresos. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<IngresoDto>> call, Throwable t) {
                notifyError(callback, "No se pudo conectar con el servidor de ingresos.");
            }
        });
    }

    public static void refreshGastos(RepositoryCallback<List<Gasto>> callback) {
        gastoRepository.obtenerGastos(new Callback<List<GastoDto>>() {
            @Override
            public void onResponse(Call<List<GastoDto>> call, Response<List<GastoDto>> response) {
                if (response.isSuccessful()) {
                    List<GastoDto> body = response.body();
                    List<Gasto> nuevos = new ArrayList<>();
                    if (body != null) {
                        for (GastoDto dto : body) {
                            Gasto gasto = fromDto(dto);
                            if (gasto != null) {
                                nuevos.add(gasto);
                            }
                        }
                    }
                    replaceGastos(nuevos);
                    notifySuccess(callback, getGastos());
                } else {
                    notifyError(callback, "No se pudo obtener los gastos. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GastoDto>> call, Throwable t) {
                notifyError(callback, "No se pudo conectar con el servidor de gastos.");
            }
        });
    }

    public static void addIngreso(Ingreso ingreso, RepositoryCallback<Ingreso> callback) {
        if (ingreso == null) {
            notifyError(callback, "El ingreso es inválido.");
            return;
        }
        ingresoRepository.guardarIngreso(toDto(ingreso), new Callback<IngresoDto>() {
            @Override
            public void onResponse(Call<IngresoDto> call, Response<IngresoDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Ingreso creado = fromDto(response.body());
                    if (creado != null) {
                        ingresos.add(creado);
                        if (creado.isRecurrente()) {
                            ingresosRecurrentes.add(creado);
                        }
                    }
                    notifySuccess(callback, creado);
                } else {
                    notifyError(callback, "No se pudo guardar el ingreso. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<IngresoDto> call, Throwable t) {
                notifyError(callback, "Error al conectar con el servidor de ingresos.");
            }
        });
    }

    public static void addGasto(Gasto gasto, RepositoryCallback<Gasto> callback) {
        if (gasto == null) {
            notifyError(callback, "El gasto es inválido.");
            return;
        }
        gastoRepository.guardarGasto(toDto(gasto), new Callback<GastoDto>() {
            @Override
            public void onResponse(Call<GastoDto> call, Response<GastoDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Gasto creado = fromDto(response.body());
                    if (creado != null) {
                        gastos.add(creado);
                        if (creado.isRecurrente()) {
                            gastosRecurrentes.add(creado);
                        }
                    }
                    notifySuccess(callback, creado);
                } else {
                    notifyError(callback, "No se pudo guardar el gasto. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GastoDto> call, Throwable t) {
                notifyError(callback, "Error al conectar con el servidor de gastos.");
            }
        });
    }

    public static void removeLastIngreso(RepositoryCallback<Boolean> callback) {
        if (ingresos.isEmpty()) {
            notifySuccess(callback, false);
            return;
        }
        Ingreso ultimo = ingresos.get(ingresos.size() - 1);
        if (ultimo.getId() == null) {
            notifyError(callback, "El último ingreso no tiene identificador remoto.");
            return;
        }
        ingresoRepository.eliminarIngreso(ultimo.getId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    eliminarIngresoPorId(ultimo.getId());
                    notifySuccess(callback, true);
                } else {
                    notifyError(callback, "No se pudo eliminar el ingreso. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                notifyError(callback, "Error al eliminar el ingreso en el servidor.");
            }
        });
    }

    public static void removeLastGasto(RepositoryCallback<Boolean> callback) {
        if (gastos.isEmpty()) {
            notifySuccess(callback, false);
            return;
        }
        Gasto ultimo = gastos.get(gastos.size() - 1);
        if (ultimo.getId() == null) {
            notifyError(callback, "El último gasto no tiene identificador remoto.");
            return;
        }
        gastoRepository.eliminarGasto(ultimo.getId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    eliminarGastoPorId(ultimo.getId());
                    notifySuccess(callback, true);
                } else {
                    notifyError(callback, "No se pudo eliminar el gasto. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                notifyError(callback, "Error al eliminar el gasto en el servidor.");
            }
        });
    }

    private static void replaceIngresos(List<Ingreso> nuevos) {
        ingresos.clear();
        ingresos.addAll(nuevos);
        ingresosRecurrentes.clear();
        for (Ingreso ingreso : ingresos) {
            if (ingreso.isRecurrente()) {
                ingresosRecurrentes.add(ingreso);
            }
        }
    }

    private static void replaceGastos(List<Gasto> nuevos) {
        gastos.clear();
        gastos.addAll(nuevos);
        gastosRecurrentes.clear();
        for (Gasto gasto : gastos) {
            if (gasto.isRecurrente()) {
                gastosRecurrentes.add(gasto);
            }
        }
    }

    private static void eliminarIngresoPorId(Integer id) {
        ingresos.removeIf(ingreso -> Objects.equals(ingreso.getId(), id));
        ingresosRecurrentes.removeIf(ingreso -> Objects.equals(ingreso.getId(), id));
    }

    private static void eliminarGastoPorId(Integer id) {
        gastos.removeIf(gasto -> Objects.equals(gasto.getId(), id));
        gastosRecurrentes.removeIf(gasto -> Objects.equals(gasto.getId(), id));
    }

    private static Ingreso fromDto(IngresoDto dto) {
        if (dto == null) {
            return null;
        }
        String articulo = dto.getDescripcionIngreso();
        String monto = montoToString(dto.getMontoIngreso());
        String fecha = formatDate(dto.getFechaIngresos());
        String periodo = dto.getPeriodicidadIngreso();
        boolean recurrente = esRecurrente(periodo);
        return new Ingreso(dto.getIdIngresos(), articulo, monto, fecha, periodo, recurrente);
    }

    private static Gasto fromDto(GastoDto dto) {
        if (dto == null) {
            return null;
        }
        String articulo = dto.getArticuloGasto() != null ? dto.getArticuloGasto() : dto.getDescripcionGasto();
        String monto = montoToString(dto.getMontoGasto());
        String fecha = formatDate(dto.getFechaGastos());
        String periodo = dto.getPeriodoGastos();
        boolean recurrente = esRecurrente(periodo);
        return new Gasto(dto.getIdGastos(), articulo, monto, fecha, periodo, recurrente);
    }

    private static IngresoDto toDto(Ingreso ingreso) {
        return IngresoDto.builder()
                .idIngresos(ingreso.getId())
                .idUsuario(DEFAULT_USER_ID)
                .idFuente(DEFAULT_FUENTE_ID)
                .montoIngreso(parseMonto(ingreso.getDescripcion()))
                .periodicidadIngreso(ingreso.getPeriodo())
                .fechaIngresos(parseDate(ingreso.getFecha()))
                .descripcionIngreso(ingreso.getArticulo())
                .build();
    }

    private static GastoDto toDto(Gasto gasto) {
        return GastoDto.builder()
                .idGastos(gasto.getId())
                .idUsuario(DEFAULT_USER_ID)
                .idCategoria(DEFAULT_CATEGORIA_ID)
                .idPresupuesto(DEFAULT_PRESUPUESTO_ID)
                .descripcionGasto(gasto.getArticulo())
                .articuloGasto(gasto.getArticulo())
                .montoGasto(parseMonto(gasto.getDescripcion()))
                .fechaGastos(parseDate(gasto.getFecha()))
                .periodoGastos(gasto.getPeriodo())
                .build();
    }

    private static boolean esRecurrente(String periodo) {
        return periodo != null && !periodo.trim().isEmpty();
    }

    private static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(UI_DATE_FORMAT);
    }

    private static LocalDate parseDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(date, UI_DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            try {
                return LocalDate.parse(date);
            } catch (DateTimeParseException ignored) {
                return LocalDate.now();
            }
        }
    }

    private static BigDecimal parseMonto(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        String normalizado = RegistroFinanciero.normalizarMonto(valor);
        try {
            return new BigDecimal(normalizado);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private static String montoToString(BigDecimal monto) {
        if (monto == null) {
            return "0";
        }
        BigDecimal normalizado = monto.stripTrailingZeros();
        return normalizado.toPlainString();
    }

    private static <T> void notifySuccess(RepositoryCallback<T> callback, T data) {
        if (callback != null) {
            callback.onSuccess(data);
        }
    }

    private static void notifyError(RepositoryCallback<?> callback, String message) {
        if (callback != null) {
            callback.onError(message);
        }
    }
}
