package com.example.economix_android.network.repository;

import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.api.ContactoApi;
import com.example.economix_android.network.dto.ContactoDto;

import java.util.List;

import retrofit2.Callback;

public class ContactoRepository {

    private final ContactoApi contactoApi = ApiClient.getContactoApi();

    public void obtenerContactos(Callback<List<ContactoDto>> callback) {
        contactoApi.getAll().enqueue(callback);
    }

    public void crearContacto(ContactoDto dto, Callback<ContactoDto> callback) {
        contactoApi.create(dto).enqueue(callback);
    }

    public void actualizarContacto(Integer id, ContactoDto dto, Callback<ContactoDto> callback) {
        contactoApi.update(id, dto).enqueue(callback);
    }

    public void eliminarContacto(Integer id, Callback<Void> callback) {
        contactoApi.delete(id).enqueue(callback);
    }
}
