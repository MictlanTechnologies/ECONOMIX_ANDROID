package com.mictlan.economix.sql.service.impl;

import com.mictlan.economix.sql.model.Contacto;
import com.mictlan.economix.sql.repository.ContactoRepository;
import com.mictlan.economix.sql.service.ContactoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ContactoServiceImpl implements ContactoService {

    private final ContactoRepository contactoRepository;

    @Override
    public List<Contacto> getAll() {
        return contactoRepository.findAll();
    }

    @Override
    public Contacto getById(Integer id) {
        return contactoRepository.findById(id).orElse(null);
    }

    @Override
    public Contacto save(Contacto contacto) {
        return contactoRepository.save(contacto);
    }

    @Override
    public void delete(Integer id) {
        contactoRepository.deleteById(id);
    }

    @Override
    public Contacto update(Integer id, Contacto contacto) {
        return contactoRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(contacto, existing, "idContactos");
                    return contactoRepository.save(existing);
                })
                .orElse(null);
    }
}
