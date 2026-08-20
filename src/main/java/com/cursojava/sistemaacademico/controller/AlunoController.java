package com.cursojava.sistemaacademico.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cursojava.sistemaacademico.model.Aluno;
import com.cursojava.sistemaacademico.service.AlunoService;

@Controller
public class AlunoController {
    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/alunos")
    public String listar(Model model) {
        model.addAttribute("alunos", alunoService.listarTodos());

        return "alunos";
    }

    @GetMapping("/alunos/novo")
    public String formularioAluno() {
        return "aluno-form";
    }

    @PostMapping("/alunos")
    public String cadastarAluno(@RequestParam String nome,
            @RequestParam String email,
            @RequestParam String matricula,
            @RequestParam int idade) {
        alunoService.cadastrarAluno(nome, email, matricula, idade);

        return "redirect:/alunos";
    }

    @GetMapping("/alunos/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        Aluno aluno = alunoService.buscarPorId(id);

        if (aluno == null) {
            return "redirect:/alunos";
        }

        model.addAttribute("aluno", aluno);
        return "aluno-detalhes";
    }

    @GetMapping("/alunos/{id}/notas/nova")
    public String formularioNota(@PathVariable Long id,
            Model model) {
        Aluno aluno = alunoService.buscarPorId(id);

        if(aluno == null) {
            return "redirect:/alunos";
        }

        model.addAttribute("aluno", aluno);
        return "nota-form";
    }

    @PostMapping("/alunos/{id}/notas")
    public String cadastrarNota(@PathVariable Long id,
                                @RequestParam String descricao,
                                @RequestParam double valor
    ) {
        alunoService.cadastrarNota(id, descricao, valor);

        return "redirect:/alunos/" + id;
    }
}
