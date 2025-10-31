package com.example.economix_android.Model.data;

public class Ingreso extends RegistroFinanciero {
    public Ingreso(String articulo, String descripcion, String fecha, String periodo, boolean recurrente) {
        super(articulo, descripcion, fecha, periodo, recurrente);
    }

    @Override
    public String getTipo() {
        return "Ingreso";
    }
}