package com.example.economix_android.network;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class ServerConnectionChecker {

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    private ServerConnectionChecker() {
    }

    public static Result ping(@NonNull String baseUrl) {
        String[] paths = new String[]{"health/ping", "economix/api/test"};

        for (String path : paths) {
            String url = baseUrl + path;
            Request request = new Request.Builder().url(url).get().build();
            try (Response response = CLIENT.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return Result.success("Conexión OK (" + response.code() + ")", url);
                }
                return Result.error("Respuesta HTTP " + response.code(), url);
            } catch (SocketTimeoutException timeoutException) {
                return Result.error("Timeout al conectar", url);
            } catch (UnknownHostException unknownHostException) {
                return Result.error("Host inaccesible", url);
            } catch (IOException ioException) {
                return Result.error("Error de red: " + ioException.getClass().getSimpleName(), url);
            }
        }

        return Result.error("No se pudo validar conectividad", baseUrl);
    }

    public static final class Result {
        private final boolean success;
        private final String message;
        private final String url;

        private Result(boolean success, String message, String url) {
            this.success = success;
            this.message = message;
            this.url = url;
        }

        public static Result success(String message, String url) {
            return new Result(true, message, url);
        }

        public static Result error(String message, String url) {
            return new Result(false, message, url);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getUrl() {
            return url;
        }
    }
}
