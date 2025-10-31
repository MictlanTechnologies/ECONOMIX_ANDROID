package com.example.economix_android.Model.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

public abstract class RegistroFinanciero {
    private static final Pattern MONTO_PATTERN = Pattern.compile("^\\d+(?:[\\.,]\\d+)?$");

    private final String articulo;
    private final String descripcion;
    private final String fecha;
    private final String periodo;
    private final boolean recurrente;

    protected RegistroFinanciero(String articulo, String descripcion, String fecha, String periodo, boolean recurrente) {
        this.articulo = articulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.periodo = periodo;
        this.recurrente = recurrente;
    }

    public String getArticulo() {
        return articulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getPeriodo() {
        return periodo;
    }

    public boolean isRecurrente() {
        return recurrente;
    }

    public float getMonto() {
        return parseMonto(descripcion);
    }

    public static boolean esMontoValido(String valor) {
        if (valor == null) {
            return false;
        }
        String trimmed = valor.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        return MONTO_PATTERN.matcher(trimmed).matches();
    }

    public static String normalizarMonto(String valor) {
        if (!esMontoValido(valor)) {
            return "0";
        }
        String sanitized = valor.trim().replace(',', '.');
        BigDecimal monto = new BigDecimal(sanitized);
        monto = monto.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        return monto.toPlainString();
    }

    public static float parseMonto(String valor) {
        if (!esMontoValido(valor)) {
            return 0f;
        }
        String sanitized = valor.trim().replace(',', '.');
        try {
            BigDecimal monto = new BigDecimal(sanitized);
            return monto.floatValue();
        } catch (NumberFormatException ex) {
            return 0f;
        }
    }

    public abstract String getTipo();
}