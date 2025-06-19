package com.example.controle_financeiro.service;

import com.example.controle_financeiro.dto.CategoriaRequestDTO;
import com.example.controle_financeiro.dto.CategoriaResponseDTO;
import com.example.controle_financeiro.enums.TipoTransacao;
import com.example.controle_financeiro.repository.CategoriaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepo categoriaRepo;

    @Transactional
    public ResponseEntity<Object> create(CategoriaRequestDTO dto) {
        return categoriaRepo.findByNomeAndTipo(dto.getNome(), dto.getTipo())
                .map(c -> ResponseEntity.badRequest().body(null))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(CategoriaResponseDTO.fromEntity(categoriaRepo.save(dto.toEntity()))));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<CategoriaResponseDTO> getById(Long id) {
        return categoriaRepo.findById(id)
                .map(categoria -> ResponseEntity.ok(CategoriaResponseDTO.fromEntity(categoria)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<CategoriaResponseDTO>> getAll() {
        List<CategoriaResponseDTO> categorias = categoriaRepo.findAll().stream()
                .map(CategoriaResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(categorias);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<CategoriaResponseDTO>> getByTipo(TipoTransacao tipo) {
        List<CategoriaResponseDTO> categorias = categoriaRepo.findByTipo(tipo).stream()
                .map(CategoriaResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(categorias);
    }

    @Transactional
    public ResponseEntity<?> update(Long id, CategoriaRequestDTO dto) {
        return categoriaRepo.findById(id)
                .map(categoria -> {
                    if (categoriaRepo.findByNomeAndTipo(dto.getNome(), dto.getTipo())
                            .filter(c -> !c.getId().equals(id)).isPresent()) {
                        return ResponseEntity.badRequest().body(null);
                    }
                    categoria.setNome(dto.getNome());
                    categoria.setTipo(dto.getTipo());
                    return ResponseEntity.ok(CategoriaResponseDTO.fromEntity(categoriaRepo.save(categoria)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<Object> deletar(Long id) {
        return categoriaRepo.findById(id)
                .map(categoria -> {
                    categoriaRepo.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
