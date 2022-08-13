package com.example.androidteste.model;

import java.util.ArrayList;
import java.util.List;

public class CategoriaCatalogo {

    private String nome;
    private List<Produto> produtoList;

    public CategoriaCatalogo(String nome, List<Produto> produtoList) {
        this.nome = nome;
        this.produtoList = produtoList;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Produto> getProdutoList() {
        return produtoList;
    }

    public void setProdutoList(List<Produto> produtoList) {
        this.produtoList = produtoList;
    }
}
