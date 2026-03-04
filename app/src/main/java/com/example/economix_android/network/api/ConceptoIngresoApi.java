package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.ConceptoIngresoDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ConceptoIngresoApi {
    @GET("/economix/api/conceptos-ingreso")
    Call<List<ConceptoIngresoDto>> getAll();

    @GET("/economix/api/conceptos-ingreso/{id}")
    Call<ConceptoIngresoDto> getById(@Path("id") Integer id);

    @POST("/economix/api/conceptos-ingreso")
    Call<ConceptoIngresoDto> create(@Body ConceptoIngresoDto dto);

    @PUT("/economix/api/conceptos-ingreso/{id}")
    Call<ConceptoIngresoDto> update(@Path("id") Integer id, @Body ConceptoIngresoDto dto);

    @DELETE("/economix/api/conceptos-ingreso/{id}")
    Call<Void> delete(@Path("id") Integer id);
}
