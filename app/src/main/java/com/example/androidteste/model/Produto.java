package com.example.androidteste.model;

import com.example.androidteste.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class Produto implements Serializable {

    public String id;
    public String nome;
    public Long idLocal;
    public String idEmpresa;
    public String idCategoria;
    public Double valor;
    public Double valor_antigo;
    public String descricao;
    public String urlImagem;
    private Boolean addMais = false;

    public Produto() {
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference();
        setId(produtoRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(FirebaseHelper.getIdFirebase())
                .child(getId());
        produtoRef.setValue(this);

    }
    public void remover(){
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(FirebaseHelper.getIdFirebase())
                .child(getId());
        produtoRef.removeValue();

        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("produtos")
                .child(FirebaseHelper.getIdFirebase())
                .child(getId() + ".JPEG");
        storageReference.delete();


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Exclude
    public Long getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Long idLocal) {
        this.idLocal = idLocal;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Double getValor_antigo() {
        return valor_antigo;
    }

    public void setValor_antigo(Double valor_antigo) {
        this.valor_antigo = valor_antigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    @Exclude
    public Boolean getAddMais() {
        return addMais;
    }

    public void setAddMais(Boolean addMais) {
        this.addMais = addMais;
    }
}
