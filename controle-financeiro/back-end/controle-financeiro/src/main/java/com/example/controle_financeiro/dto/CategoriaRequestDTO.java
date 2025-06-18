package com.example.controle_financeiro.dto;

import com.example.controle_financeiro.entity.Categoria;
import com.example.controle_financeiro.enums.TipoTransacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class CategoriaRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
    private String nome;

    @NotNull(message = "Tipo é obrigatório")
    private TipoTransacao tipo;

    public CategoriaRequestDTO(String nome){
        this.nome = nome;
    }

    public Categoria toEntity(){
        return new Categoria(this.nome, this.tipo);
    }
}
