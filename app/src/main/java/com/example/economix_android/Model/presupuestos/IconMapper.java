package com.example.economix_android.Model.presupuestos;

public final class IconMapper {
    private IconMapper() {}

    public static String iconFromKey(String key) {
        if (key == null) return "💸";
        switch (key) {
            case "food": return "🍔";
            case "clothes": return "👖";
            case "home": return "🏠";
            case "transport": return "🚌";
            case "health": return "💊";
            default: return "💸";
        }
    }
}
