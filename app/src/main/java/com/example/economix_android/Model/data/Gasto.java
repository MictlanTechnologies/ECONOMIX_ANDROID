package com.example.economix_android.Model.data;

public class Gasto extends RegistroFinanciero {
    public Gasto(String articulo, String descripcion, String fecha, String periodo, boolean recurrente) {
        super(articulo, descripcion, fecha, periodo, recurrente);
    }

    @Override
    public String getTipo() {
        return "Gasto";
    }
}