package com.cursojava.sistemaacademico.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cursojava.sistemaacademico.model.Aluno;
import com.cursojava.sistemaacademico.model.Nota;
import com.cursojava.sistemaacademico.repository.AlunoRepository;
import com.cursojava.sistemaacademico.repository.NotaRepository;

import jakarta.transaction.Transactional;

@Service
public class AlunoService {
    private final AlunoRepository alunoRepository;
    private final NotaRepository notaRepository;

    public AlunoService(AlunoRepository alunoRepository,
            NotaRepository notaRepository) {
        this.alunoRepository = alunoRepository;
        this.notaRepository = notaRepository;
    }

    public List<Aluno> listarTodos() {
        List<Aluno> alunos = alunoRepository.findAll();

        return alunos;
    }

    public Aluno buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElse(null);

        return aluno;
    }

    public Aluno cadastrarAluno(String nome, String email,
            String matricula, int idade) {
        Aluno aluno = new Aluno(null, nome, email,
                matricula, idade);

        return alunoRepository.save(aluno);
    }

    @Transactional
    public Nota cadastrarNota(Long alunoId, String descricao,
            double valor) {
                Aluno aluno = alunoRepository.findById(alunoId)
                .orElse(null);

        if (aluno == null) {
            return null;
        }

        if (!alunoRepository.existsById(alunoId)) {
            return null;
        }

        Nota nota = new Nota(descricao, valor);

        return notaRepository.save(nota);
    }

    public boolean dadosInvalidos(Aluno aluno) {
        return aluno.getNome() == null
                || aluno.getNome().isBlank()
                || aluno.getEmail() == null
                || aluno.getEmail().isBlank()
                || aluno.getMatricula() == null
                || aluno.getMatricula().isBlank()
                || aluno.getIdade() <= 0;
    }

    public boolean notaInvalida(Nota nota) {
        return nota.getDescricao() == null
                || nota.getDescricao().isBlank()
                || nota.getValor() < 0.0
                || nota.getValor() > 10.0;
    }

    public List<Aluno> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return listarTodos();
        }

        List<Aluno> encontrados = new ArrayList<>();
        String nomeProcurado = nome.trim().toLowerCase();

        for (Aluno aluno : listarTodos()) {
            if (aluno.getNome().toLowerCase()
                    .contains(nomeProcurado)) {
                encontrados.add(aluno);
            }
        }

        return encontrados;
    }

    public Aluno atualizarAluno(Long id, Aluno dados) {
        Aluno aluno = alunoRepository.findById(id).orElse(null);

        if (aluno == null) {
            return null;
        }

        aluno.setNome(dados.getNome());
        aluno.setEmail(dados.getEmail());
        aluno.setIdade(dados.getIdade());
        aluno.setMatricula(dados.getMatricula());

        Aluno alunoAtualizado = alunoRepository.save(aluno);

        return alunoAtualizado;
    }

    @Transactional
    public boolean excluirAluno(Long id) {
        if (!alunoRepository.existsById(id)) {
            return false;
        }

        alunoRepository.deleteById(id);
        return true;
    }

    public List<Nota> listarNotasPorAluno(Long alunoId) {
        return notaRepository.findByAlunoId(alunoId);
    }

}
