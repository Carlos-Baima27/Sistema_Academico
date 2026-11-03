package com.cursojava.sistemaacademico.service;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.cursojava.sistemaacademico.model.Aluno;
import com.cursojava.sistemaacademico.model.MatriculaSequence;
import com.cursojava.sistemaacademico.model.Nota;
import com.cursojava.sistemaacademico.repository.AlunoRepository;
import com.cursojava.sistemaacademico.repository.MatriculaSequenceRepository;
import com.cursojava.sistemaacademico.repository.NotaRepository;

import jakarta.transaction.Transactional;

@Service
public class AlunoService {
    private final AlunoRepository alunoRepository;
    private final NotaRepository notaRepository;
    private final MatriculaSequenceRepository matriculaSequenceRepository;
    private final JdbcTemplate jdbcTemplate;

    public AlunoService(AlunoRepository alunoRepository,
            NotaRepository notaRepository,
            MatriculaSequenceRepository matriculaSequenceRepository,
            JdbcTemplate jdbcTemplate) {
        this.alunoRepository = alunoRepository;
        this.notaRepository = notaRepository;
        this.matriculaSequenceRepository = matriculaSequenceRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Aluno> listarTodos() {
        List<Aluno> alunos = alunoRepository.findAll();

        return alunos;
    }

    public long contarAlunos() {
        return alunoRepository.count();
    }

    public Aluno buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElse(null);

        return aluno;
    }

    @Transactional
    public Aluno cadastrarAluno(String nome, String email, int idade) {
        String matricula = proximaMatricula();
        Aluno aluno = new Aluno(null, nome, email,
                matricula, idade);

        return alunoRepository.save(aluno);
    }

    @Transactional
    public void inicializarMatriculas() {
        int anoAtual = Year.now().getValue();
        if (matriculaSequenceRepository.existsById(1L)) {
            criarIndiceUnicoDeMatricula();
            return;
        }

        List<Aluno> alunos = alunoRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        for (int indice = 0; indice < alunos.size(); indice++) {
            alunos.get(indice).setMatricula(formatarMatricula(anoAtual, indice + 1L));
        }

        alunoRepository.saveAll(alunos);
        alunoRepository.flush();
        matriculaSequenceRepository.save(
                new MatriculaSequence(1L, anoAtual, alunos.size() + 1L));
        criarIndiceUnicoDeMatricula();
    }

    @Transactional
    public Nota cadastrarNota(Long alunoId, String descricao,
            double valor) {
                Aluno aluno = alunoRepository.findById(alunoId)
                .orElse(null);

        if (aluno == null) {
            return null;
        }

        Nota nota = new Nota(descricao, valor);
        aluno.adicionarNota(nota);

        return notaRepository.save(nota);
    }

    public boolean dadosInvalidos(Aluno aluno) {
        return aluno.getNome() == null
                || aluno.getNome().isBlank()
                || aluno.getEmail() == null
                || aluno.getEmail().isBlank()
                || aluno.getIdade() <= 0;
    }

    public boolean notaInvalida(Nota nota) {
        return nota.getDescricao() == null
                || nota.getDescricao().isBlank()
                || !Double.isFinite(nota.getValor())
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

        Aluno alunoAtualizado = alunoRepository.save(aluno);

        return alunoAtualizado;
    }

    private String proximaMatricula() {
        MatriculaSequence sequencia = matriculaSequenceRepository.findLockedById(1L)
                .orElseThrow(() -> new IllegalStateException(
                        "A sequência de matrículas não foi inicializada."));
        int anoAtual = Year.now().getValue();
        if (sequencia.getAno() != anoAtual) {
            sequencia.setAno(anoAtual);
            sequencia.setProximoNumero(1L);
        }

        long numero = sequencia.getProximoNumero();
        sequencia.setProximoNumero(numero + 1);
        return formatarMatricula(anoAtual, numero);
    }

    private String formatarMatricula(int ano, long numero) {
        return String.format(Locale.ROOT, "%d-%04d", ano, numero);
    }

    private void criarIndiceUnicoDeMatricula() {
        jdbcTemplate.execute(
                "CREATE UNIQUE INDEX IF NOT EXISTS uk_alunos_matricula ON alunos (matricula)");
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
