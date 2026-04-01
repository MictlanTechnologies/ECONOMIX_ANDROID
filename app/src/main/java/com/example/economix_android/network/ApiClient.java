package com.example.economix_android.network;

import static com.example.economix_android.network.NetworkConfig.BASE_URL;

import android.content.Context;

import com.example.economix_android.network.api.AhorroApi;
import com.example.economix_android.network.api.ConceptoGastoApi;
import com.example.economix_android.network.api.ConceptoIngresoApi;
import com.example.economix_android.network.api.ContactoApi;
import com.example.economix_android.network.api.DomicilioApi;
import com.example.economix_android.network.api.GastoApi;
import com.example.economix_android.network.api.IngresoApi;
import com.example.economix_android.network.api.PersonaApi;
import com.example.economix_android.network.api.UsuarioApi;
import com.example.economix_android.network.auth.AuthApi;
import com.example.economix_android.network.auth.AuthServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    // IMPORTANTE: la URL base debe terminar con "/" para Retrofit
    private static final Retrofit retrofit;
    private static Context appContext;

    static {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        // Configuración de Gson con adaptadores para LocalDate y LocalDateTime
        Gson gson = new GsonBuilder()
                // LocalDate: se serializa/deserializa como "yyyy-MM-dd"
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> {
                    try {
                        return LocalDate.parse(json.getAsString());
                    } catch (Exception e) {
                        throw new JsonParseException(e);
                    }
                })
                // LocalDateTime: se serializa/deserializa como "yyyy-MM-dd'T'HH:mm:ss"
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

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    private ApiClient() {
    }


    /**
     * Inicializa dependencias que requieren Context de aplicación.
     * Mantiene compatibilidad con código existente que invoca ApiClient.init(...).
     */
    public static void init(Context context) {
        if (context != null) {
            appContext = context.getApplicationContext();
        }
    }

    /**
     * Punto de acceso de compatibilidad para AuthApi sin parámetro Context.
     */
    public static AuthApi getAuthApi() {
        if (appContext == null) {
            throw new IllegalStateException("ApiClient no está inicializado. Llama ApiClient.init(context) en Application.onCreate().");
        }
        return AuthServiceFactory.getAuthApi(appContext);
    }

    public static AhorroApi getAhorroApi() {
        return retrofit.create(AhorroApi.class);
    }

    public static ConceptoGastoApi getConceptoGastoApi() {
        return retrofit.create(ConceptoGastoApi.class);
    }

    public static ConceptoIngresoApi getConceptoIngresoApi() {
        return retrofit.create(ConceptoIngresoApi.class);
    }

    public static ContactoApi getContactoApi() {
        return retrofit.create(ContactoApi.class);
    }

    public static DomicilioApi getDomicilioApi() {
        return retrofit.create(DomicilioApi.class);
    }

    public static GastoApi getGastoApi() {
        return retrofit.create(GastoApi.class);
    }

    public static IngresoApi getIngresoApi() {
        return retrofit.create(IngresoApi.class);
    }

    public static PersonaApi getPersonaApi() {
        return retrofit.create(PersonaApi.class);
    }

    public static UsuarioApi getUsuarioApi() {
        return retrofit.create(UsuarioApi.class);
    }
}
