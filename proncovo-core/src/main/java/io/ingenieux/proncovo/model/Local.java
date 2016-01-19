package io.ingenieux.proncovo.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class Local implements Serializable, Comparable<Local> {
    private static final long serialVersionUID = 6950017520680127162L;

    String id;

    String nome;

    Integer distancia;

    String icone;

    Integer pessoas;

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

    public Integer getDistancia() {
        return distancia;
    }

    public void setDistancia(Integer distancia) {
        this.distancia = distancia;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public Integer getPessoas() {
        return pessoas;
    }

    public void setPessoas(Integer pessoas) {
        this.pessoas = pessoas;
    }

    public int compareTo(Local other) {
        return new CompareToBuilder()
                .append(this.distancia, other.distancia)
                .append(other.pessoas, this.pessoas)
                .append(this.id, other.id).toComparison();
    }

}
