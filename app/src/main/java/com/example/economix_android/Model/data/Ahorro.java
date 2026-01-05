package com.example.economix_android.Model.data;

public class Ahorro {
    private final Integer id;
    private final String objetivo;
    private final String descripcion;
    private final String meta;
    private final String ahorrado;
    private final String fechaLimite;

    public Ahorro(Integer id, String objetivo, String descripcion, String meta, String ahorrado, String fechaLimite) {
        this.id = id;
        this.objetivo = objetivo;
        this.descripcion = descripcion;
        this.meta = meta;
        this.ahorrado = ahorrado;
        this.fechaLimite = fechaLimite;
    }

    public Integer getId() {
        return id;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getMeta() {
        return meta;
    }

    public String getAhorrado() {
        return ahorrado;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public int getPorcentajeAvance() {
        float metaValue = RegistroFinanciero.parseMonto(meta);
        float ahorroValue = RegistroFinanciero.parseMonto(ahorrado);
        if (metaValue <= 0f) {
            return 0;
        }
        float porcentaje = (ahorroValue / metaValue) * 100f;
        return (int) Math.min(100, Math.round(porcentaje));
    }
}
