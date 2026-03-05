package com.example.economix_android.Model.data;

public class Gasto extends RegistroFinanciero {

    private final Integer categoriaId;

    public Gasto(Integer id, String articulo, String descripcion, String fecha, String periodo, boolean recurrente) {
        this(id, articulo, descripcion, fecha, periodo, recurrente, null);
    }

    public Gasto(Integer id, String articulo, String descripcion, String fecha, String periodo, boolean recurrente, Integer categoriaId) {
        super(id, articulo, descripcion, fecha, periodo, recurrente);
        this.categoriaId = categoriaId;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    @Override
    public String getTipo() {
        return "Gasto";
    }
}
