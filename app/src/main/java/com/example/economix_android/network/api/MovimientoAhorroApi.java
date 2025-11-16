package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.MovimientoAhorroDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MovimientoAhorroApi {
    @GET("/economix/api/movimientos-ahorro")
    Call<List<MovimientoAhorroDto>> getAll();

    @GET("/economix/api/movimientos-ahorro/{id}")
    Call<MovimientoAhorroDto> getById(@Path("id") Integer id);

    @POST("/economix/api/movimientos-ahorro")
    Call<MovimientoAhorroDto> create(@Body MovimientoAhorroDto dto);

    @PUT("/economix/api/movimientos-ahorro/{id}")
    Call<MovimientoAhorroDto> update(@Path("id") Integer id, @Body MovimientoAhorroDto dto);

    @DELETE("/economix/api/movimientos-ahorro/{id}")
    Call<Void> delete(@Path("id") Integer id);
}
