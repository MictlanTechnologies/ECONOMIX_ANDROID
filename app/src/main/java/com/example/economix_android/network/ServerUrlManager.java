package com.example.economix_android.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.economix_android.BuildConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;

public final class ServerUrlManager {

    private static final String PREFS_NAME = "economix_network_prefs";
    private static final String KEY_CUSTOM_BASE_URL = "custom_base_url";
    private static final Pattern HOST_PORT_PATTERN = Pattern.compile("^([^/:?#]+):(\\d{1,5})$");

    private ServerUrlManager() {
    }

    public static String getBaseUrl(Context context) {
        SharedPreferences preferences = getPreferences(context);
        String saved = preferences.getString(KEY_CUSTOM_BASE_URL, null);
        if (!TextUtils.isEmpty(saved)) {
            return ensureTrailingSlash(saved);
        }
        return ensureTrailingSlash(BuildConfig.BASE_URL);
    }

    public static void saveCustomUrl(Context context, String input) {
        String validationError = validate(input);
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }
        String normalized = normalize(input);
        getPreferences(context)
                .edit()
                .putString(KEY_CUSTOM_BASE_URL, normalized)
                .apply();
    }

    public static void clearCustomUrl(Context context) {
        getPreferences(context)
                .edit()
                .remove(KEY_CUSTOM_BASE_URL)
                .apply();
    }

    public static String validate(String input) {
        if (TextUtils.isEmpty(input) || TextUtils.isEmpty(input.trim())) {
            return "Ingresa una IP o dominio del servidor";
        }

        String normalized = normalize(input);
        if (TextUtils.isEmpty(normalized)) {
            return "Formato de URL inválido";
        }

        HttpUrl parsed = HttpUrl.parse(normalized);
        if (parsed == null) {
            return "Formato de URL inválido";
        }

        String host = parsed.host();
        if (TextUtils.isEmpty(host)) {
            return "La URL no contiene un host válido";
        }

        boolean validHost = Patterns.IP_ADDRESS.matcher(host).matches()
                || Patterns.DOMAIN_NAME.matcher(host).matches()
                || "localhost".equalsIgnoreCase(host);
        if (!validHost) {
            return "Host inválido. Usa IP o dominio válido";
        }

        if (parsed.port() < 1 || parsed.port() > 65535) {
            return "El puerto debe estar entre 1 y 65535";
        }

        return null;
    }

    public static String normalize(String input) {
        String raw = input == null ? "" : input.trim();
        if (raw.isEmpty()) {
            return "";
        }

        boolean hasScheme = raw.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*");
        String withScheme = hasScheme ? raw : "http://" + raw;

        HttpUrl parsed = HttpUrl.parse(withScheme);
        if (parsed == null) {
            return ensureTrailingSlash(withScheme);
        }

        boolean hasExplicitPort = hasPort(raw);
        boolean rawIsIp = Patterns.IP_ADDRESS.matcher(parsed.host()).matches();

        HttpUrl.Builder builder = parsed.newBuilder()
                .encodedPath("/")
                .query(null)
                .fragment(null);

        if (hasExplicitPort) {
            builder.port(parsed.port());
        } else if (!hasScheme && rawIsIp) {
            builder.port(8080);
        }

        return ensureTrailingSlash(builder.build().toString());
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static boolean hasPort(String rawInput) {
        String noScheme = rawInput.replaceFirst("^[a-zA-Z][a-zA-Z0-9+.-]*://", "");
        String hostPart = noScheme.replaceFirst("/.*$", "");
        Matcher matcher = HOST_PORT_PATTERN.matcher(hostPart);
        return matcher.matches();
    }

    private static String ensureTrailingSlash(String input) {
        if (TextUtils.isEmpty(input)) {
            return input;
        }
        return input.endsWith("/") ? input : input + "/";
    }
}
