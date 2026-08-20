package com.cursojava.sistemaacademico.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String matricula;
    private int idade;

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Nota> notas = new ArrayList<>();

    public Aluno() {
    }

    public Aluno(Long id, String nome, String email,
            String matricula, int idade) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.matricula = matricula;
        this.idade = idade;
    }

    // setters

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    // getters

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public int getIdade() {
        return idade;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public List<Nota> getNotas() {
        return notas;
    }

    public void carregarNotas(List<Nota> notas) {
        this.notas = new ArrayList<>(notas);
    }

    public void adicionarNota(Nota nota) {
        notas.add(nota);
        nota.setAluno(this);
    }

    public double getMedia() {
        if (notas.isEmpty()) {
            return 0.0;
        }

        double soma = 0.0;

        for (Nota nota : notas) {
            soma += nota.getValor();
        }

        return soma / notas.size();
    }

    public String getSituacao() {
        if (notas.isEmpty()) {
            return "Sem notas";
        }

        if (getMedia() >= 7.0) {
            return "Aprovado";
        }

        return "Reprovado";
    }
}
