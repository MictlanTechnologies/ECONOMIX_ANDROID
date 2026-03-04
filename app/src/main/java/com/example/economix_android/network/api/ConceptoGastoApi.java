package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.ConceptoGastoDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ConceptoGastoApi {
    @GET("/economix/api/conceptos-gasto")
    Call<List<ConceptoGastoDto>> getAll();

    @GET("/economix/api/conceptos-gasto/{id}")
    Call<ConceptoGastoDto> getById(@Path("id") Integer id);

    @POST("/economix/api/conceptos-gasto")
    Call<ConceptoGastoDto> create(@Body ConceptoGastoDto dto);

    @PUT("/economix/api/conceptos-gasto/{id}")
    Call<ConceptoGastoDto> update(@Path("id") Integer id, @Body ConceptoGastoDto dto);

    @DELETE("/economix/api/conceptos-gasto/{id}")
    Call<Void> delete(@Path("id") Integer id);
}
