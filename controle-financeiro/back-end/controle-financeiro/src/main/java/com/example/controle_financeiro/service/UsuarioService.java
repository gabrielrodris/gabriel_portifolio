package com.example.controle_financeiro.service;

import com.example.controle_financeiro.dto.UsuarioRequestDTO;
import com.example.controle_financeiro.dto.UsuarioResponseDTO;
import com.example.controle_financeiro.entity.Usuario;
import com.example.controle_financeiro.repository.UsuarioRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseEntity<UsuarioResponseDTO> create(UsuarioRequestDTO dto) {
        return Optional.of(dto.getEmail())
                .filter(email -> !usuarioRepo.existsByEmail(email))
                .map(email -> {
                    Usuario usuario = dto.toEntity();
                    usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(UsuarioResponseDTO.fromEntity(usuarioRepo.save(usuario)));
                })
                .orElseGet(() -> ResponseEntity.badRequest().body(null));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<UsuarioResponseDTO> getById(Long id) {
        return usuarioRepo.findById(id)
                .map(usuario -> ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuario)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<UsuarioResponseDTO>> getAll(){
        List<UsuarioResponseDTO> usuarios = usuarioRepo.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @Transactional
    public ResponseEntity<UsuarioResponseDTO> update(Long id, UsuarioRequestDTO dto) {
        return (ResponseEntity<UsuarioResponseDTO>) usuarioRepo.findById(id)
                .map(usuario -> {
                    if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepo.existsByEmail(dto.getEmail())) {
                        return ResponseEntity.badRequest().body(null);
                    }
                    usuario.setNome(dto.getNome());
                    usuario.setEmail(dto.getEmail());
                    Optional.ofNullable(dto.getSenha())
                            .filter(senha -> !senha.isBlank())
                            .ifPresent(senha -> usuario.setSenha(passwordEncoder.encode(senha)));
                    return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuarioRepo.save(usuario)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<Object> delete(Long id) {
        return usuarioRepo.findById(id)
                .map(usuario -> {
                    usuarioRepo.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
