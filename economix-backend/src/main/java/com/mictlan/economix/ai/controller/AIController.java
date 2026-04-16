package com.mictlan.economix.ai.controller;

import com.mictlan.economix.ai.dto.AISummaryResponse;
import com.mictlan.economix.ai.dto.AnomalyResponse;
import com.mictlan.economix.ai.dto.BudgetRiskResponse;
import com.mictlan.economix.ai.dto.CompareMeansRequest;
import com.mictlan.economix.ai.dto.ConfidenceIntervalResponse;
import com.mictlan.economix.ai.dto.HypothesisTestResponse;
import com.mictlan.economix.ai.dto.SpendForecastResponse;
import com.mictlan.economix.ai.service.AIDataService;
import com.mictlan.economix.ai.service.ForecastService;
import com.mictlan.economix.ai.service.InferentialStatsService;
import com.mictlan.economix.sql.model.Ahorro;
import com.mictlan.economix.sql.model.Gasto;
import com.mictlan.economix.sql.model.Presupuesto;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/ai")
@AllArgsConstructor
public class AIController {

    private final AIDataService aiDataService;
    private final ForecastService forecastService;
    private final InferentialStatsService inferentialStatsService;

    @GetMapping("/summary")
    public ResponseEntity<AISummaryResponse> getSummary(
            @RequestParam Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<Gasto> gastos = aiDataService.getGastos(userId, from, to, null);
        List<BigDecimal> gastoMontos = gastos.stream().map(Gasto::getMontoGasto).filter(Objects::nonNull).toList();
        if (gastoMontos.size() < 10) {
            return ResponseEntity.ok(insufficientSummary());
        }

        BigDecimal totalGastos = sum(gastoMontos);
        BigDecimal totalIngresos = sum(aiDataService.getIngresos(userId, from, to).stream().map(i -> nz(i.getMontoIngreso())).toList());
        BigDecimal totalAhorros = sum(aiDataService.getAhorros(userId).stream().map(a -> nz(a.getMontoAhorrado())).toList());

        long days = Math.max(1, ChronoUnit.DAYS.between(from, to) + 1);
        BigDecimal promedioDiarioGasto = div(totalGastos, BigDecimal.valueOf(days));
        BigDecimal promedioDiarioIngreso = div(totalIngresos, BigDecimal.valueOf(days));

        YearMonth ym = YearMonth.from(to);
        List<Gasto> gastosMes = aiDataService.getGastos(userId, ym.atDay(1), ym.atEndOfMonth(), null);
        int dayOfMonth = Math.max(1, to.getDayOfMonth());
        BigDecimal burnRateMes = div(sum(gastosMes.stream().map(g -> nz(g.getMontoGasto())).toList()), BigDecimal.valueOf(dayOfMonth));

        Map<Integer, String> categorias = aiDataService.categoriaNombreMap(userId);
        List<AISummaryResponse.TopEntry> topArticulos = gastos.stream()
                .collect(Collectors.groupingBy(Gasto::getArticuloGasto, Collectors.mapping(Gasto::getMontoGasto, Collectors.reducing(BigDecimal.ZERO, this::add))))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .map(e -> AISummaryResponse.TopEntry.builder().label(e.getKey()).total(scale2(e.getValue())).build())
                .toList();

        List<AISummaryResponse.TopEntry> topCategorias = gastos.stream()
                .collect(Collectors.groupingBy(g -> categorias.getOrDefault(g.getIdCategoria(), "Sin categoría"), Collectors.mapping(Gasto::getMontoGasto, Collectors.reducing(BigDecimal.ZERO, this::add))))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .map(e -> AISummaryResponse.TopEntry.builder().label(e.getKey()).total(scale2(e.getValue())).build())
                .toList();

        List<AISummaryResponse.SavingGoalProgress> metas = aiDataService.getAhorros(userId).stream()
                .map(this::toGoal)
                .toList();

        return ResponseEntity.ok(AISummaryResponse.builder()
                .status("OK")
                .totalGastos(scale2(totalGastos))
                .totalIngresos(scale2(totalIngresos))
                .totalAhorros(scale2(totalAhorros))
                .promedioDiarioGasto(scale2(promedioDiarioGasto))
                .promedioDiarioIngreso(scale2(promedioDiarioIngreso))
                .burnRateMes(scale2(burnRateMes))
                .topArticulos(topArticulos)
                .topCategorias(topCategorias)
                .progresoMetasAhorro(metas)
                .build());
    }

