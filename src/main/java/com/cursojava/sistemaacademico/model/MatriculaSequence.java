package com.cursojava.sistemaacademico.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "matricula_sequence")
public class MatriculaSequence {

    @Id
    private Long id;

    private int ano;
    private long proximoNumero;

    public MatriculaSequence() {
    }

    public MatriculaSequence(Long id, int ano, long proximoNumero) {
        this.id = id;
        this.ano = ano;
        this.proximoNumero = proximoNumero;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public long getProximoNumero() {
        return proximoNumero;
    }

    public void setProximoNumero(long proximoNumero) {
        this.proximoNumero = proximoNumero;
    }
}
