package com.example.economix_android.network;

import android.content.Context;

import com.example.economix_android.BuildConfig;
import com.example.economix_android.ai.AiApi;
import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.network.api.AhorroApi;
import com.example.economix_android.network.api.CategoriaGastoApi;
import com.example.economix_android.network.api.ConceptoGastoApi;
import com.example.economix_android.network.api.ConceptoIngresoApi;
import com.example.economix_android.network.api.ContactoApi;
import com.example.economix_android.network.api.DomicilioApi;
import com.example.economix_android.network.api.GastoApi;
import com.example.economix_android.network.api.IngresoApi;
import com.example.economix_android.network.api.MovimientoAhorroApi;
import com.example.economix_android.network.api.PersonaApi;
import com.example.economix_android.network.api.PresupuestoApi;
import com.example.economix_android.network.api.UsuarioApi;
import com.example.economix_android.network.auth.AuthApi;
import com.example.economix_android.network.auth.AuthInterceptor;
import com.example.economix_android.network.auth.TokenAuthenticator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static volatile boolean initialized = false;
    private static Retrofit publicRetrofit;
    private static Retrofit authenticatedRetrofit;

    private ApiClient() {
    }

    /**
     * Inicializa (o re-inicializa) el cliente HTTP.
     */
    public static synchronized void init(Context context) {
        if (initialized) return;

        SessionManager sessionManager = new SessionManager(context.getApplicationContext());
        Gson gson = createGson();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        // Cliente público (login, refresh, register)
        OkHttpClient publicClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        publicRetrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(publicClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        AuthApi refreshApi = publicRetrofit.create(AuthApi.class);

        // Cliente autenticado
        OkHttpClient authenticatedClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(sessionManager))
                .authenticator(new TokenAuthenticator(sessionManager, refreshApi))
                .addInterceptor(logging)
                .build();

        authenticatedRetrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(authenticatedClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        initialized = true;
    }

    public static synchronized void reset() {
        initialized = false;
        publicRetrofit = null;
        authenticatedRetrofit = null;
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> {
                    try {
                        return LocalDate.parse(json.getAsString());
                    } catch (Exception e) {
                        throw new JsonParseException(e);
                    }
                })
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                    try {
                        return LocalDateTime.parse(json.getAsString());
                    } catch (Exception e) {
                        throw new JsonParseException(e);
                    }
                })
                .create();
    }

    private static Retrofit authRequiredRetrofit() {
        if (!initialized || authenticatedRetrofit == null) {
            throw new IllegalStateException("ApiClient no inicializado. Asegurate de que EconomixApp esta registrado en AndroidManifest.");
        }
        return authenticatedRetrofit;
    }

    public static AuthApi getAuthApi() {
        if (!initialized || publicRetrofit == null) {
            throw new IllegalStateException("ApiClient no inicializado. Asegurate de que EconomixApp esta registrado en AndroidManifest.");
        }
        return publicRetrofit.create(AuthApi.class);
    }

    public static AhorroApi getAhorroApi() { return authRequiredRetrofit().create(AhorroApi.class); }
    public static ConceptoGastoApi getConceptoGastoApi() { return authRequiredRetrofit().create(ConceptoGastoApi.class); }
    public static CategoriaGastoApi getCategoriaGastoApi() { return authRequiredRetrofit().create(CategoriaGastoApi.class); }
    public static ConceptoIngresoApi getConceptoIngresoApi() { return authRequiredRetrofit().create(ConceptoIngresoApi.class); }
    public static ContactoApi getContactoApi() { return authRequiredRetrofit().create(ContactoApi.class); }
    public static DomicilioApi getDomicilioApi() { return authRequiredRetrofit().create(DomicilioApi.class); }
    public static GastoApi getGastoApi() { return authRequiredRetrofit().create(GastoApi.class); }
    public static IngresoApi getIngresoApi() { return authRequiredRetrofit().create(IngresoApi.class); }
    public static MovimientoAhorroApi getMovimientoAhorroApi() { return authRequiredRetrofit().create(MovimientoAhorroApi.class); }
    public static PersonaApi getPersonaApi() { return authRequiredRetrofit().create(PersonaApi.class); }
    public static PresupuestoApi getPresupuestoApi() { return authRequiredRetrofit().create(PresupuestoApi.class); }
    public static AiApi getAiApi() { return authRequiredRetrofit().create(AiApi.class); }
    public static UsuarioApi getUsuarioApi() { return authRequiredRetrofit().create(UsuarioApi.class); }
}