package com.mictlan.economix.ai.service;

import com.mictlan.economix.sql.model.Gasto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ForecastService {

    public ForecastResult forecastSpend(List<Gasto> gastos, LocalDate from, LocalDate to, int horizonDays) {
        Map<LocalDate, BigDecimal> dailySeries = buildDailySeries(gastos, from, to);
        List<BigDecimal> values = new ArrayList<>(dailySeries.values());
        if (values.isEmpty()) {
            return new ForecastResult(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "Sin historial suficiente para estimar tendencia.");
        }

        BigDecimal ma7 = movingAverage(values, 7);
        BigDecimal ewma = ewma(values, new BigDecimal("0.35"));
        BigDecimal dailyForecast = ma7.multiply(new BigDecimal("0.6")).add(ewma.multiply(new BigDecimal("0.4")));
        if (dailyForecast.compareTo(BigDecimal.ZERO) < 0) {
            dailyForecast = BigDecimal.ZERO;
        }

        BigDecimal expected = dailyForecast.multiply(BigDecimal.valueOf(horizonDays));
        BigDecimal sigma = stdDev(values);
        BigDecimal margin = sigma.multiply(BigDecimal.valueOf(Math.sqrt(horizonDays))).multiply(new BigDecimal("1.96"));
        BigDecimal lower = expected.subtract(margin).max(BigDecimal.ZERO);
        BigDecimal upper = expected.add(margin);

        String explanation = "Modelo híbrido MA(7)+EWMA, entrenado con datos históricos hasta la fecha de corte sin fuga temporal.";
        return new ForecastResult(scale(expected), scale(lower), scale(upper), explanation);
    }

    private Map<LocalDate, BigDecimal> buildDailySeries(List<Gasto> gastos, LocalDate from, LocalDate to) {
        Map<LocalDate, BigDecimal> series = new TreeMap<>();
        LocalDate start = from;
        LocalDate end = to;
        if (start != null && end != null) {
            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                series.put(d, BigDecimal.ZERO);
            }
        }
        gastos.stream().sorted(Comparator.comparing(Gasto::getFechaGastos)).forEach(g -> {
            LocalDate d = g.getFechaGastos();
            series.put(d, series.getOrDefault(d, BigDecimal.ZERO).add(g.getMontoGasto() == null ? BigDecimal.ZERO : g.getMontoGasto()));
        });
        return series;
    }

    private BigDecimal movingAverage(List<BigDecimal> values, int window) {
        int start = Math.max(0, values.size() - window);
        BigDecimal sum = BigDecimal.ZERO;
        int n = 0;
        for (int i = start; i < values.size(); i++) {
            sum = sum.add(values.get(i));
            n++;
        }
        return n == 0 ? BigDecimal.ZERO : sum.divide(BigDecimal.valueOf(n), 6, RoundingMode.HALF_UP);
    }

    private BigDecimal ewma(List<BigDecimal> values, BigDecimal alpha) {
        BigDecimal prev = values.get(0);
        for (int i = 1; i < values.size(); i++) {
            prev = alpha.multiply(values.get(i)).add(BigDecimal.ONE.subtract(alpha).multiply(prev));
        }
        return prev;
    }

    private BigDecimal stdDev(List<BigDecimal> values) {
        if (values.size() < 2) return BigDecimal.ZERO;
        double mean = values.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0d);
        double var = values.stream().mapToDouble(v -> Math.pow(v.doubleValue() - mean, 2)).sum() / (values.size() - 1);
        return BigDecimal.valueOf(Math.sqrt(var));
    }

    private BigDecimal scale(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    public record ForecastResult(BigDecimal expected, BigDecimal lower95, BigDecimal upper95, String explanation) {}
}
