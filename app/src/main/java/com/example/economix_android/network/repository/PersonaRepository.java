package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.PersonaApi;
import com.example.economix_android.network.dto.PersonaDto;

import java.util.List;

import retrofit2.Callback;

public class PersonaRepository {

    private final PersonaApi personaApi = ApiClient.getPersonaApi();

    public void obtenerPersonas(Callback<List<PersonaDto>> callback) {
        personaApi.getAll().enqueue(callback);
    }

    public void crearPersona(PersonaDto dto, Callback<PersonaDto> callback) {
        personaApi.create(dto).enqueue(callback);
    }

    public void actualizarPersona(Integer id, PersonaDto dto, Callback<PersonaDto> callback) {
        personaApi.update(id, dto).enqueue(callback);
    }

    public void eliminarPersona(Integer id, Callback<Void> callback) {
        personaApi.delete(id).enqueue(callback);
    }
}
