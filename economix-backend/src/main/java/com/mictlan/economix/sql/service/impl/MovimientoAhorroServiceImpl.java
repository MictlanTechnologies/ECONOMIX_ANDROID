package com.mictlan.economix.sql.service.impl;

import com.mictlan.economix.sql.model.Ahorro;
import com.mictlan.economix.sql.model.MovimientoAhorro;
import com.mictlan.economix.sql.repository.AhorroRepository;
import com.mictlan.economix.sql.repository.MovimientoAhorroRepository;
import com.mictlan.economix.sql.service.MovimientoAhorroService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class MovimientoAhorroServiceImpl implements MovimientoAhorroService {

    private final MovimientoAhorroRepository movimientoAhorroRepository;
    private final AhorroRepository ahorroRepository;

    @Override
    public List<MovimientoAhorro> getAll() {
        return movimientoAhorroRepository.findAll();
    }

    @Override
    public MovimientoAhorro getById(Integer id) {
        return movimientoAhorroRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public MovimientoAhorro save(MovimientoAhorro movimientoAhorro) {
        if (movimientoAhorro == null) {
            return null;
        }
        Ahorro ahorro = obtenerAhorro(movimientoAhorro.getIdAhorro());
        if (ahorro == null || !Objects.equals(ahorro.getIdUsuario(), movimientoAhorro.getIdUsuario())) {
            return null;
        }

        BigDecimal delta = calcularDelta(movimientoAhorro);
        if (!aplicarDelta(ahorro, delta)) {
            return null;
        }

        if (movimientoAhorro.getFechaMovimiento() == null) {
            movimientoAhorro.setFechaMovimiento(java.time.LocalDateTime.now());
        }

        MovimientoAhorro guardado = movimientoAhorroRepository.save(movimientoAhorro);
        ahorroRepository.save(ahorro);
        return guardado;
    }

    @Override
    @Transactional
    public boolean delete(Integer id) {
        return movimientoAhorroRepository.findById(id)
                .map(movimiento -> {
                    Ahorro ahorro = obtenerAhorro(movimiento.getIdAhorro());
                    if (ahorro == null) {
                        return false;
                    }
                    if (!aplicarDelta(ahorro, calcularDelta(movimiento).negate())) {
                        return false;
                    }
                    movimientoAhorroRepository.deleteById(id);
                    ahorroRepository.save(ahorro);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public MovimientoAhorro update(Integer id, MovimientoAhorro movimientoAhorro) {
        return movimientoAhorroRepository.findById(id)
                .map(existing -> {
                    Ahorro ahorro = obtenerAhorro(existing.getIdAhorro());
                    if (ahorro == null || !Objects.equals(movimientoAhorro.getIdUsuario(), ahorro.getIdUsuario())) {
                        return null;
                    }

                    BigDecimal deltaExistente = calcularDelta(existing);
                    BigDecimal deltaNuevo = calcularDelta(movimientoAhorro);
                    BigDecimal ajuste = deltaNuevo.subtract(deltaExistente);

                    if (!aplicarDelta(ahorro, ajuste)) {
                        return null;
                    }

                    BeanUtils.copyProperties(movimientoAhorro, existing, "idMovimiento");
                    MovimientoAhorro actualizado = movimientoAhorroRepository.save(existing);
                    ahorroRepository.save(ahorro);
                    return actualizado;
                })
                .orElse(null);
    }

    private Ahorro obtenerAhorro(Integer idAhorro) {
        return idAhorro == null ? null : ahorroRepository.findById(idAhorro).orElse(null);
    }

    private BigDecimal calcularDelta(MovimientoAhorro movimiento) {
        if (movimiento == null || movimiento.getMonto() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal monto = movimiento.getMonto();
        return "RETIRO".equalsIgnoreCase(movimiento.getTipoMovimiento()) ? monto.negate() : monto;
    }

    private boolean aplicarDelta(Ahorro ahorro, BigDecimal delta) {
        if (ahorro == null || delta == null) {
            return false;
        }
        BigDecimal nuevoMonto = ahorro.getMontoAhorrado().add(delta);
        if (nuevoMonto.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        if (ahorro.getMeta() != null && nuevoMonto.compareTo(ahorro.getMeta()) > 0) {
            return false;
        }
        ahorro.setMontoAhorrado(nuevoMonto);
        return true;
    }
}
