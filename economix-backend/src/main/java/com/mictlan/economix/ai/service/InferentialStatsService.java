package com.mictlan.economix.ai.service;

import org.apache.commons.math3.distribution.TDistribution;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class InferentialStatsService {

    public StatisticalSummary summary(List<BigDecimal> sample) {
        int n = sample.size();
        double mean = sample.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0d);
        double var = 0d;
        if (n > 1) {
            var = sample.stream().mapToDouble(x -> Math.pow(x.doubleValue() - mean, 2)).sum() / (n - 1);
        }
        return new StatisticalSummary(n, bd(mean), bd(var));
    }

    public CIResult confidenceIntervalMean(List<BigDecimal> sample, double confidence) {
        StatisticalSummary s = summary(sample);
        if (s.n() < 2) {
            return new CIResult(s.n(), s.mean(), BigDecimal.ZERO, BigDecimal.ZERO);
        }
        double mean = s.mean().doubleValue();
        double sd = Math.sqrt(s.variance().doubleValue());
        double alpha = 1 - confidence;
        TDistribution tDistribution = new TDistribution(s.n() - 1);
        double tCrit = tDistribution.inverseCumulativeProbability(1 - alpha / 2d);
        double margin = tCrit * sd / Math.sqrt(s.n());
        return new CIResult(s.n(), bd(mean), bd(mean - margin), bd(mean + margin));
    }

    public WelchTestResult welchTTest(List<BigDecimal> sampleA, List<BigDecimal> sampleB, double alpha) {
        StatisticalSummary a = summary(sampleA);
        StatisticalSummary b = summary(sampleB);

        double meanA = a.mean().doubleValue();
        double meanB = b.mean().doubleValue();
        double varA = a.variance().doubleValue();
        double varB = b.variance().doubleValue();

        double t = (meanA - meanB) / Math.sqrt((varA / a.n()) + (varB / b.n()));
        double num = Math.pow((varA / a.n()) + (varB / b.n()), 2);
        double den = (Math.pow(varA / a.n(), 2) / (a.n() - 1.0)) + (Math.pow(varB / b.n(), 2) / (b.n() - 1.0));
        double df = num / den;

        TDistribution tDistribution = new TDistribution(df);
        double p = 2 * (1 - tDistribution.cumulativeProbability(Math.abs(t)));

        String conclusion = p < alpha
                ? "Se rechaza H0: hay diferencia estadísticamente significativa entre medias."
                : "No se rechaza H0: no hay evidencia suficiente de diferencia entre medias.";

        return new WelchTestResult(a.n(), b.n(), a.mean(), b.mean(), bd(meanA - meanB), bd(t), bd(df), bd(p), conclusion);
    }

    private BigDecimal bd(double value) {
        return BigDecimal.valueOf(value).setScale(6, RoundingMode.HALF_UP);
    }

    public record StatisticalSummary(int n, BigDecimal mean, BigDecimal variance) {}

    public record CIResult(int n, BigDecimal mean, BigDecimal lower, BigDecimal upper) {}

    public record WelchTestResult(
            int nA,
            int nB,
            BigDecimal meanA,
            BigDecimal meanB,
            BigDecimal diff,
            BigDecimal t,
            BigDecimal df,
            BigDecimal p,
            String conclusion
    ) {}
}
