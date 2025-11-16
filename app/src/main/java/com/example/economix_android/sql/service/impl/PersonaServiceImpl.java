package com.example.economix_android.sql.service.impl;

import com.example.economix_android.sql.model.Persona;
import com.example.economix_android.sql.repository.PersonaRepository;
import com.example.economix_android.sql.service.PersonaService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;

    @Override
    public List<Persona> getAll() {
        return personaRepository.findAll();
    }

    @Override
    public Persona getById(Integer id) {
        return personaRepository.findById(id).orElse(null);
    }

    @Override
    public Persona save(Persona persona) {
        return personaRepository.save(persona);
    }

    @Override
    public void delete(Integer id) {
        personaRepository.deleteById(id);
    }

    @Override
    public Persona update(Integer id, Persona persona) {
        return personaRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(persona, existing, "idPersona");
                    return personaRepository.save(existing);
                })
                .orElse(null);
    }
}
