package com.example.controle_financeiro.controller;

import com.example.controle_financeiro.dto.UsuarioRequestDTO;
import com.example.controle_financeiro.dto.UsuarioResponseDTO;
import com.example.controle_financeiro.entity.Usuario;
import com.example.controle_financeiro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> getAll(@RequestParam(required = false) String email){
        if (email != null && !email.isEmpty()){
            return usuarioService.getAllByEmail(email);
        }
        return usuarioService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getById(@PathVariable Long id) {
        Optional<UsuarioResponseDTO> usuarioDtoOptional = usuarioService.getById(id);
        if (usuarioDtoOptional.isPresent()) {
            return ResponseEntity.ok(usuarioDtoOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioRequestDTO usuarioDto) {
        try {
            UsuarioResponseDTO usuarioDtoSave = usuarioService.create(usuarioDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDtoSave);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO usuarioDto) {
        Optional<UsuarioResponseDTO> usuarioDtoOptional = usuarioService.update(id, usuarioDto);
        if (usuarioDtoOptional.isPresent()) {
            return ResponseEntity.ok(usuarioDtoOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (usuarioService.delete(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
