package com.example.controle_financeiro.service;

import com.example.controle_financeiro.dto.MetaFinanceiraRequestDTO;
import com.example.controle_financeiro.dto.MetaFinanceiraResponseDTO;
import com.example.controle_financeiro.entity.MetaFinanceira;
import com.example.controle_financeiro.entity.Usuario;
import com.example.controle_financeiro.repository.MetaFinanceiraRepo;
import com.example.controle_financeiro.repository.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MetaFinanceiraService {

    @Autowired
    private MetaFinanceiraRepo metaFinanceiraRepo;
    @Autowired
    private UsuarioRepo usuarioRepo;

    // Buscar todas as metas financeiras
    public List<MetaFinanceiraResponseDTO> getAllMetaFinanceira() {
        return metaFinanceiraRepo.findAll().stream()
                .map(MetaFinanceiraResponseDTO::fromEntity)
                .toList();
    }

    // Buscar metas financeiras por usuário
    public List<MetaFinanceiraResponseDTO> getAllByUsuario(Long usuarioId) {
        return metaFinanceiraRepo.findByUsuarioId(usuarioId).stream()
                .map(MetaFinanceiraResponseDTO::fromEntity)
                .toList();
    }

    // Buscar metas financeiras ativas por usuário
    public List<MetaFinanceiraResponseDTO> getAllMetasAtivas(Long usuarioId) {
        return metaFinanceiraRepo.findByUsuarioIdAndDataLimiteAfter(usuarioId, LocalDate.now()).stream()
                .map(MetaFinanceiraResponseDTO::fromEntity)
                .toList();
    }

    // Buscar meta financeira por ID
    public Optional<MetaFinanceiraResponseDTO> getById(Long id) {
        Optional<MetaFinanceira> metaOptional = metaFinanceiraRepo.findById(id);
        if (metaOptional.isPresent()) {
            return Optional.of(MetaFinanceiraResponseDTO.fromEntity(metaOptional.get()));
        } else {
            return Optional.empty();
        }
    }

    // Criar meta financeira
    public MetaFinanceiraResponseDTO createMetaFinanceira(MetaFinanceiraRequestDTO dto) {
        Optional<Usuario> usuario = usuarioRepo.findById(dto.getUsuarioId());
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuário inválido");
        }
        MetaFinanceira meta = dto.toEntity(usuario.get());
        meta = metaFinanceiraRepo.save(meta);
        return MetaFinanceiraResponseDTO.fromEntity(meta);
    }

    // Atualizar meta financeira
    public Optional<MetaFinanceiraResponseDTO> updateMetaFinanceira(Long id, MetaFinanceiraRequestDTO dto) {
        Optional<MetaFinanceira> metaOptional = metaFinanceiraRepo.findById(id);
        Optional<Usuario> usuario = usuarioRepo.findById(dto.getUsuarioId());
        if (metaOptional.isEmpty() || usuario.isEmpty()) {
            return Optional.empty();
        }
        MetaFinanceira meta = metaOptional.get();
        meta.setNome(dto.getNome());
        meta.setValorObjetivo(dto.getValorObjetivo());
        meta.setDataLimite(dto.getDataLimite());
        meta.setUsuario(usuario.get());
        meta = metaFinanceiraRepo.save(meta);
        return Optional.of(MetaFinanceiraResponseDTO.fromEntity(meta));
    }

    // Deletar meta financeira
    public boolean deleteMetaFinanceira(Long id) {
        if (metaFinanceiraRepo.existsById(id)) {
            metaFinanceiraRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }


}
