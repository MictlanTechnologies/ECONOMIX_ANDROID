package com.mictlan.economix.sql.controler;

import com.mictlan.economix.sql.dto.PersonaDto;
import com.mictlan.economix.sql.model.Persona;
import com.mictlan.economix.sql.service.PersonaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/personas")
@AllArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    @GetMapping
    public ResponseEntity<List<PersonaDto>> getAll() {
        List<Persona> personas = personaService.getAll();
        if (personas == null || personas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(personas.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaDto> getById(@PathVariable Integer id) {
        Persona persona = personaService.getById(id);
        if (persona == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(persona));
    }

    @PostMapping
    public ResponseEntity<PersonaDto> save(@RequestBody PersonaDto dto) {
        Persona persona = personaService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(persona));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaDto> update(@PathVariable Integer id, @RequestBody PersonaDto dto) {
        Persona updated = personaService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        personaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private PersonaDto toDto(Persona persona) {
        return PersonaDto.builder()
                .idPersona(persona.getIdPersona())
                .nombrePersona(persona.getNombrePersona())
                .apellidoP(persona.getApellidoP())
                .apellidoM(persona.getApellidoM())
                .fechaNacimiento(persona.getFechaNacimiento())
                .idUsuario(persona.getIdUsuario())
                .build();
    }

    private Persona toEntity(PersonaDto dto) {
        return Persona.builder()
                .idPersona(dto.getIdPersona())
                .nombrePersona(dto.getNombrePersona())
                .apellidoP(dto.getApellidoP())
                .apellidoM(dto.getApellidoM())
                .fechaNacimiento(dto.getFechaNacimiento())
                .idUsuario(dto.getIdUsuario())
                .build();
    }
}
