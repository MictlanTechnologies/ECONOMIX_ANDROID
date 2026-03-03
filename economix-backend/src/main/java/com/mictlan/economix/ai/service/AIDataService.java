package com.mictlan.economix.ai.service;

import com.mictlan.economix.sql.model.Ahorro;
import com.mictlan.economix.sql.model.CategoriaGasto;
import com.mictlan.economix.sql.model.Gasto;
import com.mictlan.economix.sql.model.Ingreso;
import com.mictlan.economix.sql.model.MovimientoAhorro;
import com.mictlan.economix.sql.model.Presupuesto;
import com.mictlan.economix.sql.repository.AhorroRepository;
import com.mictlan.economix.sql.repository.CategoriaGastoRepository;
import com.mictlan.economix.sql.repository.GastoRepository;
import com.mictlan.economix.sql.repository.IngresoRepository;
import com.mictlan.economix.sql.repository.MovimientoAhorroRepository;
import com.mictlan.economix.sql.repository.PresupuestoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AIDataService {

    private final GastoRepository gastoRepository;
    private final IngresoRepository ingresoRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final CategoriaGastoRepository categoriaGastoRepository;
    private final AhorroRepository ahorroRepository;
    private final MovimientoAhorroRepository movimientoAhorroRepository;

    public List<Gasto> getGastos(Integer userId, LocalDate from, LocalDate to, Integer categoryId) {
        return gastoRepository.findAll().stream()
                .filter(g -> userId.equals(g.getIdUsuario()))
                .filter(g -> categoryId == null || categoryId.equals(g.getIdCategoria()))
                .filter(g -> inRange(g.getFechaGastos(), from, to))
                .collect(Collectors.toList());
    }

    public List<Ingreso> getIngresos(Integer userId, LocalDate from, LocalDate to) {
        return ingresoRepository.findAll().stream()
                .filter(i -> userId.equals(i.getIdUsuario()))
                .filter(i -> inRange(i.getFechaIngresos(), from, to))
                .collect(Collectors.toList());
    }

    public List<Presupuesto> getPresupuestos(Integer userId, Integer month, Integer year) {
        return presupuestoRepository.findAll().stream()
                .filter(p -> userId.equals(p.getIdUsuario()))
                .filter(p -> month == null || month.equals(p.getMes()))
                .filter(p -> year == null || year.equals(p.getAnio()))
                .collect(Collectors.toList());
    }

    public List<CategoriaGasto> getCategorias(Integer userId) {
        return categoriaGastoRepository.findAll().stream()
                .filter(c -> userId.equals(c.getIdUsuario()))
                .collect(Collectors.toList());
    }

    public Map<Integer, String> categoriaNombreMap(Integer userId) {
        return getCategorias(userId).stream()
                .collect(Collectors.toMap(CategoriaGasto::getIdCategoria, CategoriaGasto::getNombreCategoria, (a, b) -> a));
    }

    public List<Ahorro> getAhorros(Integer userId) {
        return ahorroRepository.findAll().stream()
                .filter(a -> userId.equals(a.getIdUsuario()))
                .collect(Collectors.toList());
    }

    public List<MovimientoAhorro> getMovimientosAhorro(Integer userId) {
        return movimientoAhorroRepository.findAll().stream()
                .filter(m -> userId.equals(m.getIdUsuario()))
                .collect(Collectors.toList());
    }

    private boolean inRange(LocalDate value, LocalDate from, LocalDate to) {
        if (value == null) return false;
        if (from != null && value.isBefore(from)) return false;
        if (to != null && value.isAfter(to)) return false;
        return true;
    }
}
