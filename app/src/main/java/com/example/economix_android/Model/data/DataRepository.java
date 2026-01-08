package com.example.economix_android.Model.data;

import android.content.Context;

import com.example.economix_android.auth.SessionManager;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
    private static final Set<Integer> ingresosRecurrentesIds = new HashSet<>();
    private static final Set<Integer> gastosRecurrentesIds = new HashSet<>();
    private static final Map<Integer, BigDecimal> ingresosOriginales = new HashMap<>();

    private static final IngresoRepository ingresoRepository = new IngresoRepository();
    private static final GastoRepository gastoRepository = new GastoRepository();

    private static final DateTimeFormatter UI_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

    private DataRepository() {
        // No instances
    }

    public static List<Ingreso> getIngresos() {
        return Collections.unmodifiableList(ingresos);
    }

    public static Ingreso getIngresoById(Integer id) {
        if (id == null) {
            return null;
        }
        for (Ingreso ingreso : ingresos) {
            if (Objects.equals(ingreso.getId(), id)) {
                return ingreso;
            }
        }
        return null;
    }

    public static List<Ingreso> getIngresosHistorial() {
        List<Ingreso> historial = new ArrayList<>();
        for (Ingreso ingreso : ingresos) {
            String monto = ingreso.getDescripcion();
            if (ingreso.getId() != null) {
                BigDecimal original = ingresosOriginales.get(ingreso.getId());
                if (original != null) {
                    monto = original.stripTrailingZeros().toPlainString();
                }
            }
            historial.add(new Ingreso(ingreso.getId(),
                    ingreso.getArticulo(),
                    monto,
                    ingreso.getFecha(),
                    ingreso.getPeriodo(),
                    ingreso.isRecurrente()));
        }
        return Collections.unmodifiableList(historial);
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
                    registrarIngresosOriginales(nuevos);
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

    public static void addIngreso(Context context, Ingreso ingreso, RepositoryCallback<Ingreso> callback) {
        if (ingreso == null) {
            notifyError(callback, "El ingreso es inválido.");
            return;
        }
        IngresoDto dto = toDto(ingreso, context);
        if (dto == null) {
            notifyError(callback, "Debes iniciar sesión antes de crear ingresos.");
            return;
        }
        ingresoRepository.guardarIngreso(dto, new Callback<IngresoDto>() {
            @Override
            public void onResponse(Call<IngresoDto> call, Response<IngresoDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean recurrente = ingreso.isRecurrente();
                    Ingreso creado = fromDto(response.body());
                    if (creado != null) {
                        creado = marcarIngresoRecurrente(creado, recurrente);
                        ingresos.add(creado);
                        actualizarIngresoRecurrenteIds(creado, recurrente);
                        if (creado.isRecurrente()) {
                            ingresosRecurrentes.add(creado);
                        }
                        registrarIngresoOriginal(creado);
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

    public static void addGasto(Context context, Gasto gasto, RepositoryCallback<Gasto> callback) {
        if (gasto == null) {
            notifyError(callback, "El gasto es inválido.");
            return;
        }
        GastoDto dto = toDto(gasto, context);
        if (dto == null) {
            notifyError(callback, "Debes iniciar sesión antes de crear gastos.");
            return;
        }
        gastoRepository.guardarGasto(dto, new Callback<GastoDto>() {
            @Override
            public void onResponse(Call<GastoDto> call, Response<GastoDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean recurrente = gasto.isRecurrente();
                    Gasto creado = fromDto(response.body());
                    if (creado != null) {
                        creado = marcarGastoRecurrente(creado, recurrente);
                        gastos.add(creado);
                        actualizarGastoRecurrenteIds(creado, recurrente);
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
                    actualizarIngresoRecurrenteIds(ultimo, false);
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

    public static void removeIngresoById(Integer id, RepositoryCallback<Boolean> callback) {
        if (id == null) {
            notifyError(callback, "El ingreso no tiene identificador remoto.");
            return;
        }
        ingresoRepository.eliminarIngreso(id, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    actualizarIngresoRecurrenteIds(getIngresoById(id), false);
                    eliminarIngresoPorId(id);
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
                    actualizarGastoRecurrenteIds(ultimo, false);
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

    public static void removeGastoById(Integer id, RepositoryCallback<Boolean> callback) {
        if (id == null) {
            notifyError(callback, "El gasto no tiene identificador remoto.");
            return;
        }
        gastoRepository.eliminarGasto(id, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    actualizarGastoRecurrenteIds(getGastoById(id), false);
                    eliminarGastoPorId(id);
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

    public static void updateIngreso(Context context, Ingreso ingreso, RepositoryCallback<Ingreso> callback) {
        if (ingreso == null || ingreso.getId() == null) {
            notifyError(callback, "El ingreso no es válido para actualizar.");
            return;
        }
        IngresoDto dto = toDto(ingreso, context);
        if (dto == null) {
            notifyError(callback, "Debes iniciar sesión antes de actualizar ingresos.");
            return;
        }
        ingresoRepository.actualizarIngreso(ingreso.getId(), dto, new Callback<IngresoDto>() {
            @Override
            public void onResponse(Call<IngresoDto> call, Response<IngresoDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Ingreso actualizado = fromDto(response.body());
                    if (actualizado != null) {
                        actualizado = marcarIngresoRecurrente(actualizado, ingreso.isRecurrente());
                        actualizarIngresoRecurrenteIds(actualizado, ingreso.isRecurrente());
                        reemplazarIngreso(actualizado);
                        registrarIngresoOriginal(actualizado);
                    }
                    notifySuccess(callback, actualizado);
                } else {
                    notifyError(callback, "No se pudo actualizar el ingreso. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<IngresoDto> call, Throwable t) {
                notifyError(callback, "Error al actualizar el ingreso en el servidor.");
            }
        });
    }

    public static void updateGasto(Context context, Gasto gasto, RepositoryCallback<Gasto> callback) {
        if (gasto == null || gasto.getId() == null) {
            notifyError(callback, "El gasto no es válido para actualizar.");
            return;
        }
        GastoDto dto = toDto(gasto, context);
        if (dto == null) {
            notifyError(callback, "Debes iniciar sesión antes de actualizar gastos.");
            return;
        }
        gastoRepository.actualizarGasto(gasto.getId(), dto, new Callback<GastoDto>() {
            @Override
            public void onResponse(Call<GastoDto> call, Response<GastoDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Gasto actualizado = fromDto(response.body());
                    if (actualizado != null) {
                        actualizado = marcarGastoRecurrente(actualizado, gasto.isRecurrente());
                        actualizarGastoRecurrenteIds(actualizado, gasto.isRecurrente());
                        reemplazarGasto(actualizado);
                    }
                    notifySuccess(callback, actualizado);
                } else {
                    notifyError(callback, "No se pudo actualizar el gasto. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GastoDto> call, Throwable t) {
                notifyError(callback, "Error al actualizar el gasto en el servidor.");
            }
        });
    }

    public static void updateIngresoMonto(Context context, Ingreso ingreso, BigDecimal nuevoMonto,
                                          RepositoryCallback<Ingreso> callback) {
        if (ingreso == null || ingreso.getId() == null) {
            notifyError(callback, "El ingreso no es válido para actualizar.");
            return;
        }
        if (nuevoMonto == null) {
            notifyError(callback, "El monto actualizado es inválido.");
            return;
        }
        String montoNormalizado = nuevoMonto.stripTrailingZeros().toPlainString();
        Ingreso actualizado = new Ingreso(ingreso.getId(),
                ingreso.getArticulo(),
                montoNormalizado,
                ingreso.getFecha(),
                ingreso.getPeriodo(),
                ingreso.isRecurrente());
        IngresoDto dto = toDto(actualizado, context);
        if (dto == null) {
            notifyError(callback, "Debes iniciar sesión antes de actualizar ingresos.");
            return;
        }
        ingresoRepository.actualizarIngreso(ingreso.getId(), dto, new Callback<IngresoDto>() {
            @Override
            public void onResponse(Call<IngresoDto> call, Response<IngresoDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Ingreso actualizadoRemoto = fromDto(response.body());
                    if (actualizadoRemoto != null) {
                        reemplazarIngreso(actualizadoRemoto);
                    }
                    notifySuccess(callback, actualizadoRemoto);
                } else {
                    notifyError(callback, "No se pudo actualizar el ingreso. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<IngresoDto> call, Throwable t) {
                notifyError(callback, "Error al actualizar el ingreso en el servidor.");
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
        pruneIngresosOriginales(nuevos);
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
        ingresosOriginales.remove(id);
    }

    private static void reemplazarIngreso(Ingreso actualizado) {
        if (actualizado == null) {
            return;
        }
        for (int i = 0; i < ingresos.size(); i++) {
            if (Objects.equals(ingresos.get(i).getId(), actualizado.getId())) {
                ingresos.set(i, actualizado);
                break;
            }
        }
        ingresosRecurrentes.removeIf(ingreso -> Objects.equals(ingreso.getId(), actualizado.getId()));
        if (actualizado.isRecurrente()) {
            ingresosRecurrentes.add(actualizado);
        }
    }

    private static void reemplazarGasto(Gasto actualizado) {
        if (actualizado == null) {
            return;
        }
        for (int i = 0; i < gastos.size(); i++) {
            if (Objects.equals(gastos.get(i).getId(), actualizado.getId())) {
                gastos.set(i, actualizado);
                break;
            }
        }
        gastosRecurrentes.removeIf(gasto -> Objects.equals(gasto.getId(), actualizado.getId()));
        if (actualizado.isRecurrente()) {
            gastosRecurrentes.add(actualizado);
        }
    }

    private static void eliminarGastoPorId(Integer id) {
        gastos.removeIf(gasto -> Objects.equals(gasto.getId(), id));
        gastosRecurrentes.removeIf(gasto -> Objects.equals(gasto.getId(), id));
    }

    private static Gasto getGastoById(Integer id) {
        if (id == null) {
            return null;
        }
        for (Gasto gasto : gastos) {
            if (Objects.equals(gasto.getId(), id)) {
                return gasto;
            }
        }
        return null;
    }

    private static Ingreso fromDto(IngresoDto dto) {
        if (dto == null) {
            return null;
        }
        String articulo = dto.getDescripcionIngreso();
        String monto = montoToString(dto.getMontoIngreso());
        String fecha = formatDate(dto.getFechaIngresos());
        String periodo = dto.getPeriodicidadIngreso();
        boolean recurrente = dto.getIdIngresos() != null && ingresosRecurrentesIds.contains(dto.getIdIngresos());
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
        boolean recurrente = dto.getIdGastos() != null && gastosRecurrentesIds.contains(dto.getIdGastos());
        return new Gasto(dto.getIdGastos(), articulo, monto, fecha, periodo, recurrente);
    }

    private static IngresoDto toDto(Ingreso ingreso, Context context) {
        Integer userId = SessionManager.getUserId(context);
        if (userId == null) {
            return null;
        }
        return IngresoDto.builder()
                .idIngresos(ingreso.getId())
                .idUsuario(userId)
                .montoIngreso(parseMonto(ingreso.getDescripcion()))
                .periodicidadIngreso(ingreso.getPeriodo())
                .fechaIngresos(parseDate(ingreso.getFecha()))
                .descripcionIngreso(ingreso.getArticulo())
                .build();
    }

    private static GastoDto toDto(Gasto gasto, Context context) {
        Integer userId = SessionManager.getUserId(context);
        if (userId == null) {
            return null;
        }
        return GastoDto.builder()
                .idGastos(gasto.getId())
                .idUsuario(userId)
                .descripcionGasto(gasto.getArticulo())
                .articuloGasto(gasto.getArticulo())
                .montoGasto(parseMonto(gasto.getDescripcion()))
                .fechaGastos(parseDate(gasto.getFecha()))
                .periodoGastos(gasto.getPeriodo())
                .build();
    }

    private static Ingreso marcarIngresoRecurrente(Ingreso ingreso, boolean recurrente) {
        if (ingreso == null) {
            return null;
        }
        return new Ingreso(ingreso.getId(), ingreso.getArticulo(), ingreso.getDescripcion(),
                ingreso.getFecha(), ingreso.getPeriodo(), recurrente);
    }

    private static Gasto marcarGastoRecurrente(Gasto gasto, boolean recurrente) {
        if (gasto == null) {
            return null;
        }
        return new Gasto(gasto.getId(), gasto.getArticulo(), gasto.getDescripcion(),
                gasto.getFecha(), gasto.getPeriodo(), recurrente);
    }

    private static void actualizarIngresoRecurrenteIds(Ingreso ingreso, boolean recurrente) {
        if (ingreso == null || ingreso.getId() == null) {
            return;
        }
        if (recurrente) {
            ingresosRecurrentesIds.add(ingreso.getId());
        } else {
            ingresosRecurrentesIds.remove(ingreso.getId());
        }
    }

    private static void registrarIngresosOriginales(List<Ingreso> nuevos) {
        for (Ingreso ingreso : nuevos) {
            registrarIngresoOriginal(ingreso);
        }
    }

    private static void registrarIngresoOriginal(Ingreso ingreso) {
        if (ingreso == null || ingreso.getId() == null) {
            return;
        }
        BigDecimal monto = parseMonto(ingreso.getDescripcion());
        BigDecimal previo = ingresosOriginales.get(ingreso.getId());
        if (previo == null || monto.compareTo(previo) > 0) {
            ingresosOriginales.put(ingreso.getId(), monto);
        }
    }

    private static void pruneIngresosOriginales(List<Ingreso> nuevos) {
        Set<Integer> ids = new HashSet<>();
        for (Ingreso ingreso : nuevos) {
            if (ingreso.getId() != null) {
                ids.add(ingreso.getId());
            }
        }
        ingresosOriginales.keySet().retainAll(ids);
    }

    private static void actualizarGastoRecurrenteIds(Gasto gasto, boolean recurrente) {
        if (gasto == null || gasto.getId() == null) {
            return;
        }
        if (recurrente) {
            gastosRecurrentesIds.add(gasto.getId());
        } else {
            gastosRecurrentesIds.remove(gasto.getId());
        }
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
