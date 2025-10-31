package com.example.economix_android.Model.data;

public abstract class RegistroFinanciero {
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

    public abstract String getTipo();
}