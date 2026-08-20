package com.cursojava.sistemaacademico.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cursojava.sistemaacademico.model.Aluno;
import com.cursojava.sistemaacademico.model.Nota;
import com.cursojava.sistemaacademico.service.AlunoService;

@RestController
@RequestMapping("/api/alunos")
public class AlunoRestController {

    private final AlunoService alunoService;

    public AlunoRestController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    public List<Aluno> listar() {
        return alunoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluno> buscarPorId(@PathVariable Long id) {
        Aluno aluno = alunoService.buscarPorId(id);

        if (aluno == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(aluno);
    }

    @GetMapping("{id}/notas")
    public ResponseEntity<List<Nota>> listarNotas(@PathVariable Long id) {
        Aluno aluno = alunoService.buscarPorId(id);

        if (aluno == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(alunoService.listarNotasPorAluno(id));
    }

    @GetMapping("{id}/media")
    public ResponseEntity<Map<String, Object>> consultarMedia(@PathVariable Long id) {
        Aluno aluno = alunoService.buscarPorId(id);

        if (aluno == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> resposta = Map.of(
                "nome", aluno.getNome(),
                "media", aluno.getMedia(),
                "situacao", aluno.getSituacao());

        return ResponseEntity.ok(resposta);
    }

    @PostMapping
    public ResponseEntity<Aluno> cadastrar(@RequestBody Aluno aluno) {
        if (alunoService.dadosInvalidos(aluno)) {
            return ResponseEntity.badRequest().build();
        }

        Aluno alunoCadastrado = alunoService.cadastrarAluno(
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getMatricula(),
                aluno.getIdade());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(alunoCadastrado);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Aluno> atualizar(
            @PathVariable Long id,
            @RequestBody Aluno dados) {
        Aluno alunoExistente = alunoService.buscarPorId(id);

        if (alunoExistente == null) {
            return ResponseEntity.notFound().build();
        }

        if (alunoService.dadosInvalidos(dados)) {
            return ResponseEntity.badRequest().build();
        }

        Aluno alunoAtualizado = alunoService.atualizarAluno(id, dados);

        return ResponseEntity.ok(alunoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        boolean excluiu = alunoService.excluirAluno(id);

        if (!excluiu) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/notas")
    public ResponseEntity<Nota> cadastrarNota(
            @PathVariable Long id,
            @RequestBody Nota nota) {
        Aluno aluno = alunoService.buscarPorId(id);

        if (aluno == null) {
            return ResponseEntity.notFound().build();
        }

        if (alunoService.notaInvalida(nota)) {
            return ResponseEntity.badRequest().build();
        }

        Nota notaCadastrada = alunoService.cadastrarNota(id,
                nota.getDescricao(),
                nota.getValor());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notaCadastrada);
    }

    @GetMapping("/busca")
    public ResponseEntity<List<Aluno>> buscarPorNome(
            @RequestParam String nome) {
        if (nome.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        List<Aluno> encontrados = alunoService.buscarPorNome(nome);

        return ResponseEntity.ok(encontrados);
    }
}
