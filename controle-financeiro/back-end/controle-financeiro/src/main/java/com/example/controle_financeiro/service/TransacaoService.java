package com.example.controle_financeiro.service;

import com.example.controle_financeiro.dto.TransacaoRequestDTO;
import com.example.controle_financeiro.dto.TransacaoResponseDTO;
import com.example.controle_financeiro.entity.Categoria;
import com.example.controle_financeiro.entity.Transacao;
import com.example.controle_financeiro.entity.Usuario;
import com.example.controle_financeiro.enums.TipoTransacao;
import com.example.controle_financeiro.repository.CategoriaRepo;
import com.example.controle_financeiro.repository.TransacaoRepo;
import com.example.controle_financeiro.repository.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepo transacaoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private CategoriaRepo categoriaRepo;

    public List<TransacaoResponseDTO> getAll(){
        return transacaoRepo.findAll().stream().map(TransacaoResponseDTO::fromEntity)
                .toList();
    }

    public List<TransacaoResponseDTO> getAllByUsuario(Long usuarioId){
        return transacaoRepo.findByUsuarioId(usuarioId).stream()
                .map(TransacaoResponseDTO::fromEntity)
                .toList();
    }

    public List<TransacaoResponseDTO> getAllByUsuarioEPeriodo(Long usuarioId, LocalDate inicio, LocalDate fim){
        if (inicio.isAfter(fim)){
            return List.of();
        }
        return transacaoRepo.findByUsuarioIdAndDataBetween(usuarioId, inicio, fim).stream()
                .map(TransacaoResponseDTO::fromEntity)
                .toList();
    }

    public List<TransacaoResponseDTO> getAllByUsuarioETipo(Long usuarioId, TipoTransacao tipo){
        return transacaoRepo.findByUsuarioIdAndTipo(usuarioId, tipo).stream()
                .map(TransacaoResponseDTO::fromEntity)
                .toList();
    }

    public List<TransacaoResponseDTO> getAllByUsuarioECategoria(Long usuarioId, Long categoriaId) {
        return transacaoRepo.findByUsuarioIdAndCategoriaId(usuarioId, categoriaId).stream()
                .map(TransacaoResponseDTO::fromEntity)
                .toList();
    }

    public Optional<BigDecimal> calcularTotalPorTipo(Long usuarioId, TipoTransacao tipo) {
        if (!usuarioRepo.existsById(usuarioId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(transacaoRepo.calcularTotalPorTipo(usuarioId, tipo))
                .or(() -> Optional.of(BigDecimal.ZERO));
    }

    public Optional<TransacaoResponseDTO> getById(Long id) {
        Optional<Transacao> transacaoOptional = transacaoRepo.findById(id);
        if (transacaoOptional.isPresent()) {
            return Optional.of(TransacaoResponseDTO.fromEntity(transacaoOptional.get()));
        } else {
            return Optional.empty();
        }
    }


    // Criar transação
    public TransacaoResponseDTO createTransacao(TransacaoRequestDTO dto) {
        Optional<Usuario> usuario = usuarioRepo.findById(dto.getUsuarioId());
        Optional<Categoria> categoria = categoriaRepo.findById(dto.getCategoriaId());
        if (usuario.isEmpty() || categoria.isEmpty() || dto.getTipo() != categoria.get().getTipo()) {
            throw new IllegalArgumentException("Usuário ou categoria inválidos, ou tipo inconsistente");
        }
        Transacao transacao = dto.toEntity(usuario.get(), categoria.get());
        transacao = transacaoRepo.save(transacao);
        return TransacaoResponseDTO.fromEntity(transacao);
    }

    // Atualizar transação
    public Optional<TransacaoResponseDTO> updateTransacao(Long id, TransacaoRequestDTO dto) {
        Optional<Transacao> transacaoOptional = transacaoRepo.findById(id);
        Optional<Usuario> usuario = usuarioRepo.findById(dto.getUsuarioId());
        Optional<Categoria> categoria = categoriaRepo.findById(dto.getCategoriaId());
        if (transacaoOptional.isEmpty() || usuario.isEmpty() || categoria.isEmpty() ||
                dto.getTipo() != categoria.get().getTipo()) {
            return Optional.empty();
        }
        Transacao transacao = transacaoOptional.get();
        transacao.setDescricao(dto.getDescricao());
        transacao.setValor(dto.getValor());
        transacao.setData(dto.getData());
        transacao.setTipo(dto.getTipo());
        transacao.setUsuario(usuario.get());
        transacao.setCategoria(categoria.get());
        transacao = transacaoRepo.save(transacao);
        return Optional.of(TransacaoResponseDTO.fromEntity(transacao));
    }

    // Deletar transação
    public boolean deleteTransacao(Long id) {
        if (transacaoRepo.existsById(id)) {
            transacaoRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