    @GetMapping("/predict/spend")
    public ResponseEntity<SpendForecastResponse> predictSpend(
            @RequestParam Integer userId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "7") Integer horizonDays,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        if (horizonDays != 7 && horizonDays != 30) horizonDays = 7;
        LocalDate end = to == null ? LocalDate.now() : to;
        LocalDate from = end.minusDays(90);
        List<Gasto> gastos = aiDataService.getGastos(userId, from, end, categoryId);
        if (gastos.size() < 10) {
            return ResponseEntity.ok(SpendForecastResponse.builder()
                    .status("INSUFFICIENT_DATA")
                    .recommendations(recommendations())
                    .horizonDays(horizonDays)
                    .trainedUntil(end)
                    .explanation("No hay suficientes observaciones para estimar predicción robusta.")
                    .build());
        }
        ForecastService.ForecastResult fr = forecastService.forecastSpend(gastos, from, end, horizonDays);
        return ResponseEntity.ok(SpendForecastResponse.builder()
                .status("OK")
                .horizonDays(horizonDays)
                .trainedUntil(end)
                .expectedSpend(fr.expected())
                .lower95(fr.lower95())
                .upper95(fr.upper95())
                .explanation(fr.explanation())
                .build());
    }

    @GetMapping("/predict/budget-risk")
    public ResponseEntity<BudgetRiskResponse> predictBudgetRisk(
            @RequestParam Integer userId,
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        List<Presupuesto> presupuestos = aiDataService.getPresupuestos(userId, month, year);
        List<Gasto> gastosMes = aiDataService.getGastos(userId, LocalDate.of(year, month, 1), YearMonth.of(year, month).atEndOfMonth(), null);
        if (gastosMes.size() < 10) {
            return ResponseEntity.ok(BudgetRiskResponse.builder().status("INSUFFICIENT_DATA").recommendations(recommendations()).items(List.of()).build());
        }
        LocalDate now = LocalDate.now();
        int dayOfMonth = (now.getYear() == year && now.getMonthValue() == month) ? now.getDayOfMonth() : YearMonth.of(year, month).lengthOfMonth();
        int monthDays = YearMonth.of(year, month).lengthOfMonth();

        List<BudgetRiskResponse.BudgetRiskItem> items = new ArrayList<>();
        for (Presupuesto p : presupuestos) {
            BigDecimal consumido = sum(gastosMes.stream()
                    .filter(g -> Objects.equals(g.getIdCategoria(), p.getIdCategoria()))
                    .map(g -> nz(g.getMontoGasto())).toList());
            BigDecimal pct = p.getMontoMaximo() == null || p.getMontoMaximo().compareTo(BigDecimal.ZERO) <= 0
                    ? BigDecimal.ZERO : consumido.multiply(BigDecimal.valueOf(100)).divide(p.getMontoMaximo(), 2, RoundingMode.HALF_UP);
            BigDecimal proyectado = consumido.multiply(BigDecimal.valueOf(monthDays)).divide(BigDecimal.valueOf(Math.max(dayOfMonth, 1)), 2, RoundingMode.HALF_UP);
            String riesgo = proyectado.compareTo(p.getMontoMaximo()) > 0 ? "ALTO" : (pct.compareTo(new BigDecimal("80")) >= 0 ? "MEDIO" : "BAJO");
            items.add(BudgetRiskResponse.BudgetRiskItem.builder()
                    .idPresupuesto(p.getIdPresupuesto())
                    .idCategoria(p.getIdCategoria())
                    .categoria(p.getCategoria())
                    .montoMaximo(scale2(nz(p.getMontoMaximo())))
                    .montoConsumido(scale2(consumido))
                    .porcentajeConsumido(scale2(pct))
                    .proyeccionFinMes(scale2(proyectado))
                    .riesgo(riesgo)
                    .build());
        }
        return ResponseEntity.ok(BudgetRiskResponse.builder().status("OK").items(items).build());
    }

    @GetMapping("/anomalies")
    public ResponseEntity<AnomalyResponse> getAnomalies(
            @RequestParam Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<Gasto> gastos = aiDataService.getGastos(userId, from, to, null);
        List<BigDecimal> sample = gastos.stream().map(Gasto::getMontoGasto).filter(Objects::nonNull).toList();
        if (sample.size() < 10) {
            return ResponseEntity.ok(AnomalyResponse.builder().status("INSUFFICIENT_DATA").recommendations(recommendations()).anomalies(List.of()).build());
        }

        BigDecimal median = median(sample);
        List<BigDecimal> absDiff = sample.stream().map(v -> v.subtract(median).abs()).toList();
        BigDecimal mad = median(absDiff);

        List<AnomalyResponse.AnomalyItem> anomalies = new ArrayList<>();
        for (Gasto g : gastos) {
            BigDecimal monto = nz(g.getMontoGasto());
            BigDecimal z = mad.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                    monto.subtract(median).abs().divide(mad, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("0.6745"));
            if (z.compareTo(new BigDecimal("3.5")) >= 0) {
                anomalies.add(AnomalyResponse.AnomalyItem.builder()
                        .idGasto(g.getIdGastos())
                        .fecha(g.getFechaGastos())
                        .articulo(g.getArticuloGasto())
                        .idCategoria(g.getIdCategoria())
                        .monto(scale2(monto))
                        .robustZScore(scale2(z))
                        .build());
            }
        }

        return ResponseEntity.ok(AnomalyResponse.builder()
                .status("OK")
                .median(scale2(median))
                .mad(scale2(mad))
                .anomalies(anomalies)
                .build());
    }

    @GetMapping("/infer/ci-mean")
    public ResponseEntity<ConfidenceIntervalResponse> inferCiMean(
            @RequestParam Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0.95") BigDecimal confidence
    ) {
        List<BigDecimal> sample = aiDataService.getGastos(userId, from, to, categoryId).stream()
                .map(Gasto::getMontoGasto)
                .filter(Objects::nonNull)
                .toList();
        if (sample.size() < 10) {
            return ResponseEntity.ok(ConfidenceIntervalResponse.builder()
                    .status("INSUFFICIENT_DATA")
                    .recommendations(recommendations())
                    .metricDefinition("Media de gasto por transacción")
                    .sampleSize(sample.size())
                    .confidence(confidence)
                    .build());
        }

        InferentialStatsService.CIResult ci = inferentialStatsService.confidenceIntervalMean(sample, confidence.doubleValue());
        return ResponseEntity.ok(ConfidenceIntervalResponse.builder()
                .status("OK")
                .metricDefinition("Media de gasto por transacción")
                .sampleSize(ci.n())
                .mean(scale2(ci.mean()))
                .confidence(confidence)
                .lower(scale2(ci.lower()))
                .upper(scale2(ci.upper()))
                .build());
    }

    @PostMapping("/infer/compare-means")
    public ResponseEntity<HypothesisTestResponse> compareMeans(@RequestBody CompareMeansRequest request) {
        BigDecimal alpha = request.getAlpha() == null ? new BigDecimal("0.05") : request.getAlpha();

        List<BigDecimal> sampleA = aiDataService.getGastos(request.getUserId(), request.getFromA(), request.getToA(), request.getCategoryId())
                .stream().map(Gasto::getMontoGasto).filter(Objects::nonNull).toList();
        List<BigDecimal> sampleB = aiDataService.getGastos(request.getUserId(), request.getFromB(), request.getToB(), request.getCategoryId())
                .stream().map(Gasto::getMontoGasto).filter(Objects::nonNull).toList();

        if (sampleA.size() < 10 || sampleB.size() < 10) {
            return ResponseEntity.ok(HypothesisTestResponse.builder()
                    .status("INSUFFICIENT_DATA")
                    .recommendations(recommendations())
                    .nA(sampleA.size())
                    .nB(sampleB.size())
                    .conclusion("Se requieren al menos 10 observaciones por periodo para aplicar Welch t-test.")
                    .build());
        }

        InferentialStatsService.WelchTestResult res = inferentialStatsService.welchTTest(sampleA, sampleB, alpha.doubleValue());
        return ResponseEntity.ok(HypothesisTestResponse.builder()
                .status("OK")
                .nA(res.nA())
                .nB(res.nB())
                .meanA(scale2(res.meanA()))
                .meanB(scale2(res.meanB()))
                .differenceMeans(scale2(res.diff()))
                .t(scale2(res.t()))
                .df(scale2(res.df()))
                .pValue(scale2(res.p()))
                .conclusion(res.conclusion())
                .build());
    }

    private AISummaryResponse insufficientSummary() {
        return AISummaryResponse.builder()
                .status("INSUFFICIENT_DATA")
                .recommendations(recommendations())
                .topArticulos(List.of())
                .topCategorias(List.of())
                .progresoMetasAhorro(List.of())
                .build();
    }

    private List<String> recommendations() {
        return List.of(
                "Captura al menos 10 transacciones por categoría y periodo.",
                "Registra fecha exacta y artículo del gasto para mejorar tendencia y anomalías.",
                "Mantén actualizados presupuestos y metas de ahorro para análisis de riesgo."
        );
    }

    private AISummaryResponse.SavingGoalProgress toGoal(Ahorro a) {
        BigDecimal meta = nz(a.getMeta());
        BigDecimal ahorrado = nz(a.getMontoAhorrado());
        BigDecimal pct = meta.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO
                : ahorrado.multiply(BigDecimal.valueOf(100)).divide(meta, 2, RoundingMode.HALF_UP);
        return AISummaryResponse.SavingGoalProgress.builder()
                .idAhorro(a.getIdAhorro())
                .nombreObjetivo(a.getNombreObjetivo())
                .meta(scale2(meta))
                .montoAhorrado(scale2(ahorrado))
                .porcentajeProgreso(scale2(pct))
                .build();
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal add(BigDecimal a, BigDecimal b) {
        return nz(a).add(nz(b));
    }

    private BigDecimal sum(List<BigDecimal> values) {
        return values.stream().filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal div(BigDecimal a, BigDecimal b) {
        if (b == null || b.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return a.divide(b, 6, RoundingMode.HALF_UP);
    }

    private BigDecimal median(List<BigDecimal> values) {
        List<BigDecimal> sorted = values.stream().sorted(Comparator.naturalOrder()).toList();
        int n = sorted.size();
        if (n == 0) return BigDecimal.ZERO;
        if (n % 2 == 1) return sorted.get(n / 2);
        return sorted.get(n / 2 - 1).add(sorted.get(n / 2)).divide(new BigDecimal("2"), 6, RoundingMode.HALF_UP);
    }

    private BigDecimal scale2(BigDecimal v) {
        return nz(v).setScale(2, RoundingMode.HALF_UP);
    }
}
