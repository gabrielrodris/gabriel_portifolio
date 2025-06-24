package com.example.controle_financeiro.repository;

import com.example.controle_financeiro.entity.Categoria;
import com.example.controle_financeiro.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepo extends JpaRepository<Categoria, Long> {

    List<Categoria> findByTipo(TipoTransacao tipo);
    boolean findByNomeAndTipo(String nome, TipoTransacao tipo);
}
