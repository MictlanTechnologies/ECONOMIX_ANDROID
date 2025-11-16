package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.RolDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RolApi {
    @GET("/economix/api/roles")
    Call<List<RolDto>> getAll();

    @GET("/economix/api/roles/{id}")
    Call<RolDto> getById(@Path("id") Integer id);

    @POST("/economix/api/roles")
    Call<RolDto> create(@Body RolDto dto);

    @PUT("/economix/api/roles/{id}")
    Call<RolDto> update(@Path("id") Integer id, @Body RolDto dto);

    @DELETE("/economix/api/roles/{id}")
    Call<Void> delete(@Path("id") Integer id);
}
