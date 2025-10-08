// Paquete de utilidades para lectura de datos desde consola
package com.example.economix_android.Util;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Clase singleton para lectura de datos del usuario desde consola.
 * Proporciona métodos seguros para leer enteros, decimales y textos.
 */
public class readUtil {

    // Escáner único para entrada estándar
    private static Scanner scanner;

    // Instancia única de esta clase (Singleton)
    private static readUtil readUtil;

    // Constructor privado para Singleton
    private readUtil() {
        scanner = new Scanner(System.in);
    }

    /**
     * Devuelve el escáner interno.
     */
    public Scanner getScanner() {
        return scanner;
    }

    /**
     * Devuelve la instancia única de readUtil.
     */
    public static readUtil getInstance() {
        if (readUtil == null) {
            readUtil = new readUtil();
        }
        return readUtil;
    }

    /**
     * Lee una línea de texto desde consola.
     */
    public static String read() {
        return getInstance().getScanner().nextLine();
    }

    /**
     * Lee un valor entero, validando formato.
     * Reintenta hasta que el usuario escriba un número válido.
     */



    /**
     * Convierte un String a Integer de forma segura.
     * devuelve null si no es posible convertir.
     */
    public static Integer string2Integer(String valor) {
        try {
            return Integer.valueOf(valor);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convierte un String a Double de forma segura.
     * devuelve null si no es posible convertir.
     */
    public static Double string2Double(String valor) {
        try {
            return Double.valueOf(valor);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Lee un valor BigDecimal desde consola, validando que tenga formato correcto.
     */
    public static BigDecimal readBigDecimal() {
        while (true) {
            String in = scanner.nextLine().trim();
            try {
                return new BigDecimal(in);
            } catch (NumberFormatException ex) {
                System.out.print("> Formato numérico inválido. Intenta de nuevo: ");
            }
        }
    }
}
