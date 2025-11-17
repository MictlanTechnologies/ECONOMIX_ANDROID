package com.example.economix_android.Model.data;

public class Gasto extends RegistroFinanciero {
    public Gasto(Integer id, String articulo, String descripcion, String fecha, String periodo, boolean recurrente) {
        super(id, articulo, descripcion, fecha, periodo, recurrente);
    }

    @Override
    public String getTipo() {
        return "Gasto";
    }
}