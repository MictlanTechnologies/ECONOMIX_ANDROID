package com.example.economix_android.Model.data;

import android.content.Context;

import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.network.dto.ConceptoGastoDto;
import com.example.economix_android.network.dto.ConceptoIngresoDto;
import com.example.economix_android.network.dto.GastoDto;
import com.example.economix_android.network.dto.IngresoDto;
import com.example.economix_android.network.repository.ConceptoGastoRepository;
import com.example.economix_android.network.repository.ConceptoIngresoRepository;
import com.example.economix_android.network.repository.GastoRepository;
import com.example.economix_android.network.repository.IngresoRepository;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
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
    private static final Map<Integer, BigDecimal> ingresosOriginales = new HashMap<>();

    private static final IngresoRepository ingresoRepository = new IngresoRepository();
    private static final GastoRepository gastoRepository = new GastoRepository();
    private static final ConceptoIngresoRepository conceptoIngresoRepository = new ConceptoIngresoRepository();
    private static final ConceptoGastoRepository conceptoGastoRepository = new ConceptoGastoRepository();

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
        for (Ingreso ingreso : ingresosRecurrentes) {
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

    public static void refreshIngresos(Context context, RepositoryCallback<List<Ingreso>> callback) {
        Integer userId = SessionManager.getUserId(context);
        AtomicInteger pending = new AtomicInteger(2);
        List<String> errors = new ArrayList<>();
        ingresoRepository.obtenerIngresos(new Callback<List<IngresoDto>>() {
            @Override
            public void onResponse(Call<List<IngresoDto>> call, Response<List<IngresoDto>> response) {
                if (response.isSuccessful()) {
                    List<IngresoDto> body = response.body();
                    List<Ingreso> nuevos = new ArrayList<>();
                    if (body != null) {
                        for (IngresoDto dto : body) {
                            if (userId != null && !Objects.equals(dto.getIdUsuario(), userId)) {
                                continue;
                            }
                            Ingreso ingreso = fromDto(dto);
                            if (ingreso != null) {
                                nuevos.add(ingreso);
                            }
                        }
                    }
                    replaceIngresos(nuevos);
                    registrarIngresosOriginales(nuevos);
                } else {
                    errors.add("No se pudo obtener los ingresos. Código: " + response.code());
                }
                finalizarCargaIngresos(callback, errors, pending);
            }

            @Override
            public void onFailure(Call<List<IngresoDto>> call, Throwable t) {
                errors.add("No se pudo conectar con el servidor de ingresos.");
                finalizarCargaIngresos(callback, errors, pending);
            }
        });
        conceptoIngresoRepository.obtenerConceptos(new Callback<List<ConceptoIngresoDto>>() {
            @Override
            public void onResponse(Call<List<ConceptoIngresoDto>> call, Response<List<ConceptoIngresoDto>> response) {
                if (response.isSuccessful()) {
                    List<ConceptoIngresoDto> body = response.body();
                    List<Ingreso> nuevos = new ArrayList<>();
                    if (body != null) {
                        for (ConceptoIngresoDto dto : body) {
                            Ingreso ingreso = fromConceptoDto(dto);
                            if (ingreso != null) {
                                nuevos.add(ingreso);
                            }
                        }
                    }
                    replaceIngresosRecurrentes(nuevos);
                } else {
                    errors.add("No se pudo obtener los ingresos recurrentes. Código: " + response.code());
                }
                finalizarCargaIngresos(callback, errors, pending);
            }

            @Override
            public void onFailure(Call<List<ConceptoIngresoDto>> call, Throwable t) {
                errors.add("No se pudo conectar con el servidor de ingresos recurrentes.");
                finalizarCargaIngresos(callback, errors, pending);
            }
        });
    }

    public static void refreshGastos(Context context, RepositoryCallback<List<Gasto>> callback) {
        Integer userId = SessionManager.getUserId(context);
        AtomicInteger pending = new AtomicInteger(2);
        List<String> errors = new ArrayList<>();
        gastoRepository.obtenerGastos(new Callback<List<GastoDto>>() {
            @Override
            public void onResponse(Call<List<GastoDto>> call, Response<List<GastoDto>> response) {
                if (response.isSuccessful()) {
                    List<GastoDto> body = response.body();
                    List<Gasto> nuevos = new ArrayList<>();
                    if (body != null) {
                        for (GastoDto dto : body) {
                            if (userId != null && !Objects.equals(dto.getIdUsuario(), userId)) {
                                continue;
                            }
                            Gasto gasto = fromDto(dto);
                            if (gasto != null) {
                                nuevos.add(gasto);
                            }
                        }
                    }
                    replaceGastos(nuevos);
                } else {
                    errors.add("No se pudo obtener los gastos. Código: " + response.code());
                }
                finalizarCargaGastos(callback, errors, pending);
            }

            @Override
            public void onFailure(Call<List<GastoDto>> call, Throwable t) {
                errors.add("No se pudo conectar con el servidor de gastos.");
                finalizarCargaGastos(callback, errors, pending);
            }
        });
        conceptoGastoRepository.obtenerConceptos(new Callback<List<ConceptoGastoDto>>() {
            @Override
            public void onResponse(Call<List<ConceptoGastoDto>> call, Response<List<ConceptoGastoDto>> response) {
                if (response.isSuccessful()) {
                    List<ConceptoGastoDto> body = response.body();
                    List<Gasto> nuevos = new ArrayList<>();
                    if (body != null) {
                        for (ConceptoGastoDto dto : body) {
                            Gasto gasto = fromConceptoDto(dto);
                            if (gasto != null) {
                                nuevos.add(gasto);
                            }
                        }
                    }
                    replaceGastosRecurrentes(nuevos);
                } else {
                    errors.add("No se pudo obtener los gastos recurrentes. Código: " + response.code());
                }
                finalizarCargaGastos(callback, errors, pending);
            }

            @Override
            public void onFailure(Call<List<ConceptoGastoDto>> call, Throwable t) {
                errors.add("No se pudo conectar con el servidor de gastos recurrentes.");
                finalizarCargaGastos(callback, errors, pending);
            }
        });
    }

    public static void addIngreso(Context context, Ingreso ingreso, RepositoryCallback<Ingreso> callback) {
        if (ingreso == null) {
            notifyError(callback, "El ingreso es inválido.");
            return;
        }
        if (ingreso.isRecurrente()) {
            ConceptoIngresoDto conceptoDto = toConceptoDto(ingreso);
            conceptoIngresoRepository.guardarConcepto(conceptoDto, new Callback<ConceptoIngresoDto>() {
                @Override
                public void onResponse(Call<ConceptoIngresoDto> call, Response<ConceptoIngresoDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Ingreso creado = fromConceptoDto(response.body());
                        if (creado != null) {
                            ingresosRecurrentes.add(creado);
                        }
                        notifySuccess(callback, creado);
                    } else {
                        notifyError(callback, "No se pudo guardar el ingreso recurrente. Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ConceptoIngresoDto> call, Throwable t) {
                    notifyError(callback, "Error al conectar con el servidor de ingresos recurrentes.");
                }
            });
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
                    Ingreso creado = fromDto(response.body());
                    if (creado != null) {
                        ingresos.add(creado);
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
        if (gasto.isRecurrente()) {
            ConceptoGastoDto conceptoDto = toConceptoDto(gasto);
            conceptoGastoRepository.guardarConcepto(conceptoDto, new Callback<ConceptoGastoDto>() {
                @Override
                public void onResponse(Call<ConceptoGastoDto> call, Response<ConceptoGastoDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Gasto creado = fromConceptoDto(response.body());
                        if (creado != null) {
                            gastosRecurrentes.add(creado);
                        }
                        notifySuccess(callback, creado);
                    } else {
                        notifyError(callback, "No se pudo guardar el gasto recurrente. Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ConceptoGastoDto> call, Throwable t) {
                    notifyError(callback, "Error al conectar con el servidor de gastos recurrentes.");
                }
            });
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
                    Gasto creado = fromDto(response.body());
                    if (creado != null) {
                        gastos.add(creado);
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
        Ingreso ultimo = obtenerUltimoIngreso();
        if (ultimo == null || ultimo.getId() == null) {
            notifySuccess(callback, false);
            return;
        }
        if (ultimo.isRecurrente()) {
            conceptoIngresoRepository.eliminarConcepto(ultimo.getId(), new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        eliminarIngresoRecurrentePorId(ultimo.getId());
                        notifySuccess(callback, true);
                    } else {
                        notifyError(callback, "No se pudo eliminar el ingreso recurrente. Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    notifyError(callback, "Error al eliminar el ingreso recurrente en el servidor.");
                }
            });
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

    public static void removeIngresoById(Integer id, RepositoryCallback<Boolean> callback) {
        if (id == null) {
            notifyError(callback, "El ingreso no tiene identificador remoto.");
            return;
        }
        if (esIngresoRecurrente(id)) {
            conceptoIngresoRepository.eliminarConcepto(id, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        eliminarIngresoRecurrentePorId(id);
                        notifySuccess(callback, true);
                    } else {
                        notifyError(callback, "No se pudo eliminar el ingreso recurrente. Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    notifyError(callback, "Error al eliminar el ingreso recurrente en el servidor.");
                }
            });
        } else {
            ingresoRepository.eliminarIngreso(id, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
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
    }

    public static void removeLastGasto(RepositoryCallback<Boolean> callback) {
        Gasto ultimo = obtenerUltimoGasto();
        if (ultimo == null || ultimo.getId() == null) {
            notifySuccess(callback, false);
            return;
        }
        if (ultimo.isRecurrente()) {
            conceptoGastoRepository.eliminarConcepto(ultimo.getId(), new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        eliminarGastoRecurrentePorId(ultimo.getId());
                        notifySuccess(callback, true);
                    } else {
                        notifyError(callback, "No se pudo eliminar el gasto recurrente. Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    notifyError(callback, "Error al eliminar el gasto recurrente en el servidor.");
                }
            });
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

    public static void removeGastoById(Integer id, RepositoryCallback<Boolean> callback) {
        if (id == null) {
            notifyError(callback, "El gasto no tiene identificador remoto.");
            return;
        }
        if (esGastoRecurrente(id)) {
            conceptoGastoRepository.eliminarConcepto(id, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        eliminarGastoRecurrentePorId(id);
                        notifySuccess(callback, true);
                    } else {
                        notifyError(callback, "No se pudo eliminar el gasto recurrente. Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    notifyError(callback, "Error al eliminar el gasto recurrente en el servidor.");
                }
            });
        } else {
            gastoRepository.eliminarGasto(id, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
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
    }

    public static void updateIngreso(Context context, Ingreso ingreso, RepositoryCallback<Ingreso> callback) {
        if (ingreso == null || ingreso.getId() == null) {
            notifyError(callback, "El ingreso no es válido para actualizar.");
            return;
        }
        Ingreso existente = getIngresoById(ingreso.getId());
        boolean eraRecurrente = existente != null && existente.isRecurrente();
        if (ingreso.isRecurrente()) {
            if (eraRecurrente) {
                ConceptoIngresoDto dto = toConceptoDto(ingreso);
                conceptoIngresoRepository.actualizarConcepto(ingreso.getId(), dto, new Callback<ConceptoIngresoDto>() {
                    @Override
                    public void onResponse(Call<ConceptoIngresoDto> call, Response<ConceptoIngresoDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Ingreso actualizado = fromConceptoDto(response.body());
                            reemplazarIngresoRecurrente(actualizado);
                            notifySuccess(callback, actualizado);
                        } else {
                            notifyError(callback, "No se pudo actualizar el ingreso recurrente. Código: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ConceptoIngresoDto> call, Throwable t) {
                        notifyError(callback, "Error al actualizar el ingreso recurrente en el servidor.");
                    }
                });
            } else {
                Ingreso nuevoRecurrente = new Ingreso(null, ingreso.getArticulo(), ingreso.getDescripcion(),
                        ingreso.getFecha(), ingreso.getPeriodo(), true);
                ConceptoIngresoDto dto = toConceptoDto(nuevoRecurrente);
                conceptoIngresoRepository.guardarConcepto(dto, new Callback<ConceptoIngresoDto>() {
                    @Override
                    public void onResponse(Call<ConceptoIngresoDto> call, Response<ConceptoIngresoDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Ingreso creado = fromConceptoDto(response.body());
                            eliminarIngresoPorId(ingreso.getId());
                            if (creado != null) {
                                ingresosRecurrentes.add(creado);
                            }
                            ingresoRepository.eliminarIngreso(ingreso.getId(), new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        notifySuccess(callback, creado);
                                    } else {
                                        notifyError(callback, "No se pudo mover el ingreso a recurrente. Código: " + response.code());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    notifyError(callback, "Error al mover el ingreso a recurrente.");
                                }
                            });
                        } else {
                            notifyError(callback, "No se pudo guardar el ingreso recurrente. Código: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ConceptoIngresoDto> call, Throwable t) {
                        notifyError(callback, "Error al guardar el ingreso recurrente en el servidor.");
                    }
                });
            }
            return;
        }
        if (eraRecurrente) {
            Ingreso nuevoIngreso = new Ingreso(null, ingreso.getArticulo(), ingreso.getDescripcion(),
                    ingreso.getFecha(), ingreso.getPeriodo(), false);
            IngresoDto dto = toDto(nuevoIngreso, context);
            if (dto == null) {
                notifyError(callback, "Debes iniciar sesión antes de actualizar ingresos.");
                return;
            }
            ingresoRepository.guardarIngreso(dto, new Callback<IngresoDto>() {
                @Override
                public void onResponse(Call<IngresoDto> call, Response<IngresoDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Ingreso creado = fromDto(response.body());
                        eliminarIngresoRecurrentePorId(ingreso.getId());
                        if (creado != null) {
                            ingresos.add(creado);
                            registrarIngresoOriginal(creado);
                        }
                        conceptoIngresoRepository.eliminarConcepto(ingreso.getId(), new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    notifySuccess(callback, creado);
                                } else {
                                    notifyError(callback, "No se pudo mover el ingreso a normal. Código: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                notifyError(callback, "Error al mover el ingreso a normal.");
                            }
                        });
                    } else {
                        notifyError(callback, "No se pudo guardar el ingreso. Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<IngresoDto> call, Throwable t) {
                    notifyError(callback, "Error al guardar el ingreso en el servidor.");
                }
            });
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
        Gasto existente = getGastoById(gasto.getId());
        boolean eraRecurrente = existente != null && existente.isRecurrente();
        if (gasto.isRecurrente()) {
            if (eraRecurrente) {
                ConceptoGastoDto dto = toConceptoDto(gasto);
                conceptoGastoRepository.actualizarConcepto(gasto.getId(), dto, new Callback<ConceptoGastoDto>() {
                    @Override
                    public void onResponse(Call<ConceptoGastoDto> call, Response<ConceptoGastoDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Gasto actualizado = fromConceptoDto(response.body());
                            reemplazarGastoRecurrente(actualizado);
                            notifySuccess(callback, actualizado);
                        } else {
                            notifyError(callback, "No se pudo actualizar el gasto recurrente. Código: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ConceptoGastoDto> call, Throwable t) {
                        notifyError(callback, "Error al actualizar el gasto recurrente en el servidor.");
                    }
                });
            } else {
                Gasto nuevoRecurrente = new Gasto(null, gasto.getArticulo(), gasto.getDescripcion(),
                        gasto.getFecha(), gasto.getPeriodo(), true);
                ConceptoGastoDto dto = toConceptoDto(nuevoRecurrente);
                conceptoGastoRepository.guardarConcepto(dto, new Callback<ConceptoGastoDto>() {
                    @Override
                    public void onResponse(Call<ConceptoGastoDto> call, Response<ConceptoGastoDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Gasto creado = fromConceptoDto(response.body());
                            eliminarGastoPorId(gasto.getId());
                            if (creado != null) {
                                gastosRecurrentes.add(creado);
                            }
                            gastoRepository.eliminarGasto(gasto.getId(), new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        notifySuccess(callback, creado);
                                    } else {
                                        notifyError(callback, "No se pudo mover el gasto a recurrente. Código: " + response.code());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    notifyError(callback, "Error al mover el gasto a recurrente.");
                                }
                            });
                        } else {
                            notifyError(callback, "No se pudo guardar el gasto recurrente. Código: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ConceptoGastoDto> call, Throwable t) {
                        notifyError(callback, "Error al guardar el gasto recurrente en el servidor.");
                    }
                });
            }
            return;
        }
        if (eraRecurrente) {
            Gasto nuevoGasto = new Gasto(null, gasto.getArticulo(), gasto.getDescripcion(),
                    gasto.getFecha(), gasto.getPeriodo(), false);
            GastoDto dto = toDto(nuevoGasto, context);
            if (dto == null) {
                notifyError(callback, "Debes iniciar sesión antes de actualizar gastos.");
                return;
            }
            gastoRepository.guardarGasto(dto, new Callback<GastoDto>() {
                @Override
                public void onResponse(Call<GastoDto> call, Response<GastoDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Gasto creado = fromDto(response.body());
                        eliminarGastoRecurrentePorId(gasto.getId());
                        if (creado != null) {
                            gastos.add(creado);
                        }
                        conceptoGastoRepository.eliminarConcepto(gasto.getId(), new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    notifySuccess(callback, creado);
                                } else {
                                    notifyError(callback, "No se pudo mover el gasto a normal. Código: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                notifyError(callback, "Error al mover el gasto a normal.");
                            }
                        });
                    } else {
                        notifyError(callback, "No se pudo guardar el gasto. Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<GastoDto> call, Throwable t) {
                    notifyError(callback, "Error al guardar el gasto en el servidor.");
                }
            });
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
        if (ingreso.isRecurrente()) {
            notifyError(callback, "No puedes actualizar el monto de un ingreso recurrente.");
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
        pruneIngresosOriginales(nuevos);
    }

    private static void replaceGastos(List<Gasto> nuevos) {
        gastos.clear();
        gastos.addAll(nuevos);
    }

    private static void replaceIngresosRecurrentes(List<Ingreso> nuevos) {
        ingresosRecurrentes.clear();
        ingresosRecurrentes.addAll(nuevos);
    }

    private static void replaceGastosRecurrentes(List<Gasto> nuevos) {
        gastosRecurrentes.clear();
        gastosRecurrentes.addAll(nuevos);
    }

    private static void eliminarIngresoPorId(Integer id) {
        ingresos.removeIf(ingreso -> Objects.equals(ingreso.getId(), id));
        ingresosOriginales.remove(id);
    }

    private static void eliminarIngresoRecurrentePorId(Integer id) {
        ingresosRecurrentes.removeIf(ingreso -> Objects.equals(ingreso.getId(), id));
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
    }

    private static void reemplazarIngresoRecurrente(Ingreso actualizado) {
        if (actualizado == null) {
            return;
        }
        for (int i = 0; i < ingresosRecurrentes.size(); i++) {
            if (Objects.equals(ingresosRecurrentes.get(i).getId(), actualizado.getId())) {
                ingresosRecurrentes.set(i, actualizado);
                return;
            }
        }
        ingresosRecurrentes.add(actualizado);
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
    }

    private static void reemplazarGastoRecurrente(Gasto actualizado) {
        if (actualizado == null) {
            return;
        }
        for (int i = 0; i < gastosRecurrentes.size(); i++) {
            if (Objects.equals(gastosRecurrentes.get(i).getId(), actualizado.getId())) {
                gastosRecurrentes.set(i, actualizado);
                return;
            }
        }
        gastosRecurrentes.add(actualizado);
    }

    private static void eliminarGastoPorId(Integer id) {
        gastos.removeIf(gasto -> Objects.equals(gasto.getId(), id));
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
        for (Gasto gasto : gastosRecurrentes) {
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
        return new Ingreso(dto.getIdIngresos(), articulo, monto, fecha, periodo, false);
    }

    private static Gasto fromDto(GastoDto dto) {
        if (dto == null) {
            return null;
        }
        String articulo = dto.getArticuloGasto() != null ? dto.getArticuloGasto() : dto.getDescripcionGasto();
        String monto = montoToString(dto.getMontoGasto());
        String fecha = formatDate(dto.getFechaGastos());
        String periodo = dto.getPeriodoGastos();
        return new Gasto(dto.getIdGastos(), articulo, monto, fecha, periodo, false);
    }

    private static Ingreso fromConceptoDto(ConceptoIngresoDto dto) {
        if (dto == null) {
            return null;
        }
        String articulo = dto.getNombreConcepto();
        String monto = montoToString(dto.getPrecioConcepto());
        String periodo = dto.getDescripcionConcepto();
        return new Ingreso(dto.getIdConcepto(), articulo, monto, "", periodo, true);
    }

    private static Gasto fromConceptoDto(ConceptoGastoDto dto) {
        if (dto == null) {
            return null;
        }
        String articulo = dto.getNombreConcepto();
        String monto = montoToString(dto.getPrecioConcepto());
        String periodo = dto.getDescripcionConcepto();
        return new Gasto(dto.getIdConcepto(), articulo, monto, "", periodo, true);
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

    private static ConceptoIngresoDto toConceptoDto(Ingreso ingreso) {
        return ConceptoIngresoDto.builder()
                .idConcepto(ingreso.getId())
                .nombreConcepto(ingreso.getArticulo())
                .descripcionConcepto(ingreso.getPeriodo())
                .precioConcepto(parseMonto(ingreso.getDescripcion()))
                .idIngresos(null)
                .build();
    }

    private static ConceptoGastoDto toConceptoDto(Gasto gasto) {
        return ConceptoGastoDto.builder()
                .idConcepto(gasto.getId())
                .nombreConcepto(gasto.getArticulo())
                .descripcionConcepto(gasto.getPeriodo())
                .precioConcepto(parseMonto(gasto.getDescripcion()))
                .idGastos(null)
                .build();
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

    private static Ingreso obtenerUltimoIngreso() {
        if (!ingresos.isEmpty()) {
            return ingresos.get(ingresos.size() - 1);
        }
        if (!ingresosRecurrentes.isEmpty()) {
            return ingresosRecurrentes.get(ingresosRecurrentes.size() - 1);
        }
        return null;
    }

    private static Gasto obtenerUltimoGasto() {
        if (!gastos.isEmpty()) {
            return gastos.get(gastos.size() - 1);
        }
        if (!gastosRecurrentes.isEmpty()) {
            return gastosRecurrentes.get(gastosRecurrentes.size() - 1);
        }
        return null;
    }

    private static boolean esIngresoRecurrente(Integer id) {
        if (id == null) {
            return false;
        }
        for (Ingreso ingreso : ingresosRecurrentes) {
            if (Objects.equals(ingreso.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    private static boolean esGastoRecurrente(Integer id) {
        if (id == null) {
            return false;
        }
        for (Gasto gasto : gastosRecurrentes) {
            if (Objects.equals(gasto.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    private static void finalizarCargaIngresos(RepositoryCallback<List<Ingreso>> callback,
                                                   List<String> errors,
                                                   AtomicInteger pending) {
        if (pending.decrementAndGet() == 0) {
            if (errors.isEmpty()) {
                notifySuccess(callback, getIngresos());
            } else {
                notifyError(callback, errors.get(0));
            }
        }
    }

    private static void finalizarCargaGastos(RepositoryCallback<List<Gasto>> callback,
                                                 List<String> errors,
                                                 AtomicInteger pending) {
        if (pending.decrementAndGet() == 0) {
            if (errors.isEmpty()) {
                notifySuccess(callback, getGastos());
            } else {
                notifyError(callback, errors.get(0));
            }
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

    public static void clearAll() {
        ingresos.clear();
        ingresosRecurrentes.clear();
        gastos.clear();
        gastosRecurrentes.clear();
        ingresosOriginales.clear();
    }
}
