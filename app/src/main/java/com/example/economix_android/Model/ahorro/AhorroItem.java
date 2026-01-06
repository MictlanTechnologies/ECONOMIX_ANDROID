package com.example.economix_android.Model.ahorro;

public class AhorroItem {
    private final Integer idAhorro;
    private final String monto;
    private final String periodo;
    private final String fecha;
    private final Integer ingresoId;

    public AhorroItem(Integer idAhorro, String monto, String periodo, String fecha, Integer ingresoId) {
        this.idAhorro = idAhorro;
        this.monto = monto;
        this.periodo = periodo;
        this.fecha = fecha;
        this.ingresoId = ingresoId;
    }

    public Integer getIdAhorro() {
        return idAhorro;
    }

    public String getMonto() {
        return monto;
    }

    public String getPeriodo() {
        return periodo;
    }

    public String getFecha() {
        return fecha;
    }

    public Integer getIngresoId() {
        return ingresoId;
    }
}
