package com.example.economix_android.network;

import com.example.economix_android.network.api.AhorroApi;
import com.example.economix_android.network.api.CategoriaGastoApi;
import com.example.economix_android.network.api.ConceptoGastoApi;
import com.example.economix_android.network.api.ConceptoIngresoApi;
import com.example.economix_android.network.api.ContactoApi;
import com.example.economix_android.network.api.DomicilioApi;
import com.example.economix_android.network.api.EstadoApi;
import com.example.economix_android.network.api.FuenteIngresoApi;
import com.example.economix_android.network.api.GastoApi;
import com.example.economix_android.network.api.IngresoApi;
import com.example.economix_android.network.api.MovimientoAhorroApi;
import com.example.economix_android.network.api.PersonaApi;
import com.example.economix_android.network.api.PresupuestoApi;
import com.example.economix_android.network.api.RolApi;
import com.example.economix_android.network.api.SesionApi;
import com.example.economix_android.network.api.UsuarioApi;
import com.example.economix_android.network.api.UsuarioRolApi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static final String BASE_URL = "http://192.168.1.147:8080";

    private static final Retrofit retrofit;

    static {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private ApiClient() {
    }

    public static AhorroApi getAhorroApi() {
        return retrofit.create(AhorroApi.class);
    }

    public static CategoriaGastoApi getCategoriaGastoApi() {
        return retrofit.create(CategoriaGastoApi.class);
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

    public static EstadoApi getEstadoApi() {
        return retrofit.create(EstadoApi.class);
    }

    public static FuenteIngresoApi getFuenteIngresoApi() {
        return retrofit.create(FuenteIngresoApi.class);
    }

    public static GastoApi getGastoApi() {
        return retrofit.create(GastoApi.class);
    }

    public static IngresoApi getIngresoApi() {
        return retrofit.create(IngresoApi.class);
    }

    public static MovimientoAhorroApi getMovimientoAhorroApi() {
        return retrofit.create(MovimientoAhorroApi.class);
    }

    public static PersonaApi getPersonaApi() {
        return retrofit.create(PersonaApi.class);
    }

    public static PresupuestoApi getPresupuestoApi() {
        return retrofit.create(PresupuestoApi.class);
    }

    public static RolApi getRolApi() {
        return retrofit.create(RolApi.class);
    }

    public static SesionApi getSesionApi() {
        return retrofit.create(SesionApi.class);
    }

    public static UsuarioApi getUsuarioApi() {
        return retrofit.create(UsuarioApi.class);
    }

    public static UsuarioRolApi getUsuarioRolApi() {
        return retrofit.create(UsuarioRolApi.class);
    }
}
