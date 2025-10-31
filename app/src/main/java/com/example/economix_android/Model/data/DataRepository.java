package com.example.economix_android.Model.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DataRepository {

    private static final List<Ingreso> ingresos = new ArrayList<>();
    private static final List<Ingreso> ingresosRecurrentes = new ArrayList<>();
    private static final List<Gasto> gastos = new ArrayList<>();
    private static final List<Gasto> gastosRecurrentes = new ArrayList<>();
    private static final List<UserAccount> usuarios = new ArrayList<>();

    private DataRepository() {
        // No instances
    }

    public static void addIngreso(Ingreso ingreso) {
        ingresos.add(ingreso);
        if (ingreso.isRecurrente()) {
            ingresosRecurrentes.add(ingreso);
        }
    }

    public static void addGasto(Gasto gasto) {
        gastos.add(gasto);
        if (gasto.isRecurrente()) {
            gastosRecurrentes.add(gasto);
        }
    }

    public static boolean removeLastIngreso() {
        if (ingresos.isEmpty()) {
            return false;
        }
        Ingreso removed = ingresos.remove(ingresos.size() - 1);
        if (removed.isRecurrente()) {
            ingresosRecurrentes.remove(removed);
        }
        return true;
    }

    public static boolean removeLastGasto() {
        if (gastos.isEmpty()) {
            return false;
        }
        Gasto removed = gastos.remove(gastos.size() - 1);
        if (removed.isRecurrente()) {
            gastosRecurrentes.remove(removed);
        }
        return true;
    }

    public static List<Ingreso> getIngresos() {
        return Collections.unmodifiableList(ingresos);
    }

    public static List<Ingreso> getIngresosRecurrentes() {
        return Collections.unmodifiableList(ingresosRecurrentes);
    }

    public static List<Gasto> getGastos() {
        return Collections.unmodifiableList(gastos);
    }

    public static List<Gasto> getGastosRecurrentes() {
        return Collections.unmodifiableList(gastosRecurrentes);
    }
    public static boolean addUsuario(UserAccount usuario) {
        if (usuario == null) {
            return false;
        }
        if (existeCorreo(usuario.getEmail())) {
            return false;
        }
        usuarios.add(usuario);
        return true;
    }

    public static boolean existeCorreo(String correo) {
        String normalizado = normalizarCorreo(correo);
        for (UserAccount usuario : usuarios) {
            if (normalizado.equals(normalizarCorreo(usuario.getEmail()))) {
                return true;
            }
        }
        return false;
    }

    public static boolean validarCredenciales(String correo, String contrasena) {
        String normalizado = normalizarCorreo(correo);
        for (UserAccount usuario : usuarios) {
            if (normalizado.equals(normalizarCorreo(usuario.getEmail()))
                    && contrasena.equals(usuario.getPassword())) {
                return true;
            }
        }
        return false;
    }

    private static String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase();
    }
}